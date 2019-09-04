package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.PlatformFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SampleFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.SeriesFile;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.util.FileDownloadUtils;

import java.io.File;

@Service
public class GeoService {
    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";

    /**
     * public GeoDataset getOne(){
     * return new GeoDataset
     * }
     **/

    private File getSoftFile(String id) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", id)
                .queryParam("targ", "self")
                .queryParam("form", "text")
                .queryParam("view", "full");
        File tmpFile = File.createTempFile("omics", id + ".soft");
        FileDownloadUtils.httpDownloadFile(builder.toUriString(), tmpFile);
        return tmpFile;
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
