package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

import java.io.File;
import java.util.List;

/**
 * Created by azorin on 28/11/2017.
 */


public class SeriesFile extends SoftFile {
    private static final String SERIES_TYPE = "SERIES";

    public SeriesFile(File file) throws Exception {
        super(file, SeriesAttribute.getKeys());

        if (!this.type.equals(SERIES_TYPE)) {
            throw new Exception("expected SERIES, received " + this.type);
        }
    }

    public List<String> getSampleIds() {
        return getAttribute(SeriesAttribute.Series_sample_id.getName());
    }

    public String getOrganism() {
        return this.getFirstAttribute(SeriesAttribute.Series_sample_organism.getName());
    }

    public List<String> getSeriesSuplimentraryFile() {
        return getAttribute(SeriesAttribute.Series_supplementary_file.getName());
    }

    public List<String> getSeriesContactName() {
        return getAttribute(SeriesAttribute.Series_contact_name.getName());
    }

    public List<String> getSeriesContactEmail() {
        return getAttribute(SeriesAttribute.Series_contact_email.getName());
    }

    public List<String> getSeriesContactInstitute() {
        return getAttribute(SeriesAttribute.Series_contact_institute.getName());
    }

    public String getPlatformId() {
        List<String> keys = getAttribute(SeriesAttribute.Series_platform_id.getName());
        return keys.isEmpty() ? null : keys.get(0);
    }

    public String getPubmedId() {
        List<String> keys = getAttribute(SeriesAttribute.Series_pubmed_id.getName());
        return keys.isEmpty() ? null : keys.get(0);
    }

    public String getStatus() {
        List<String> keys = getAttribute(SeriesAttribute.Series_status.getName());
        return keys.isEmpty() ? null : keys.get(0);
    }

    public String getSubmissionDate() {
        List<String> keys = getAttribute(SeriesAttribute.Series_submission_date.getName());
        return keys.isEmpty() ? null : keys.get(0);
    }
}
