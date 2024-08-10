package servent.message;


import app.SuzukiKasamiToken;

public class TokenMessage extends BasicMessage {

    private SuzukiKasamiToken token;

    public TokenMessage(int senderPort, int receiverPort, SuzukiKasamiToken token) {
        super(MessageType.TOKEN, senderPort, receiverPort);
        this.token = token;
    }

    public SuzukiKasamiToken getToken() {
        return token;
    }

}
