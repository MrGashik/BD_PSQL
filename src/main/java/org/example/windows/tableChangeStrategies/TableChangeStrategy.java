package org.example.windows.tableChangeStrategies;

import org.hibernate.SessionFactory;

import javax.swing.*;
import java.util.List;

public interface TableChangeStrategy {
    void createOKActionListener(List<JTextField> textFields, SessionFactory factory, String state);
}
