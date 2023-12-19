package org.example.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "departments")
public class Departments {
    @Id
    @Column(name = "id_d")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_d;

    @Column(name = "name_d")
    private String name_d;

    @OneToMany(mappedBy = "department_id")
    private List< Project > projectList;

    @OneToMany(mappedBy = "department_id")
    private List< Employees > employeesList;

    public Departments() {}

    public Departments(String name_d) {
        this.name_d = name_d;
    }

    public int getId_d() {
        return id_d;
    }

    public void setId_d(int id_d) {
        this.id_d = id_d;
    }

    public String getName_d() {
        return name_d;
    }

    public void setName_d(String name_d) {
        this.name_d = name_d;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Employees> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(List<Employees> employeesList) {
        this.employeesList = employeesList;
    }
}
