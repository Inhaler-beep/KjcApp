package com.example.kjc;

public class Faculties
{
    String facultyimage,facultyfullname,facultydepartment;


  public Faculties()

    {
    }


    public Faculties(String facultyimage, String facultyfullname, String facultydepartment) {
        this.facultyimage = facultyimage;
        this.facultyfullname = facultyfullname;
        this.facultydepartment = facultydepartment;
    }

    public String getFacultyimage() {
        return facultyimage;
    }

    public void setFacultyimage(String facultyimage) {
        this.facultyimage = facultyimage;
    }

    public String getFacultyfullname() {
        return facultyfullname;
    }

    public void setFacultyfullname(String facultyfullname) {
        this.facultyfullname = facultyfullname;
    }

    public String getFacultydepartment() {
        return facultydepartment;
    }

    public void setFacultydepartment(String facultydepartment) {
        this.facultydepartment = facultydepartment;
    }
}

