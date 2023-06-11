package Client;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// 客户端的登录界面
public class Client {
    public static void main(String[] args) {
        // 1.连接服务器
        clientMakeConnection();

        // 2.设置界面风格为metal
        JFrame.setDefaultLookAndFeelDecorated(true);  // 设置顶级窗口启用操作系统的外观修饰
        JDialog.setDefaultLookAndFeelDecorated(true); // 设置对话框启用操作系统的外观修饰
        try {
            // 通过获取要跨的平台的名字来设置UI风格
            String LookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(LookAndFeel);

            // System.out.println(LookAndFeel);
            // 默认javax.swing.plaf.metal.MetalLookAndFeel为经典metal风格
        } catch (Exception e){
            e.printStackTrace();
        }

        // 3.登录
        new ClientLogin();
    }

    // 连接服务器的具体实现
    public static void clientMakeConnection(){
        // 获取IP和端口号
        String clientIP = ClientDataStore.setProperties.getProperty("IP");
        int clientPort = Integer.parseInt(ClientDataStore.setProperties.getProperty("Port"));
        System.out.println("ip:port" + clientIP + ": " + clientPort);
        try{
            // 连接服务器
            ClientDataStore.clientSocket = new Socket(clientIP,clientPort);
            // 传输入输出流，在其他地方可以通过.objOS的方式获取输入输出信息
            ClientDataStore.objOS = new ObjectOutputStream(ClientDataStore.clientSocket.getOutputStream());
            ClientDataStore.objIS = new ObjectInputStream(ClientDataStore.clientSocket.getInputStream());
        } catch (Exception e){
            JOptionPane.showMessageDialog(new JFrame(),
                    "无法连接到服务器，请稍后再试！",
                    "Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}


//        // 创建一个 JFrame
//        JFrame frame = new JFrame("示例窗口");
//        frame.setSize(300, 200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//        // 创建一个对话框
//        JDialog dialog = new JDialog();
//        dialog.setTitle("示例对话框");
//        dialog.setSize(300, 200);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        dialog.setVisible(true);