package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public class FooMessage extends Message {

    public FooMessage(String receiver, String sender, String foo, String bar) {
        super(receiver, sender, Action.FOO, foo, bar);
    }
}
