package com.likelionsns.final_project.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.likelionsns.final_project.config.AwsConstants.ORIGIN_POST_FOLDER;
import static com.likelionsns.final_project.config.AwsConstants.ORIGIN_USER_FOLDER;
import static com.likelionsns.final_project.exception.ErrorCode.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadUserOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_USER_FOLDER);
    }

    public String uploadPostOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_POST_FOLDER);
    }

    public String upload(MultipartFile file, String bucket, String folder) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String originalFileName = file.getOriginalFilename();

        // 파일 형식 체크
        FileUtils.checkFileFormat(originalFileName);

        // 파일 생성
        String key = FileUtils.makeFileName(originalFileName, folder);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new SnsAppException(FILE_UPLOAD_ERROR, FILE_UPLOAD_ERROR.getMessage());
        }

        String storedFileUrl = amazonS3Client.getUrl(bucket, key).toString();

        return storedFileUrl;
    }

    public void deleteUserImage(String originFileName) {
        delete(ORIGIN_USER_FOLDER + "/" + originFileName, bucket);
    }

    public void deletePostImage(String originFileName) {
        delete(ORIGIN_POST_FOLDER + "/" + originFileName, bucket);
    }

    public void delete(String filePath, String bucket) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }


}