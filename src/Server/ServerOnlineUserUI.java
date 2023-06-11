package Server;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ServerOnlineUserUI extends AbstractTableModel {
    // private static final long serialVersionUID = -3577801384082059991L;

    private String[] Title = {"Account","Nickname","Sex"};

    private List<String[]> UserLines = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return Title.length;
    }

    public String getColumnName(int index) {
        return Title[index];
    }

    @Override
    public int getRowCount() {
        return UserLines.size();
    }

    public String getValueAt(int row, int column) {
        return (UserLines.get(row))[column];
    }

    public void add(String[] value) {
        int size = UserLines.size();
        UserLines.add(value);
        fireTableRowsInserted(size, size);
    }

    // 移除特定id
    public void remove (long id) {
        int index = -1;
        for (int i=0;i<=UserLines.size();++i) {
            if(String.valueOf(id).equals(getValueAt(i, 0))) {
                index=i;
                break;
            }
        }
        UserLines.remove(index);
        fireTableRowsDeleted(2, 3);
    }
}