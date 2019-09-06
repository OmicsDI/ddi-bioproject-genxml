package uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bioprojectxml")
public class DdiBioProjectProperties {

    private String outputFolder;

    private String inputFolder;

    private String intermediateFolder = "/tmp";

    private String database;

    private String prefix = "BioProject-";

    private int entriesPerFile = 10;

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public String getIntermediateFolder() {
        return intermediateFolder;
    }

    public void setIntermediateFolder(String intermediateFolder) {
        this.intermediateFolder = intermediateFolder;
    }

    public int getEntriesPerFile() {
        return entriesPerFile;
    }

    public void setEntriesPerFile(int entriesPerFile) {
        this.entriesPerFile = entriesPerFile;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

}
