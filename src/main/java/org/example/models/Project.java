package org.example.models;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "id_p")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_p;

    @Column(name = "name_p")
    private String name_p;

    @Column(name = "cost_p")
    private int cost_p;

    @Column(name = "date_beg")
    private Date date_beg;

    @Column(name = "date_end")
    private Date date_end;

    @Column(name = "date_end_real")
    private Date date_end_real;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id_d")
    private Departments department_id;

    public Project() {}

    public Project(String name_p, int cost_p, Date date_beg, Date date_end, Date date_end_real, Departments departments_id) {
        this.name_p = name_p;
        this.cost_p = cost_p;
        this.date_beg = date_beg;
        this.date_end = date_end;
        this.date_end_real = date_end_real;
        this.department_id = departments_id;
    }

    public int getId_p() {
        return id_p;
    }

    public void setId_p(int id_p) {
        this.id_p = id_p;
    }

    public String getName_p() {
        return name_p;
    }

    public void setName_p(String name_p) {
        this.name_p = name_p;
    }

    public int getCost_p() {
        return cost_p;
    }

    public void setCost_p(int cost_p) {
        this.cost_p = cost_p;
    }

    public Date getDate_beg() {
        return date_beg;
    }

    public void setDate_beg(Date date_beg) {
        this.date_beg = date_beg;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public Date getDate_end_real() {
        return date_end_real;
    }

    public void setDate_end_real(Date date_end_real) {
        this.date_end_real = date_end_real;
    }

    public Departments getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Departments department_id) {
        this.department_id = department_id;
    }
}
