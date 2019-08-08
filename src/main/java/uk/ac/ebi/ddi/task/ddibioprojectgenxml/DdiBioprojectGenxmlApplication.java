package uk.ac.ebi.ddi.task.ddibioprojectgenxml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.service.DdiBioProjectGenService;

@SpringBootApplication
public class DdiBioprojectGenxmlApplication implements CommandLineRunner {

    @Autowired
    private DdiBioProjectGenService ddiBioProjectGenService;

    @Autowired
    private DdiBioProjectProperties ddiBioProjectProperties;

	public static void main(String[] args) {
		SpringApplication.run(DdiBioprojectGenxmlApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Your code goes here
        ddiBioProjectGenService.generateXML(ddiBioProjectProperties.getOutputFolder(),
                ddiBioProjectProperties.getReleaseDate(), ddiBioProjectProperties.getDatabases(),
                ddiBioProjectProperties.getFilePath());
	}
}
