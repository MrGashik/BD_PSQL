package org.example.windows;

import org.example.models.Project;
import org.example.windows.tableChangeStrategies.ProjectChangeStrategy;
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

public class JournalTableWindow {
    private final int frameWidth = 1280;
    private final int frameHeight = 720;
    private final SessionFactory sessionFactory;
    private JFrame frame = new JFrame();
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table = new JTable(tableModel);
    String choice = "none";

    private List<?> standardTableInit() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        Field[] fields = Project.class.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Column.class))
                tableModel.addColumn(field.getAnnotation(Column.class).name());
            if (field.isAnnotationPresent(ManyToOne.class))
                tableModel.addColumn("Department");
        }

        String name = Project.class.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        System.out.println(name);

        List<?> result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, (Class<?>) Project.class);
            result = query.getResultList();
            session.getTransaction().commit();
        }

        return result;
    }

    private void createProjectTable() {
        List<?> temp = standardTableInit();
        List<Project> result = new ArrayList<>();

        for (Object obj: temp)
            result.add((Project) obj);

        for (Project project: result) {
            Object[] data = {
                    String.valueOf(project.getId_p()),
                    project.getName_p(),
                    String.valueOf(project.getCost_p()),
                    (project.getDate_beg() == null) ? "" : project.getDate_beg().toString(),
                    (project.getDate_end() == null) ? "" : project.getDate_end().toString(),
                    (project.getDate_end_real() == null) ? "" : project.getDate_end_real().toString(),
                    project.getDepartment_id().getName_d()
            };
            tableModel.addRow(data);
        }
    }

    public JournalTableWindow(SessionFactory factory, Point pos, boolean admin) {
        this.sessionFactory = factory;
        frame.setLocation(pos);

        String[] variants = {
                "project"
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
            if (choice.equals("project")) {
                new EditingWindow(factory, jFrame, Project.class, new ProjectChangeStrategy(), p);
            }
        });
        editButton.setVisible(admin);

        frame.add(editButton);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, frameHeight / 6, frameWidth, frameHeight * 4 / 6);
        frame.getContentPane().add(pane);

        frame.setSize(frameWidth, frameHeight);
        frame.setLayout(null);
        frame.setTitle("Журналы");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        jComboBox.addActionListener(e->{
            String choice = jComboBox.getItemAt(jComboBox.getSelectedIndex());
            if (choice.equals("project")) {
                createProjectTable();
            }
        });
    }
}
