package com.MisInfotech.Model;

public class NotesModel {
    String subject;
    String date;
    String status;

    public NotesModel(String subject, String date, String status) {
        this.subject = subject;
        this.date = date;
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
