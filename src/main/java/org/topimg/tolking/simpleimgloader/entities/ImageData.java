package org.topimg.tolking.simpleimgloader.entities;

import org.springframework.core.io.ByteArrayResource;

public class ImageData {
    private final ByteArrayResource resource;
    private final String mimeType;

    public ImageData(ByteArrayResource resource, String mimeType) {
        this.resource = resource;
        this.mimeType = mimeType;
    }

    public ByteArrayResource getResource() {
        return resource;
    }

    public String getMimeType() {
        return mimeType;
    }
}
