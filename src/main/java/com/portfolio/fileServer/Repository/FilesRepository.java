package com.portfolio.fileServer.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.fileServer.Entities.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {
    public Optional<Files> findByPath(String path);

    public List<Files> findByFolderId(Long id);

    // this allows me to get the updated data of the file, avoiding hibernate cache
    @Query("SELECT f.currentBytes FROM Files f WHERE f.id = :id")
    Long findCurrentBytesById(@Param("id") Long id);
}
