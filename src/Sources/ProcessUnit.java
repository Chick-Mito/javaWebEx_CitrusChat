package Sources;

import Server.ServerDataStore;
import Server.ServerLogin;
import Server.ServerRecord;


import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import static Server.ServerDataStore.OnlineCount;
import static Server.ServerLogin.msgArea;

public class ProcessUnit implements Runnable {

    private Socket clientSocket;

    public ProcessUnit(Socket clientSocket){
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        boolean Continue = true;
        try {
            ServerRecord thisRecord = new ServerRecord(
                    new ObjectInputStream(clientSocket.getInputStream()),
                    new ObjectOutputStream(clientSocket.getOutputStream()));

            while (Continue){
                Plea plea = (Plea)thisRecord.getObject_IS().readObject();
                String action = plea.getAction();
                System.out.println("收到来自"+clientSocket.getLocalAddress() +":"+clientSocket.getPort() +"的请求："+ action);
                switch (action) {
                    case "Register":
                        RegisterUser(thisRecord, plea);
                        break;
                    case "Login":
                        Login(thisRecord, plea);
                        break;
                    case "Exit":
                        Continue = Exit(thisRecord, plea);
                        break;
                    case "Chat":
                        Chat(plea);
                        break;
                    case "Shake":
                        // Shake(plea);
                        break;
                    case "ToSendFile":
                        // ToSendFile(plea);
                        break;
                    case "AgreeReceiveFile":
                        // AgreeReceiveFile(plea);
                        break;
                    case "RefuseReceiveFile":
                        // RefuseReceiveFile(plea);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    // 注册
    public void RegisterUser(ServerRecord thisRecord,Plea plea) throws IOException {
        UserMessage user = (UserMessage)plea.getData("User");
        ServiceForUser service = new ServiceForUser();
        service.AddUser(user);

        Reply reply = new Reply();
        reply.setPhase(ReplyPhase.SUCCESS);
        reply.setData("User", user);

        thisRecord.getObject_OS().writeObject(reply);
        thisRecord.getObject_OS().flush();

        ServerDataStore.RegisterUserUI.add(new String[]{
                        String.valueOf(user.getID()),
                        user.getPassword(),
                        user.getNickname(),
                        String.valueOf(user.getSex())
                });
    }

    // 登录
    public void Login(ServerRecord thisRecord,Plea plea) throws IOException {
        String ID = (String)plea.getData("ID");
        String Password = (String)plea.getData("Password");
        ServiceForUser service = new ServiceForUser();
        UserMessage user = service.Login(Long.parseLong(ID), Password);

        Reply reply = new Reply();

        if(user!=null) {
            // 已登录
            if(ServerDataStore.OnlineUserMap.containsKey(user.getID())) {
                reply.setPhase(ReplyPhase.SUCCESS);
                reply.setData("Message", "账号已登录");
                thisRecord.getObject_OS().writeObject(reply);
                thisRecord.getObject_OS().flush();
            } else { // 更新在线表
                ServerDataStore.OnlineUserMap.put(user.getID(), user);
                reply.setData("OnlineUsers", new CopyOnWriteArrayList<UserMessage>(ServerDataStore.OnlineUserMap.values()));
                reply.setPhase(ReplyPhase.SUCCESS);
                reply.setData("User", user);
                thisRecord.getObject_OS().writeObject(reply);
                thisRecord.getObject_OS().flush();

                Reply anotherReply = new Reply();
                anotherReply.setType(ReplyType.LOG_IN);
                anotherReply.setData("LoginUser", user);
                IterativeReply(anotherReply);

                ServerDataStore.OnlineInfoMap.put(user.getID(),thisRecord);

                // OnlineCount("当前在线用户数：" + ServerDataStore.OnlineUserMap.size());
                ServerDataStore.OnlineUserUI.add(new String[]{
                                String.valueOf(user.getID()),
                                user.getNickname(),
                                String.valueOf(user.getSex())
                        });
                OnlineCount = ServerDataStore.OnlineInfoMap.size();
                ServerLogin.OnlineCount.setText("当前在线用户数：" + OnlineCount);
            }
        } else {
            reply.setPhase(ReplyPhase.SUCCESS);
            reply.setData("Message", "密码错误！");
            thisRecord.getObject_OS().writeObject(reply);
            thisRecord.getObject_OS().flush();
        }
    }

    // 退出
    public boolean Exit(ServerRecord thisRecord,Plea plea) throws IOException {
        System.out.println(clientSocket.getInetAddress().getHostAddress()+
                ":"+clientSocket.getPort()+" 离开了.");

        UserMessage user = (UserMessage)plea.getData("User");
        ServerDataStore.OnlineInfoMap.remove(user.getID());
        ServerDataStore.OnlineUserMap.remove(user.getID());
        OnlineCount = ServerDataStore.OnlineInfoMap.size();
        ServerLogin.OnlineCount.setText("当前在线用户数：" + OnlineCount);

        Reply reply = new Reply();
        reply.setType(ReplyType.LOG_OUT);
        reply.setData("LogoutUser", user);
        thisRecord.getObject_OS().writeObject(reply);
        thisRecord.getObject_OS().flush();
        clientSocket.close();

        ServerDataStore.OnlineUserUI.remove(user.getID());
        IterativeReply(reply);

        return false;
    }

    // 群聊
    public void Chat(Plea plea) throws IOException {
        Message message = (Message)plea.getData("Message");
        Reply reply = new Reply();
        reply.setPhase(ReplyPhase.SUCCESS);
        reply.setType(ReplyType.CHAT);
        reply.setData("TextMessage", message);

        // 私聊
        if(message.getReceiver()!=null) {
            ServerRecord service = ServerDataStore.OnlineInfoMap.get(message.getReceiver().getID());
            SendReply(service, reply);
        } else {
            // 群聊
            for(Long ID:ServerDataStore.OnlineInfoMap.keySet()) {
                if(message.getSender().getID()==ID) {
                    //Skip sender.
                    continue;
                } else {
                    ServerRecord service=ServerDataStore.OnlineInfoMap.get(ID);
                    SendReply(service,reply);
                }
            }
        }
    }

    // 回复
    private void IterativeReply(Reply reply) throws IOException {
        for(ServerRecord recordedClient:ServerDataStore.OnlineInfoMap.values()) {
            ObjectOutputStream Object_OS = recordedClient.getObject_OS();
            Object_OS.writeObject(reply);
            Object_OS.flush();
        }
    }

    private void SendReply(ServerRecord thisRecord,Reply reply) throws IOException {
        ObjectOutputStream Object_OS = thisRecord.getObject_OS();
        Object_OS.writeObject(reply);
        Object_OS.flush();
    }

    private static void SendReplyOfficial(ServerRecord thisRecord,Reply reply) throws IOException {
        ObjectOutputStream Object_OS = thisRecord.getObject_OS();
        Object_OS.writeObject(reply);
        // msgArea.append(reply.toString());
        Object_OS.flush();
    }

    // 系统广播
    public static void Broadcast(String msg) throws IOException {
        UserMessage system = new UserMessage(8080,"system");
        Message message = new Message();
        message.setSender(system);
        message.setSendTime(new Date());

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(dateFormat.format(message.getSendTime())).append(" ");
        buffer.append("【System Notice】\n"+msg+"\n");
        message.setContent(buffer.toString());

        Reply reply = new Reply();
        reply.setPhase(ReplyPhase.SUCCESS);
        reply.setType(ReplyType.BROADCAST);
        reply.setData("TextMessage", message);

        // 广播
        for(Long ID:ServerDataStore.OnlineInfoMap.keySet()) {
            SendReplyOfficial(ServerDataStore.OnlineInfoMap.get(ID),reply);
        }
    }

    // 系统私聊
    public static void Broadcast2(String msg, UserMessage ThatUser) throws IOException {
        UserMessage system = new UserMessage(8080,"system");
        Message message = new Message();
        message.setSender(system);
        message.setSendTime(new Date());
        message.setReceiver(ThatUser);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(dateFormat.format(message.getSendTime())).append(" ");
        buffer.append("【System Notice】\n"+msg+"\n");
        message.setContent(buffer.toString());

        Reply reply = new Reply();
        reply.setPhase(ReplyPhase.SUCCESS);
        reply.setType(ReplyType.CHAT);
        reply.setData("TextMessage", message);

        ServerRecord record = ServerDataStore.OnlineInfoMap.get(message.getReceiver().getID());
        SendReplyOfficial(record,reply);
    }

    // 踢出群聊
    public static void Remove(UserMessage KickedUser)throws IOException {
        UserMessage system = new UserMessage(8080,"system");
        Message message = new Message();
        message.setSender(system);
        message.setSendTime(new Date());
        message.setReceiver(KickedUser);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(dateFormat.format(message.getSendTime())).append(" ");
        buffer.append("【System Notice】\n 你已被踢出群聊！\n");
        message.setContent(buffer.toString());

        Reply reply = new Reply();
        reply.setPhase(ReplyPhase.SUCCESS);
        reply.setType(ReplyType.KICK_OUT);
        reply.setData("TextMessage", message);

         ServerRecord record = ServerDataStore.OnlineInfoMap.get(message.getReceiver().getID());
         SendReplyOfficial(record,reply);


    }

    public static void RemoveAll() throws IOException{
        // 广播
        for(Long ID:ServerDataStore.OnlineInfoMap.keySet()) {
            UserMessage system = new UserMessage(8080,"system");
            Message message = new Message();
            message.setSender(system);
            message.setSendTime(new Date());

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            StringBuffer buffer = new StringBuffer();
            buffer.append(" ").append(dateFormat.format(message.getSendTime())).append(" ");
            buffer.append("【System Notice】\n 你已被踢出群聊！\n");
            message.setContent(buffer.toString());

            Reply reply = new Reply();
            reply.setPhase(ReplyPhase.SUCCESS);
            reply.setType(ReplyType.KICK_OUT);
            reply.setData("TextMessage", message);
            ServerRecord s1= ServerDataStore.OnlineInfoMap.get(ID);
            SendReplyOfficial(s1,reply);
        }
    }

}

