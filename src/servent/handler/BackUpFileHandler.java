package servent.handler;

import app.AppConfig;
import servent.message.BackUpFileMessage;
import servent.message.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BackUpFileHandler implements MessageHandler{

    private Message clientMessage;

    public BackUpFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage instanceof BackUpFileMessage backUpFileMessage){
            String path = saveLocally(backUpFileMessage.getFile().getFileName(), backUpFileMessage.getFile().getFileContent());

            if(path == null) {
                AppConfig.timestampedErrorPrint("File: " + backUpFileMessage.getFile().getFileName() +" already exists locally");
                return;
            }

            AppConfig.chordState.addFileToStorage(backUpFileMessage.getFile().getFileName(), backUpFileMessage.getFile().getFileContent(),
                    backUpFileMessage.getFile().isPublic(),path);
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
