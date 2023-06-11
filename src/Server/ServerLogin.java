package Server;

import Sources.ProcessUnit;
import Sources.ServiceForUser;
import Sources.UserMessage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ServerLogin extends JFrame {

    // 广播文本框、在线用户表、已注册用户表
    private JTextField BroadcastField;
    private JTable OnlineUsersTable;
    private JTable RegisteredUsersTable;
    public static JTextArea msgArea;
    public static JLabel OnlineCount;

    public ServerLogin() {
        Initialize();      // GUI界面
        LoadUsers();       // 加载用户
        setVisible(true);
    }

    public void Initialize(){
        // 初始化：字体、界面大小、背景颜色、位置
        Font myFont = new Font("Arial Rounded MT Bold",Font.BOLD,15);
        setTitle("CitrusChat");
        int width = 900;
        int height = 600;
        this.setBackground(new Color(179, 220, 241));  // 浅蓝色
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((ScreenSize.width-width)/2,(ScreenSize.height-height)/2, width, height);
        setResizable(false);
        setLayout(new BorderLayout());   // 设置为东西南北中布局

        /**
         * 左半部分
         * 0。广播与关闭服务端按钮
         * 1.广播内容文本框
         * 2.实时显示在线用户数
         * 3.在线用户列表 & 已注册用户列表
         */

        // 左半部分
        JPanel leftPart = new JPanel();
        leftPart.setLayout(new BorderLayout());
        leftPart.setOpaque(true); // 透明
        leftPart.setBackground(new Color(179, 220, 241));

        // 左标题栏
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        leftPart.setBorder(BorderFactory.createTitledBorder(border,"服务器信息",
                TitledBorder.LEFT,TitledBorder.TOP));

        // 主、辅助、广播面板
        JPanel MainPanel = new JPanel();
        JPanel UtilPanel = new JPanel();
        JPanel BroadPanel = new JPanel();

        UtilPanel.setLayout(new BorderLayout());

        // 按钮 & 标签 & 文本框
        JButton BroadcastButton = new JButton("广播");
        BroadcastButton.setFont(new Font("Arial Rounded MT Bold",Font.BOLD,15));
        JButton ExitButton = new JButton(" 关闭服务器");
        ExitButton.setFont(new Font("Arial Rounded MT Bold",Font.BOLD,15));
        JLabel SystemNotice = new JLabel("系统通知：");
        SystemNotice.setFont(myFont);
        BroadcastField = new JTextField(15);

        // 在线用户数量
        OnlineCount = new JLabel("当前在线用户数：" + ServerDataStore.OnlineUserMap.size());
        OnlineCount.setFont(myFont);

        // 给广播按钮和文本框增加广播事件的监听器
        ActionListener broadcast = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    BroadcastMessage();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        BroadcastButton.addActionListener(broadcast);
        BroadcastField.addActionListener(broadcast);

        // 在线用户 & 已注册用户
        OnlineUsersTable = new JTable(ServerDataStore.OnlineUserUI);
        RegisteredUsersTable = new JTable(ServerDataStore.RegisterUserUI);

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.add("在线用户列表",new JScrollPane(OnlineUsersTable));
        jTabbedPane.add("注册用户列表",new JScrollPane(RegisteredUsersTable));
        jTabbedPane.setTabComponentAt(0, new JLabel("在线用户列表"));
        jTabbedPane.setOpaque(true);
        jTabbedPane.setBackground(new Color(179, 220, 241));

        // 设置弹出菜单选项（实现选中用户可以私聊或踢人）
        JPopupMenu PopupMenu = GetPopupMenu();
        OnlineUsersTable.setComponentPopupMenu(PopupMenu);

        // 关闭服务器
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ServerExit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        ExitButton.addActionListener(e -> {
            try {
                ServerExit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        // 设置标签及面板布局
        MainPanel.add(BroadcastButton);
        BroadPanel.add(SystemNotice);
        BroadPanel.add(BroadcastField);

        MainPanel.setOpaque(true);
        MainPanel.setBackground(new Color(179, 220, 241));
        BroadPanel.setOpaque(true);
        BroadPanel.setBackground(new Color(179, 220, 241));
        OnlineCount.setOpaque(true);
        OnlineCount.setBackground(new Color(179, 220, 241));

        UtilPanel.add(BroadPanel,BorderLayout.NORTH);
        UtilPanel.add(MainPanel,BorderLayout.CENTER);
        UtilPanel.add(OnlineCount,BorderLayout.SOUTH);

        leftPart.add(UtilPanel,BorderLayout.NORTH);
        leftPart.add(jTabbedPane,BorderLayout.CENTER);
        leftPart.add(ExitButton,BorderLayout.SOUTH);

        this.add(leftPart,BorderLayout.WEST);

        /**
         * 右半部分：操作日志
         *
         */

        // 面板
        JPanel msgPanel = new JPanel();
        JPanel optPanel = new JPanel();
        JPanel rightPart = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        optPanel.setLayout(new BorderLayout());
        rightPart.setLayout(new BorderLayout());

        // 显示群组消息
        JLabel TipLabel = new JLabel("【聊天室信息】");
        TipLabel.setOpaque(true);
        TipLabel.setBackground(new Color(179, 220, 241));

        msgArea = new JTextArea();
        msgArea.setBackground(new Color(241, 241, 217));
        msgArea.setLineWrap(true);                  // 自动换行
        msgPanel.add(new JScrollPane(msgArea,       // 垂直滚动条
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // 分割线
        JSplitPane SplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,msgPanel,optPanel);
        SplitPane.setDividerLocation(height/2);
        // SplitPane.setDividerSize(1);
        SplitPane.setOneTouchExpandable(true);

        // 显示操作日志
        JLabel TipLabel2 = new JLabel("【操作日志】");
        TipLabel.setOpaque(true);
        TipLabel.setBackground(new Color(179, 220, 241));

        JTextArea optArea = new JTextArea();
        optArea.setBackground(new Color(241, 241, 217));
        optArea.setLineWrap(true);                           // 自动换行
        optPanel.add(new JScrollPane(optArea,                // 垂直滚动条
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));


        // 位置布局
        msgPanel.add(TipLabel,BorderLayout.NORTH);
        msgPanel.add(msgArea,BorderLayout.CENTER);

        optPanel.add(TipLabel2,BorderLayout.NORTH);
        optPanel.add(optArea,BorderLayout.CENTER);

        //rightPart.add(msgPanel,BorderLayout.NORTH);
        rightPart.add(SplitPane,BorderLayout.CENTER);
        //rightPart.add(optPanel,BorderLayout.SOUTH);

        this.add(rightPart,BorderLayout.CENTER);

    }

    // 加载用户列表
    private void LoadUsers() {
        List<UserMessage> userList = new ServiceForUser().LoadUsers();
        for(UserMessage user:userList){
            ServerDataStore.RegisterUserUI.add(new String[]{
                    String.valueOf(user.getID()),
                    user.getPassword(),
                    user.getNickname(),
                    String.valueOf(user.getSex())
            });
        }
        System.out.println("用户信息加载完成！");
    }

    // 关闭服务器
    private void ServerExit() throws IOException{
        int Choice = JOptionPane.showConfirmDialog(
                ServerLogin.this,
                "关闭服务器将导致全体用户下线，确定?\n",
                "Exit", JOptionPane.YES_NO_OPTION);

        if(Choice==JOptionPane.YES_OPTION) {
            // 将所有用户踢下线
            ProcessUnit.RemoveAll();
            System.exit(0);
        } else {
            // 不执行任何操作
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }


    // 设置弹出菜单的选项
    private JPopupMenu GetPopupMenu(){
        JPopupMenu jPopupMenu = new JPopupMenu();
        JMenuItem sendItem = new JMenuItem("发送信息");
        JMenuItem kickOut = new JMenuItem("踢出群聊");

        sendItem.setActionCommand("sendMessage");
        kickOut.setActionCommand("kickOut");

        ActionListener listener = e -> {
            String command = e.getActionCommand();
            ConductCommand(command);
        };

        sendItem.addActionListener(listener);
        kickOut.addActionListener(listener);
        jPopupMenu.add(sendItem);
        jPopupMenu.add(kickOut);

        return jPopupMenu;
    }

    // 选项按钮功能的实现
    private void ConductCommand(String command){
        final int selectedRow = OnlineUsersTable.getSelectedRow();                   // 行号
        String userID = (String) OnlineUsersTable.getValueAt(selectedRow,0);  // 选择第1列
        System.out.println(userID);

        // 不选用户时，不执行任何操作
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this,"选择一个用户");
            return;
        }

        if(command.equals("kickOut")){
            try{
                ProcessUnit.Remove(ServerDataStore.OnlineUserMap.get(Long.valueOf(userID))); // 移除此用户
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if(command.equals("sendMessage")){
            final JDialog Dialog = new JDialog(this,true);
            final JTextField TextField = new JTextField(20);
            JButton Button = new JButton("Send");
            Dialog.setLayout(new FlowLayout());
            Dialog.setSize(250, 150);
            Dialog.add(TextField);
            Dialog.add(Button);

            Button.addActionListener(e -> {
                System.out.println("服务器发送了信息");
                String SystemMessage = TextField.getText();
                try {
                    ProcessUnit.Broadcast2(SystemMessage, ServerDataStore.OnlineUserMap.get(Long.valueOf(userID)));
                } catch(IOException  ex) {
                    ex.printStackTrace();
                }
                TextField.setText("");
                Dialog.dispose();
            });
            Dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Unknown Command:"+command);
        }
        SwingUtilities.updateComponentTreeUI(OnlineUsersTable);
    }

    // 广播
    private void BroadcastMessage() throws IOException {
        ProcessUnit.Broadcast(BroadcastField.getText());
        BroadcastField.setText("");
    }
}
