package com.yedam.compani.file.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yedam.compani.file.mapper.FileMapper;
import com.yedam.compani.file.service.FileService;
import com.yedam.compani.file.service.FileVO;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	FileMapper filemapper;
	
	// 파일 리스트 확인
	@Override
	public List<FileVO> fileList() {
	
		return filemapper.fileList();
	}
	
	// 조회??;
	@Override
	public FileVO fileInto(FileVO fileVO) {

		return filemapper.fileInfo(fileVO);
	}
	
	// 파일 등록
	@Override
	public int getfileInsert(FileVO fileVO) {

		return filemapper.fileInsert(fileVO);
	}
	
	// 파일 검색
	@Override
	public List<FileVO> fileSearch(FileVO fileVO) {
		
		return filemapper.fileSearch(fileVO);
	}
	
	// 파일 삭제
	@Override
	public int fileDelete(Integer fileNo) {
		
		return filemapper.fileDelete(fileNo);
	}

	@Override
	public int fileSelDel(List<Integer> files) {
		for(int i=0; i<files.size(); i++) {
			filemapper.fileDelete(files.get(i));
		}
		return files.size();
	}

}

