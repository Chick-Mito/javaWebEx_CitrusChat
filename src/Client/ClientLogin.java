package Client;


import Sources.Plea;
import Sources.Reply;
import Sources.ReplyPhase;
import Sources.UserMessage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class ClientLogin extends JFrame {
    //seriaVersionUID

    private JTextField ID_Input;
    private JPasswordField Password_Input;

    public ClientLogin(){
        Initialize();           // 登录界面具体实现
        setVisible(true);
    }

    public void Initialize(){
        this.setTitle("CitrusChat登录界面");
        this.setSize(425,325);   // 参考了qq的登录界面的大小
        this.setResizable(false);             // 大小不可调整

        // 设置登录位置在屏幕的正中央
        int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();  // toolkit类访问系统资源
        int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        x = (x - this.getWidth())/2;
        y = (y - this.getHeight())/2;
        this.setLocation(x,y);

        // 设置上面板
        //Icon Logo = new ImageIcon("../images/photo_2022-10-16_15-26-30_看图王.jpg");
        Icon Logo = new ImageIcon("C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\bigEye.jpg");
        //Icon Logo = new ImageIcon("C:\\Users\\Citrus\\Desktop\\photo_2022-10-16_15-26-30_看图王.jpg");
        JLabel LogoLabel = new JLabel(Logo);
        LogoLabel.setPreferredSize(new Dimension(415,100));
        this.add(LogoLabel,BorderLayout.NORTH);

        // 主面板**
        JPanel MainLoginPanel = new JPanel();
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        MainLoginPanel.setBorder(BorderFactory.createTitledBorder(border,"Login", TitledBorder.CENTER,TitledBorder.TOP));
        this.add(MainLoginPanel,BorderLayout.CENTER);
        MainLoginPanel.setLayout(null);
        MainLoginPanel.setOpaque(true);
        MainLoginPanel.setBackground(new Color(255,255,255));

        JLabel IDLabel = new JLabel("账号:");
        JLabel PasswordLabel = new JLabel("密码:");
        IDLabel.setFont(new Font("Arial Rounded MT Bold",Font.BOLD,18));
        PasswordLabel.setFont(new Font("Arial Rounded MT Bold",Font.BOLD,18));

        // 账号
        IDLabel.setBounds(80,40,110,30);
        MainLoginPanel.add(IDLabel);
        ID_Input = new JTextField();
        ID_Input.setBounds(140,40,150,30);
        ID_Input.requestFocusInWindow();
        MainLoginPanel.add(ID_Input);

        // 密码
        PasswordLabel.setBounds(80,80,110,30);
        MainLoginPanel.add(PasswordLabel);
        Password_Input = new JPasswordField();
        Password_Input.setBounds(140,80,150,30);
        MainLoginPanel.add(Password_Input);

        // 登录
        JButton LoginButton = new JButton("登录");
        LoginButton.setBounds(175,120,70,25);
        MainLoginPanel.add(LoginButton);

        // 注册 & 二维码
        JPanel ButtonPanel=new JPanel();
        this.add(ButtonPanel, BorderLayout.SOUTH);
        ButtonPanel.setLayout(new BorderLayout());
        ButtonPanel.setBorder(new EmptyBorder(2,8,4,8));

        JButton RegisterButton = new JButton("注册");
        JButton CodeButton = new JButton("二维码");

        ButtonPanel.add(RegisterButton,BorderLayout.WEST);
        ButtonPanel.add(CodeButton,BorderLayout.EAST);
        ButtonPanel.setOpaque(true);
        ButtonPanel.setBackground(new Color(193, 210, 240));

        // 退出方法
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //super.windowClosing(e);
                Plea plea = new Plea();
                plea.setAction("Exit");
                try {
                    ClientToServer.sendMessage(plea);
                } catch (IOException exception){
                    exception.printStackTrace();
                } finally {
                    System.exit(0);
                }
            }
        });

        // 注册按钮
        RegisterButton.addActionListener(e -> new ClientRegister());

        // 二维码
        CodeButton.addActionListener(e -> new QRCode());

        // 登录按钮
        LoginButton.addActionListener(e -> Login());
    }

    // 登录
    private void Login(){
        // 检查ID与密码
        if(ID_Input.getText().length() == 0 || Password_Input.getPassword().length == 0){
            JOptionPane.showMessageDialog(ClientLogin.this,
                    "账号和密码不能为空！",
                    "ERROR!",JOptionPane.ERROR_MESSAGE);
            ID_Input.requestFocusInWindow(); // 将焦点回到ID输入框
            return;
        }

        // 检查特殊字符
        if(!ID_Input.getText().matches("^\\d*$")) {
            JOptionPane.showMessageDialog(ClientLogin.this,
                    "账号仅限数字，不得含有特殊字符！",
                    "ERROR!",JOptionPane.ERROR_MESSAGE);
            ID_Input.requestFocusInWindow();
            return;
        }

        // 向服务器发送请求
        Plea plea = new Plea();
        plea.setAction("Login");
        plea.setData("ID", ID_Input.getText());
        plea.setData("Password", new String(Password_Input.getPassword()));

        // 向Server发送连接请求
        Reply reply = null;
        try {
            reply = ClientToServer.sendMessage(plea);
        } catch (IOException e){
            e.printStackTrace();
        }

        // 如果成功
        if(reply.getPhase() == ReplyPhase.SUCCESS){
            UserMessage thisUser = (UserMessage)reply.getData("User");
            if(thisUser!=null) {
                ClientDataStore.thisUser = thisUser;
                ClientDataStore.onlineUsers = (List<UserMessage>)reply.getData("OnlineUsers");
                ClientLogin.this.dispose();
                new ClientChat();
            } else {
                String info = (String)reply.getData("Message");
                JOptionPane.showMessageDialog(ClientLogin.this,
                        info,"ERROR",JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(ClientLogin.this,
                    "网络超时，连接失败！",
                    "ERROR!",JOptionPane.ERROR_MESSAGE);
        }
    }

}

// 二维码
class QRCode extends JFrame {
    public QRCode() {
        Initialize();
        setVisible(true);
    }

    private void Initialize(){
        setTitle("QRCode");
        setBounds((ClientDataStore.screenSize.width-400)/2,
                (ClientDataStore.screenSize.height-300)/2,400,300);
        getContentPane().setLayout(null);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 创建显示二维码的标签
        JPanel QRCode = new JPanel();
        QRCode.setLayout(new BorderLayout());
        JLabel WeChat = new JLabel(new ImageIcon("C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\WeChat.jpg"));
        JLabel AliPay = new JLabel(new ImageIcon("C:\\Users\\Citrus\\IdeaProjects\\javaWebEx_myChatApp\\images\\AliPay.jpg"));

        QRCode.add(WeChat, BorderLayout.WEST);
        QRCode.add(AliPay, BorderLayout.EAST);
        add(QRCode, BorderLayout.CENTER);

        // 创建友链
        JButton gitHub = new JButton("友链");
        gitHub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Chick-Mito"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(gitHub, BorderLayout.SOUTH);
    }
}

// 注册
class ClientRegister extends JFrame {
    // private static final long serialVersionUID = 7171383032964745287L;

    // 昵称
    private JTextField Nickname;
    // 密码
    private JPasswordField Password;
    private JPasswordField ConfirmPassword;
    // 政策
    private JComboBox Profile;
    // 性别
    private JRadioButton Male;
    private JRadioButton Female;

    private JButton OK;
    private JButton Cancel;
    private JButton Clear;

    public ClientRegister() {
        Initialize();
        setVisible(true);
    }

    public void Initialize() {
        getContentPane().setBackground(new Color(179, 220, 241));
        setTitle("注册");
        setBounds((ClientDataStore.screenSize.width-400)/2,
                (ClientDataStore.screenSize.height-300)/2,340,250);
        getContentPane().setLayout(null);
        setResizable(false);

        // 昵称、密码
        JLabel nickname = new JLabel("昵称:");
        Nickname = new JTextField();
        JLabel password = new JLabel("密码:");
        JLabel confirmPassword = new JLabel("确认密码:");
        Password = new JPasswordField();
        ConfirmPassword = new JPasswordField();

        // 大小 & 布局设置
        nickname.setBounds(24,36,65,17);
        Nickname.setBounds(90, 34, 110, 22);
        password.setBounds(24,72,65,17);
        confirmPassword.setBounds(24,107,65,17);
        Password.setBounds(90,70,110,22);
        ConfirmPassword.setBounds(90,105,110,22);
        getContentPane().add(nickname);
        getContentPane().add(Nickname);
        getContentPane().add(password);
        getContentPane().add(confirmPassword);
        getContentPane().add(Password);
        getContentPane().add(ConfirmPassword);

        // 性别
        JLabel sex = new JLabel("性别:");
        Male = new JRadioButton("男",true);
        Female = new JRadioButton("女");

        // 大小 & 布局设置
        sex.setBounds(24,142,30,17);
        Male.setBounds(85,138,60,25);
        Female.setBounds(145, 138, 75, 25);
        getContentPane().add(sex);
        getContentPane().add(Male);
        getContentPane().add(Female);
        // 选项
        ButtonGroup buttonGroup = new ButtonGroup();
        Male.setOpaque(true);
        Female.setOpaque(true);
        Male.setBackground(new Color(179, 220, 241));
        Female.setBackground(new Color(179, 220, 241));
        buttonGroup.add(Male);
        buttonGroup.add(Female);

        // 头像
        /*

        JLabel profile = new JLabel("Profile:");
        profile.setBounds(210,72,55,17);
        getContentPane().add(profile);

        Profile = new JComboBox();
        Profile.setBounds(278,70,65,45);
        Profile.setMaximumRowCount(3);
        for(int i=0;i<11;++i) {
            Profile.addItem(new ImageIcon("D:\\NewDesktop\\Chat_Room-master\\ChatRoom\\images\\" + i + ".png"));
        }
        Profile.setSelectedIndex(0);
        getContentPane().add(Profile);

         */

        // 其他按钮
        OK = new JButton("确认");
        Clear = new JButton("清空");

        Clear.setBounds(24,170,76,30);
        OK.setBounds(224,170,76,30);
        getContentPane().add(Clear);
        getContentPane().add(OK);


        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(Nickname.getText().length() == 0||Password.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(ClientRegister.this, "请填写昵称和密码！");
                } else if(!(new String(Password.getPassword()).equals(new String(ConfirmPassword.getPassword())))) {
                    JOptionPane.showMessageDialog(ClientRegister.this, "两次密码不一致！");
                    Password.setText("");
                    ConfirmPassword.setText("");
                    Password.requestFocusInWindow();
                } else {
                    char SEX = Male.isSelected()?'M':'F';
                    // int PROFILE = Profile.getSelectedIndex();
                    UserMessage newUser = new UserMessage(Nickname.getText(),new String(Password.getPassword()),SEX);

                    try {
                        // 注册结束显示是否成功
                        ClientRegister.this.RealRegis(newUser);
                    } catch(IOException ea) {
                        ea.printStackTrace();
                    } catch(ClassNotFoundException eb) {
                        eb.printStackTrace();
                    }
                }
            }
        });

        // 关
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ClientRegister.this.dispose();
            }
        });
        // 清空
        Clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String Reset="";
                Nickname.setText(Reset);
                Password.setText(Reset);
                ConfirmPassword.setText(Reset);
                Nickname.requestFocusInWindow();
            }
        });
    }

    // 注册结果
    private void RealRegis(UserMessage newUser) throws IOException,ClassNotFoundException {
        Plea plea = new Plea();
        plea.setAction("Register");
        plea.setData("User", newUser);

        Reply reply  = ClientToServer.sendMessage(plea);
        ReplyPhase phase = reply.getPhase();

        switch(phase) {
            case SUCCESS:
                UserMessage tempUser = (UserMessage)reply.getData("User");
                System.out.println(tempUser);
                JOptionPane.showMessageDialog(ClientRegister.this,
                        "注册成功！欢迎 "+tempUser.getNickname()+"\n请记住你的账号："+tempUser.getID(),
                        "成功！",JOptionPane.INFORMATION_MESSAGE);
                this.setVisible(false);
                break;
            default:
                JOptionPane.showMessageDialog(ClientRegister.this,
                        "注册失败","ERROR",
                        JOptionPane.ERROR_MESSAGE);
        }
    }
}