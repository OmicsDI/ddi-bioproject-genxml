package uk.ac.ebi.ddi.task.ddibioprojectgenxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.service.DdiBioProjectGenService;
import uk.ac.ebi.ddi.xml.validator.parser.marshaller.OmicsDataMarshaller;
import uk.ac.ebi.ddi.xml.validator.parser.model.Database;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class DdiBioprojectGenxmlApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdiBioprojectGenxmlApplication.class);

    @Autowired
    private DdiBioProjectGenService ddiBioProjectGenService;

    @Autowired
    private DdiBioProjectProperties taskProperties;

    @Autowired
    private IFileSystem fileSystem;

    private List<Entry> entries = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(DdiBioprojectGenxmlApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        fileSystem.cleanDirectory(taskProperties.getOutputFolder());
        AtomicInteger fileCount = new AtomicInteger(0);

        List<String> datasetFiles = fileSystem.listFilesFromFolder(taskProperties.getInputFolder());

        for (String file : datasetFiles) {
            try {
                File inputFile = fileSystem.getFile(file);
                entries.addAll(ddiBioProjectGenService.getDatasets(inputFile, taskProperties.getDatabase()));
                inputFile.delete();
            } catch (Exception e) {
                LOGGER.error("Exception occurred when trying to get datasets for file: {}, ", file, e);
            }
            if (entries.size() > 0 && entries.size() > taskProperties.getEntriesPerFile()) {
                writeDatasetsToFile(entries, taskProperties.getEntriesPerFile(), fileCount);
            }
        }

        writeDatasetsToFile(entries, entries.size(), fileCount.getAndIncrement());
    }

    private void writeDatasetsToFile(List<Entry> entries, int cap, AtomicInteger counter) throws IOException {
        List<Entry> tobeWritten = new ArrayList<>();
        while (entries.size() > 0) {
            tobeWritten.add(entries.remove(0));
            if (tobeWritten.size() % cap == 0) {
                writeDatasetsToFile(tobeWritten, cap, counter.getAndIncrement());
            }
        }
    }

    private void writeDatasetsToFile(List<Entry> entries, int total, int fileCount) throws IOException {
        if (entries.size() < 1) {
            return;
        }

        String releaseDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        ConvertibleOutputStream outputStream = new ConvertibleOutputStream();
        try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            OmicsDataMarshaller mm = new OmicsDataMarshaller();

            Database database = new Database();
            if (taskProperties.getDatabase().equalsIgnoreCase(Constants.GEO)) {
                database.setDescription(Constants.GEO_DESCRIPTION);
            }
            database.setName(taskProperties.getDatabase());
            database.setRelease(releaseDate);
            database.setEntries(entries);
            database.setEntryCount(total);
            mm.marshall(database, w);
        }

        String filePath = taskProperties.getOutputFolder() + "/" + taskProperties.getPrefix() + fileCount + ".xml";
        LOGGER.info("Attempting to write data to {}", filePath);
        fileSystem.saveFile(outputStream, filePath);
        entries.clear();
    }
}
