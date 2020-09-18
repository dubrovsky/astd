package com.isc.astd.service;

import com.isc.astd.config.ApplicationProperties;
import com.isc.astd.domain.Catalog;
import com.isc.astd.domain.Doc;
import com.isc.astd.domain.File;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileSystemService {

    private final ApplicationProperties properties;

    public FileSystemService(ApplicationProperties properties) {
        this.properties = properties;
    }

    public void writeFile(MultipartFile file, Doc doc, Long fileId) throws IOException {
        Path path = getFilePath(file.getOriginalFilename(), fileId, doc);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
    }

    public void deleteFile(File file, Doc doc) throws IOException {
        Path path = getFilePath(file.getName(), file.getId(), doc);
        Files.deleteIfExists(path);
    }

    public byte[] readFile(File file) throws IOException {
        Path path = getFilePath(file.getName(), file.getId(), file.getDoc());
        return Files.readAllBytes(path);
    }

    public Path getFilePath(String fileName, Long fileId, Doc doc) {
        Path path = getCatalogPath(doc.getCatalog()).resolve(String.valueOf(doc.getId())).resolve(fileId + "_" + fileName);
        return Paths.get(properties.getBasePath()).resolve(path);
    }

    public Path getCatalogPath(Catalog catalog, int level) {
        List<Long> catalogIds = new ArrayList<>();
        catalogIds.add(catalog.getId());
        while(catalog.getLevel() != level){
            catalogIds.add(catalog.getParentCatalog().getId());
            catalog = catalog.getParentCatalog();
        }
        Collections.reverse(catalogIds);
        return Paths.get(catalogIds.stream().map(Object::toString).collect(Collectors.joining(FileSystems.getDefault().getSeparator())));
    }

    public Path getCatalogPath(Catalog catalog) {
        return getCatalogPath(catalog, 0);
    }

    public void deleteCatalog(Catalog catalog) throws IOException {
        Path path = Paths.get(properties.getBasePath()).resolve(getCatalogPath(catalog));
        delete(path);
    }

    public void deleteDoc(Doc doc) throws IOException {
        Path path = Paths.get(properties.getBasePath()).resolve(getCatalogPath(doc.getCatalog())).resolve(String.valueOf(doc.getId()));
        delete(path);
    }

    private void delete(Path path) throws IOException {
        if(Files.exists(path)) {
            Files.walkFileTree(path,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.deleteIfExists(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.deleteIfExists(file);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        }
    }

    public void moveDoc(Catalog schemeCatalog, Catalog targetCatalog, long docId) throws IOException {
        Path pathToMove = Paths.get(properties.getBasePath()).resolve(getCatalogPath(schemeCatalog)).resolve(String.valueOf(docId));
        Path targetPath = Paths.get(properties.getBasePath()).resolve(getCatalogPath(targetCatalog)).resolve(getCatalogPath(schemeCatalog, 2)).resolve(String.valueOf(docId));
        Files.createDirectories(targetPath);
        move(pathToMove, targetPath);
    }

    private void move(Path pathToMove, Path targetPath) throws IOException {
        Files.walkFileTree(pathToMove,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.move(pathToMove.resolve(file.getFileName()), targetPath.resolve(file.getFileName()));
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }

    public void copyDoc(Doc srcDoc, Doc destDoc) throws IOException {
        Path targetPath = Paths.get(properties.getBasePath()).resolve(getCatalogPath(destDoc.getCatalog())).resolve(String.valueOf(destDoc.getId()));
        Files.createDirectories(targetPath);
        for (File srcFile : srcDoc.getFiles()) {
            File destFile = destDoc.getFiles().stream().filter(file -> file.getHash().equals(srcFile.getHash())).findFirst().orElseThrow(() -> new RuntimeException("File with this hash not found"));
            Files.copy(getFilePath(srcFile.getName(), srcFile.getId(), srcDoc), getFilePath(destFile.getName(), destFile.getId(), destDoc));
        }
    }

    public void copyFile(File srcFile, File destFile) throws IOException {
        Path targetPath = Paths.get(properties.getBasePath()).resolve(getCatalogPath(destFile.getDoc().getCatalog())).resolve(String.valueOf(destFile.getDoc().getId()));
        Files.createDirectories(targetPath);
        Files.copy(getFilePath(srcFile.getName(), srcFile.getId(), srcFile.getDoc()), getFilePath(destFile.getName(), destFile.getId(), destFile.getDoc()));
    }

    public String removeExtension(Path filePath) {
        return filePath.normalize().getFileName().toString().replaceFirst("[.][^.]+$", "");
    }
}
