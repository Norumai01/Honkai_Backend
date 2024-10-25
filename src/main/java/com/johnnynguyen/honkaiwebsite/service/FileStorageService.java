package com.johnnynguyen.honkaiwebsite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    // At constructor, set the default location. If directory doesn't exist, make the director.
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to create directory: " + this.fileStorageLocation, ex);
        }
    }

    // Store the uploaded files, with a unique name.
    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to store file: " + fileName, ex);
        }
    }

    // Load the stored file to view the file (or image).
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not read file: " + fileName, ex);
        }
    }

    // Generate download URI
    public String generateFileDownloadURI(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();
    }

    // Check validity of the uploaded file.
    public Boolean checkIfFileValid(String fileType) {
        // Check for null.
        if (fileType == null || fileType.isEmpty()) {
            return false;
        }

        // Allowed file types.
        final List<String> allowMimeTypes = List.of(
                "image/jpeg",     // .jpg, .jpeg
                "image/jpg",      // Alternative MIME type for JPEG
                "image/png",      // .png
                "image/webp",     // .webp
                "image/gif",      // .gif
                "image/bmp",      // .bmp
                "image/tiff",     // .tiff, .tif
                "image/svg+xml",  // .svg
                "image/heic",     // .heic (common on iOS devices)
                "image/heif",     // .heif (common on iOS devices)
                "image/avif"      // .avif (newer format with good compression)
        );

        /*
        TODO: HEIC may need additional server-side processing libraries.
        TODO: May need to limit sizes for each file types.
        TODO: SVG may be consider, but need some security validation as they contains executable code.
        */

        return allowMimeTypes.contains(fileType.toLowerCase());
    }

    // Delete any old files if changed (i.e. profile picture change).
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Extract fileName from URL.
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();

            Files.deleteIfExists(targetLocation);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to delete file: " + fileUrl, ex);
        }
    }
}
