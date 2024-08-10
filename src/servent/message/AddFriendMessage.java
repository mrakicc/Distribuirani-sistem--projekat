package servent.message;

public class AddFriendMessage extends BasicMessage{

    private int originalSenderPort;

    private int originalRecieverPort;

    public AddFriendMessage(int sender, int receiver, int originalSenderPort, int originalRecieverPort) {
        super(MessageType.ADD_FRIEND, sender, receiver);
        this.originalSenderPort = originalSenderPort;
        this.originalRecieverPort = originalRecieverPort;
    }

    public int getOriginalSenderPort() {
        return originalSenderPort;
    }

    public int getOriginalRecieverPort() {
        return originalRecieverPort;
    }

}
