package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.storage.images-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Dossier de stockage initialisé : {}", this.fileStorageLocation);
        } catch (IOException ex) {
            // Ici on garde IllegalStateException car c'est au démarrage de l'app (pas lors d'une requête HTTP)
            throw new IllegalStateException("Impossible de créer le dossier de stockage des fichiers.", ex);
        }
    }

    public String sauvegarderFichier(MultipartFile file) throws IOException {
        // NOUVEAU : On vérifie si le fichier est vide -> 400 Bad Request
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Impossible de sauvegarder un fichier vide.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // On utilise IllegalArgumentException au lieu de RuntimeException -> 400 Bad Request
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Le nom du fichier contient un chemin invalide : " + originalFilename);
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        String nouveauNom = UUID.randomUUID() + (extension != null ? "." + extension : "");

        Path targetLocation = this.fileStorageLocation.resolve(nouveauNom);

        // Si ça plante ici (disque plein), l'IOException remonte au Controller
        // et ton @ExceptionHandler(Exception.class) renverra un 500 Internal Server Error
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return nouveauNom;
    }

    public Resource chargerFichierCommeRessource(String nomFichier) {
        try {
            Path filePath = this.fileStorageLocation.resolve(nomFichier).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Fichier introuvable -> IllegalArgumentException -> 400 Bad Request
                throw new IllegalArgumentException("L'image est introuvable ou illisible : " + nomFichier);
            }
        } catch (MalformedURLException ex) {
            // Mauvais formatage d'URL -> IllegalArgumentException -> 400 Bad Request
            throw new IllegalArgumentException("Erreur de format de chemin pour l'image : " + nomFichier, ex);
        }
    }

    public void supprimerFichier(String nomFichier) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(nomFichier).normalize();
        // Si le fichier ne peut pas être supprimé, l'IOException remontera (500)
        Files.deleteIfExists(filePath);
    }
}
