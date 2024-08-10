package servent.handler;

import app.AppConfig;
import app.MyFile;
import servent.message.AddFileMessage;
import servent.message.BackUpFileMessage;
import servent.message.Message;
import servent.message.UpdateFinishMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddFileHandler implements MessageHandler{

    private final Message clientMessage;

    public AddFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof AddFileMessage addFileMessage){

            String filePath = saveLocally(addFileMessage.getFileName(), addFileMessage.getFileContent());

            if(filePath == null){
                AppConfig.timestampedErrorPrint("File: " + addFileMessage.getFileName() + " already exists.");
                return;
            }

            AppConfig.chordState.addFileToStorage(addFileMessage.getFileName(), addFileMessage.getFileContent() , addFileMessage.isPublic(), filePath);

            //BackUp file
            MyFile file = AppConfig.chordState.getFileFromStorage(addFileMessage.getFileName());

            while (!AppConfig.isUpdated.get()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            AppConfig.timestampedStandardPrint("Sending file to backup");

            MyFile fileCopy = new MyFile("BACK_UP_" + file.getFileName(), file.getFilePath(), file.getFileContent(), file.isPublic());

            BackUpFileMessage saveFileBackUpMessage1 = new BackUpFileMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getSuccessorTable()[0].getListenerPort(),
                    fileCopy);

            MessageUtil.sendMessage(saveFileBackUpMessage1);

            BackUpFileMessage saveFileBackUpMessage2 = new BackUpFileMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getPredecessor().getListenerPort(),
                    fileCopy);

            MessageUtil.sendMessage(saveFileBackUpMessage2);
        }
    }

    private String saveLocally(String fileName, String fileContent) {
        String serventName = "servent-" + AppConfig.myServentInfo.getChordId();

        File serventFolder = createServentFolder(AppConfig.root, serventName);

        File file = new File(serventFolder, fileName);

        if (file.exists()) {
            return null;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(fileContent);
            AppConfig.timestampedStandardPrint("File saved locally: " + file.getPath());
            return file.getPath();
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Error saving file: " + e.getMessage());
            return null;
        }
    }

    private File createServentFolder(String workingRoot, String serventName) {
        File serventFolder = new File(workingRoot, serventName);
        if (!serventFolder.exists()) {
            serventFolder.mkdirs();
        }
        return serventFolder;
    }
}
