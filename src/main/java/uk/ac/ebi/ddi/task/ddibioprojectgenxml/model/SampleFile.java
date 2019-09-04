package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

import java.io.File;

/**
 * Created by azorin on 28/11/2017.
 */
public class SampleFile extends SoftFile {
    private static final String SAMPLE_TYPE = "SAMPLE";

    public SampleFile(File file) throws Exception {
        super(file);

        if (!this.type.equals(SAMPLE_TYPE)) {
            throw new Exception("expected SAMPLE, received " + this.type);
        }
    }

    public String get_Title() {
        return this.getFirstAttribute(SampleAttribute.Sample_title.getName());
    }

    public String getCellType() {
        return this.findAttributeValue(SampleAttribute.Sample_characteristics_ch1.getName(), "cell type");
    }

    public String getSampleProtocol() {
        return this.getFirstAttribute(SampleAttribute.Sample_growth_protocol_ch1.getName());
    }

    public String getDataProtocol() {
        return this.getFirstAttribute(SampleAttribute.Sample_data_processing.getName());
    }
}
