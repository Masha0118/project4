package com.mysite.sbb.File;

import com.mysite.sbb.File.FileAttachmentRepository;
import com.mysite.sbb.File.FileAttachments;
import com.mysite.sbb.myhistory.History;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileAttachmentRepository fileAttachmentRepository;

    public List<FileAttachments> saveFiles(List<MultipartFile> files, History history, String author) throws IOException {
        String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return files.stream().map(file -> {
            try {
                // 업로드된 파일 이름(원본 이름)
                String originalFileName = file.getOriginalFilename();
                // 작성자, 날짜, 원본 파일 이름으로 파일 이름 생성
                String uniqueFileName = generateFileName(originalFileName, author, dateString);
                Path filePath = Paths.get(uploadDir, uniqueFileName);  // Paths -> 경로 문자열을 Path 객체로 반환하는 메서드 제공

                // 파일 저장
                Files.copy(file.getInputStream(), filePath);

                // 파일 첨부 정보를 DB에 저장
                FileAttachments fileAttachments = new FileAttachments();
                fileAttachments.setFileName(uniqueFileName);  // Save the unique file name
                fileAttachments.setFilePath(filePath.toString());
                fileAttachments.setFileSize(file.getSize());
                fileAttachments.setHistory(history);

                return fileAttachmentRepository.save(fileAttachments);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
            }
        }).collect(Collectors.toList());
    }

    private String generateFileName(String originalFileName, String author, String dateString) {
        String fileExtension = getFileExtension(originalFileName);
        String baseName = getBaseName(originalFileName);

        // 작성자, 날짜 및 원본 파일 이름으로 생성
        return String.format("%s_%s_%s%s", dateString, author, baseName, fileExtension);
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}
