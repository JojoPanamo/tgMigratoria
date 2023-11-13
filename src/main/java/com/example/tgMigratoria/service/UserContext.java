package com.example.tgMigratoria.service;

public class UserContext {
    private String documentNumber;
    private String birthDay;
    private String birthMonth;
    private String birthYear;

    public UserContext() {
        // Конструктор по умолчанию
    }

    public UserContext(String documentNumber, String birthDay, String birthMonth, String birthYear) {
        this.documentNumber = documentNumber;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
    }

    public UserContext(Long aLong) {
    }

    public UserContext(String s) {
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void reset() {
        this.documentNumber = null;
        this.birthDay = null;
        this.birthMonth = null;
        this.birthYear = null;
    }
}
