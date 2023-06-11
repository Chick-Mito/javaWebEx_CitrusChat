package Client;

import Sources.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// 聊天室
public class ClientChat extends JFrame {
    // 提示信息
    private JLabel TipLabel;

    private JLabel ThisUserLabel;

    // 聊天框
    public static JTextArea GroupMessageArea;

    public static JTextArea RemainToSendArea;

    public static JList OnlineUsersList;
    public static JLabel OnlineUsersCount;

    // 要发送的文件
    // public static FileData FileToSend;

    // 私聊按钮
    public JCheckBox OneChat;

    public ClientChat() {
        Initialize();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 关就直接关
        setVisible(true);
    }

    // 聊天窗口
    public void Initialize(){
        // 初始化信息
        setTitle("CitrusChat");
        setSize(630,500);
        // setResizable(false);
        int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        x = (x - this.getWidth())/2;
        y = (y - this.getHeight())/2;
        this.setLocation(x,y);


        // 主窗口、在线用户俩panel
        JPanel MainChatPanel = new JPanel();
        JPanel UserPanel = new JPanel();
        MainChatPanel.setLayout(new BorderLayout());
        UserPanel.setLayout(new BorderLayout());
        // 主窗与用户列表的分界线（垂直）
        JSplitPane SplitPane_a = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, MainChatPanel,UserPanel);
        SplitPane_a.setDividerLocation(380);
        SplitPane_a.setDividerSize(10);
        SplitPane_a.setOneTouchExpandable(true);   //收缩
        // SplitPane_a.setEnabled(false);
        this.add(SplitPane_a,BorderLayout.CENTER);

        // 聊天消息、打字信息俩panel
        JPanel MessageDisplayPanel = new JPanel();
        JPanel TypeInPanel = new JPanel();
        MessageDisplayPanel.setLayout(new BorderLayout());
        TypeInPanel.setLayout(new BorderLayout());
        // 聊天与打字的分界线（水平）
        JSplitPane SplitPane_b = new JSplitPane(JSplitPane.VERTICAL_SPLIT, MessageDisplayPanel,TypeInPanel);
        SplitPane_b.setDividerLocation(300);
        SplitPane_b.setDividerSize(1);
        MainChatPanel.add(SplitPane_b,BorderLayout.CENTER);


        /**
         * 左半部分：
         * 上：聊天窗
         * 下：功能卡、打字区域
         */

        // 系统信息
        TipLabel = new JLabel("【聊天室】");
        TipLabel.setOpaque(true);
        TipLabel.setBackground(new Color(179, 220, 241));
        MessageDisplayPanel.add(TipLabel,BorderLayout.NORTH);

        // 显示群组消息
        GroupMessageArea = new JTextArea();
        GroupMessageArea.setLineWrap(true); // 自动换行
        // 滚动条：水平不显示，垂直显示
        MessageDisplayPanel.add(new JScrollPane(GroupMessageArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));


        JPanel FunctionPanel = new JPanel();
        FunctionPanel.setLayout(new BorderLayout());
        TypeInPanel.add(FunctionPanel,BorderLayout.NORTH);

        // 私聊选项
        OneChat = new JCheckBox("私聊");
        OneChat.setOpaque(true);
        OneChat.setBackground(new Color(179, 220, 241));

        FunctionPanel.add(OneChat,BorderLayout.EAST);

        // 小功能面板panel：表情包、GIF、截图、文件、图片、震动、其他 **
        JPanel ButtonPanel = new JPanel();
        ButtonPanel.setOpaque(true);
        ButtonPanel.setBackground(new Color(179, 220, 241));
        ButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        FunctionPanel.add(ButtonPanel,BorderLayout.CENTER);

        // 表情包
        String ButtonAddr = "C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\表情.png";
        JButton EmojiButton = new JButton(new ImageIcon(ButtonAddr));
        EmojiButton.setMargin(new Insets(0,0,0,0));
        EmojiButton.setToolTipText("发送表情");
        // 截图
        ButtonAddr="C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\截图.png";
        JButton FontButton = new JButton( new ImageIcon(ButtonAddr));
        FontButton.setMargin(new Insets(0,0,0,0));
        FontButton.setToolTipText("截图");
        // 文件
        ButtonAddr = "C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\文件.png";
        JButton SendFileButton = new JButton(new ImageIcon(ButtonAddr));
        SendFileButton.setMargin(new Insets(0,0,0,0));
        SendFileButton.setToolTipText("发送文件");
        // 震动
        ButtonAddr = "C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\震动.png";
        JButton ShakingButton = new JButton(new ImageIcon(ButtonAddr));
        ShakingButton.setMargin(new Insets(0,0,0,0));
        ShakingButton.setToolTipText("窗口震动");

