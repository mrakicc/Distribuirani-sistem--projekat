package servent.message;

public class AddFileMessage extends BasicMessage {

    private String fileName;
    private Boolean isPublic;
    private String fileContent;

    public AddFileMessage(int senderPort, int receiverPort, String fileName, String fileContent, Boolean isPublic) {
        super(MessageType.ADD_FILE, senderPort, receiverPort);
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
}
