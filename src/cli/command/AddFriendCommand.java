package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.AddFriendMessage;
import servent.message.RequestTokenMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class AddFriendCommand implements CLICommand{

    @Override
    public String commandName() {
        return "add_friend";
    }

    @Override
    public void execute(String args) {
        String[] addressParts = args.split(":");
        if (addressParts.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid address format. Use address:port");
            return;
        }

        try {
            int port = Integer.parseInt(addressParts[1]);

            ServentInfo newFriend = AppConfig.getNodeByPort(port);

            if (newFriend == null) {
                AppConfig.timestampedErrorPrint("No node is listening on port " + port);
                return;
            }

            if(!isMyFriendActive(port)) {
                AppConfig.timestampedErrorPrint("Friend is not active yet.");
                return;
            }

            AppConfig.chordState.addFriend(newFriend);

            // Send a message to the new friend, and reciever will add this servent as a friend
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newFriend.getChordId());

            AddFriendMessage addFriendMessage = new AddFriendMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(),
                    AppConfig.myServentInfo.getListenerPort(), port);
            MessageUtil.sendMessage(addFriendMessage);

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number: " + addressParts[1]);
        }
    }

    private boolean isMyFriendActive(int port) {
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
