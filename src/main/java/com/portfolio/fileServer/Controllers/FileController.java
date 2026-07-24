package com.portfolio.fileServer.Controllers;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.portfolio.fileServer.Entities.Files;
import com.portfolio.fileServer.Services.FileService;
import com.portfolio.fileServer.dto.ResponseFileDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "${fileServer.cors}")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<Object> uploadFile(
            @RequestBody byte[] data,
            @RequestHeader("X-Folder-Id") Long folderId,
            @RequestHeader("Content-Size") Long size,
            @RequestHeader("X-File-Type") String type,
            @RequestHeader("X-File-Name") String fileNameEncoded,
            @RequestHeader("X-Is-First-Chunk") Boolean isFirstChunk) {
        String fileName = URLDecoder.decode(fileNameEncoded, StandardCharsets.UTF_8);

        try {

            Files file = fileService.writeFile(data, folderId, fileName, size, type, isFirstChunk);
            System.out.print(data);
            return ResponseEntity.ok().body(new ResponseFileDto((long) file.getId(), file.getSize(), file.getType(),
                    file.getName(), file.getFolder().getId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while trying to write the file: " + e.getMessage());
        }
    }

    @GetMapping("/folder/{id}")
    public ResponseEntity<List<ResponseFileDto>> getFilesByFolder(@PathVariable Long id) {
        List<Files> files = fileService.getFilesByFolderId(id);
        List<ResponseFileDto> filesDto = Files.convertFilesToDtos(files);
        return ResponseEntity.ok().body(filesDto);
    }

    /*
     * @GetMapping("/download/{id}")
     * public ResponseEntity<byte[]> getDownloadFile(@PathVariable Long id,
     * 
     * @RequestHeader("X-Current-Bytes") Long currentBytes) throws Exception {
     * try {
     * ResponseFileChunk file = fileService.downloadFile(id, currentBytes);
     * 
     * return
     * ResponseEntity.status(206).contentType(MediaType.APPLICATION_OCTET_STREAM)
     * .contentLength((file.chunk().length)).header("Access-Control-Expose-Headers",
     * "X-Is-Final")
     * .header("X-Is-Final", file.isLastChunk().toString()).body(file.chunk());
     * } catch (Exception e) {
     * // return ResponseEntity.internalServerError().body(e.getMessage());
     * System.out.
     * println("PROBLEMA DE DESCARGA+++++++++++++++++++++++++++++=+++++++++++++++++++"
     * );
     * System.out.println(e.getMessage());
     * throw e;
     * }
     * }
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long id) {
        Files file = fileService.getFilesById(id);
        try {
            StreamingResponseBody resource = fileService.downloadFileStream(id);
            return ResponseEntity.status(206).contentLength(file.getSize())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8))
                    .body(resource);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
}
