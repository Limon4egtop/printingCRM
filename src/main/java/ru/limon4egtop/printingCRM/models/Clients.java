package ru.limon4egtop.printingCRM.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Clients {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String companyName;
    private String contactPhoneNumber;
    private String contactName;
    private String email;
    private String website;

    public Clients(Long id, String companyName, String contactPhoneNumber, String contactName, String email, String website) {
        this.id = id;
        this.companyName = companyName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactName = contactName;
        this.email = email;
        this.website = website;
    }

    public Clients(Long id, String companyName, String contactPhoneNumber, String contactName) {
        this.id = id;
        this.companyName = companyName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactName = contactName;
    }

    public Clients(String companyName, String contactPhoneNumber, String contactName, String email) {
        this.companyName = companyName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactName = contactName;
        this.email = email;
    }

    public Clients() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
