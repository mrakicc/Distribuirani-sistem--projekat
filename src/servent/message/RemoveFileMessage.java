package servent.message;

public class RemoveFileMessage extends BasicMessage{

    private String fileName;

    public RemoveFileMessage(int senderPort, int receiverPort, String fileName) {
        super(MessageType.REMOVE_FILE, senderPort, receiverPort);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
