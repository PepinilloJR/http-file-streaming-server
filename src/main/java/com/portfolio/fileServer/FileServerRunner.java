package com.portfolio.fileServer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.portfolio.fileServer.Entities.Folders;
import com.portfolio.fileServer.Repository.FoldersRepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class FileServerRunner implements CommandLineRunner {
    // create the root folder if not created by reading the property on
    // application.propeties

    @Value("${fileServer.rootPath}")
    private String rootPath;

    final FoldersRepository foldersRepository;

    @Override
    public void run(String[] args) {
        Folders existingFolder = foldersRepository.findById(1L).orElseGet(this::createRootFolder);
        System.out.println("");
        System.out.println("=====ROOT FOLDER STATUS=====");
        System.out.println("Root folder available on: " + existingFolder.getPath());
    }

    public Folders createRootFolder() {
        Folders folder = new Folders("root", rootPath, null);
        Folders folderCreated = foldersRepository.save(folder);
        return folderCreated;
    }
}
