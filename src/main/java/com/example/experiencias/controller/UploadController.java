package com.example.experiencias.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.experiencias.exception.BadRequestException;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final String BASE_DIR = "uploads";

    @PostMapping("/{folder}")
    public ResponseEntity<?> upload(@PathVariable String folder,
                                    @RequestParam("file") MultipartFile file) {

        if (!List.of("experiencias", "categorias", "usuarios").contains(folder)) {
            throw new BadRequestException("Carpeta no válida");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Solo imágenes");
        }

        try {
            String original  = file.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String filename  = UUID.randomUUID() + extension;

            Path path = Paths.get(BASE_DIR, folder, filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return ResponseEntity.ok(Map.of("url", "/uploads/" + folder + "/" + filename));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al subir archivo");
        }
    }
}
