package com.portfolio.fileServer.dto;

public record ResponseFileChunk(
    Long id, Long size, String type, String name, byte[] chunk, Boolean isLastChunk
) {}
