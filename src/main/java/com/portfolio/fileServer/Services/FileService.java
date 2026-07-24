package com.portfolio.fileServer.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.portfolio.fileServer.Entities.Files;
import com.portfolio.fileServer.Entities.Folders;
import com.portfolio.fileServer.Repository.FilesRepository;
import com.portfolio.fileServer.Repository.FoldersRepository;
import com.portfolio.fileServer.dto.ResponseFileChunk;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${fileServer.rootPath}")
    private String rootPath;
    final FilesRepository filesRepository;
    final FoldersRepository foldersRepository;

    @Transactional
    public void deleteFile(Files file) {
        File fileManager = new File(file.getPath());
        fileManager.delete();
        filesRepository.delete(file);
    }


    public Files writeFile(byte[] fileChunk, Long folderId, String fileName, Long size, String type,
            Boolean isFirstChunk)
            throws IOException {

        try {
            Folders folder = foldersRepository.findById(folderId).get();
            String path = folder.getPath() + "/" + fileName;
            File targetFile = new File(path);
            System.out.println("Recibiendo bytes: ");

            // this replaces the file if it is the first chunk of bytes
            // instead, it simply returns the same values to be saved on the same table
            Files file = filesRepository.findByPath(path).map(file_ -> {
                if (isFirstChunk) {
                    deleteFile(file_);
                    return new Files(size, 0l, fileName, type, path, folder);
                } else {
                    return file_;
                }

            }).orElse(new Files(size, 0l, fileName, type, path, folder));

            try (FileOutputStream writer = new FileOutputStream(targetFile, true)) {
                System.out.println("Escribiendo: " + fileChunk.length + " bytes");
                writer.write(fileChunk);
                writer.close();
            } catch (IOException e) {
                throw e;
            }

            file.setCurrentBytes(file.getCurrentBytes() + fileChunk.length);
            file = filesRepository.saveAndFlush(file);
            System.out.println("Bytes resultantes: " + file.getCurrentBytes() + " bytes de " + file.getSize());

            return file;
        } catch (IOException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        }

        // Files file = new Files(size, fileName, type, path);

    }

    @Transactional
    public List<Files> getFilesByFolderId(Long folderId) {
        List<Files> files = filesRepository.findByFolderId(folderId);

        return files;
    }

    @Transactional
    public Files getFilesById(Long folderId) {
        Files files = filesRepository.findById(folderId).get();

        return files;
    }

    public StreamingResponseBody downloadFileStream(Long fileId) throws FileNotFoundException {

        Files file = filesRepository.findById(fileId).get();

        File realFile = new File(file.getPath());

        InputStream inputStream = new FileInputStream(realFile);

        StreamingResponseBody responseBody = outputStream -> {
            byte[] chunk = new byte[1024 * 20];

            Integer bytesToWrite;
            while (true) {
                Long currentBytes = filesRepository.findCurrentBytesById(fileId);

                bytesToWrite = inputStream.read(chunk, 0, chunk.length);

                if (bytesToWrite == -1 && currentBytes.compareTo((file.getSize())) >= 0) {

                    System.out.println("Termino el archivo");
                    System.out.println(currentBytes + " / " + file.getSize());
                    outputStream.flush();
                    break;
                } else if (bytesToWrite == -1) {
                    System.out.println("Esperando a mas bytes");
                    System.out.println(currentBytes + " / " + file.getSize());
                    try {
                        Thread.sleep(5000);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    outputStream.write(chunk, 0, bytesToWrite);
                }

            }

        };

        return responseBody;

    }

    @Transactional
    public ResponseFileChunk downloadFile(Long fileId, Long currentBytes) throws FileNotFoundException, IOException {
        Files file = filesRepository.findById(fileId).get();

        File realFile = new File(file.getPath());

        byte[] chunk = new byte[0];
        Boolean isLastChunk = false;

        int bytesToRead;
        if ((currentBytes + (1024 * 1024 * 20) >= file.getCurrentBytes())) {
            if (file.getSize().equals(file.getCurrentBytes())) {
                isLastChunk = true;
            }

            bytesToRead = (int) ((-currentBytes + (file.getCurrentBytes())));
        } else {
            bytesToRead = (int) ((1024 * 1024 * 20));
        }

        try (RandomAccessFile reader = new RandomAccessFile(realFile, "r")) {
            reader.seek(currentBytes);
            chunk = new byte[bytesToRead];
            reader.readFully(chunk);
            return new ResponseFileChunk(file.getId(), file.getSize(), file.getType(), file.getName(), chunk,
                    isLastChunk);

        } catch (IOException e) {
            throw e;
        }

    }
}
