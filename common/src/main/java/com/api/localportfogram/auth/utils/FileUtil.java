package com.api.localportfogram.auth.utils;


import com.api.localportfogram.Image.entity.ImageEntity;
import com.api.localportfogram.exception.dto.BadRequestException;
import com.api.localportfogram.exception.dto.ExceptionEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@Component
public class FileUtil {

    public byte[] getFile(ImageEntity imageEntity) {
        String uploadDir = "C:\\ProtfoGram\\PortfoGram\\src\\main\\resources\\static\\images\\";
        File file = new File(uploadDir +  imageEntity.getFileName());
         byte[] byfile = null;

        try{
            byfile = Files.readAllBytes(file.toPath());
        }catch (Exception e){
            throw new BadRequestException(ExceptionEnum.RESPONSE_INTERNAL_SEVER_ERROR,e.getMessage());
        }

        return byfile;
    }
    public File saveFile(MultipartFile multipartFile, String name) {
        String uploadDir = "C:\\ProtfoGram\\PortfoGram\\src\\main\\resources\\static\\images\\";

        File uploadFile = new File(uploadDir + name);
        try {
            multipartFile.transferTo(uploadFile);
        } catch (Exception e) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_INTERNAL_SEVER_ERROR, e.getMessage());
        }

        return uploadFile;
    }




}