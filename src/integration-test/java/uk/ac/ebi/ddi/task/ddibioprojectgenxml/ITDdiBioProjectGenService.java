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
        "bioprojectxml.outputFolder=/tmp/testing/bioprojects",
        "bioprojectxml.database=GEO",
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
    }

    @Test
    public void contextLoads() throws Exception {
        ddiBioprojectGenxmlApplication.run();
        Assert.assertTrue(fileSystem.listFilesFromFolder(ddiBioProps.getOutputFolder()).size() > 0);
    }

}
