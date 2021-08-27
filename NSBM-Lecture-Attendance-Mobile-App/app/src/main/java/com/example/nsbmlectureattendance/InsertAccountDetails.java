package com.example.nsbmlectureattendance;

public class InsertAccountDetails {

    public InsertAccountDetails() {}

    String id, name, email, batch;

    public InsertAccountDetails(String id, String name, String email, String batch) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.batch = batch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
