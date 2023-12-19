package org.example.windows.tableChangeStrategies;

import org.example.models.Departments;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.postgresql.util.PSQLException;

import javax.swing.*;
import java.util.List;

public class DepartmentsChangeStrategy implements TableChangeStrategy {
    @Override
    public void createOKActionListener(List<JTextField> textFields, SessionFactory factory, String state) {
        switch (state) {
            case "adding" -> {
                boolean isValid = true;
                for (int i = 1; i < textFields.size(); ++i) {
                    isValid &= !textFields.get(i).getText().isEmpty();
                }

                if (!isValid) {
                    System.out.println("tutututututututututu");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Departments model = new Departments(
                            textFields.get(1).getText()
                    );
                    try {
                        session.persist(model);
                        session.getTransaction().commit();
                    } catch (RuntimeException e) {
                        Throwable rootCause = com.google.common.base.Throwables.getRootCause(e);
                        if (rootCause instanceof PSQLException) {
                            JOptionPane.showMessageDialog(new JFrame(), rootCause.getMessage());
                        }
                    }
                }
                for (JTextField a: textFields) {
                    a.setText("");
                }
            }
            case "editing" -> {
                if (textFields.get(0).getText().isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_d.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Departments model = session.get(Departments.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Отдел под таким ID не существует.");
                    } else {
                        for (int i = 1; i < 2; ++i) {
                            if (!textFields.get(i).getText().isEmpty()) {
                                model.setName_d(textFields.get(1).getText());
                            }
                        }
                    }
                    try {
                        session.getTransaction().commit();
                    } catch (RuntimeException e) {
                        Throwable rootCause = com.google.common.base.Throwables.getRootCause(e);
                        if (rootCause instanceof PSQLException) {
                            JOptionPane.showMessageDialog(new JFrame(), rootCause.getMessage());
                        }
                    }
                }
                for (JTextField a: textFields) {
                    a.setText("");
                }
            }
            case "deleting" -> {
                if (textFields.get(0).getText().isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_d.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Departments model = session.get(Departments.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Отдел под таким ID не существует.");
                    } else {
                        session.remove(model);
                    }
                    try {
                        session.getTransaction().commit();
                    } catch (RuntimeException e) {
                        Throwable rootCause = com.google.common.base.Throwables.getRootCause(e);
                        if (rootCause instanceof PSQLException) {
                            JOptionPane.showMessageDialog(new JFrame(), rootCause.getMessage());
                        }
                    }
                }
                for (JTextField a: textFields) {
                    a.setText("");
                }
            }
        }
    }
}
