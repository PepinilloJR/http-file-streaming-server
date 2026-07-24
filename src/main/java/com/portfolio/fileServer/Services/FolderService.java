package com.portfolio.fileServer.Services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.portfolio.fileServer.Entities.Folders;
import com.portfolio.fileServer.Repository.FilesRepository;
import com.portfolio.fileServer.Repository.FoldersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
    
    @Value("${fileServer.rootPath}")
    private String rootPath;
    final FilesRepository filesRepository;
    final FoldersRepository foldersRepository;

    @Transactional
    public Folders createFolder(Long fatherId, String name) throws IOException {

        Folders father = foldersRepository.findById(fatherId).get();

        String directoryPath = father.getPath() + "/" + name;

        File directory = new File(directoryPath);
        if (directory.mkdir()) {
            System.out.println("Directory created successfully at: " + directoryPath);
        } else {
            throw new IOException("The directory already exists.");
        }

        Folders folder = new Folders(name, directoryPath, father);

        Folders folderCreated = foldersRepository.save(folder);

        return folderCreated;
    }

    @Transactional
    public List<Folders> getFoldersByFatherId(Long fatherId) {
        List<Folders> folders = foldersRepository.findByFatherFolderId(fatherId);
        
        return folders;
    }

    @Transactional
    public Folders getFolderById(Long id) {
        Folders folder = foldersRepository.findById(id).get();
        return folder;
    }
}
