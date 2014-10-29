package yolo.ioopm.mud.communication;

public class ClientAdapter implements Adapter {
    @Override
    public void sendMessage(Message message) throws CommunicationError {

    }

    @Override
    public Message pollForMessage() throws CommunicationError {
        return null;
    }
}
