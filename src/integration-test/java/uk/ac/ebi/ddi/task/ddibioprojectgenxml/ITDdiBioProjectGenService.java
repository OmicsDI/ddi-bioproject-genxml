package uk.ac.ebi.ddi.task.ddibioprojectgenxml;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.service.DdiBioProjectGenService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DdiBioprojectGenxmlApplication.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "bioprojectxml.filePath=/tmp/prod/",
        "bioprojectxml.releaseDate=080819",
        "bioprojectxml.outputFolder=/tmp/prod/bioprojects",
        "bioprojectxml.databases=GEO,dbGaP",
        "file.provider=local"
})
public class ITDdiBioProjectGenService {

    @Autowired
    private DdiBioProjectGenService ddiBioProjectGenService;

    @Autowired
    private DdiBioProjectProperties ddiBioProps;

    @Test
    public void contextLoads() throws Exception {
        ddiBioProjectGenService.generateXML(ddiBioProps.getOutputFolder(), ddiBioProps.getReleaseDate()
        ,ddiBioProps.getDatabases(), ddiBioProps.getFilePath());
        Path path = Paths.get(ddiBioProps.getOutputFolder());
        Assert.assertTrue(Files.exists(path));
    }
}
