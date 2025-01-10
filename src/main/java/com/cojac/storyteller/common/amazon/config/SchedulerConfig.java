package com.cojac.storyteller.common.amazon.config;

import com.cojac.storyteller.common.amazon.AmazonS3Service;
import com.cojac.storyteller.common.amazon.domain.S3DeleteFile;
import com.cojac.storyteller.common.amazon.dto.S3DeleteFileDTO;
import com.cojac.storyteller.common.amazon.repository.S3DeleteFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final S3DeleteFileRepository s3DeleteFileRepository;
    private final AmazonS3Service amazonS3Service;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void deleteFilesFromS3() {
        List<S3DeleteFile> filesToBeDeleted = s3DeleteFileRepository.findAll();

        List<S3DeleteFileDTO> s3FileDeletionInfo = filesToBeDeleted.stream()
                .map(file -> S3DeleteFileDTO.create(file.getFilePath(), file.getFileUid()))
                .toList();


        s3DeleteFileRepository.deleteAll(filesToBeDeleted);
        amazonS3Service.deleteFilesS3(s3FileDeletionInfo);
    }

}
