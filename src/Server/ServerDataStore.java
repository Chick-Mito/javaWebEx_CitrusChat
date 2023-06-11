package Server;

import Sources.UserMessage;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListMap;

// 服务器信息存储
public class ServerDataStore {

    public static ServerSocket serverSocket;

    public static Map<Long,ServerRecord> OnlineInfoMap;
    public static Map<Long, UserMessage> OnlineUserMap;
    public static int OnlineCount;

    public static ServerOnlineUserUI OnlineUserUI;
    public static ServerRegisterUserUI RegisterUserUI;

    // 用户账号信息存储
    public static Properties ServerProperty;
    public static Dimension ScreenSize;

    static {
        OnlineInfoMap = new ConcurrentSkipListMap<Long,ServerRecord>();
        OnlineUserMap = new ConcurrentSkipListMap<Long,UserMessage>();
        ServerProperty = new Properties();
        OnlineUserUI = new ServerOnlineUserUI();
        RegisterUserUI = new ServerRegisterUserUI();
        ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

        try {
            ServerProperty.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("ServerProperty.properties"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

// 在线用户类
//class ServerOnlineUserUI extends AbstractTableModel {
//    // private static final long serialVersionUID = -3577801384082059991L;
//
//    private String[] Title = {"Account","Nickname","Sex"};
//
//    private List<String[]> UserLines = new ArrayList<>();
//
//    @Override
//    public int getColumnCount() {
//        return Title.length;
//    }
//
//    public String getColumnName(int index) {
//        return Title[index];
//    }
//
//    @Override
//    public int getRowCount() {
//        return UserLines.size();
//    }
//
//    public String getValueAt(int row, int column) {
//        return (UserLines.get(row))[column];
//    }
//
//    public void add(String[] value) {
//        int size = UserLines.size();
//        UserLines.add(value);
//        fireTableRowsInserted(size, size);
//    }
//
//    // 移除特定id
//    public void remove (long id) {
//        int index = -1;
//        for (int i=0;i<=UserLines.size();++i) {
//            if(String.valueOf(id).equals(getValueAt(i, 0))) {
//                index=i;
//                break;
//            }
//        }
//        UserLines.remove(index);
//        fireTableRowsDeleted(2, 3);  // 更新表格行数 **
//    }
//}

// 注册用户类

//class ServerRegisterUserUI extends AbstractTableModel {
//    // private static final long serialVersionUID = -4793662403864686552L;
//
//    private String[] Title = {"Account","Password","Nickname","Sex"};
//
//    private List<String[]> UserLines = new ArrayList<>();
//
//    @Override
//    public int getColumnCount() {
//        return Title.length;
//    }
//
//    @Override
//    public int getRowCount() {
//        return UserLines.size();
//    }
//
//    public String getColumnName(int index) {
//        return Title[index];
//    }
//
//    @Override
//    public String getValueAt(int rowIndex, int columnIndex) {
//        return (UserLines.get(rowIndex))[columnIndex];
//    }
//
//    public void add(String[] value) {
//        int size=UserLines.size();
//        UserLines.add(value);
//        fireTableRowsInserted(size, size);
//    }
//
//    public void remove(long id) {
//        int index=-1;
//        for(int i=0;i<=UserLines.size();++i) {
//            if(String.valueOf(id).equals(getValueAt(i,0))) {
//                index = i;
//                break;
//            }
//        }
//        UserLines.remove(index);
//        fireTableRowsDeleted(2, 3);
//    }
//}
//
