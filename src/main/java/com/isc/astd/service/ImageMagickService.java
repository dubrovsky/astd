package com.isc.astd.service;

import com.isc.astd.config.ApplicationProperties;
import com.isc.astd.service.dto.FileViewDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Transactional
public class ImageMagickService {

    private static final String DIFF_SUFFIX = "_DIFF";
    private static final String DIFF_EXTENSION = ".gif";
    private static final String DIFF_PAGE = "[0]";
    private static final String MIME_GIF = "image/gif";

    private final FileSystemService fileSystemService;
    private final ApplicationProperties applicationProperties;

    public ImageMagickService(FileSystemService fileSystemService, ApplicationProperties applicationProperties) {
        this.fileSystemService = fileSystemService;
        this.applicationProperties = applicationProperties;
    }

    public FileViewDTO compareFiles(Path filePath1, Path filePath2) throws IOException, InterruptedException {
        String targetFileName = fileSystemService.removeExtension(filePath1) + DIFF_SUFFIX + DIFF_EXTENSION;
        ApplicationProperties.ImageMagick imageMagickProps = applicationProperties.getImageMagick();
        ProcessBuilder processBuilder = new ProcessBuilder(
                imageMagickProps.getOsPath(),
                "convert",
                "-density",
                imageMagickProps.getConvert().getDensity(),
                "-delay",
                imageMagickProps.getConvert().getDelay(),
                filePath1.normalize().toString() + DIFF_PAGE,
                filePath2.normalize().toString() + DIFF_PAGE,
                "-loop",
                imageMagickProps.getConvert().getLoop(),
                targetFileName
        );
        processBuilder.directory(filePath1.getParent().normalize().toFile());
        processBuilder.redirectErrorStream(true);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
        byte[] bytes = Files.readAllBytes(filePath1.getParent().resolve(targetFileName).normalize());
        FileViewDTO fileViewDTO = new FileViewDTO();
        fileViewDTO.setFile(bytes);
        fileViewDTO.setSize(bytes.length);
        fileViewDTO.setContentType(MIME_GIF);
        fileViewDTO.setName(targetFileName);
        return fileViewDTO;
    }
}
