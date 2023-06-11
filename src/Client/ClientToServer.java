package Client;

import Server.ServerLogin;
import Sources.Plea;
import Sources.Reply;

import java.io.IOException;

public class ClientToServer {
    public static Reply sendMessage(Plea plea) throws IOException {
        Reply reply=null;
        try {
            // 发送信息
            ClientDataStore.objOS.writeObject(plea);
            ClientDataStore.objOS.flush();

            System.out.println("客户端发送请求: "+plea.getAction());

            if(!"Exit".equals(plea.getAction())) {
                reply = (Reply)ClientDataStore.objIS.readObject();
                System.out.println("Server回应: "+reply.getPhase());
            } else {
                System.out.println("客户端离开");
            }
        } catch(IOException e) {
            throw e;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return reply;
    }

    // 客户端请求
    public static void Simple_SendMessage(Plea plea) throws IOException {
        try {
            ClientDataStore.objOS.writeObject(plea);
            ClientDataStore.objOS.flush();
            System.out.println("客户端发送请求 : "+plea.getAction());
        } catch(IOException e) {
            throw e;
        }
    }

    // 向聊天室里发送信息
    public static void appendText(String txt) {
        ClientChat.GroupMessageArea.append(txt);
        ClientChat.GroupMessageArea.setCaretPosition(ClientChat.GroupMessageArea.getDocument().getLength());
        // ServerLogin.msgArea.append(txt);
    }
}
