package Client;


import Sources.UserMessage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

// client数据存储类
public class ClientDataStore {
    public static Socket clientSocket;
    public static String ClientIP;

    // 当前用户
    public static UserMessage thisUser;
    //Users online.
    public static List<UserMessage> onlineUsers;

    //输入输出流
    public static ObjectOutputStream objOS;
    public static ObjectInputStream objIS;

    // Server properties
    public static Properties setProperties;
    public static Dimension screenSize;


    // 在线用户
    public static ClientOnlineUsersModel clientModel;

    static {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setProperties = new Properties();
        try {
            ClientIP = InetAddress.getLocalHost().getHostAddress();
            // 加载账号信息
            setProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("ServerProperty.properties"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

// 在线UI
class ClientOnlineUsersModel extends AbstractListModel {
    // private static final long serialVersionUID = -3043640290296787803L;

    private List<UserMessage> onlineUsers;

    public ClientOnlineUsersModel(List<UserMessage> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    @Override
    public Object getElementAt(int arg0) {
        return onlineUsers.get(arg0);
    }
    @Override
    public int getSize() {
        return onlineUsers.size();
    }

    public void addUser(Object obj) {
        if(onlineUsers.contains(obj)) {
            return;
        }
        int index = onlineUsers.size();
        onlineUsers.add((UserMessage) obj);

        fireIntervalAdded(this, index, index);
    }

    public boolean removeUser(Object obj) {
        int index = onlineUsers.indexOf(obj);
        if(index>=0) {
            fireIntervalRemoved(this, index, index);
        }
        return onlineUsers.remove(obj);
    }

    public List<UserMessage> getOnlineUsers() {
        return onlineUsers;
    }
}
