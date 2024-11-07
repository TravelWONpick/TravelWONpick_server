package wonpick.travel.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // 고유한 파일명 생성
        String key = "event_img/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        // MultipartFile을 임시 파일로 변환
        File file = convertMultiPartToFile(multipartFile);

        // S3에 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ) // ACL 설정 추가
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        // 업로드된 파일의 URL 반환
        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toExternalForm();

        // 임시 파일 삭제
        if (file.exists()) {
            Files.delete(file.toPath());
        }

        return fileUrl;
    }

    // 파일 삭제 메서드
    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // URL에서 S3 키 추출
    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf("uploads/")); // 경로의 시작이 uploads/일 경우
    }

    // MultipartFile을 임시 파일로 변환
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;
    }
}
