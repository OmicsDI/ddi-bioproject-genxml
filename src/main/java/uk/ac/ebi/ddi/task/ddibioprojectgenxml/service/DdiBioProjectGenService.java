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
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.xml.validator.parser.marshaller.OmicsDataMarshaller;
import uk.ac.ebi.ddi.xml.validator.parser.model.Database;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DdiBioProjectGenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdiBioProjectGenService.class);

    private BioprojectsClient bioprojectsClient;

    private static final int LINES_PER_SPLIT = 50;

    private static final String BIOPROJECT_ENDPOINT = "ftp://ftp.ncbi.nlm.nih.gov/bioproject/summary.txt";

    @Autowired
    private IDatasetService datasetService;

    @Autowired
    private IFileSystem fileSystem;

    public void generateXML(String outputFolder, String releaseDate, String databases, String filePath) {
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            releaseDate = formatter.format(date);
            GeoClient geoClient = new GeoClient(filePath + "/ncbi/Geo");
            bioprojectsClient = new BioprojectsClient(filePath + "/ncbi", geoClient);
            LOGGER.info("Output folder is {}", outputFolder);
            generate(databases, outputFolder, releaseDate);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during generation of xml file {} ", e);
        }
    }

    public void generate(String databases, String outputFolder, String releaseDate) throws Exception {

        OmicsDataMarshaller mm = new OmicsDataMarshaller();

        LOGGER.info("Calling GenerateBioprojectsOmicsXML generate");
        Path tempIdPath = Paths.get("tmp/ids");
        Files.createDirectories(tempIdPath);

        //String summaryPath = bioprojectsClient.getFilePath() + "/summary.txt";
        String summaryPath = "tmp/summary.txt";

        File summaryFile = new File(summaryPath);
        URL website = new URL(BIOPROJECT_ENDPOINT);
        try (InputStream in = website.openStream()) {
            Files.copy(in, summaryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        //splitFile(summaryPath, bioprojectsClient.getFilePath() + "/ids");
        splitFile(summaryPath, tempIdPath.toString());

        //File idFolder = new File(bioprojectsClient.getFilePath() + "/ids");
        File idFolder = new File(tempIdPath.toString());

        if (idFolder.isDirectory()) {
            int count = 1;
            for (File idFile: idFolder.listFiles()) {

            List<BioprojectDataset> datasets = bioprojectsClient.getAllDatasets(idFile.getPath())
                    .stream().filter(Objects::nonNull).collect(Collectors.toList());

            LOGGER.info("All datasets count is " + datasets.size());

            if (datasets.size() == 0) {
                LOGGER.info("bioprojectsClient.getAllDatasets() returned zero datasets");
                continue;
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

                String outputFilePath = outputFolder + "/" + databaseName + "_" + count + "_data.xml";

                try (ConvertibleOutputStream outputStream = new ConvertibleOutputStream()) {

                    LOGGER.info("output folder Filepath is " + outputFilePath);

                    Database database = new Database();
                    //database.setDescription(Constants.GEO_DESCRIPTION);
                    database.setName(databaseName); //Constants.GEO
                    database.setRelease(releaseDate);
                    database.setEntries(entries);
                    database.setEntryCount(entries.size());

                    LOGGER.info("Writing bioproject idFile at location " + outputFilePath);

                    mm.marshall(database, outputStream);

                    fileSystem.saveFile(outputStream, outputFilePath);

                    outputStream.close();

                    LOGGER.info(String.format("Exported %s %d to %s", databaseName, entries.size(), outputFilePath));

                    count++;
                }
            }

            }
        }
    }

    public void splitFile(String inputFilePath, String outputFolderPath) {
        long linesWritten = 0;
        int count = 1;
        File inputFile = new File(inputFilePath);
        try (
                InputStream inputFileStream = new BufferedInputStream(new FileInputStream(inputFile));
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputFileStream))
                ) {

            String line = reader.readLine();

            String fileName = inputFile.getName();
            String outfileName = outputFolderPath + "/" + fileName.replace(".txt", "");

            while (line != null) {
                File outFile = new File(outfileName + "_" + count + ".txt");
                Writer writer = new OutputStreamWriter(new FileOutputStream(outFile));

                while (line != null && linesWritten < LINES_PER_SPLIT) {
                    writer.write(line);
                    writer.write(System.lineSeparator());
                    line = reader.readLine();
                    linesWritten++;
                }

                writer.close();
                linesWritten = 0; //next file
                count++; //nect file count
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
