package org.example.windows.tableChangeStrategies;

import org.example.models.Departments;
import org.example.models.Employees;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.postgresql.util.PSQLException;

import javax.swing.*;
import java.util.List;

public class EmployeesChangeStrategy implements TableChangeStrategy {
    @Override
    public void createOKActionListener(List<JTextField> textFields, SessionFactory factory, String state) {
        switch (state) {
            case "adding" -> {
                boolean isValid = true;
                for (int i = 1; i < textFields.size(); ++i) {
                    isValid &= !textFields.get(i).getText().isEmpty();
                }

                if (!isValid) {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните все поля.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Departments departments = session.get(Departments.class, Integer.parseInt(textFields.get(6).getText()));
                    Employees model = new Employees(
                            textFields.get(1).getText(),
                            textFields.get(2).getText(),
                            textFields.get(3).getText(),
                            textFields.get(4).getText(),
                            Integer.parseInt(textFields.get(5).getText()),
                            departments
                    );
                    departments.getEmployeesList().add(model);
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
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_e.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Employees model = session.get(Employees.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Работник под таким ID не существует.");
                    } else {
                        for (int i = 1; i < 7; ++i) {
                            if (!textFields.get(i).getText().isEmpty()) {
                                switch (i) {
                                    case 1 -> model.setFirst_name_e(textFields.get(1).getText());
                                    case 2 -> model.setLast_name_e(textFields.get(2).getText());
                                    case 3 -> model.setPather_name_e(textFields.get(3).getText());
                                    case 4 -> model.setPosition_e(textFields.get(4).getText());
                                    case 5 -> model.setSalary_e(Integer.parseInt(textFields.get(5).getText()));
                                    case 6 -> {
                                        Departments departments = session.get(Departments.class, Integer.parseInt(textFields.get(i).getText()));
                                        model.getDepartment_id().getEmployeesList().remove(model);
                                        model.setDepartment_id(departments);
                                        departments.getEmployeesList().add(model);
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
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните поле id_e.");
                    break;
                }

                try (Session session = factory.openSession()) {
                    session.beginTransaction();
                    Employees model = session.get(Employees.class, Integer.parseInt(textFields.get(0).getText()));
                    if (model == null) {
                        JOptionPane.showMessageDialog(new JFrame(), "Работник под таким ID не существует.");
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
