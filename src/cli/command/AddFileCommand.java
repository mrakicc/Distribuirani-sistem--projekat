package cli.command;

import app.AppConfig;
import servent.message.AddFileMessage;
import servent.message.util.MessageUtil;

public class AddFileCommand implements CLICommand{
    @Override
    public String commandName() {
        return "add_file";
    }


    @Override
    public void execute(String args) {
        String[] argParts = args.split(" ");
        if (argParts.length < 3) {
            AppConfig.timestampedErrorPrint("Invalid command format. Correct: add_file <file_name> <public/private> <file_content>");
            return;
        }

        String fileName = argParts[0];
        boolean isPublic = argParts[1].equalsIgnoreCase("public");
        String fileContent = joinContent(argParts);

        String asciiContent = toAsciiContent(fileContent);

        AddFileMessage addFileMessage = new AddFileMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.myServentInfo.getListenerPort(),
                fileName,
                asciiContent,
                isPublic
        );

        MessageUtil.sendMessage(addFileMessage);
    }

    private String joinContent(String[] argParts) {
        StringBuilder fileContentBuilder = new StringBuilder();
        for (int i = 2; i < argParts.length; i++) {
            fileContentBuilder.append(argParts[i]).append(" ");
        }
        return fileContentBuilder.toString().trim();
    }

    private String toAsciiContent(String content) {
        StringBuilder asciiContentBuilder = new StringBuilder();
        for (char c : content.toCharArray()) {
            asciiContentBuilder.append((int) c).append(",");
        }
        return asciiContentBuilder.toString().replaceAll(",$", ""); // Uklanjanje poslednjeg zareza
    }
}
