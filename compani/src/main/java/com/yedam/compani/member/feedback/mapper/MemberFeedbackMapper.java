package com.yedam.compani.member.feedback.mapper;

import java.util.List;
import java.util.Map;

import com.yedam.compani.member.feedback.service.MemberFeedbackVO;

public interface MemberFeedbackMapper {
	public List<MemberFeedbackVO> getMemberFeedbackList();
	
	public List<MemberFeedbackVO> selectFeedbackList(MemberFeedbackVO vo);
	
	public int insertFeedbackPersonal(MemberFeedbackVO vo);
	
	public List<Map<String, Object>> selectBusinessPersonal(Map<String, String> map);
}
