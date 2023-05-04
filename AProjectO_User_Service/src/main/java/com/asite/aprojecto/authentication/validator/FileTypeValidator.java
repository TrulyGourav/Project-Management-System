package com.asite.aprojecto.authentication.validator;

import com.asite.aprojecto.authentication.annotations.FileType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {

    private String[] allowedTypes;

    @Override
    public void initialize(FileType fileType) {
        allowedTypes = fileType.value();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) {
            return true;
        }

        String fileType = file.getContentType();
        if (fileType == null) {
            return false;
        }

        for (String allowedType : allowedTypes) {
            if (fileType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
}