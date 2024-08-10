package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.FilesMessage;
import servent.message.Message;
import servent.message.ViewFilesMessage;
import servent.message.util.MessageUtil;

public class ViewFilesHandler implements MessageHandler{

    private Message clientMessage;

    public ViewFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof ViewFilesMessage viewFilesMessage){
            ServentInfo targetNode = AppConfig.getNodeByPort(viewFilesMessage.getTargetNodePort());

            if(AppConfig.chordState.isKeyMine(targetNode.getChordId())) {
                FilesMessage filesMessage = new FilesMessage(AppConfig.myServentInfo.getListenerPort(), targetNode.getListenerPort(), null, viewFilesMessage.getViewFilesNodePort());
                MessageUtil.sendMessage(filesMessage);
            } else {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(targetNode.getChordId());
                ViewFilesMessage forwardMessage = new ViewFilesMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), viewFilesMessage.getViewFilesNodePort(), viewFilesMessage.getTargetNodePort());
                MessageUtil.sendMessage(forwardMessage);
            }
        }
    }
}
