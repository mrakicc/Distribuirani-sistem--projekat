package servent.message;

import app.MyFile;

public class BackUpFileMessage extends BasicMessage {

    private MyFile file;

    public BackUpFileMessage(int senderPort, int receiverPort, MyFile file) {
        super(MessageType.BACKUP_FILE, senderPort, receiverPort);
        this.file = file;
    }

    public MyFile getFile() {
        return file;
    }
}
