package servent.handler;

import app.AppConfig;
import app.MyFile;
import app.ServentInfo;
import servent.message.FilesMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.util.List;

public class FilesHandler implements MessageHandler{

    private Message clientMessage;

    public FilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof FilesMessage filesMessage){
            //node who requested files
            if(filesMessage.getFiles() != null){
                AppConfig.timestampedStandardPrint("Files from port: " + filesMessage.getSenderPort());
                for(MyFile file : filesMessage.getFiles()){
                    AppConfig.timestampedStandardPrint("File: " + file.getFileName()+ ", Public: " + file.isPublic() +", Path: " + file.getFilePath());
                }
            }
            //node who has files that need to be sent
            else {
                ServentInfo targetNode = AppConfig.getNodeByPort(filesMessage.getTargetNodePort());

                if(!AppConfig.chordState.getFriends().contains(targetNode)){
                    //If is node who execute command "view files" not my friend, sent him only public files
                    List<MyFile> publicFiles = AppConfig.chordState.getFileStorage().stream()
                            .filter(MyFile::isPublic)
                            .toList();

                    FilesMessage returnFiles = new FilesMessage(AppConfig.myServentInfo.getListenerPort(), targetNode.getListenerPort(), publicFiles, targetNode.getListenerPort());
                    MessageUtil.sendMessage(returnFiles);
                } else {
                    //If is node who execute command "view files" my friend, sent him all files
                    FilesMessage returnFiles = new FilesMessage(AppConfig.myServentInfo.getListenerPort(), targetNode.getListenerPort(), AppConfig.chordState.getFileStorage(), targetNode.getListenerPort());
                    MessageUtil.sendMessage(returnFiles);
                }
            }
        }
    }
}
