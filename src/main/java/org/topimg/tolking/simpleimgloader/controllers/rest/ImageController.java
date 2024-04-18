package org.topimg.tolking.simpleimgloader.controllers.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.topimg.tolking.simpleimgloader.entities.Image;
import org.topimg.tolking.simpleimgloader.services.ImageService;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService service;
    private final Logger logger = LoggerFactory.getLogger(ImageController.class);

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<Image> searchImage(@RequestParam("description") String desc) {
        return service.fullTextSearch(desc);
    }

    @GetMapping("/all")
    public List<Image> getAllImages() {
        return service.getAll();
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveImage(
            @RequestParam("description") String description,
            @RequestParam("file_id") String fileId) {

        String name = service.saveImage(description, fileId);
        String infoMsg = "Image saved successfully: " + name;

        logger.info(infoMsg);
        return new ResponseEntity<>(infoMsg, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam("name") String name) {
        String infoMsg = "Image deleted successfully: " + name;

        service.deleteImage(name);

        logger.info(infoMsg);
        return new ResponseEntity<>(infoMsg, HttpStatus.OK);
    }

}
