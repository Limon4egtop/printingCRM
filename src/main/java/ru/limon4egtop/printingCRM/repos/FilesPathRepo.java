package ru.limon4egtop.printingCRM.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.limon4egtop.printingCRM.models.filesPath;

import java.util.List;

@Repository
public interface FilesPathRepo extends JpaRepository<filesPath, Long> {
    List<filesPath> findByOrderIdAndFileType(Long orderId, String fileType);
    Integer countByOrderIdAndFileType(Long orderId, String fileType);
}
