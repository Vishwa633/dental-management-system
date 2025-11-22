package com.example_SE_Dental_Management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * This method configures a resource handler to serve static files (the uploaded X-rays)
     * from a directory on the file system.
     * It maps the URL path "/xray-images/**" to the "uploads/xrays/" directory.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = "file:" + projectRoot + "/uploads/xrays/";

        System.out.println("Configuring static resource handler for: " + uploadPath);

        registry.addResourceHandler("/xray-images/**")
                .addResourceLocations(uploadPath);

        // Emergency X-ray images
        String emergencyUploadPath = "file:" + projectRoot + "/uploads/emergency-xrays/";
        System.out.println("Configuring static resource handler for emergency: " + emergencyUploadPath);
        registry.addResourceHandler("/emergency-xray-images/**")
                .addResourceLocations(emergencyUploadPath);

        String profileUploadPath = "file:" + projectRoot + "/uploads/profiles/";
        System.out.println("Configuring static resource handler for profiles: " + profileUploadPath);
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations(profileUploadPath);
    }


}
