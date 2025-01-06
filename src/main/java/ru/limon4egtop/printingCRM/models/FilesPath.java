package ru.limon4egtop.printingCRM.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FilesPath {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String fileType;
    private String fileUrl;

    public FilesPath(Long id, Long orderId, String fileType, String fileName) {
        this.id = id;
        this.orderId = orderId;
        this.fileType = fileType;
        this.fileUrl = fileName;
    }

    public FilesPath(Long orderId, String fileType, String fileUrl) {
        this.orderId = orderId;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }

    public FilesPath() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
