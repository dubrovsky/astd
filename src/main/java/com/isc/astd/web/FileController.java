package com.isc.astd.web;

import com.isc.astd.domain.File;
import com.isc.astd.service.FileService;
import com.isc.astd.service.dto.*;
import com.isc.astd.web.commons.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping()
    public ResponseEntity<Response<FileBaseDTO>> getAllFiles(@RequestParam long docId, File.BranchType branchType, PageableDTO pageableDTO, @AuthenticationPrincipal User principal) throws IOException {
        PageRequestDTO<FileBaseDTO> fileDTOS = fileService.getAllFiles(docId, branchType, principal, pageableDTO);
        return ResponseEntity.ok(new Response<>(fileDTOS.getContent(), fileDTOS.getTotalElements()));
    }

    @PostMapping()
    public ResponseEntity<Response<FileDTO>> saveFile(FileDTO dto, @AuthenticationPrincipal User principal) throws Exception {
        if (dto.getFile().isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }
        FileDTO fileDTO;
        if(dto.getId() < 0){
            fileDTO = fileService.createFile(dto, principal);
        } else {
            if(dto.getStatus() == File.Status.APPROVED || dto.getStatus() == File.Status.REFERENCE || dto.getStatus() == File.Status.REJECTED) {
                fileDTO = fileService.newVersionFile(dto, principal);
            }
            else {
                fileDTO = fileService.updateFile(dto, principal);
            }
        }
        return ResponseEntity.ok(new Response<>(Collections.singletonList(fileDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteFile(@PathVariable("id") long id, @AuthenticationPrincipal User principal) throws IOException {
        fileService.deleteFile(id, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable("id") long id) throws IOException {
        return getFileContent(id, "inline");
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") long id) throws IOException {
        return getFileContent(id, "attachment");
    }

    private ResponseEntity<byte[]> getFileContent(long id, String attachType) throws IOException {
        FileViewDTO file = fileService.viewFile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(file.getSize());
        headers.set(HttpHeaders.CONTENT_TYPE, file.getContentType());
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, attachType + "; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, attachType + "; filename=" + URLEncoder.encode(fileService.generateFileName(file.getId()), "UTF-8"));
        return ResponseEntity.ok().headers(headers).body(file.getFile());
    }

    @GetMapping("/ecp")
    public ResponseEntity<Response<EcpHashDTO>> getEcpData(@RequestParam("id") long fileId, @AuthenticationPrincipal User principal) throws Exception {
        EcpHashDTO ecpDTO = fileService.getEcpData(fileId, principal);
        return ResponseEntity.ok(new Response<>(ecpDTO));
    }

    @PostMapping("/ecp/{id}")
    public ResponseEntity<Response<EcpHashDTO>> saveEcp(EcpDTO ecp, @PathVariable("id") long fileId, @AuthenticationPrincipal User principal) throws Exception {
        fileService.saveEcp(fileId, principal, ecp);
        return ResponseEntity.ok(new Response<>());
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity rejectFile(RejectFileDTO rejectFile, @PathVariable("id") long fileId, @AuthenticationPrincipal User principal) {
        fileService.rejectFile(fileId, principal, rejectFile);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/cancel/{id}")
    public ResponseEntity rejectCancelFile(@PathVariable("id") long fileId, @AuthenticationPrincipal User principal) {
        fileService.rejectCancelFile(fileId, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/archive/{id}")
    public ResponseEntity archiveFile(@PathVariable("id") long fileId) throws Exception {
        fileService.archiveFile(fileId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/archive/cancel/{id}")
    public ResponseEntity archiveCancelFile(@PathVariable("id") long fileId) throws Exception {
        fileService.archiveCancelFile(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{direction}/{id}")
    public ResponseEntity<Response<FileBaseDTO>> getVersion(@PathVariable("id") long fileId, @PathVariable("direction") String direction, @AuthenticationPrincipal User principal) {
        FileBaseDTO fileDTO = fileService.getVersion(fileId, direction, principal);
        return ResponseEntity.ok(new Response<>(fileDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<FileBaseDTO>> getFile(@PathVariable("id") long fileId, @AuthenticationPrincipal User principal) {
        FileBaseDTO fileDTO = fileService.getFileById(fileId, principal);
        return ResponseEntity.ok(new Response<>(fileDTO));
    }

    @PostMapping("/paper/{id}")
    public ResponseEntity<Response<EcpHashDTO>> savePaperChange(@PathVariable("id") long fileId, @RequestParam("value") boolean value, @RequestParam("field") String fieldName) {
        fileService.savePaperChange(fileId, value, fieldName);
        return ResponseEntity.ok(new Response<>());
    }
}
