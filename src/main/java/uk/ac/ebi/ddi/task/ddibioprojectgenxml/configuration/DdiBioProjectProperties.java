package uk.ac.ebi.ddi.task.ddibioprojectgenxml.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bioprojectxml")
public class DdiBioProjectProperties {

    private String filePath;

    private String releaseDate;

    private String outputFolder;

    private String databases;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

}
