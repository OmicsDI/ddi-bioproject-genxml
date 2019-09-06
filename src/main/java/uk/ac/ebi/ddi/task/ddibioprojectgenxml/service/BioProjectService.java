package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.api.readers.utils.XMLUtils;
import uk.ac.ebi.ddi.ddidomaindb.database.DB;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.BioprojectDataset;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.PlatformFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SampleFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SeriesFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BioProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BioProjectService.class);
    private static final ThreadLocal<XPathFactory> XPATH_FACTORY = ThreadLocal.withInitial(XPathFactory::newInstance);

    private static final ThreadLocal<DocumentBuilderFactory> DB_FACTORY =
            ThreadLocal.withInitial(DocumentBuilderFactory::newInstance);

    @Autowired
    private GeoService geoService;

    @Autowired
    private DdiBioProjectProperties properties;

    private void addGeoAdditionInformations(BioprojectDataset dataset) throws Exception {
        SeriesFile series = geoService.getSeries(dataset.getIdentifier());
        series.getSeriesSuplimentraryFile().forEach(dataset::addDatasetFile);
        series.getSeriesContactName().stream()
                .map(x -> x.replace(",", " "))
                .map(x -> x.replace("  ", " "))
                .forEach(dataset::addSubmitter);
        series.getSeriesContactEmail().stream()
                .flatMap(x -> Arrays.stream(x.split(",")))
                .filter(x -> !x.isEmpty())
                .forEach(dataset::addSubmitterEmail);

        series.getSeriesContactInstitute().forEach(dataset::addSubmitterAffiliations);


        if (null != series.getPubmedId()) {
            dataset.addCrossReference("pubmed", series.getPubmedId());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parser = new SimpleDateFormat("MMM d yyyy");

        String status = series.getStatus();
        if (null != status) {
            try {
                String input = status.replace("Public on ", ""); //"Aug 11 2009";
                Date date = parser.parse(input);
                dataset.addDate("publication", formatter.format(date));
            } catch (ParseException exception) {
                LOGGER.error("Cannot parse date: {}", status, exception);
            }
        }

        String submissionDate = series.getSubmissionDate();
        if (null != submissionDate) {
            try {
                Date date = parser.parse(submissionDate);
                dataset.addDate("submission", formatter.format(date));
            } catch (ParseException exception) {
                LOGGER.error("Cannot parse date: {}, ", submissionDate, exception);
            }
        }
        String platformId = series.getPlatformId();
        if (null != platformId) {
            PlatformFile platformFile = geoService.getPlatform(platformId);
            dataset.addInstrument(platformFile.getTitle());
        }
        dataset.setFullLink("https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + dataset.getIdentifier());
        if (series.getSampleIds().size() > 0) {
            String sampleId = series.getSampleIds().get(0);
            try {
                SampleFile sample = geoService.getSample(sampleId);
                String celltype = sample.getCellType();
                dataset.addCellType(celltype);
                dataset.setDataProtocol(sample.getDataProtocol());
                dataset.setSampleProtocol(sample.getSampleProtocol());
                LOGGER.info("Downloaded 1 of {} sampleIds celltype: {}", series.getSampleIds().size(), celltype);
            } catch (Exception e) {
                LOGGER.error("Unable to download sample file for {}, ", sampleId, e);
            }
        }
    }

    private BioprojectDataset parseDataset(Element datasetNode, XPath xPath, String database) throws Exception {
        BioprojectDataset dataset = new BioprojectDataset();
        String id = XMLUtils.readFirstElement(datasetNode, "./ProjectDescr/ExternalLink/dbXREF/ID", xPath);
        String title = XMLUtils.readFirstElement(datasetNode, "./ProjectDescr/Title", xPath);
        String description = XMLUtils.readFirstElement(datasetNode, "./ProjectDescr/Description", xPath);
        String pubDate = XMLUtils.readFirstElement(datasetNode, "./ProjectDescr/ProjectReleaseDate", xPath);

        String omicsType = XMLUtils.readFirstElement(
                datasetNode, "./ProjectType/ProjectTypeSubmission/ProjectDataTypeSet/DataType", xPath);
        if ((null != omicsType) && omicsType.contains("Transcriptome")) {
            dataset.addOmicsType("Transcriptomics");
        }

        String organismName = XMLUtils.readFirstElement(
                datasetNode, "./ProjectType/ProjectTypeSubmission/Target/Organism/OrganismName", xPath);
        if (null != organismName) {
            dataset.addSpecies(organismName);
        }
        dataset.setRepository(database);

        if (null != id) {
            dataset.setIdentifier(id);
        }

        if (null != title) {
            dataset.setName(title);
        }

        if (null != description) {
            dataset.setDescription(description);
        }

        if (null != pubDate) {
            String[] datePart = pubDate.split("T");
            if (datePart.length > 0) {
                pubDate = datePart[0];
            }
            dataset.setPublicationDate(pubDate);
        }

        if (database.equals(Constants.GEO)) {
            try {
                addGeoAdditionInformations(dataset);
            } catch (Exception e) {
                LOGGER.error(
                        "Exception occurred when trying to fetch addition data for {}, ", dataset.getIdentifier(), e);
            }
        } else if (database.equals(DB.DB_GAP.getDBName())) {
            dataset.setFullLink("https://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=" + id);
        }

        return dataset;
    }

    private BioprojectDataset readDatasetInfo(Element datasetNode, String databaseName) {
        String accession = "Unable to retrieve";
        try {
            // XPath is not thread-safe
            XPath xPath = XPATH_FACTORY.get().newXPath();
            accession = XMLUtils.readFirstAttribute(
                    datasetNode, "./ProjectID/ArchiveID", "accession", xPath);
            String database = XMLUtils.readFirstAttribute(
                    datasetNode, "./ProjectDescr/ExternalLink/dbXREF", "db", xPath);
            if (databaseName.equalsIgnoreCase(database)) {
                return parseDataset(datasetNode, xPath, database);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred when processing dataset {}", accession, e);
        }
        return null;
    }

    public List<BioprojectDataset> getDatasets(File datasetFile, String databaseName) throws Exception {
        List<BioprojectDataset> results = new ArrayList<>();
        DocumentBuilder dBuilder = DB_FACTORY.get().newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new FileInputStream(datasetFile)));
        XPath xPath = XPATH_FACTORY.get().newXPath();
        NodeList datasetsXml = XMLUtils.findElements(doc, "/PackageSet/Package/Project/Project", xPath);
        int totalResults = datasetsXml.getLength();

        for (int i = 0; i < totalResults; i++) {
            BioprojectDataset dataset = readDatasetInfo((Element) datasetsXml.item(i), databaseName);
            if (dataset != null) {
                results.add(dataset);
            }
        }
        return results;
    }
}


