package servent.message;

public class RequestTokenMessage extends BasicMessage{

    private int key;

    private int value;

    public RequestTokenMessage(int senderPort, int receiverPort, int key, int value){
        super(MessageType.REQUEST_TOKEN, senderPort, receiverPort);
        this.key = key;
        this.value = value;
    }

    public int getKey(){
        return key;
    }

    public int getValue(){
        return value;
    }
}
