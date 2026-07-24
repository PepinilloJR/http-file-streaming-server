package com.portfolio.fileServer.Entities;

import java.util.List;
import java.util.stream.Collectors;

import com.portfolio.fileServer.dto.ResponseFolderDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "FOLDER")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Folders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOLDER_ID")
    private Long id;

    @Column(name = "FOLDER_NAME")
    @NonNull
    private String name;

    @Column(name = "PATH")
    @NonNull
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "FATHER_ID", nullable = true)
    private Folders fatherFolder;

    public Folders(String name, String path, Folders father) {
        this.name = name;
        this.path = path;
        this.fatherFolder = father;
    }

    static public List<ResponseFolderDto> convertFoldersToDtos(List<Folders> folders) {
        List<ResponseFolderDto> foldersDto = folders.stream().map((Folders folder) -> {
            return new ResponseFolderDto(folder.getId(), folder.getName(), folder.getFatherFolder().getId());
        }).collect(Collectors.toList());
        return foldersDto;
    }
    static public ResponseFolderDto convertFoldersToDtos(Folders folder) {
       
        if (folder.getFatherFolder() == null) {
            return new ResponseFolderDto(folder.getId(), folder.getName(), null);
        }
        
        return new ResponseFolderDto(folder.getId(), folder.getName(), folder.getFatherFolder().getId());
    }

}
