package com.smartcampus.incidents.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Persists ticket evidence images with strict validation (type, size, safe names, path traversal checks).
 */
@Service
public class TicketAttachmentStorageService {

    public static final String FILES_URL_PREFIX = "/api/tickets/files/";

    private static final int MAX_FILES = 3;
    private static final long MAX_BYTES_PER_FILE = 5 * 1024 * 1024L;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    /** Stored filenames only: uuid.ext — prevents path traversal and arbitrary names. */
    public static final Pattern STORED_FILENAME_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|jpeg|png|gif|webp)$"
    );

    private final Path uploadRoot;

    public TicketAttachmentStorageService(
            @Value("${app.ticket-upload.dir:${user.dir}/data/ticket-uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void ensureDirectory() throws IOException {
        Files.createDirectories(uploadRoot);
    }

    /**
     * Validates and stores up to {@value #MAX_FILES} non-empty image parts; returns public API paths
     * (e.g. {@value #FILES_URL_PREFIX}{uuid}.png).
     */
    public List<String> storeImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        List<MultipartFile> nonEmpty = files.stream().filter(f -> f != null && !f.isEmpty()).toList();
        if (nonEmpty.size() > MAX_FILES) {
            throw new IllegalArgumentException("At most " + MAX_FILES + " images are allowed");
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : nonEmpty) {
            if (file.getSize() > MAX_BYTES_PER_FILE) {
                throw new IllegalArgumentException("Each image must be at most 5 MB");
            }
            String ext = extensionFromFilename(file.getOriginalFilename());
            if (ext == null || !ALLOWED_EXTENSIONS.contains(ext)) {
                throw new IllegalArgumentException("Only JPEG, PNG, GIF, or WebP images are allowed");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type");
            }
            byte[] data = file.getBytes();
            if (data.length > MAX_BYTES_PER_FILE) {
                throw new IllegalArgumentException("Each image must be at most 5 MB");
            }
            if (data.length < 12) {
                throw new IllegalArgumentException("Invalid image file");
            }
            if (!magicBytesMatchImage(data, ext)) {
                throw new IllegalArgumentException("File content does not match an allowed image type");
            }

            String storedName = UUID.randomUUID().toString().toLowerCase(Locale.ROOT) + "." + ext;
            Path target = uploadRoot.resolve(storedName).normalize();
            if (!target.startsWith(uploadRoot)) {
                throw new IllegalStateException("Invalid storage path");
            }
            Files.write(target, data);
            urls.add(FILES_URL_PREFIX + storedName);
        }
        return urls;
    }

    public Path resolveExistingFile(String filename) {
        if (filename == null || !STORED_FILENAME_PATTERN.matcher(filename).matches()) {
            return null;
        }
        Path p = uploadRoot.resolve(filename).normalize();
        if (!p.startsWith(uploadRoot) || !Files.isRegularFile(p)) {
            return null;
        }
        return p;
    }

    private static String extensionFromFilename(String original) {
        if (original == null || original.isBlank()) {
            return null;
        }
        String name = original.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        String base = slash >= 0 ? name.substring(slash + 1) : name;
        int dot = base.lastIndexOf('.');
        if (dot < 0 || dot == base.length() - 1) {
            return null;
        }
        return base.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean magicBytesMatchImage(byte[] b, String ext) {
        return switch (ext) {
            case "jpg", "jpeg" -> b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case "png" -> b.length >= 8
                    && b[0] == (byte) 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47
                    && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A;
            case "gif" -> b.length >= 6
                    && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                    && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
            case "webp" -> b.length >= 12
                    && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }
}
