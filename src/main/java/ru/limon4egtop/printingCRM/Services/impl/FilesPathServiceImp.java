package ru.limon4egtop.printingCRM.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.limon4egtop.printingCRM.Services.FilesPathService;
import ru.limon4egtop.printingCRM.models.FilesPath;
import ru.limon4egtop.printingCRM.repos.FilesPathRepo;

import java.util.List;

@Service
public class FilesPathServiceImp implements FilesPathService {
    private FilesPathRepo filesPathRepo;

    @Autowired
    public FilesPathServiceImp(FilesPathRepo filesPathRepo) {
        this.filesPathRepo = filesPathRepo;
    }

    @Override
    public FilesPath addFile(final FilesPath file) {
        return this.filesPathRepo.save(file);
    }

    @Override
    public void deleteFile(FilesPath file) {
        this.filesPathRepo.delete(file);
    }

    @Override
    public List<FilesPath> getByOrderIdAndFileType(Long orderId, String fileType) {
        return this.filesPathRepo.findByOrderIdAndFileType(orderId, fileType);
    }
}
