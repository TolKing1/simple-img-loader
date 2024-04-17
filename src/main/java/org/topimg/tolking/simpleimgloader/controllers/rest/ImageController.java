package org.topimg.tolking.simpleimgloader.controllers.rest;

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
public class ImageController {
    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImageByDesc(@PathVariable("imageName") String name) throws IOException {
        ImageData imageData = service.getImageFromLocal(name);
        ByteArrayResource imageByte = imageData.getResource();
        String mimeType = imageData.getMimeType();


        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(imageByte.contentLength())
                .contentType(MediaType.valueOf("image/"+mimeType))
                .body(imageByte);
    }



    @GetMapping("/images/search/")
    public List<Image> searchImage(@RequestParam("text") String desc){
        return service.fullTextSearch(desc);
    }

    @PostMapping("/image/save")
    public ResponseEntity<String> saveImage(@RequestParam String description,
                            @RequestParam("image")MultipartFile file) {
        try {
            service.saveImage(description, file);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Image saved successfully");
    }

}
