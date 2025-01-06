package ru.limon4egtop.printingCRM.Services;

import ru.limon4egtop.printingCRM.models.FilesPath;

import java.util.List;

public interface FilesPathService {
    FilesPath addFile(FilesPath file);
    void deleteFile(FilesPath file);
    List<FilesPath> getByOrderIdAndFileType(final Long orderId, final String fileType);
}
