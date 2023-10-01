import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class DataBase extends JFrame{
    private JButton GOButton;
    private JTable table1;
    private JPanel Main;
    private JTextArea textArea1;
    private JButton plainReportButton;
    private JButton subreportButton;
    private JButton diagramReportButton;
    static final String DB_URL = "jdbc:postgresql://localhost:5432/Driver";
    static final String USER = "postgres";
    static final String PASS = "12345";


    public DataBase() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(Main);
        this.setSize(800, 500);
        GOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = textArea1.getText().toString();
                try {
                    table1.setModel(DBconnection(sql));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        plainReportButton.addActionListener(new ActionListener() {//простий звіт
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                        JasperReport report =(JasperReport) JRLoader.loadObjectFromFile("C:\\Users\\PC\\JaspersoftWorkspace\\MyReports\\Plain_report.jasper");
                        JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap<>(), connection);
                        JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\PC\\Desktop\\Plain_report.pdf");
                        connection.close();
                } catch (JRException e2) { e2.printStackTrace(); }
                catch (SQLException e3) { e3.printStackTrace(); }
            }
        });
        subreportButton.addActionListener(new ActionListener() {//вкладений звіт
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    JasperReport report =(JasperReport) JRLoader.loadObjectFromFile("C:\\Users\\PC\\JaspersoftWorkspace\\MyReports\\Big_report.jasper");
                    JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap<>(), connection);
                    JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\PC\\Desktop\\Big_report.pdf");
                    connection.close();
                } catch (JRException e2) { e2.printStackTrace(); }
                catch (SQLException e3) { e3.printStackTrace(); }
            }
        });
        diagramReportButton.addActionListener(new ActionListener() {//діаграма
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    JasperReport report =(JasperReport) JRLoader.loadObjectFromFile("C:\\Users\\PC\\JaspersoftWorkspace\\MyReports\\Diagramm.jasper");
                    JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap<>(), connection);
                    JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\PC\\Desktop\\Diagramm.pdf");
                    connection.close();
                } catch (JRException e2) { e2.printStackTrace(); }
                catch (SQLException e3) { e3.printStackTrace(); }
            }
        });
    }

    public DefaultTableModel DBconnection(String SQL) throws SQLException {
        DefaultTableModel model = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (connection != null) {
                System.out.println("Connection successful");
                try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    boolean isRetrieved = statement.execute(SQL);
                    if (isRetrieved) {
                        System.out.println("Result is obtained");
                        try (ResultSet result = statement.getResultSet()) {
                            ResultSetMetaData rsHeader = result.getMetaData();//заголовки таблиць
                            int columnCount = rsHeader.getColumnCount();

                            Object[] headers = new Object[columnCount];
                            for (int i = 0; i < columnCount; i++) {
                                headers[i] = (rsHeader.getColumnName(i + 1));
                            }
                            model = new DefaultTableModel(headers, 0);
                            Object[] row = new Object[columnCount];
                            //                result.absolute(5);//переводить курсор на певний р
                            result.beforeFirst();
                            while (result.next()) {//цикл стільки разів, скільки у нас стрічок
                                for (int i = 0; i < columnCount; i++) {
                                    row[i] = result.getString(i + 1);//зчитуємо всі таблички (колонки)
                                }
                                model.addRow(row);
                            }
                        }
                    }
                }
                connection.close();//
            } else {
                System.out.println("No connection");
            }
        }
        return model;
    }

    public static void main(String[] args) {
        JFrame frame = new DataBase();
        frame.setVisible(true);

    }
}

