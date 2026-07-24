package com.portfolio.fileServer.Entities;

import java.util.List;
import java.util.stream.Collectors;

import com.portfolio.fileServer.dto.ResponseFileDto;

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
@Table(name = "FILE")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;
    @NonNull
    @Column(name = "SIZE_BYTES")
    private Long size;
    @NonNull
    @Column(name = "CURRENT_BYTES")
    private Long currentBytes;
    @Column(name = "NAME")
    @NonNull
    private String name;
    @Column(name = "TYPE")
    @NonNull
    private String type;
    @Column(name = "PATH")
    @NonNull
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    @NonNull
    private Folders folder;

    static public List<ResponseFileDto> convertFilesToDtos(List<Files> files) {
        List<ResponseFileDto> filesDto = files.stream().map((Files file) -> {
            return new ResponseFileDto(file.getId(), file.getSize(), file.getType(), file.getName(), file.getFolder().getId());
        }).collect(Collectors.toList());
        return filesDto;
    }
}
