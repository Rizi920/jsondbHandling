package com.example.jsondbHandling;

import java.util.UUID;

public class employee {
    String id= UUID.randomUUID().toString();;
    String fullName;
    String age;
    String salary;

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAge() {
        return age;
    }

    public String getSalary() {
        return salary;
    }
}
