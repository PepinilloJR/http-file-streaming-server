package com.portfolio.fileServer.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.fileServer.Entities.Folders;

public interface FoldersRepository extends JpaRepository<Folders, Long> {
    public List<Folders> findByFatherFolderId(Long id);

}
