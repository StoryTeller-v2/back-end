package com.cojac.storyteller.common.amazon.eventHandler;

import com.cojac.storyteller.common.amazon.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class S3RollbackHandler {

    private final AmazonS3Service amazonS3Service;

    /**
     * AFTER_ROLLBACK: 트랜잭션 실패하여 롤백되면 감지해 해당 메서드 실행
     * amazonS3Service를 활용해 해당 경로의 S3 객체 삭제
     * @param event S3 객체에 관한 정보가 담긴 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollbackS3(UploadS3Event event) {
        amazonS3Service.deleteS3(event.objectPath());
    }
}

