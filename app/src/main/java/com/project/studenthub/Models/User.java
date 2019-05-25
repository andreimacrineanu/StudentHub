package com.project.studenthub.Models;

import java.util.HashMap;
import java.util.List;

public class User {
    private String UID;
    private String email;
    private String nume;
    private String prenume;
    private String grupa;
    private HashMap<String, String> classes;

    public User (String UID, String email, String nume, String prenume, String grupa){
        this.UID = UID;
        this.email = email;
        this.nume = nume;
        this.prenume = prenume;
        this.grupa = grupa;
    }

    public User (){
        this.classes = new HashMap<>();
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return email;
    }

    public String getNume() {
        return nume;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getGrupa() {
        return grupa;
    }

    public HashMap<String, String> getClasses() {
        return classes;
    }

    public void setClasses(HashMap<String, String> classes) {
        this.classes = classes;
    }
}
