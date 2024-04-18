package org.topimg.tolking.simpleimgloader.services;

import org.springframework.stereotype.Service;
import org.topimg.tolking.simpleimgloader.entities.Image;
import org.topimg.tolking.simpleimgloader.repos.ImageRepository;

import java.util.List;

@Service
public class ImageService {
    private final ImageRepository repository;

    public ImageService(ImageRepository repository) {
        this.repository = repository;
    }

    public List<Image> fullTextSearch(String desc) {
        return repository.searchFullText(desc);
    }

    public List<Image> getAll() {
        return repository.findAll();
    }

    public String saveImage(String description, String fileId) {
        return repository.save(new Image(fileId, description)).getDescription();
    }

    public long deleteImage(String name) {
        return repository.deleteImageByFileIdEqualsIgnoreCase(name);
    }

}
