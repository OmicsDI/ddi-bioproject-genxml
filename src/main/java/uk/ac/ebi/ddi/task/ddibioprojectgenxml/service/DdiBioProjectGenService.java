package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.api.readers.utils.Transformers;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.exceptions.FileDownloadException;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.BioprojectDataset;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.util.FileDownloadUtils;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DdiBioProjectGenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdiBioProjectGenService.class);
    private static final String BIOPROJECT_ENDPOINT = "ftp://ftp.ncbi.nlm.nih.gov/bioproject/summary.txt";

    @Autowired
    private BioProjectService bioProjectService;

    @Autowired
    private IDatasetService datasetService;

    public List<String> getBioProjectIds() throws IOException, FileDownloadException, InterruptedException {
        File summaryFile = File.createTempFile("omics", "summary.txt");
        FileDownloadUtils.downloadFile(BIOPROJECT_ENDPOINT, summaryFile);

        List<String> result = new ArrayList<>();
        LineIterator it = FileUtils.lineIterator(summaryFile, "UTF-8");
        try {
            while (it.hasNext()) {
                String accession = it.nextLine().split("\t")[2];

                if (accession.startsWith("PRJNA")) {
                    result.add(accession);
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        return result;
    }


    public List<Entry> getDatasets(List<String> ids, String databaseName) throws Exception {
        List<BioprojectDataset> datasets = bioProjectService.getDatasets(ids, databaseName);
        List<Entry> entries = new ArrayList<>();
        for (BioprojectDataset dataset : datasets) {
            if (dataset.getIdentifier() != null) {
                dataset.addOmicsType(Constants.GENOMICS_TYPE);
                String accession = dataset.getIdentifier();
                if (datasetService.existsBySecondaryAccession(accession)) {
                    // Dataset already exists in OmicsDI,
                    // TODO: add some data
                    LOGGER.info("Accession " + accession + " exists as secondary accession");
                } else {
                    entries.add(Transformers.transformAPIDatasetToEntry(dataset)); //
                }
            }
        }
        return entries;
    }
}
