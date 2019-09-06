package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration.DdiBioProjectProperties;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.PlatformFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SampleFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SeriesFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.util.FileDownloadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GeoService {
    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";
    private static final String CACHE_DIR = "soft";

    /**
     * public GeoDataset getOne(){
     * return new GeoDataset
     * }
     **/

    @Autowired
    private IFileSystem fileSystem;

    @Autowired
    private DdiBioProjectProperties properties;

    public GeoService() throws IOException {
        Files.createDirectories(new File(CACHE_DIR).toPath());
    }

    private File getSoftFile(String id) throws Exception {
        File softFile = new File("soft", id + ".soft");
        if (softFile.isFile()) {
            return softFile;
        }
        String filePath = properties.getIntermediateFolder() + "/" + id + ".soft";
        if (fileSystem.isFile(filePath)) {
            File file = fileSystem.getFile(filePath);
            Files.copy(file.toPath(), new FileOutputStream(softFile));
            return file;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", id)
                .queryParam("targ", "self")
                .queryParam("form", "text")
                .queryParam("view", "quick");
        FileDownloadUtils.httpDownloadFile(builder.toUriString(), softFile);
        fileSystem.copyFile(softFile, filePath);
        return softFile;
    }

    public SeriesFile getSeries(String id) throws Exception {
        File f = getSoftFile(id);
        if (f.exists()) {
            return new SeriesFile(f);
        }
        return null;
    }

    public PlatformFile getPlatform(String id) throws Exception {
        File f = getSoftFile(id);
        if (f.exists()) {
            return new PlatformFile(f);
        }
        return null;
    }

    public SampleFile getSample(String id) throws Exception {
        File f = getSoftFile(id);
        if (f.exists()) {
            return new SampleFile(f);
        }
        return null;
    }
}
