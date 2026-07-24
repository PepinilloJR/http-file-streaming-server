package com.portfolio.fileServer.dto;

public record ResponseFileDto(
    Long id, Long size, String type, String name, Long folderId
) {}