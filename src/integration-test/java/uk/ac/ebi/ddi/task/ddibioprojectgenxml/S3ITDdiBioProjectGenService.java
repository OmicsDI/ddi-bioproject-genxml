package uk.ac.ebi.ddi.task.ddibioprojectgenxml;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
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
        "s3.env_auth=true",
        "s3.endpoint_url=https://s3.embassy.ebi.ac.uk",
        "s3.bucket_name=caas-omicsdi",
        "s3.region=eu-west-2"
})
public class S3ITDdiBioProjectGenService {

    @Autowired
    private DdiBioProjectGenService ddiBioProjectGenService;

    @Autowired
    private DdiBioProjectProperties ddiBioProps;

    @Autowired
    private IFileSystem fileSystem;

    @Test
    public void contextLoads() throws Exception {
        ddiBioProjectGenService.generateXML(ddiBioProps.getOutputFolder(), ddiBioProps.getReleaseDate()
                ,ddiBioProps.getDatabases(), ddiBioProps.getFilePath());
        Path path = Paths.get(ddiBioProps.getOutputFolder());
        Assert.assertTrue(Files.exists(path));
    }
}
