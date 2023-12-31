package com.yedam.compani.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yedam.compani.issue.file.service.IssueFileVO;

/*
 * 작성자: 권오준
 * 작성일자: 2023/11/10
 * 내용: 파일 업로드, 수정, 삭제
 */
public class FileUtils {	
	private static String uploadPath = Paths.get("/home/ec2-user/upload/issue-files").toString();

    /**
     * 다중 파일 업로드
     *
     * @param multipartFiles - 파일 객체 List
     * @return DB에 저장할 파일 정보 List
     */
    public static List<IssueFileVO> uploadFiles(final List<MultipartFile> multipartFiles) {
        List<IssueFileVO> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                continue;
            }
            files.add(uploadFile(multipartFile));
        }
        return files;
    }

    /**
     * 단일 파일 업로드
     *
     * @param multipartFile - 파일 객체
     * @return DB에 저장할 파일 정보
     */
    public static IssueFileVO uploadFile(final MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String saveName = generateSaveFilename(multipartFile.getOriginalFilename());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")).toString();
        String uploadPath = getUploadPath(today) + File.separator + saveName;
        File uploadFile = new File(uploadPath);

        try {
            multipartFile.transferTo(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  IssueFileVO.builder()
                .issuFileNm(multipartFile.getOriginalFilename())
                .issuFilePath(saveName)
                .issuFileSize(multipartFile.getSize())
                .build();
    }

    /**
     * 저장 파일명 생성
     *
     * @param filename 원본 파일명
     * @return 디스크에 저장할 파일명
     */
    private static String generateSaveFilename(final String filename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }

    /**
     * 업로드 경로 반환
     *
     * @return 업로드 경로
     */
    private static String getUploadPath() {
        return makeDirectories(uploadPath);
    }

    /**
     * 업로드 경로 반환
     *
     * @param addPath - 추가 경로
     * @return 업로드 경로
     */
    private static String getUploadPath(final String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    /**
     * 업로드 폴더(디렉터리) 생성
     *
     * @param path - 업로드 경로
     * @return 업로드 경로
     */
    private static String makeDirectories(final String path) {
        File dir = new File(path);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return dir.getPath();
    }

    /**
     * 다운로드할 첨부파일(리소스) 조회 (as Resource)
     *
     * @param file - 첨부파일 상세정보
     * @return 첨부파일(리소스)
     */
    public static Resource readFileAsResource(final IssueFileVO file) {
        String uploadedDate = file.getIssuFileDt().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String filename = file.getIssuFilePath();
        Path filePath = Paths.get(uploadPath, uploadedDate, filename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() == false || resource.isFile() == false) {
                throw new RuntimeException("file not found : " + filePath.toString());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("file not found : " + filePath.toString());
        }
    }

    /**
     * 파일 삭제 (from Disk)
     *
     * @param files - 삭제할 파일 정보 List
     */
    public static void deleteFiles(final List<IssueFileVO> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (IssueFileVO file : files) {
            String uploadedDate = file.getIssuFileDt().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
            deleteFile(uploadedDate, file.getIssuFilePath());
        }
    }

    /**
     * 파일 삭제 (from Disk)
     *
     * @param addPath  - 추가 경로
     * @param filename - 파일명
     */
    private static void deleteFile(final String addPath, final String filename) {
        String filePath = Paths.get(uploadPath, addPath, filename).toString();
        deleteFile(filePath);
    }

    /**
     * 파일 삭제 (from Disk)
     *
     * @param filePath - 파일 경로
     */
    private static void deleteFile(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
