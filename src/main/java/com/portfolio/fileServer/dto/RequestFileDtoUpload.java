package com.portfolio.fileServer.dto;

public record RequestFileDtoUpload(
    String fileName, byte[] buffer, Boolean isFirstChunk
) {}
