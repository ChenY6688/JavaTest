package com.example.xlsxprocessor.controller;

import com.example.xlsxprocessor.exception.FileProcessingException;
import com.example.xlsxprocessor.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/document")
public class FileUploadController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(excelService.processExcelFile(file));
    }
}
