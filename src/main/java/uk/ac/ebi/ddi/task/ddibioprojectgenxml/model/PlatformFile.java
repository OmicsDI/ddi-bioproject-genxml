package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

import java.io.File;

/**
 * Created by azorin on 04/12/2017.
 */
public class PlatformFile extends SoftFile {
    private static final String PLATFORM_TYPE = "PLATFORM";

    public PlatformFile(File file) throws Exception {
        super(file, PlatformAttribute.getKeys());

        if (!this.type.equals(PLATFORM_TYPE)) {
            throw new Exception("expected PLATFORM, received " + this.type);
        }
    }

    public String getTitle() {
        return this.getFirstAttribute(PlatformAttribute.Platform_title.getName());
    }
}
