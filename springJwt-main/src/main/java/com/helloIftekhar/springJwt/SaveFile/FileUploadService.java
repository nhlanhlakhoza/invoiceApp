package com.helloIftekhar.springJwt.SaveFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class FileUploadService {
    @Value("${upload.directory}")
    private String uploadDirectory;

    public void saveFile(MultipartFile file) {
        try {
            String filePath = uploadDirectory + file.getOriginalFilename();
            try (OutputStream outputStream = new FileOutputStream(filePath);
                 InputStream inputStream = file.getInputStream()) {
                int readBytes;
                byte[] buffer = new byte[1024];
                while ((readBytes = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file save error
        }
    }
}
