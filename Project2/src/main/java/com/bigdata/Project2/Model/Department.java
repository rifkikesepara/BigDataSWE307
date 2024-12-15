package com.bigdata.Project2.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "dept")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deptno;

    public int getDeptno() {
        return deptno;
    }

    public void setDeptno(int deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String deptname) {
        this.dname = deptname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    private String dname;
    private String loc;

    public Department(){}

    public Department(int deptno, String deptname, String loc) {
        this.deptno = deptno;
        this.dname = deptname;
        this.loc = loc;
    }


}
