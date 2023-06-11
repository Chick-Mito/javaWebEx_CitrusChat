package Sources;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    //Receiver.
    private UserMessage Receiver;

    //Sender.
    private UserMessage Sender;

    //Content of message.
    private String content;

    //Send time.
    private Date SendTime;

    public UserMessage getReceiver()
    {
        return Receiver;
    }

    public void setReceiver(UserMessage receiver)
    {
        Receiver = receiver;
    }

    public UserMessage getSender()
    {
        return Sender;
    }

    public void setSender(UserMessage sender)
    {
        Sender = sender;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getSendTime()
    {
        return SendTime;
    }

    public void setSendTime(Date sendTime)
    {
        SendTime = sendTime;
    }
}
