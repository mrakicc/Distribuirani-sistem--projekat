package servent.handler;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.AddFriendMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class AddFriendHandler implements MessageHandler{

    private final Message clientMessage;

    public AddFriendHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof AddFriendMessage addFriendMessage) {
            if(AppConfig.myServentInfo.getListenerPort() == addFriendMessage.getOriginalRecieverPort()){
                ServentInfo newFriend = AppConfig.getNodeByPort(addFriendMessage.getOriginalSenderPort());
                AppConfig.chordState.addFriend(newFriend);
            } else {
                ServentInfo newFriend = AppConfig.getNodeByPort(addFriendMessage.getOriginalRecieverPort());

                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newFriend.getChordId());

                AddFriendMessage forwardMessage = new AddFriendMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(),
                        addFriendMessage.getOriginalSenderPort(), addFriendMessage.getOriginalRecieverPort());
                MessageUtil.sendMessage(forwardMessage);
            }
        }

    }
}
