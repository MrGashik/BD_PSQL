package org.example.models;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employees {
    @Id
    @Column(name = "id_e")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_e;

    @Column(name = "first_name_e")
    private String first_name_e;

    @Column(name = "last_name_e")
    private String last_name_e;

    @Column(name = "pather_name_e")
    private String pather_name_e;

    @Column(name = "position_e")
    private String position_e;

    @Column(name = "salary_e")
    private int salary_e;

    @ManyToOne
    @JoinTable(
            name = "departments_employees",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Departments department_id;

    public Employees() {}

    public Employees(String first_name_e, String last_name_e, String pather_name_e,
                     String position_e, int salary_e, Departments department_id) {
        this.first_name_e = first_name_e;
        this.last_name_e = last_name_e;
        this.pather_name_e = pather_name_e;
        this.position_e = position_e;
        this.salary_e = salary_e;
        this.department_id = department_id;
    }

    public int getId_e() {
        return id_e;
    }

    public void setId_e(int id_e) {
        this.id_e = id_e;
    }

    public String getFirst_name_e() {
        return first_name_e;
    }

    public void setFirst_name_e(String first_name_e) {
        this.first_name_e = first_name_e;
    }

    public String getLast_name_e() {
        return last_name_e;
    }

    public void setLast_name_e(String last_name_e) {
        this.last_name_e = last_name_e;
    }

    public String getPather_name_e() {
        return pather_name_e;
    }

    public void setPather_name_e(String pather_name_e) {
        this.pather_name_e = pather_name_e;
    }

    public String getPosition_e() {
        return position_e;
    }

    public void setPosition_e(String position_e) {
        this.position_e = position_e;
    }

    public int getSalary_e() {
        return salary_e;
    }

    public void setSalary_e(int salary_e) {
        this.salary_e = salary_e;
    }

    public Departments getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Departments department_id) {
        this.department_id = department_id;
    }
}
