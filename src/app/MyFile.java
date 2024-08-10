package app;

import java.io.Serializable;

public class MyFile implements Serializable {

    private String fileName;

    private String filePath;

    private String fileContent;

    private boolean isPublic;

    public MyFile(String fileName, String filePath, String fileContent, boolean isPublic) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileContent = fileContent;
        this.isPublic = isPublic;

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileContent() {
        return fileContent;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
