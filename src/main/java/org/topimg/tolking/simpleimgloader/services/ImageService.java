package org.topimg.tolking.simpleimgloader.services;

import jakarta.persistence.EntityManager;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.topimg.tolking.simpleimgloader.entities.Image;
import org.topimg.tolking.simpleimgloader.entities.ImageData;
import org.topimg.tolking.simpleimgloader.repos.ImageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final EntityManager entityManager;

    public ImageService(ImageRepository repository, EntityManager entityManager) throws IOException {
        this.repository = repository;
        this.entityManager = entityManager;
        // Ensure that PATH directory exists
        createDirectoryIfNotExists();
    }

    public List<Image> search(String desc) {
        return repository.searchImagesByDescriptionContainingIgnoreCase(desc);
    }

    public List<Image> fullTextSearch(String desc) {
        return entityManager.createNativeQuery("select * from Image img where to_tsvector(img.description) @@ plainto_tsquery(:first) OR img.description like :second", Image.class)
                .setParameter("first", desc)
                .setParameter("second", "%"+desc+"%")
                .getResultList();
    }

    public ImageData getImageFromLocal(String name) throws IOException {
        byte[] imageByte;
        String mime;
        imageByte = Files.readAllBytes(Paths.get(DIR_NAME,name));

        mime = getFileExtension(name);
        // Response contentType doesn't support jpg
        if (Objects.equals(mime, "jpg")) {
            mime = "jpeg";
        }
        return new ImageData(new ByteArrayResource(imageByte), mime);
    }

    public void saveImage(String description, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Basic file type validation
        String originalFilename = file.getOriginalFilename();
        String mime = getFileExtension(originalFilename);
        if (!isValidFileType(originalFilename)) {
            throw new IOException("Not supported file type: " + mime);
        }

        String newFileName = UUID.randomUUID() + "." + mime;
        File newFile = Paths.get(DIR_NAME,newFileName).toFile();
        InputStream inputStream = file.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(newFile);
        try {
            IOUtils.copy(inputStream, outputStream);
            repository.insertImage(description, newFileName);
        } catch (IOException e) {
            throw new IOException("Failed to save file.", e);
        }

    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidFileNameException("Invalid", "File is null or does not contain an extension.");
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
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
