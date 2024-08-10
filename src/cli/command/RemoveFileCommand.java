package cli.command;

import app.AppConfig;
import servent.message.RemoveFileMessage;
import servent.message.util.MessageUtil;

public class RemoveFileCommand implements CLICommand{
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        String[] argParts = args.split(" ");
        if (argParts.length != 1) {
            AppConfig.timestampedErrorPrint("Invalid command format. Correct: remove_file <file_name>");
            return;
        }

        String fileName = argParts[0];

        RemoveFileMessage rfm = new RemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.myServentInfo.getListenerPort(), fileName);
        MessageUtil.sendMessage(rfm);
    }
}
