package com.isc.astd.web.validator;

import com.isc.astd.domain.File;
import com.isc.astd.repository.FileRepository;
import com.isc.astd.service.dto.FileBaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class FileListNumValidator implements ConstraintValidator<FileListNumConstraint, FileBaseDTO> {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public boolean isValid(FileBaseDTO fileDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(fileDTO.getListNum())) {
            return true;
        }

        final List<File> files;
        final List<File.BranchType> branchTypes = Arrays.asList(File.BranchType.DEFAULT, File.BranchType.APPROVED);
        final List<File.Status> statuses = Arrays.asList(File.Status.REJECTED, File.Status.APPROVED, File.Status.REFERENCE);

        if (fileDTO.getId() == null || fileDTO.getId() < 0 || fileDTO.getStatus() == File.Status.APPROVED || fileDTO.getStatus() == File.Status.REFERENCE || fileDTO.getStatus() == File.Status.REJECTED) {
            files = fileRepository.findAllByDocIdAndBranchTypeInAndListNumAndStatusNotIn(fileDTO.getDocId(), branchTypes, fileDTO.getListNum(), statuses);
        } else {
            files = fileRepository.findAllByDocIdAndBranchTypeInAndListNumAndIdNotAndStatusNotIn(fileDTO.getDocId(), branchTypes, fileDTO.getListNum(), fileDTO.getId(), statuses);
        }
//        return files.stream().allMatch(file -> file.getId().equals(fileBaseDTO.getId()));
        return files.isEmpty();
    }
}
