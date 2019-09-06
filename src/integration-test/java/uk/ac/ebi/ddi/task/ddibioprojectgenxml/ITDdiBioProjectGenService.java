package uk.ac.ebi.ddi.task.ddibioprojectgenxml;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;

import java.io.File;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DdiBioprojectGenxmlApplication.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "bioprojectxml.outputFolder=/tmp/testing/bioprojects/out",
        "bioprojectxml.inputFolder=/tmp/testing/bioprojects/in",
        "bioprojectxml.database=GEO",
        "bioprojectxml.batch_size=100",
        "file.provider=local"
})
public class ITDdiBioProjectGenService {

    @Autowired
    private DdiBioprojectGenxmlApplication ddiBioprojectGenxmlApplication;

    @Autowired
    private DdiBioProjectProperties ddiBioProps;

    @Autowired
    private IFileSystem fileSystem;

    @Before
    public void setUp() throws Exception {
        Files.createDirectories(new File(ddiBioProps.getOutputFolder()).toPath());
        Files.createDirectories(new File(ddiBioProps.getInputFolder()).toPath());
        Files.createDirectories(new File(ddiBioProps.getIntermediateFolder()).toPath());
        File importFile = new File(getClass().getClassLoader().getResource("output_0.xml").getFile());
        fileSystem.copyFile(importFile, ddiBioProps.getInputFolder() + "/output_0.xml");

        importFile = new File(getClass().getClassLoader().getResource("output_1.xml").getFile());
        fileSystem.copyFile(importFile, ddiBioProps.getInputFolder() + "/output_1.xml");
    }

    @Test
    public void contextLoads() throws Exception {
        ddiBioprojectGenxmlApplication.run();
        Assert.assertTrue(fileSystem.listFilesFromFolder(ddiBioProps.getOutputFolder()).size() > 0);
    }

}
