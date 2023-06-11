package Sources;

import Server.ServerDataStore;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceForUser {
    private static int UserCount = LoadUsers().size() + 10000;

    public static void main(String[] args) {
        ServiceForUser service = new ServiceForUser();
        service.InternalTestUser();
        List<UserMessage> UserList = service.LoadUsers();
        for(UserMessage user:UserList) {
            System.out.println(user);
        }
    }

    // test
    public void InternalTestUser() {
        UserMessage testA = new UserMessage("Admin","123456",'M');
        UserMessage testB = new UserMessage("TestB","123",'F');
        UserMessage testC = new UserMessage("TestC","456",'F');

        testA.setID(10001);
        testB.setID(10002);
        testC.setID(10003);

        List<UserMessage> UserList = new CopyOnWriteArrayList<UserMessage>();
        UserList.add(testA);
        UserList.add(testB);
        UserList.add(testC);

        this.UpdateUserDatabase(UserList);
    }

    private void UpdateUserDatabase(List<UserMessage> UserList) {
        ObjectOutputStream Object_OS = null;
        try {
            Object_OS = new ObjectOutputStream(new FileOutputStream(ServerDataStore.ServerProperty.getProperty("DBpath")));
            Object_OS.writeObject(UserList);
            Object_OS.flush();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            IO_close.Close(Object_OS);
        }
    }


    // 加载全部用户
    public static List<UserMessage> LoadUsers() {
        List<UserMessage> UserList = null;
        ObjectInputStream Object_IS = null;
        try {
            Object_IS = new ObjectInputStream(Files.newInputStream(Paths.get(ServerDataStore.ServerProperty.getProperty("DBpath"))));
            UserList = (List<UserMessage>)Object_IS.readObject();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            IO_close.Close(Object_IS);
        }
        return UserList;
    }

    // 添加用户
    public void AddUser(UserMessage NewUser) {
        NewUser.setID(++UserCount);
        List<UserMessage> UserList = LoadUsers();
        UserList.add(NewUser);
        UpdateUserDatabase(UserList);
    }

    // 登录（数据库对比数据）
    public UserMessage Login(long ID,String Password) {
        UserMessage ThisUser = null;
        List<UserMessage> UserList = LoadUsers();
        for(UserMessage user:UserList) {
            if(ID == user.getID() && Password.equals(user.getPassword())) {
                ThisUser = user;
                break;
            }
        }
        return ThisUser;
    }
}

class IO_close {
    public static void Close(InputStream IS) {
        if(IS != null) {
            try {
                IS.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Close(OutputStream OS) {
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