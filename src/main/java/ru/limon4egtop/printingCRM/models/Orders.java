package ru.limon4egtop.printingCRM.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long clientId;
    private String managerUsername;
    private String paymentStatus;
    private String comment;
    private LocalDate dateCreate;
    private LocalDate dateEnd;
    private Machine machine;
    private Boolean isSentPrinting;
    private Boolean isPrinted;

    public Orders(Long id, Long clientId, String managerUsername, String paymentStatus, String comment, LocalDate dateCreate, LocalDate dateEnd, Machine machine) {
        this.id = id;
        this.clientId = clientId;
        this.managerUsername = managerUsername;
        this.paymentStatus = paymentStatus;
        this.comment = comment;
        this.dateCreate = dateCreate;
        this.dateEnd = dateEnd;
        this.machine = machine;
        this.isSentPrinting = false;
        this.isPrinted = false;
    }

    public Orders() {
        this.dateCreate = LocalDate.now();
        this.isSentPrinting = false;
        this.isPrinted = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getManagerUsername() {
        return managerUsername;
    }

    public void setManagerUsername(String managerId) {
        this.managerUsername = managerId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Boolean getSentPrinting() {
        return isSentPrinting;
    }

    public void setSentPrinting(Boolean sentPrinting) {
        isSentPrinting = sentPrinting;
    }

    public Boolean getPrinted() {
        return isPrinted;
    }

    public void setPrinted(Boolean printed) {
        isPrinted = printed;
    }
}
