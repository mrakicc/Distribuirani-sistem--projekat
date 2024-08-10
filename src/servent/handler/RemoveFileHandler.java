package servent.handler;

import app.AppConfig;
import app.MyFile;
import app.ServentInfo;
import servent.message.Message;
import servent.message.RemoveFileMessage;
import servent.message.util.MessageUtil;

import java.io.File;

public class RemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof RemoveFileMessage removeFileMessage){
            String fileName = removeFileMessage.getFileName();

            MyFile file = AppConfig.chordState.getFileFromStorage(fileName);

            if (file != null) {
                boolean removed = removeLocally(fileName);

                if (removed) {
                    AppConfig.chordState.removeFileFromStorage(fileName);
                    AppConfig.timestampedStandardPrint("File " + file.getFilePath() + " removed locally");
                } else {
                    AppConfig.timestampedErrorPrint("File " + file.getFilePath() + " does not exist");
                }
            } else {

                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(AppConfig.myServentInfo.getChordId() + 1);

                RemoveFileMessage forwardMessage = new RemoveFileMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        nextNode.getListenerPort(),
                        fileName
                );

                MessageUtil.sendMessage(forwardMessage);
            }


        }
    }

    private boolean removeLocally(String fileName) {
        String serventName = "servent-" + AppConfig.myServentInfo.getChordId();

        File serventFolder = new File(AppConfig.root, serventName);

        File file = new File(serventFolder, fileName);
        if (!file.exists()) {
            return false;
        }

        return file.delete();
    }
}
