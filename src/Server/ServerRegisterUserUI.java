package Server;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ServerRegisterUserUI extends AbstractTableModel {
    // private static final long serialVersionUID = -4793662403864686552L;

    private String[] Title = {"Account","Password","Nickname","Sex"};

    private List<String[]> UserLines = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return Title.length;
    }

    @Override
    public int getRowCount() {
        return UserLines.size();
    }

    public String getColumnName(int index) {
        return Title[index];
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        return (UserLines.get(rowIndex))[columnIndex];
    }

    public void add(String[] value) {
        int size = UserLines.size();
        UserLines.add(value);
        fireTableRowsInserted(size, size);
    }

    public void remove(long id) {
        int index=-1;
        for(int i=0;i<=UserLines.size();++i) {
            if(String.valueOf(id).equals(getValueAt(i,0))) {
                index = i;
                break;
            }
        }
        UserLines.remove(index);
        fireTableRowsDeleted(2, 3);
    }
}

