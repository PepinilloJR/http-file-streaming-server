package com.portfolio.fileServer.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.fileServer.Entities.Folders;
import com.portfolio.fileServer.Services.FolderService;
import com.portfolio.fileServer.dto.RequestFolderDtoCreate;
import com.portfolio.fileServer.dto.ResponseFolderDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/folders")
@CrossOrigin(origins = "${fileServer.cors}")

@RequiredArgsConstructor

public class FolderController {
    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<Object> createFolder(@RequestBody RequestFolderDtoCreate body,
            @RequestHeader("Father-Id") Long fatherId) {
        String folderName = body.folderName();
        try {
            Folders folder = folderService.createFolder(fatherId, folderName);
            return ResponseEntity.ok().body(folder);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while trying to write the file: " + e.getMessage());

        }

    }

    @GetMapping("/father/{id}")
    public ResponseEntity<List<ResponseFolderDto>> getFoldersByFatherId(@PathVariable Long id) {
        List<Folders> folders = folderService.getFoldersByFatherId(id);
        List<ResponseFolderDto> folderDtos = Folders.convertFoldersToDtos(folders);
        return ResponseEntity.ok().body(folderDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseFolderDto> getFoldersById(@PathVariable Long id) {
        Folders folders = folderService.getFolderById(id);
        ResponseFolderDto folderDtos = Folders.convertFoldersToDtos(folders);
        return ResponseEntity.ok().body(folderDtos);
    }

}
