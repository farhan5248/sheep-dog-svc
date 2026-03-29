package org.farhan.mbt.maven;

public class TransformableFile {

    private String fileName;
    private String fileContent;
    private String tags;
    private String fileChecksum;

    public TransformableFile() {
    }

    public TransformableFile(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public TransformableFile(String fileName, String fileContent, String tags) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.tags = tags;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getTags() {
        return tags;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getFileChecksum() {
        return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
        this.fileChecksum = fileChecksum;
    }
}
