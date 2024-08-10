package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.ViewFilesMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ViewFilesCommand implements CLICommand{
    @Override
    public String commandName() {
        return "view_files";
    }

    @Override
    public void execute(String args) {
        String[] argParts = args.split(" ");

        if (argParts.length != 1) {
            AppConfig.timestampedErrorPrint("Invalid command format. Correct: get_files <node_port>");
            return;
        }

        int port = Integer.parseInt(argParts[0]);

        //target node
        ServentInfo targetNode = AppConfig.getNodeByPort(port);

        if(targetNode != null) {

            if(!isActive(port)) {
                AppConfig.timestampedErrorPrint("Node with port " + port + " is not active yet.");
                return;
            }


            int viewFilesNodePort = AppConfig.myServentInfo.getListenerPort();
            // Sening first message to myself
            ViewFilesMessage viewFilesMessage = new ViewFilesMessage(viewFilesNodePort, viewFilesNodePort, viewFilesNodePort, targetNode.getListenerPort());
            MessageUtil.sendMessage(viewFilesMessage);
        } else {
            AppConfig.timestampedErrorPrint("Node with port " + port + " does not exist.");
        }
    }

    private boolean isActive(int port) {
        Socket bsSocket = null;
        PrintWriter bsWriter = null;
        Scanner bsScanner = null;
        String[] parts;

        try {
            bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);
            bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write("Active Servents\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
            bsWriter.flush();

            bsScanner = new Scanner(bsSocket.getInputStream());
            parts = bsScanner.nextLine().split(",");

            AppConfig.activeServents = Arrays.stream(parts).map(Integer::parseInt).toList();
            bsSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return AppConfig.activeServents.contains(port);
    }

}
