package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// 存储服务器输入输出流
public class ServerRecord {
    private ObjectInputStream ObjectIS;
    private ObjectOutputStream ObjectOS;

    public ServerRecord(ObjectInputStream objectIS, ObjectOutputStream objectOS) {
        super();
        ObjectIS = objectIS;
        ObjectOS = objectOS;
    }

    public ObjectInputStream getObject_IS() {
        return ObjectIS;
    }

    public ObjectOutputStream getObject_OS() {
        return ObjectOS;
    }
}
