package Server;

import Sources.ProcessUnit;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) {
        // 1.连接
        final int SERVERPORT = 8080; // 绑定端口
        try{
            ServerDataStore.serverSocket = new ServerSocket(SERVERPORT);
            System.out.println("服务器已启动，监听端口:"+ ServerDataStore.serverSocket.getLocalPort());
        } catch (IOException e){
            e.printStackTrace();
        }

        // 2.监听
        new Thread(() -> {
            try {
                while (true){
                    Socket temp = ServerDataStore.serverSocket.accept();
                    String tempIP = temp.getInetAddress().getHostAddress();
                    int tempPort = temp.getPort();
                    System.out.println("用户：" + tempIP + ": " + tempPort + "请求连接");
                    new Thread(new ProcessUnit(temp)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 3.设置界面风格
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            // 使用默认的metal，与client端保持一致
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // 4.初始化界面
        new ServerLogin();
    }
}


