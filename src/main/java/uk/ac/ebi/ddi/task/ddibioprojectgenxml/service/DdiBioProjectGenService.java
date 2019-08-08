package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ddi.api.readers.bioprojects.ws.client.BioprojectsClient;
import uk.ac.ebi.ddi.api.readers.bioprojects.ws.client.GeoClient;
import uk.ac.ebi.ddi.api.readers.bioprojects.ws.model.BioprojectDataset;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.api.readers.utils.Transformers;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.service.db.repo.dataset.IDatasetRepo;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.xml.validator.parser.marshaller.OmicsDataMarshaller;
import uk.ac.ebi.ddi.xml.validator.parser.model.Database;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DdiBioProjectGenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdiBioProjectGenService.class);

    private BioprojectsClient bioprojectsClient;

    @Autowired
    private IDatasetService datasetService;

    public void generateXML(String outputFolder, String releaseDate,String databases,String filePath){
        try {
            GeoClient geoClient = new GeoClient(filePath+"ncbi/Geo");
            bioprojectsClient = new BioprojectsClient(filePath+"ncbi", geoClient);
            LOGGER.info("Output folder is {}", outputFolder);
            generate(databases, outputFolder, releaseDate);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during generation of xml file, ", e);
        }
    }

    public void generate(String databases,String outputFolder, String releaseDate) throws Exception {

        OmicsDataMarshaller mm = new OmicsDataMarshaller();

        LOGGER.info("Calling GenerateBioprojectsOmicsXML generate");

        if (bioprojectsClient == null) {
            throw new Exception("bioprojectsClient is null");
        }

        List<BioprojectDataset> datasets = bioprojectsClient.getAllDatasets()
                .stream().filter(Objects::nonNull).collect(Collectors.toList());

        LOGGER.info("All datasets count is " + datasets.size());

        if (datasets.size() == 0) {
            LOGGER.info("bioprojectsClient.getAllDatasets() returned zero datasets");
            return;
        }

        LOGGER.info("Returned {} datasets", datasets.size());

        LOGGER.info("Starting to insert datasets...");

        for (String databaseName : databases.split(",")) {
            List<Entry> entries = new ArrayList<>();

            LOGGER.info("Processing database: {} ", databaseName);

            datasets.forEach(dataset -> {
                if (dataset.getIdentifier() != null && dataset.getRepository().equals(databaseName)) {
                    dataset.addOmicsType(Constants.GENOMICS_TYPE);
                    String accession = dataset.getIdentifier();
                    //List<Dataset> existingDatasets = this.datasetService.getBySecondaryAccession(accession);
                    if (datasetService.existsBySecondaryAccession(accession)) {
                        //dataset already exists in OmicsDI, TODO: add some data
                        //this.datasetService.setDatasetNote();
                        LOGGER.info("Accession " + accession + " exists as secondary accession");
                    } else {
                        entries.add(Transformers.transformAPIDatasetToEntry(dataset)); //
                    }
                }
            });

            LOGGER.info("Found datasets entries : {}", entries.size());

            String filepath = outputFolder + "/" + databaseName + "_data.xml";

            ConvertibleOutputStream outputStream = new ConvertibleOutputStream();

            LOGGER.info("Filepath is " + filepath);

            Database database = new Database();
            //database.setDescription(Constants.GEO_DESCRIPTION);
            database.setName(databaseName); //Constants.GEO
            database.setRelease(releaseDate);
            database.setEntries(entries);
            database.setEntryCount(entries.size());


            LOGGER.info("Writing bioproject file at location " + filepath);
            mm.marshall(database, outputStream);

            LOGGER.info(String.format("Exported %s %d to %s", databaseName, entries.size(), filepath));
        }
    }
}
