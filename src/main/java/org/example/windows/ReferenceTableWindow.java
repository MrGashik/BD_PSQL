package org.example.windows;

import org.example.models.Departments;
import org.example.models.Employees;
import org.example.windows.tableChangeStrategies.DepartmentsChangeStrategy;
import org.example.windows.tableChangeStrategies.EmployeesChangeStrategy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReferenceTableWindow {
    private final int frameWidth = 1280;
    private final int frameHeight = 720;
    private JFrame frame = new JFrame();
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table = new JTable(tableModel);
    private SessionFactory sessionFactory;
    String choice = "none";

    private List<?> standardTableInit() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        Field[] fields = Departments.class.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Column.class)) {
                tableModel.addColumn(field.getAnnotation(Column.class).name());
            }
        }

        String name = Departments.class.getName();
        name = name.substring(name.lastIndexOf('.') + 1);

        List<?> result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, (Class<?>) Departments.class);
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

    private void createEmployeesTable() {
        List<?> temp = employeesTableInit();
        List<Employees> result = new ArrayList<>();

        for (Object obj: temp)
            result.add((Employees) obj);

        for (Employees employees: result) {
            Object[] data = {
                    String.valueOf(employees.getId_e()),
                    employees.getFirst_name_e(),
                    employees.getLast_name_e(),
                    employees.getPather_name_e(),
                    employees.getPosition_e(),
                    String.valueOf(employees.getSalary_e()),
                    (employees.getDepartment_id() == null) ? "" : (employees.getDepartment_id().getName_d())
            };
            tableModel.addRow(data);
        }
    }

    private void createDepartmentsTable() {
        List<?> temp = standardTableInit();
        List<Departments> result = new ArrayList<>();

        for (Object obj: temp)
            result.add((Departments) obj);

        for (Departments departments: result) {
            Object[] data = {
                    String.valueOf(departments.getId_d()),
                    departments.getName_d()
            };
            tableModel.addRow(data);
        }
    }

    public ReferenceTableWindow(SessionFactory factory, Point pos, boolean admin) {
        this.sessionFactory = factory;
        frame.setLocation(pos);

        String[] variants = {
                "employees",
                "departments"
        };

        JComboBox<String> jComboBox = new JComboBox<>(variants);
        jComboBox.setFont(new Font("Times New Romans", Font.BOLD, 20));
        jComboBox.setBounds(new Rectangle(frameWidth / 12, frameHeight / 24, frameWidth / 2, frameHeight / 12));

        frame.add(jComboBox);

        JButton editButton = new JButton("Изменить");
        editButton.setBounds(frameWidth * 3 / 5, frameHeight / 24, frameWidth / 5, frameHeight / 12);
        editButton.setBackground(Color.WHITE);
        editButton.addActionListener(e -> {
            Point p = frame.getLocation();
            p.x += frameWidth;
            JFrame jFrame = new JFrame();
            choice = jComboBox.getItemAt(jComboBox.getSelectedIndex());
            switch (choice) {
                case "employees" -> new EditingWindow(factory, jFrame, Employees.class, new EmployeesChangeStrategy(), p);
                case "departments" -> new EditingWindow(factory, jFrame, Departments.class, new DepartmentsChangeStrategy(), p);
            }
        });
        editButton.setVisible(admin);

        frame.add(editButton);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, frameHeight / 6, frameWidth, frameHeight * 4 / 6);
        frame.getContentPane().add(pane);

        frame.setSize(frameWidth, frameHeight);
        frame.setLayout(null);
        frame.setTitle("Справочники");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        jComboBox.addActionListener(e->{
            String choice = jComboBox.getItemAt(jComboBox.getSelectedIndex());
            switch (choice) {
                case "employees"->createEmployeesTable();
                case "departments"->createDepartmentsTable();
            }
        });
    }
}
