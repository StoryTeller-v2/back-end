package com.cojac.storyteller.common.amazon.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "s3_delete_queue")
@NoArgsConstructor
public class S3DeleteFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date_time", nullable = false)
    private LocalDateTime createdDateTime; // 생성일시

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath; // 파일 경로

    @Column(name = "file_uid", nullable = false, length = 255)
    private String fileUid; // 파일 식별자

    /**
     * S3DeleteQueue 객체 생성
     */
    public static S3DeleteFile create(String filePath, String fileUid) {
        S3DeleteFile queue = new S3DeleteFile();
        queue.filePath = filePath;
        queue.fileUid = fileUid;
        queue.createdDateTime = LocalDateTime.now();
        return queue;
    }
}
