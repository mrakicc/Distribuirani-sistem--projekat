package servent.message;

public class UpdateFinishMessage extends BasicMessage{

    private Boolean updateFinish;

    public UpdateFinishMessage(int senderPort, int receiverPort, Boolean updateFinish) {
        super(MessageType.UPDATE_FINISH, senderPort, receiverPort);
        this.updateFinish = updateFinish;
    }

    public Boolean isUpdateFinish() {
        return updateFinish;
    }
}
