package servent.message;

public class ViewFilesMessage extends BasicMessage{

    private int viewFilesNodePort;

    private int targetNodePort;

    public ViewFilesMessage(int senderPort, int receiverPort, int viewFilesNodePort, int targetNodePort) {
        super(MessageType.VIEW_FILES, senderPort, receiverPort);
        this.viewFilesNodePort = viewFilesNodePort;
        this.targetNodePort = targetNodePort;
    }

    public int getViewFilesNodePort() {
        return viewFilesNodePort;
    }

    public int getTargetNodePort() {
        return targetNodePort;
    }
}
