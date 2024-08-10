package servent.message;

import app.MyFile;

import java.util.List;

public class FilesMessage extends BasicMessage{

    private List<MyFile> files;

    private int targetNodePort;

    public FilesMessage(int senderPort, int receiverPort, List<MyFile> files, int targetNodePort) {
        super(MessageType.FILES, senderPort, receiverPort);
        this.files = files;
        this.targetNodePort = targetNodePort;
    }

    public List<MyFile> getFiles() {
        return files;
    }

    public int getTargetNodePort() {
        return targetNodePort;
    }
}
