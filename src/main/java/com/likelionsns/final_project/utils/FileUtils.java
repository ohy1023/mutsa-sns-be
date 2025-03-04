package com.likelionsns.final_project.utils;


import com.likelionsns.final_project.exception.SnsAppException;

import java.util.UUID;

import static com.likelionsns.final_project.config.AwsConstants.ORIGINAL_BUCKET_NAME;
import static com.likelionsns.final_project.exception.ErrorCode.*;


public class FileUtils {

    public static void checkFileFormat(String originalFileName) {

        int index;
        try {
            index = originalFileName.lastIndexOf(".");
        } catch (StringIndexOutOfBoundsException e) {
            throw new SnsAppException(WRONG_FILE_FORMAT, WRONG_FILE_FORMAT.getMessage());
        }

        String ext = originalFileName.substring(index + 1);
        if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))) {
            throw new SnsAppException(WRONG_FILE_FORMAT, WRONG_FILE_FORMAT.getMessage());
        }
    }

    public static String makeFileName(String originalFileName, String folder) {

        int index = originalFileName.lastIndexOf(".");
        String ext = originalFileName.substring(index + 1);

        // 저장할 파일 이름
        String storedFileName = UUID.randomUUID() + "." + ext;

        // 저장할 디렉토리 경로 + 파일 이름
        return folder + "/" + storedFileName;
    }

    // 이미지 파일 이름만 추출(디렉토리까진 추출x)
    public static String extractFileName(String path) {
        int idx = path.lastIndexOf("/");

        return path.substring(idx + 1);
    }

    public static String convertBucket(String url, String bucketName) {
        return url.replaceFirst(ORIGINAL_BUCKET_NAME, bucketName);
    }

    public static String convertFolder(String url, String folder, String newFolder) {
        return url.replaceFirst(folder, newFolder);
    }
}