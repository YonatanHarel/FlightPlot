package me.drton.flightplot;

import me.drton.jmavlib.log.LogReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: ton Date: 27.10.13 Time: 17:45
 */
public class LogInfo {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTable infoTable;
    private DefaultTableModel infoTableModel;
    private JTable parametersTable;
    private JTextField textSearch;
    private DefaultTableModel parametersTableModel;
    private DateFormat dateFormat;
    private TableRowSorter sorter;


    public LogInfo() {
        mainFrame = new JFrame("Log Info");
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        textSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterFields(textSearch.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                filterFields(textSearch.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                filterFields(textSearch.getText());
            }
        });
    }

    public JFrame getFrame() {
        return mainFrame;
    }

    public void setVisible(boolean visible) {
        mainFrame.setVisible(visible);
    }

    public void updateInfo(LogReader logReader) {
        while (infoTableModel.getRowCount() > 0) {
            infoTableModel.removeRow(0);
        }
        while (parametersTableModel.getRowCount() > 0) {
            parametersTableModel.removeRow(0);
        }
        if (logReader != null) {
            infoTableModel.addRow(new Object[]{"Format", logReader.getFormat()});
            infoTableModel.addRow(new Object[]{"System", logReader.getSystemName()});
            infoTableModel.addRow(new Object[]{
                    "Length, s", String.format(Locale.ROOT, "%.3f", logReader.getSizeMicroseconds() * 1e-6)});
            String startTimeStr = "";
            if (logReader.getUTCTimeReferenceMicroseconds() > 0) {
                startTimeStr = dateFormat.format(
                        new Date((logReader.getStartMicroseconds() + logReader.getUTCTimeReferenceMicroseconds()) / 1000)) + " UTC";
            }
            infoTableModel.addRow(new Object[]{
                    "Start Time", startTimeStr});
            infoTableModel.addRow(new Object[]{"Updates count", logReader.getSizeUpdates()});
            infoTableModel.addRow(new Object[]{"Errors", logReader.getErrors().size()});
            Map<String, Object> ver = logReader.getVersion();
            infoTableModel.addRow(new Object[]{"Hardware Version", ver.get("HW")});
            infoTableModel.addRow(new Object[]{"Firmware Version", ver.get("FW")});
            infoTableModel.addRow(new Object[]{"FC Custom Version", /*longToByte((Long) */ver.get("FC_ver")});
            Map<String, Object> parameters = logReader.getParameters();
            List<String> keys = new ArrayList<String>(parameters.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                parametersTableModel.addRow(new Object[]{key, parameters.get(key).toString()});
            }
        }
    }

    private void createUIComponents() {
        // Info table
        infoTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        infoTableModel.addColumn("Property");
        infoTableModel.addColumn("Value");
        infoTable = new JTable(infoTableModel);
        // Parameters table
        parametersTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        parametersTableModel.addColumn("Parameter");
        parametersTableModel.addColumn("Value");
        parametersTable = new JTable(parametersTableModel);
        sorter = new TableRowSorter<DefaultTableModel>(parametersTableModel);
        parametersTable.setRowSorter(sorter);
    }

    public String longToByte(long value)
    {
        byte [] data = new byte[3];
        data[3] = (byte) (value >>> 8*0);
        data[2] = (byte) (value >>> 8*1);
        data[1] = (byte) (value >>> 8*2);
        data[0] = (byte) (value >>> 8*3);

        StringBuffer sb = new StringBuffer();
        sb.append("v").
                append(String.valueOf(data[0] | data[1])).append(".").
                append(String.valueOf(data[2])).append(".").
                append(String.valueOf(data[3]));
        return sb.toString();
    }

    private void filterFields(String str) {
        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)"  + Pattern.quote(str), 0);
        sorter.setRowFilter(rf);
    }
}
