package Sources;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// 客户端请求
public class Plea implements Serializable {

    private ReplyType Type;

    private String Action;

    private Map<String,Object> DataMap;

    public Plea() {
        DataMap = new HashMap<>();
    }

//    public ReplyType getType() {
//        return Type;
//    }

//    public void setType(ReplyType type) {
//        Type = type;
//    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

//    public Map<String, Object> getDataMap() {
//        return DataMap;
//    }

    public Object getData(String key) {
        return DataMap.get(key);
    }

    public void setData(String key,Object value) {
        DataMap.put(key,value);
    }

//    public void removeData(String key) {
//        DataMap.remove(key);
//    }
//
//    public void clearData() {
//        DataMap.clear();
//    }
}