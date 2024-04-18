package org.topimg.tolking.simpleimgloader.services;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.multipart.MultipartFile;
import org.topimg.tolking.simpleimgloader.entities.Image;
import org.topimg.tolking.simpleimgloader.entities.ImageData;
import org.topimg.tolking.simpleimgloader.repos.ImageRepository;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {
    private static final String DIR_NAME = "images";
    private final ImageRepository repository;

    public ImageService(ImageRepository repository) throws IOException {
        this.repository = repository;

        createDirectoryIfNotExists();
    }

    public List<Image> fullTextSearch(String desc) {
        return repository.searchFullText(desc);
    }

    public List<Image> getAll() {
        return repository.findAll();
    }

    public ImageData getImageFromLocal(String name) throws FileNotFoundException {
        byte[] imageByte;
        String mime;
        try {
            imageByte = Files.readAllBytes(Paths.get(DIR_NAME, name));
        } catch (IOException e) {
            throw new FileNotFoundException("File not found: "+name);
        }

        mime = getFileExtension(name);
        // Response contentType doesn't support jpg
        if (Objects.equals(mime, "jpg")) {
            mime = "jpeg";
        }

        return new ImageData(new ByteArrayResource(imageByte), mime);
    }

    public void saveImage(String description, MultipartFile file) throws IOException {
        String mime = validateFileAndGetMime(file);

        String newFileName = UUID.randomUUID() + "." + mime;
        File newFile = Paths.get(DIR_NAME, newFileName).toFile();

        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(newFile)) {
            IOUtils.copy(inputStream, outputStream);

            repository.save(new Image(newFileName, description));
        } catch (IOException e) {
            throw new IOException("Failed to save file.", e);
        }

    }


    public void deleteImage(String name) throws IOException {
        try {
            Path path = Paths.get(DIR_NAME, name);

            Files.delete(path);
            repository.deleteImageByNameEqualsIgnoreCase(name);
        } catch (IOException e) {
            throw new IOException("Failed to delete the file: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidFileNameException("Invalid", "File is null or does not contain an extension.");
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String validateFileAndGetMime(MultipartFile file) throws FileSystemException {
        if (file.isEmpty()) {
            throw new FileSystemException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        String mime = getFileExtension(originalFilename);

        if (!isValidFileType(originalFilename)) {
            throw new InvalidMimeTypeException(mime,"Not supported file type");
        }

        return mime;
    }

    private boolean isValidFileType(String fileName) {
        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList(allowedExtensions).contains(extension);
    }

    private void createDirectoryIfNotExists() throws IOException {
        Path path = Paths.get(DIR_NAME);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }


}
