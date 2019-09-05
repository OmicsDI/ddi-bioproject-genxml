package uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bioprojectxml")
public class DdiBioProjectProperties {

    private String outputFolder;

    private String database;

    private String prefix = "BioProject-";

    private String apiKey = "";

    private int batchSize = 30;

    private int entriesPerFile = 10;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
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
