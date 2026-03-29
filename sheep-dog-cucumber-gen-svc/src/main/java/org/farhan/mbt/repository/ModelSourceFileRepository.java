package org.farhan.mbt.repository;

import java.util.List;
import org.farhan.mbt.model.ModelSourceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModelSourceFileRepository extends JpaRepository<ModelSourceFile, String> {
    
    @Query("SELECT m FROM ModelSourceFile m WHERE m.fileName LIKE :pattern")
    List<ModelSourceFile> findByFileNameLike(@Param("pattern") String fileNamePattern);
    
    ModelSourceFile findByFileName(String fileName);
}
