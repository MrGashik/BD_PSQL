package org.example.windows;

import org.example.windows.tableChangeStrategies.TableChangeStrategy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditingWindow {
    private final int frameWidth = 600;
    private final int frameHeight = 900;
    private JFrame frame;
    List<JTextField> textFields = new ArrayList<>();
    List<JLabel> labels = new ArrayList<>();
    enum State {
        adding,
        editing,
        deleting
    }
    private State state;
    private SessionFactory sessionFactory;
    private TableChangeStrategy tableChangeStrategy;
    private void initButtons() {
        JButton insertButton = new JButton("Добавить");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton exitButton = new JButton("Выход");
        JButton OKButton = new JButton("Применить");
        insertButton.setBackground(Color.WHITE);
        updateButton.setBackground(Color.WHITE);
        deleteButton.setBackground(Color.WHITE);
        exitButton.setBackground(Color.WHITE);
        OKButton.setBackground(Color.WHITE);

        insertButton.addActionListener(e->{
            state = State.adding;
            for (int i = 0; i < textFields.size(); ++i) {
                if (i == 0) {
                    textFields.get(0).setVisible(false);
                    labels.get(0).setVisible(false);
                }
                else {
                    textFields.get(i).setVisible(true);
                    labels.get(i).setVisible(true);
                }
            }
        });
        updateButton.addActionListener(e->{
            state = State.editing;
            for (int i = 0; i < textFields.size(); ++i) {
                textFields.get(i).setVisible(true);
                labels.get(i).setVisible(true);
            }
        });
        deleteButton.addActionListener(e->{
            state = State.deleting;
            for (int i = 0; i < textFields.size(); ++i) {
                if (i == 0) {
                    textFields.get(0).setVisible(true);
                    labels.get(0).setVisible(true);
                }
                else {
                    textFields.get(i).setVisible(false);
                    labels.get(i).setVisible(false);
                }
            }
        });
        exitButton.addActionListener(e -> frame.dispose());

        OKButton.addActionListener(e-> tableChangeStrategy.createOKActionListener(textFields, sessionFactory, state.toString()));

        insertButton.setBounds(frameWidth / 24, frameHeight / 24, frameWidth / 6, frameHeight / 12);
        updateButton.setBounds(frameWidth / 24, frameHeight * 4 / 24, frameWidth / 6, frameHeight / 12);
        deleteButton.setBounds(frameWidth / 24, frameHeight * 7 / 24, frameWidth / 6, frameHeight / 12);
        exitButton.setBounds(frameWidth / 24, frameHeight * 11 / 24, frameWidth / 6, frameHeight / 12);
        OKButton.setBounds(frameWidth * 7 / 24, frameHeight * 19 / 24, frameWidth * 31 / 48, frameHeight / 18);

        frame.add(insertButton);
        frame.add(updateButton);
        frame.add(deleteButton);
        frame.add(exitButton);
        frame.add(OKButton);
    }
    private void addNewJTextField() {
        JTextField temp = new JTextField("", 50);
        int pos = textFields.size();
        temp.setBounds(frameWidth * 7 / 24, frameHeight / 12 + pos * frameHeight / 12, frameWidth * 31 / 48, frameHeight / 27);

        textFields.add(temp);
    }
    private void addNewJLabel(String text) {
        JLabel temp = new JLabel(text, SwingConstants.CENTER);
        int pos = textFields.size();
        temp.setBounds(frameWidth * 7 / 24, frameHeight / 24 + pos * frameHeight / 12, frameWidth * 31 / 48, frameHeight / 27);

        labels.add(temp);
        frame.add(temp);
    }
    public EditingWindow(SessionFactory factory, JFrame frame, Class<?> modelClass, TableChangeStrategy tableChangeStrategy, Point p) {
        this.sessionFactory = factory;
        this.tableChangeStrategy = tableChangeStrategy;
        this.frame = frame;

        initButtons();

        JScrollPane pane = new JScrollPane();
        pane.setBounds(frameWidth / 4, 0, frameWidth * 3 / 4, frameHeight * 3 / 4);

        String name = modelClass.getName();
        name = name.substring(name.lastIndexOf('.') + 1);

        List<?> temp = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("FROM " + name, modelClass);
            temp = query.getResultList();
            session.getTransaction().commit();
        }

        Field[] fields = modelClass.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Column.class)) {
                addNewJLabel(field.getAnnotation(Column.class).name());
                addNewJTextField();
            }
            if (!name.equals("Employees")) {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    addNewJLabel(field.getAnnotation(JoinColumn.class).name());
                    addNewJTextField();
                }
            }
            else {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    addNewJLabel("Department");
                    addNewJTextField();
                }
            }
        }
        for (JTextField txt: textFields)
            frame.add(txt);

        frame.getContentPane().add(pane);

        frame.setLocation(p);
        frame.setSize(frameWidth, frameHeight);
        frame.setLayout(null);
        frame.setTitle("Изменение данных");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
