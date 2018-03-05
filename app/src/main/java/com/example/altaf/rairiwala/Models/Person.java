package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Person {
    private String name;
    private int person_id;
    private String person_phone_number;
    private String pin;
    private String Rule;
    private String status;

    public void setName(String name) {
        this.name = name;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public void setPerson_phone_number(String person_phone_number) {
        this.person_phone_number = person_phone_number;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setRule(String rule) {
        Rule = rule;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getPerson_id() {
        return person_id;
    }

    public String getPerson_phone_number() {
        return person_phone_number;
    }

    public String getPin() {
        return pin;
    }

    public String getRule() {
        return Rule;
    }

    public String getStatus() {
        return status;
    }
}
