package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.RequestTokenMessage;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

public class RequestTokenHandler implements MessageHandler{

    private Message clientMessage;

    public RequestTokenHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage instanceof RequestTokenMessage requestCSMessage) {
            int chordIdKey = requestCSMessage.getKey();
            int valueFromSender = requestCSMessage.getValue();
            int valueFromReciever = AppConfig.myServentInfo.getRequestMap().get(chordIdKey);

            AppConfig.myServentInfo.getRequestMap().put(chordIdKey, Math.max(valueFromReciever,valueFromSender));

            if(AppConfig.myServentInfo.haveToken.get()){
                if(AppConfig.myServentInfo.getToken().getTokenMap().get(chordIdKey) + 1 == AppConfig.myServentInfo.getRequestMap().get(chordIdKey)) {
                    TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo.getListenerPort(), requestCSMessage.getSenderPort(), AppConfig.myServentInfo.getToken());
                    MessageUtil.sendMessage(tokenMessage);
                    AppConfig.myServentInfo.setToken(null);
                    AppConfig.myServentInfo.haveToken.set(false);
                }
            }
        }
    }
}
