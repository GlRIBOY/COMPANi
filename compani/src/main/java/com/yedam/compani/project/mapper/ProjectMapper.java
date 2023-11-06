package com.yedam.compani.project.mapper;

import java.util.List;
import java.util.Map;

import com.yedam.compani.business.service.BusinessVO;
import com.yedam.compani.member.service.MemberVO;
import com.yedam.compani.project.service.ProjectVO;

public interface ProjectMapper {
	//전체조회
	public List<ProjectVO> selectAllProject(MemberVO memberVO);
	public List<Map<Object,Object>> selectProjectAndMemberList(String membId);
	public List<ProjectVO> getProjectStateList(ProjectVO projectVO);
	public int updateFavorite(ProjectVO projectVO); 
	public int updateBusiness(BusinessVO businessVO);
	public int insertProject(ProjectVO projectVO);
	public Map<Object,Object> projectSelect(Integer prjtNo);
	public int updateProject(ProjectVO project);
}
