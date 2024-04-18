package org.topimg.tolking.simpleimgloader.controllers.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.topimg.tolking.simpleimgloader.entities.Image;
import org.topimg.tolking.simpleimgloader.entities.ImageData;
import org.topimg.tolking.simpleimgloader.services.ImageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService service;
    private final Logger logger = LoggerFactory.getLogger(ImageController.class);

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImageByDesc(@PathVariable("imageName") String name)
            throws IOException {
        ImageData imageData = service.getImageFromLocal(name);
        ByteArrayResource imageByte = imageData.getResource();
        String mimeType = imageData.getMimeType();

        logger.info("GET IMAGE FILE: {}", name);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(imageByte.contentLength())
                .contentType(MediaType.valueOf("image/"+mimeType))
                .body(imageByte);
    }



    @GetMapping("/search")
    public List<Image> searchImage(@RequestParam("description") String desc){
        logger.info("SEARCH IMAGES by PROMPT: {}", desc);
        return service.fullTextSearch(desc);
    }
    @GetMapping("/all")
    public List<Image> getAllImages(){
        logger.info("GET all images");
        return service.getAll();
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveImage(@RequestParam("description") String description,
                            @RequestParam("image") MultipartFile file) {
        try {
            service.saveImage(description, file);
        } catch (IOException e) {
            logger.warn("FAILED TO SAVE IMAGE: {}", description);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        logger.info("SAVE IMAGE: {}", description);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Image saved successfully");
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam("name") String name) {
        try {
            service.deleteImage(name);
        } catch (IOException e) {
            logger.warn("FAILED TO DELETE IMAGE: {}", name);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        logger.info("DELETED IMAGE: {}", name);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Image deleted successfully");
    }

}
