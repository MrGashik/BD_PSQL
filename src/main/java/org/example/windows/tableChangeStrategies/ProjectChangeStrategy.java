package org.example.windows.tableChangeStrategies;

import org.example.models.Departments;
import org.example.models.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.postgresql.util.PSQLException;

import javax.swing.*;
import java.sql.Date;
import java.util.List;

public class ProjectChangeStrategy implements TableChangeStrategy {
    @Override
    public void createOKActionListener(List<JTextField> textFields, SessionFactory factory, String state) {
        switch (state) {
            case "adding" -> {
                boolean isValid = !textFields.get(1).getText().isEmpty() &
                        !textFields.get(2).getText().isEmpty() &
                        !textFields.get(6).getText().isEmpty();

                if (!isValid) {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поля name_p, cost_p, department_id.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Departments departments = session.get(Departments.class, Integer.parseInt(textFields.get(6).getText()));
                    Project model = new Project(
                            textFields.get(1).getText(),
                            Integer.parseInt(textFields.get(2).getText()),
                            (textFields.get(3).getText().isEmpty()) ? null : Date.valueOf(textFields.get(3).getText()),
                            (textFields.get(4).getText().isEmpty()) ? null : Date.valueOf(textFields.get(4).getText()),
                            (textFields.get(5).getText().isEmpty()) ? null : Date.valueOf(textFields.get(5).getText()),
                            departments
                    );
                    departments.getProjectList().add(model);
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
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_p.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Project model = session.get(Project.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Проект под таким ID не существует.");
                    } else {
                        for (int i = 1; i < 7; ++i) {
                            if (!textFields.get(i).getText().isEmpty()) {
                                switch (i) {
                                    case 1 -> model.setName_p(textFields.get(1).getText());
                                    case 2 -> model.setCost_p(Integer.parseInt(textFields.get(2).getText()));
                                    case 3 -> model.setDate_beg(Date.valueOf(textFields.get(3).getText()));
                                    case 4 -> model.setDate_end(Date.valueOf(textFields.get(4).getText()));
                                    case 5 -> model.setDate_end_real(Date.valueOf(textFields.get(5).getText()));
                                    case 6 -> {
                                        Departments departments = session.get(Departments.class, Integer.parseInt(textFields.get(i).getText()));
                                        model.getDepartment_id().getProjectList().remove(model);
                                        model.setDepartment_id(departments);
                                        departments.getProjectList().add(model);
                                    }
                                }
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
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_p.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Project model = session.get(Project.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Проект под таким ID не существует.");
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
