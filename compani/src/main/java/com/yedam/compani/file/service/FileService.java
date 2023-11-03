package com.yedam.compani.file.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yedam.compani.issue.file.service.IssueFileVO;

public interface FileService {
	
	// 전체보기
	public List<FileVO> fileList(String pSearch, String pKeyword, int prjtNo);
	
	// 조회
	public FileVO fileInfo(Integer FileNO);
	
	// 파일 등록
	public int fileInsert(final FileVO fileVO);
	
	// 삭제
	public int fileDelete(Integer fileNo);
	
	// 선택삭제
	public int fileSelDel(List<Integer> FileNo);
	
	// 업무 모달 파일함 검색기능
	public Page<FileVO> fileList(int pageNo, String search, String keywordFile, int bussNo );
	
	// 드라이브 업로드
	public void driveFileInsert(List<FileVO> files);
}
