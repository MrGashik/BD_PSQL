package org.example.windows;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.models.Departments;
import org.example.models.Employees;
import org.example.models.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReportsTableWindow {

    private final int frameWidth = 1280;
    private final int frameHeight = 720;
    private JFrame frame = new JFrame();
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table = new JTable(tableModel);
    private SessionFactory sessionFactory;
    String choice = "none";
    String text;
    BaseFont bf;
    com.itextpdf.text.Font font;

    private void pushDataIntoReport(String name_file, List<String[]> data, int n) {
        try {
            bf = BaseFont.createFont("src/main/resources/TimesNewRomanRegular.ttf", BaseFont.IDENTITY_H , BaseFont.EMBEDDED);
            font = new com.itextpdf.text.Font(bf);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }

        Document document = new Document();
        document.setPageSize(PageSize.A3);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(name_file));
        } catch (DocumentException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();

        PdfPTable table = new PdfPTable(n);
        for (String[] arr_str: data) {
            for (String str: arr_str) {
                Phrase ph = new Phrase();
                ph.setFont(font);
                ph.add(str);
                table.addCell(ph);
            }
        }

        try {
            document.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();
    }
    private List<?> standardTableInit() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        Field[] fields = Project.class.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Column.class))
                tableModel.addColumn(field.getAnnotation(Column.class).name());
            if (field.isAnnotationPresent(ManyToOne.class))
                tableModel.addColumn(field.getAnnotation(JoinColumn.class).name());
        }

        String name = Project.class.getName();
        name = name.substring(name.lastIndexOf('.') + 1);

        List<?> result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, (Class<?>) Project.class);
            result = query.getResultList();
            session.getTransaction().commit();
        }

        return result;
    }

    private List<?> employeesTableInit() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        Field[] fields = Employees.class.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Column.class)) {
                tableModel.addColumn(field.getAnnotation(Column.class).name());
            }
            if (field.isAnnotationPresent(ManyToOne.class)) {
                tableModel.addColumn("Department");
            }
        }

        String name = Employees.class.getName();
        name = name.substring(name.lastIndexOf('.') + 1);

        List<?> result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, (Class<?>) Employees.class);
            result = query.getResultList();
            session.getTransaction().commit();
        }

        return result;
    }

    private List<?> unstandardTableInit() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        tableModel.addColumn("id_p");
        tableModel.addColumn("name_p");
        tableModel.addColumn("Expenses");

        String name = Project.class.getName();
        name = name.substring(name.lastIndexOf('.') + 1);

        List<?> result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, (Class<?>) Project.class);
            result = query.getResultList();
            session.getTransaction().commit();
        }

        return result;
    }

    private void createWorkIntervalView(String down_diap, String up_diap) {
        List<?> temp = standardTableInit();
        List<Project> result = new ArrayList<>();

        for (Object obj : temp) {
            result.add((Project) obj);
        }

        List<?> temp2 = employeesTableInit();
        List<Employees> employees = new ArrayList<>();

        for (Object obj : temp2) {
            employees.add((Employees) obj);
        }

        Date beginDate;
        Date endDate;
        beginDate = Date.valueOf(down_diap);
        endDate = Date.valueOf(up_diap);

        List<String[]> toTxtTable = new ArrayList<>();

        for (Project proj : result) {
            if (proj.getDate_beg() == null) {
                continue;
            }
            if ((proj.getDate_beg().after(beginDate)) && (proj.getDate_beg().before(endDate))) {
                Departments departments = proj.getDepartment_id();
                System.out.println(departments.getName_d());

                for (Employees emp : employees) {
                    if ((emp.getDepartment_id() != null) &&
                            (emp.getDepartment_id().getId_d() == departments.getId_d())) {
                        String[] data = {
                                String.valueOf(emp.getId_e()),
                                emp.getLast_name_e(),
                                emp.getFirst_name_e(),
                                emp.getPather_name_e(),
                                emp.getPosition_e(),
                                String.valueOf(emp.getSalary_e()),
                                emp.getDepartment_id().getName_d()
                        };
                        toTxtTable.add(data);
                        tableModel.addRow(data);
                    }
                }
            }
        }

        text = "Список людей, работающие с " + beginDate.toString() + " по " + endDate.toString();
        pushDataIntoReport("WorkIntervalView.pdf", toTxtTable, 7);
    }

    private void createShowExpensesProject() {
        List<?> temp2 = employeesTableInit();
        List<Employees> employees = new ArrayList<>();

        for (Object obj : temp2) {
            employees.add((Employees) obj);
        }

        List<?> temp = unstandardTableInit();
        List<Project> result = new ArrayList<>();

        for (Object obj : temp) {
            result.add((Project) obj);
        }

        List<String[]> toTxtTable = new ArrayList<>();

        for (Project proj : result) {
            int sum = 0;
            for (Employees emp: employees) {
                if ((emp.getDepartment_id() != null) &&
                        (emp.getDepartment_id().getId_d() == proj.getDepartment_id().getId_d())) {
                    sum += emp.getSalary_e();
                }
            }
            String[] data = {
                    String.valueOf(proj.getId_p()),
                    proj.getName_p(),
                    String.valueOf(sum)
            };
            toTxtTable.add(data);
            tableModel.addRow(data);
        }

        text = "Затраты на реализацию проектов за месяц\n";
        pushDataIntoReport("ShowExpensesProject.pdf", toTxtTable, 3);
    }

    public ReportsTableWindow(SessionFactory factory, Point pos) {
        this.sessionFactory = factory;
        frame.setLocation(pos);

        String[] variants = {
                "Рабочий период",
                "Показать затраты на проекты за месяц"
        };

        JComboBox<String> jComboBox = new JComboBox<>(variants);
        jComboBox.setFont(new Font("Times New Romans", Font.BOLD, 20));
        jComboBox.setBounds(new Rectangle(frameWidth / 12, frameHeight / 12, frameWidth / 3, frameHeight / 24));

        JTextField textField1 = new JTextField();
        textField1.setBounds(frameWidth * 3 / 7, frameHeight / 12, frameWidth / 12, frameHeight / 24);
        textField1.setVisible(false);

        JTextField textField2 = new JTextField();
        textField2.setBounds(frameWidth * 4 / 7, frameHeight / 12, frameWidth / 12, frameHeight / 24);
        textField2.setVisible(false);

        JButton applyFilterButton = new JButton("Применить");
        applyFilterButton.setBounds(frameWidth * 4/ 5, frameHeight / 12, frameWidth / 6, frameHeight / 24);
        applyFilterButton.setBackground(Color.WHITE);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, frameHeight / 5, frameWidth, frameHeight * 5 / 6);

        frame.add(applyFilterButton);
        frame.add(jComboBox);
        frame.getContentPane().add(pane);
        frame.add(textField1);
        frame.add(textField2);

        frame.setSize(frameWidth, frameHeight);
        frame.setLayout(null);
        frame.setTitle("Отчёты");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        jComboBox.addActionListener(e->{
            choice = jComboBox.getItemAt(jComboBox.getSelectedIndex());

            if (choice.equals("Рабочий период")) {
                textField1.setVisible(true);
                textField2.setVisible(true);
            }
            else {
                textField1.setVisible(false);
                textField2.setVisible(false);
            }
        });

        applyFilterButton.addActionListener(e -> {
            if (textField1.getText().isEmpty() || textField2.getText().isEmpty())
                JOptionPane.showMessageDialog(new JFrame(), "Заполните все поля.");
            switch (choice) {
                case "Рабочий период"-> createWorkIntervalView(textField1.getText(), textField2.getText());
                case "Показать затраты на проекты за месяц"-> createShowExpensesProject();
            }
        });
    }
}