        ButtonPanel.add(EmojiButton);
        ButtonPanel.add(FontButton);
        ButtonPanel.add(SendFileButton);
        ButtonPanel.add(ShakingButton);


        // 发送信息panel（水平滑动条）
        RemainToSendArea = new JTextArea();
        RemainToSendArea.setLineWrap(true);
        TypeInPanel.add(new JScrollPane(RemainToSendArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        JPanel UtilPanel = new JPanel();
        UtilPanel.setOpaque(true);
        UtilPanel.setBackground(new Color(179, 220, 241));
        UtilPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.add(UtilPanel,BorderLayout.SOUTH);

        // 发送按钮
        JButton SendButton = new JButton("发送");
        UtilPanel.add(SendButton);
        TypeInPanel.add(UtilPanel,BorderLayout.SOUTH);


        /**
         * 右半部分
         * 在线列表
         * 个人信息
         */
        // 面板：在线 & 当前用户
        JPanel OnlineUsersListPanel = new JPanel();
        OnlineUsersListPanel.setLayout(new BorderLayout());
        OnlineUsersCount = new JLabel(" 在线用户列表 【1人在线】");
        OnlineUsersCount.setOpaque(true);
        OnlineUsersCount.setBackground(new Color(179, 220, 241));

        ThisUserLabel = new JLabel();
        JPanel ThisUserPanel = new JPanel();
        ThisUserPanel.setOpaque(true);
        ThisUserPanel.setBackground(new Color(179, 220, 241));

        // 分割线
        JSplitPane SplitPane_c = new JSplitPane(JSplitPane.VERTICAL_SPLIT, OnlineUsersListPanel,ThisUserPanel);
        SplitPane_c.setDividerLocation(340);
        SplitPane_c.setDividerSize(1);
        UserPanel.add(SplitPane_c,BorderLayout.CENTER);

        // 在线用户列表
        ClientDataStore.clientModel = new ClientOnlineUsersModel(ClientDataStore.onlineUsers);
        OnlineUsersList = new JList(ClientDataStore.clientModel);
        OnlineUsersList.setCellRenderer(new UserCellRenderer());
        OnlineUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
        OnlineUsersListPanel.add(new JScrollPane(OnlineUsersList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // 位置布局
        OnlineUsersListPanel.add(OnlineUsersCount,BorderLayout.NORTH);
        ThisUserPanel.setLayout(new BorderLayout());
        ThisUserPanel.add(new JLabel("【自己】"),BorderLayout.NORTH);
        ThisUserPanel.add(ThisUserLabel,BorderLayout.CENTER);


        /**
         * 按钮监听器们
         */

        // 窗口的
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ChatExit();
            }
        });

        // 私聊
        OneChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OneChat.isSelected()) {
                    UserMessage ChosenUser = (UserMessage)OnlineUsersList.getSelectedValue();
                    if(ChosenUser==null) {
                        TipLabel.setText("【选择一位在线用户】");
                    } else if(ClientDataStore.thisUser.getID() == ChosenUser.getID()) {
                        TipLabel.setText("【无法选中自己】");
                    } else {
                        TipLabel.setText("【私聊模式："+ ChosenUser.getNickname()+"("+ChosenUser.getID()+") 】");
                    }
                } else {
                    TipLabel.setText("【聊天室】");
                }
            }
        });
        // 选择用户
        OnlineUsersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UserMessage ChosenUser = (UserMessage)OnlineUsersList.getSelectedValue();
                if(OneChat.isSelected()) {
                    if(ClientDataStore.thisUser.getID() == ChosenUser.getID()) {
                        TipLabel.setText("【无法选中自己】");
                    } else {
                        TipLabel.setText("【私聊模式："+ ChosenUser.getNickname()+"("+ChosenUser.getID()+") 】");
                    }
                }
            }
        });

        // 发送信息：回车 or 点击按钮
        RemainToSendArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == Event.ENTER) {
                    SendMessage();
                }
            }
        });
        SendButton.addActionListener(e -> SendMessage());

        // 表情
        // 震动
        // 文件
        // 截图

        // 启动一个新线程，进行聊天
        LoadData();
    }

    public void LoadData() {
        if(ClientDataStore.thisUser != null) {
            ThisUserLabel.setForeground(Color.BLUE);
//            ThisUserLabel.setIcon(new ImageIcon(
//                    "D:\\NewDesktop\\Chat_Room-master\\ChatRoom\\images\\"
//                            + ClientDataStore.thisUser.getProfile() + ".png"));
            ThisUserLabel.setText(ClientDataStore.thisUser.getNickname()
                    + "(" + ClientDataStore.thisUser.getID() + ")");
            ThisUserLabel.setOpaque(true);
            ThisUserLabel.setBackground(Color.WHITE);
        }
        OnlineUsersCount.setText("在线用户列表 【当前"+ClientDataStore.clientModel.getSize()+"人在线】");
        new ClientThread(this).start();
    }

    // 退出
    private void ChatExit() {
        int inquire = JOptionPane.showConfirmDialog(ClientChat.this,
                "确定离开?","提示",
                JOptionPane.YES_NO_OPTION);
        if(inquire==JOptionPane.YES_OPTION) {
            Plea plea = new Plea();
            plea.setAction("Exit");
            plea.setData("User", ClientDataStore.thisUser);
            try {
                ClientToServer.sendMessage(plea);
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        } else {
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }

    // 发送信息
    public void SendMessage() {
        String Message = RemainToSendArea.getText();
        if("".equals(Message)) {
            JOptionPane.showMessageDialog(ClientChat.this, "消息不得为空");
        } else {
            UserMessage ChosenUser = (UserMessage)OnlineUsersList.getSelectedValue();

            Sources.Message ThisMessage = new Message();
            if(OneChat.isSelected()) {
                if(ChosenUser == null) {
                    JOptionPane.showMessageDialog(ClientChat.this, "选择一名用户",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if(ClientDataStore.thisUser.getID()==ChosenUser.getID()) {
                    JOptionPane.showMessageDialog(ClientChat.this, "无法与自己交流",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    ThisMessage.setReceiver(ChosenUser);
                }
            }

            ThisMessage.setSender(ClientDataStore.thisUser);
            ThisMessage.setSendTime(new Date());

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            StringBuffer buffer = new StringBuffer();

            buffer.append(" ").append(dateFormat.format(ThisMessage.getSendTime())).append(" ")
                    .append(ThisMessage.getSender().getNickname()).append("(")
                    .append(ThisMessage.getSender().getID()).append(")");

            // 广播
            if(OneChat.isSelected()) {
                buffer.append("【私聊模式】");
            }
            buffer.append("\n").append(Message).append("\n");
            ThisMessage.setContent(buffer.toString());

            // 先向服务器请求
            Plea plea = new Plea();
            plea.setAction("Chat");
            plea.setData("Message", ThisMessage);
            try {
                ClientToServer.Simple_SendMessage(plea);
            } catch(IOException e) {
                e.printStackTrace();
            }

            // 发完消息清空缓存
            InputMap inputmap = RemainToSendArea.getInputMap();
            ActionMap actionmap = RemainToSendArea.getActionMap();
            Object TransferTextActionKey = "TRANSFER_TEXT";

            inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), TransferTextActionKey);
            actionmap.put(TransferTextActionKey, new AbstractAction() {
                // private static final long serialVersionUID = 5644604112667652457L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    RemainToSendArea.setText("");
                    RemainToSendArea.requestFocus();
                }
            });
            RemainToSendArea.setText("");

            ClientToServer.appendText(ThisMessage.getContent());
        }
    }

    public static void KickOut() {
        JOptionPane.showMessageDialog(null,
                "你被踢出群聊!", "【系统通知】",JOptionPane.WARNING_MESSAGE);
        Plea plea = new Plea();
        plea.setAction("Exit");
        plea.setData("User", ClientDataStore.thisUser);
        try {
            ClientToServer.sendMessage(plea);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}


class ClientThread extends Thread{
    private JFrame ThisFrame;

    public ClientThread(JFrame frame) {
        ThisFrame = frame;
    }

    public void run() {
        try {
            while(ClientDataStore.clientSocket.isConnected()) {
                Reply reply = (Reply)ClientDataStore.objIS.readObject();
                ReplyType type = reply.getType();

                System.out.println("客户端进行了 "+ type);
                switch (type) {
                    case LOG_IN: {
                        // 登录
                        UserMessage user = (UserMessage) reply.getData("LoginUser");
                        ClientDataStore.clientModel.addUser(user);
                        ClientChat.OnlineUsersCount.setText("在线用户列表 【当前"+ClientDataStore.clientModel.getSize()+"人在线】");
                        ClientToServer.appendText("用户" + user.getNickname() + "加入了聊天室！\n");
                        break;
                    }
//                    case LOG_OUT: {
//                        // 退出
//                        UserMessage user = (UserMessage) reply.getData("LogoutUser");
//                        ClientDataStore.clientModel.removeUser(user);
//                        ClientChat.OnlineUsersCount.setText("在线用户列表 【当前"+ClientDataStore.clientModel.getSize()+"人在线】");
//                        ClientToServer.appendText("用户" + user.getNickname() + "离开了\n");
//                        break;
//                    }
                    case CHAT:
                    case BROADCAST: {
                        // 广播
                        Message message = (Message) reply.getData("TextMessage");
                        ClientToServer.appendText(message.getContent());
                        break;
                    }
                    case SHAKE_WINDOW: {
                        //Shake.
                        // Message message = (Message) reply.getData("Shake");
                        // ClientToServer.appendText(message.getContent());
                        // new ShakeFrame(ThisFrame).StartShake();
                        break;
                    }
                    case GOING_TO_SENT_FILE:
                        // GoingSendFile(reply);
                        break;
                    case AGREE_RECEIVE_FILE:
                        // SendFile(reply);
                        break;
                    case REFUSE_TO_RECEIVE_FILE:
                        // ClientToServer.appendText("Server Receiver has refused to accept file.\n");
                        break;
                    case RECEIVE_FILE:
                        // ReceiveFile(reply);
                        break;
                    case KICK_OUT:
                        ClientChat.KickOut();
                        break;
                }
            }
        } catch(IOException ea) {
        } catch(ClassNotFoundException eb) {
            eb.printStackTrace();
        }
    }
    // 发送文件
    /*
    public void GoingSendFile(Reply reply) {
        Client.SocketClose.FileData goingSendFile = (Client.SocketClose.FileData)reply.getData("SendFile");

        String SenderInfo = goingSendFile.getSender().getNickname()+
                "("+goingSendFile.getSender().getID()+")";

        String FileName = goingSendFile.getSourceFilename().substring(goingSendFile.getSourceFilename().lastIndexOf(File.separator)+1);

        int Choice = JOptionPane.showConfirmDialog(ThisFrame,
                SenderInfo+" want to send a file¡¾"+FileName+"¡¿ to you.\nWill you agree?",
                "Accept file.",JOptionPane.YES_NO_OPTION);

        try {
            Plea plea = new Plea();
            plea.setData("SendFile", goingSendFile);

            if(Choice==JOptionPane.YES_OPTION)
            {
                JFileChooser FileChooser = new JFileChooser();
                FileChooser.setSelectedFile(new File(FileName));

                int Answer = FileChooser.showSaveDialog(ThisFrame);

                if(Answer==JFileChooser.APPROVE_OPTION) {
                    goingSendFile.setDestFilename(FileChooser.getSelectedFile().getCanonicalPath());
                    goingSendFile.setTargetIP(ClientDataStore.ClientIP);
                    goingSendFile.setTargetPort(ClientDataStore.PORT_FOR_FILES);
                    plea.setAction("AgreeReceiveFile");

                    ClientToServer.appendText("¡¾Server¡¿You have agreed to receive files from "+
                            SenderInfo+" , and we are downloading.\n");
                } else {
                    plea.setAction("RefuseReceiveFile");
                    ClientToServer.appendText("¡¾Server¡¿You have refused to receive files from "+SenderInfo+"\n");
                }
            } else {
                plea.setAction("RefuseReceiveFile");
                ClientToServer.appendText("¡¾Server¡¿You have refused to receive files from "+SenderInfo+"\n");
            }
            ClientToServer.Simple_SendMessage(plea);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void SendFile(Reply reply) {
        final Client.SocketClose.FileData SendFile = (Client.SocketClose.FileData)reply.getData("SendFile");

        Socket socket = null;
        BufferedInputStream Buffer_IS = null;
        BufferedOutputStream Buffer_OS = null;

        try {
            socket = new Socket(SendFile.getTargetIP(),SendFile.getTargetPort());
            Buffer_IS = new BufferedInputStream(new FileInputStream(SendFile.getSourceFilename()));
            Buffer_OS = new BufferedOutputStream(socket.getOutputStream());

            byte[] buffer_ = new byte[1024];
            int Sentinel=-1;
            while((Sentinel=Buffer_IS.read(buffer_))!=-1)
            {
                Buffer_OS.write(buffer_,0,Sentinel);
            }
            Buffer_OS.flush();
            synchronized (this)
            {
                ClientToServer.appendText("¡¾Server¡¿File has been sent successfully!\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            IO_close.ShutDown(Buffer_IS, Buffer_OS);
            SocketClose.Close(socket);
        }
    }
    private void ReceiveFile(Reply reply) {
        final Client.SocketClose.FileData SendFile = (Client.SocketClose.FileData)reply.getData("SendFile");

        ServerSocket Ssocket = null;
        Socket socket = null;
        BufferedInputStream Buffer_IS = null;
        BufferedOutputStream Buffer_OS = null;

        try {
            Ssocket = new ServerSocket(SendFile.getTargetPort());
            socket = Ssocket.accept();
            Buffer_IS = new BufferedInputStream(socket.getInputStream());
            Buffer_OS = new BufferedOutputStream(new FileOutputStream(SendFile.getDestFilename()));

            byte[] buffer_ = new byte[1024];
            int Sentinel=-1;
            while((Sentinel=Buffer_IS.read(buffer_))!=-1)
            {
                Buffer_OS.write(buffer_,0,Sentinel);
            }
            Buffer_OS.flush();
            synchronized (this)
            {
                ClientToServer.appendText("ServerFile has been received!\nPosition:"+
                        SendFile.getDestFilename()+"\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            IO_close.ShutDown(Buffer_IS, Buffer_OS);
            SocketClose.Close(socket);
            SocketClose.Close(Ssocket);
        }
    }
    */
}

/*
class IO_close {
    private static void Close(InputStream IS) {
        if(IS!=null) {
            try {
                IS.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void Close(OutputStream OS) {
        if(OS!=null) {
            try {
                OS.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ShutDown(InputStream IS,OutputStream OS) {
        Close(IS);
        Close(OS);
    }
}

class SocketClose {
    public static void Close(Socket s) {
        if (s != null && !s.isClosed()) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Close(ServerSocket s) {
        if (s != null && !s.isClosed()) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class FileData implements Serializable {
    // private static final long serialVersionUID = 7807586177895424411L;

    //Sender.
    private UserMessage Sender;

    //Receiver.
    private UserMessage Receiver;

    //Source file name.
    private String SourceFilename;

    //Destination file name.
    private String DestFilename;

    //Send time.
    private Date SendTime;

    private String TargetIP;
    private int TargetPort;

    public UserMessage getSender() {
        return Sender;
    }

    public void setSender(UserMessage sender) {
        Sender = sender;
    }

    public UserMessage getReceiver() {
        return Receiver;
    }

    public void setReceiver(UserMessage receiver) {
        Receiver = receiver;
    }

    public String getSourceFilename() {
        return SourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        SourceFilename = sourceFilename;
    }

    public String getDestFilename() {
        return DestFilename;
    }

    public void setDestFilename(String destFilename) {
        DestFilename = destFilename;
    }

    public Date getSendTime() {
        return SendTime;
    }

    public void setSendTime(Date sendTime) {
        SendTime = sendTime;
    }

    public String getTargetIP() {
        return TargetIP;
    }

    public void setTargetIP(String TargetIP) {
        this.TargetIP = TargetIP;
    }

    public int getTargetPort() {
        return TargetPort;
    }

    public void setTargetPort(int TargetPort) {
        this.TargetPort = TargetPort;
    }
}
*/


// 渲染单元格
class UserCellRenderer extends JLabel implements ListCellRenderer {
    // private static final long serialVersionUID = 4390793858305735219L;

    @Override
    public Component getListCellRendererComponent(JList list, Object obj, int index, boolean isChosen, boolean hasFocus) {
        UserMessage Abstract_User = (UserMessage)obj;
        String Name = Abstract_User.getNickname()+ "("+Abstract_User.getID()+")";
        setText(Name);
        setIcon(Abstract_User.getImageIcon());
        if(isChosen) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true); // 不透明

        return this;
    }
}
