package org.example;

import org.example.models.Departments;
import org.example.models.Employees;
import org.example.models.Project;
import org.example.windows.JournalTableWindow;
import org.example.windows.ReferenceTableWindow;
import org.example.windows.ReportsTableWindow;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainRegister {
    private static final String file = "src/main/resources/passwords.json";
    private static final File jsonFile = new File(file);
    private static JSONObject jsonLogins;
    private final int frameWidth = 600;
    private final int frameHeight = 400;
    private final JFrame frame = new JFrame();
    private final SessionFactory sessionFactory;
    private Configuration conf = new Configuration();
    private String text = "";
    private final String codeAdmin = "0000";

    public static void main(String[] args) {
        new MainRegister();
    }

    MainRegister() {
        conf.addAnnotatedClass(Departments.class);
        conf.addAnnotatedClass(Employees.class);
        conf.addAnnotatedClass(Project.class);
        sessionFactory = conf.buildSessionFactory();

        JLabel textLogin = new JLabel("Логин", SwingConstants.CENTER);
        textLogin.setBounds(new Rectangle(frameWidth / 4,frameHeight / 16, frameWidth / 2, frameHeight / 16));

        JTextField login = new JTextField("", 50);
        login.setBounds(new Rectangle(frameWidth / 4, frameHeight * 2 / 16, frameWidth / 2, frameHeight / 16));

        JLabel textPassword = new JLabel("Пароль", SwingConstants.CENTER);
        textPassword.setBounds(new Rectangle(frameWidth / 4, frameHeight * 3 / 16, frameWidth / 2, frameHeight / 16));

        JPasswordField password = new JPasswordField("", 50);
        password.setBounds(new Rectangle(frameWidth / 4, frameHeight * 4 / 16, frameWidth / 2, frameHeight / 16));

        JLabel textInvalid = new JLabel(text, SwingConstants.CENTER);
        textInvalid.setBounds(new Rectangle(frameWidth / 4,frameHeight * 6 / 16, frameWidth / 2, frameHeight / 16));
        textInvalid.setForeground(Color.RED);

        JButton logIn = new JButton("Войти");
        logIn.setBounds(new Rectangle(frameWidth * 3 / 8, frameHeight * 5 / 8, frameWidth / 4, frameHeight / 16));
        logIn.setBackground(Color.WHITE);

        JButton registry = new JButton("Регистрация");
        registry.setBounds(new Rectangle(frameWidth * 3 / 8, frameHeight * 6 / 8, frameWidth / 4,frameHeight / 16));
        registry.setBackground(Color.WHITE);

        jsonLogins = createJSON();
        System.out.println(jsonLogins.get("Admins"));

        logIn.addActionListener(e -> {
            if (searchKey(((JSONArray)jsonLogins.get("Admins")), login.getText())) {
                for (Object o : ((JSONArray)jsonLogins.get("Admins"))) {
                    if (((JSONObject) o).containsKey(login.getText())) {
                        if (BCrypt.checkpw(password.getText(), ((JSONObject) o).get(login.getText()).toString())) {
                            startMenu(frame, true);
                        }
                    }
                }
                text = "Неверный пароль.";
            }
            else if (searchKey(((JSONArray)jsonLogins.get("Users")), login.getText())) {
                for (Object o : ((JSONArray)jsonLogins.get("Users"))) {
                    if (((JSONObject) o).containsKey(login.getText())) {
                        if (BCrypt.checkpw(password.getText(), ((JSONObject) o).get(login.getText()).toString())) {
                            startMenu(frame, false);
                        }
                    }
                }
                text = "Неверный пароль.";
            }
            else {
                text = "Неверный логин";
            }

            textInvalid.setText(text);
            frame.revalidate();
            frame.repaint();
        });

        registry.addActionListener(e -> {
            if (!login.getText().equals("") || (password.getPassword().length != 0)) {
                JFrame frameReg = new JFrame();

                JTextField code = new JTextField("CODE", 4);
                code.setBounds(new Rectangle(frameWidth * 3 / 8, frameHeight * 2 / 8, frameWidth * 2 / 8, frameHeight / 8));
                code.setVisible(false);

                JRadioButton radioAdmin = new JRadioButton("Admin");
                radioAdmin.setBounds(new Rectangle(frameWidth / 4, frameHeight / 8, frameWidth / 4, frameHeight / 8));

                JRadioButton radioUser = new JRadioButton("User");
                radioUser.setBounds(new Rectangle(frameWidth / 2, frameHeight / 8, frameWidth / 4, frameHeight / 8));

                JButton OKButton = new JButton("Ok");
                OKButton.setBounds(new Rectangle(frameWidth / 4, frameHeight * 6 / 8, frameWidth / 2, frameHeight / 8));
                OKButton.setBackground(Color.WHITE);

                radioAdmin.addActionListener(ev -> {
                    code.setVisible(radioAdmin.isSelected());
                    if (radioUser.isSelected() && radioAdmin.isSelected()) {
                        radioUser.doClick();
                    }
                });
                radioUser.addActionListener(ev -> {
                    if (radioUser.isSelected() && !radioAdmin.isSelected()) {
                        code.setVisible(false);
                    }
                    if (radioUser.isSelected() && radioAdmin.isSelected()) {
                        radioAdmin.doClick();
                    }
                });
                OKButton.addActionListener(ev -> {
                    try (PrintWriter outFile = new PrintWriter(new FileWriter(jsonFile))) {
                        if ((!searchKey(((JSONArray)jsonLogins.get("Admins")), login.getText())) &&
                                (!searchKey(((JSONArray)jsonLogins.get("Users")), login.getText()))) {
                            JSONObject tempObj = new JSONObject();
                            String bcryptHashString = BCrypt.hashpw(password.getText(), BCrypt.gensalt(10));
                            tempObj.put(login.getText(), bcryptHashString);
                            if (radioAdmin.isSelected() && !radioUser.isSelected()) {
                                if (code.getText().equals(codeAdmin)) {
                                    ((JSONArray) jsonLogins.get("Admins")).add(tempObj);
                                    outFile.write(jsonLogins.toString());
                                } else {
                                    JOptionPane.showMessageDialog(new JFrame(), "Неверный код.");
                                }
                            } else if (!radioAdmin.isSelected() && radioUser.isSelected()) {
                                ((JSONArray) jsonLogins.get("Users")).add(tempObj);
                                outFile.write(jsonLogins.toString());
                            } else {
                                JOptionPane.showMessageDialog(new JFrame(), "Выберите один из вариантов.");
                            }
                            frameReg.dispose();
                        }
                        else {
                            JOptionPane.showMessageDialog(new JFrame(), "Человек под таким логином уже зарегистрирован.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                radioUser.doClick();

                frameReg.add(radioAdmin);
                frameReg.add(radioUser);
                frameReg.add(code);
                frameReg.add(OKButton);

                frameReg.setLayout(null);
                frameReg.setSize(frameWidth, frameHeight);
                frameReg.setTitle("Registry");
                frameReg.setVisible(true);
                frameReg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                text = "";
            }
            else {
                text = "Заполните поля логин и пароль.";
            }
            textInvalid.setText(text);
            frame.revalidate();
            frame.repaint();
        });

        frame.add(textLogin);
        frame.add(login);
        frame.add(textPassword);
        frame.add(password);
        frame.add(textInvalid);
        frame.add(logIn);
        frame.add(registry);

        frame.setLocation(frameWidth / 2, frameHeight / 2);
        frame.setLayout(null);
        frame.setSize(frameWidth, frameHeight);
        frame.setTitle("Course project");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    boolean searchKey(JSONArray jsonArr, String key) {
        for (Object o : jsonArr) {
            if (((JSONObject) o).containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    JSONObject createJSON() {
        JSONObject temp;
        try {
            if ((jsonFile.exists()) && (jsonFile.length() != 0)) {
                temp = (JSONObject) new JSONParser().parse(new FileReader(jsonFile));
            }
            else {
                temp = new JSONObject();
                temp.put("Admins", new JSONArray());
                temp.put("Users", new JSONArray());
                try (PrintWriter outFile = new PrintWriter(new FileWriter(jsonFile))) {
                    outFile.write(temp.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException | ParseException ex) {
            throw new RuntimeException(ex);
        }
        return temp;
    }

    void startMenu(JFrame frame, boolean modeAdmin) {
        JFrame frameMenu = new JFrame();
        frameMenu.setLayout(null);
        frameMenu.setSize(frameWidth, frameHeight);
        frameMenu.setTitle("База данных");
        frameMenu.setVisible(true);
        frameMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton refBookButton = new JButton("Справочники");
        JButton journalButton = new JButton("Журналы");
        JButton reportButton = new JButton("Отчёты");
        refBookButton.setBackground(Color.WHITE);
        journalButton.setBackground(Color.WHITE);
        reportButton.setBackground(Color.WHITE);

        refBookButton.addActionListener(e_1 -> new ReferenceTableWindow(sessionFactory, new Point(50, 50), modeAdmin));
        journalButton.addActionListener(e_1 -> new JournalTableWindow(sessionFactory, new Point(50, 50), modeAdmin));
        reportButton.addActionListener(e_1 -> new ReportsTableWindow(sessionFactory, new Point(50, 50)));

        Rectangle rect = new Rectangle(frameWidth / 4, frameHeight / 6, frameWidth / 2, frameHeight / 5);
        refBookButton.setBounds(rect);
        rect.y += frameHeight / 4;
        journalButton.setBounds(rect);
        rect.y += frameHeight / 4;
        reportButton.setBounds(rect);

        frameMenu.add(refBookButton);
        frameMenu.add(journalButton);
        frameMenu.add(reportButton);

        frame.dispose();
    }
}