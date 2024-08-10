package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.TokenMessage;

public class TokenHandler implements MessageHandler{

    private Message clientMessage;

    public TokenHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage instanceof TokenMessage) {
            TokenMessage tokenMessage = (TokenMessage) clientMessage;

            AppConfig.myServentInfo.setToken(tokenMessage.getToken());
            AppConfig.myServentInfo.haveToken.set(true);
        }
    }
}
