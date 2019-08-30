package uk.ac.ebi.ddi.task.ddibioprojectgenxml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.service.DdiBioProjectGenService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class DdiBioprojectGenxmlApplication implements CommandLineRunner {

    @Autowired
    private DdiBioProjectGenService ddiBioProjectGenService;

    @Autowired
    private DdiBioProjectProperties ddiBioProjectProperties;

    @Autowired
    private IFileSystem fileSystem;

    public static void main(String[] args) {
        SpringApplication.run(DdiBioprojectGenxmlApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // creating temporary folder
        Path geoPath = Paths.get(ddiBioProjectProperties.getFilePath() + "/ncbi/Geo");
        Files.createDirectories(geoPath);
        Path bioprojectPath = Paths.get(ddiBioProjectProperties.getFilePath() + "/bioprojects");
        Files.createDirectories(bioprojectPath);
        // Your code goes here
        ddiBioProjectGenService.generateXML(bioprojectPath.toString(),
                ddiBioProjectProperties.getReleaseDate(), ddiBioProjectProperties.getDatabases(),
                ddiBioProjectProperties.getFilePath());
//        fileSystem.copyDirectory(ddiBioProjectProperties.getOutputFolder(),
//                new File(bioprojectPath.toString()));
    }
}
