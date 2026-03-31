package com.eventify.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud.name}")
    private String cloudName;

    @Value("${cloudinary.api.key}")
    private String apiKey;

    @Value("${cloudinary.api.secret}")
    private String apiSecret;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("resource_type", "auto");
        uploadParams.put("folder", "eventify");

        if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().contains("profile")) {
            uploadParams.put("transformation", 
                "c_fill,h_150,w_150,g_face,ar_1:1.0,b_rgb:ffffff,z_0");
        } else {
            uploadParams.put("transformation", 
                "c_fill,h_600,w_1200,q_auto,f_auto");
        }

        // Simulate upload result for now
        return "https://res.cloudinary.com/" + cloudName + "/image/upload/" + 
               System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }

    public void deleteImage(String publicId) {
        // Simulate deletion
        System.out.println("Deleting image: " + publicId);
    }

    public String extractPublicId(String imageUrl) {
        // Extract public ID from Cloudinary URL
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.split("\\.")[0];
    }
}
