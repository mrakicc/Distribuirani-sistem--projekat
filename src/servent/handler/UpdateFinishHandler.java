package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.UpdateFinishMessage;
import servent.message.util.MessageUtil;

public class UpdateFinishHandler implements MessageHandler{

    private Message clientMessage;

    public UpdateFinishHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof UpdateFinishMessage updateFinishMessage){
            if(updateFinishMessage.isUpdateFinish() == null) {
                UpdateFinishMessage updateFinish = new UpdateFinishMessage(AppConfig.myServentInfo.getListenerPort(), updateFinishMessage.getSenderPort(), AppConfig.isUpdated.get());
                MessageUtil.sendMessage(updateFinish);
            } else {
                AppConfig.isUpdated.set(updateFinishMessage.isUpdateFinish());
            }
        }
    }
}
