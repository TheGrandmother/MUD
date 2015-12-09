package ioopm.mud.communication;

import ioopm.mud.communication.messages.Message;

public interface Adapter {

    /**
     * Polls the oldest message from the inbox.
     *
     * @return - Retrieves and removes head of inbox, null if inbox is empty.
     */
    Message poll();

    /**
     * Tries to send a message trough the adapter
     *
     * @param m - The message to send.
     */
    void sendMessage(Message m);
}
