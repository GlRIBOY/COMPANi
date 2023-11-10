package com.yedam.compani.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yedam.compani.business.member.service.BusinessMemberService;
import com.yedam.compani.business.service.BusinessService;
import com.yedam.compani.business.service.BusinessVO;
import com.yedam.compani.business.service.FormVO;
import com.yedam.compani.member.service.MemberService;
import com.yedam.compani.member.service.MemberVO;
import com.yedam.compani.project.member.service.ProjectMemberService;
import com.yedam.compani.session.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
/*
 * 작성자 : 신대철
 *   기능 : 업무 등록시 ,업무 등록 , 상세보기 , 수정 
 */
public class BusinessModalController {

	private final BusinessService businessService;
	private final MemberService memberService;
	private final BusinessMemberService businessMemberService;
	private final SessionService sessionService;
	
	@GetMapping("/modal/business/insert")
	public String businessModalInsertHome(Model model, HttpServletRequest request) {
		int prjtNo = sessionService.getProjectNo(request);
		
		// 회사 멤버 list -> 프로젝트 참여자 list
		List<MemberVO> projectMemberList = memberService.projectMemberList(prjtNo);
		model.addAttribute("projectMemberList", projectMemberList);

		List<BusinessVO> busineesNameList = businessService.bussinessNameList(prjtNo);
		model.addAttribute("businessNameList", busineesNameList);

		return "modal/modal-business-insert";
	}

	@PostMapping("insertBusiness")
	@ResponseBody
	public Map<String, Object> insertBusiness(@RequestBody FormVO formVO) {

		Map<String, Object> map = new HashMap<>();

		businessService.insertBusiness(formVO.getBusiness()); 
		businessMemberService.insertBusinessMember(formVO);
		
		// 이거 한번 고민 해보자구 mapper 설계
		if (!formVO.getBusiness().getBussDep().equals("")) {
			businessService.updateBusiness(formVO.getBusiness());
		}

		return map;

	}

	//업무 상세보기 
	@GetMapping("/businessInfo/{bussNo}")
	public String businessInfo(@PathVariable Integer bussNo ,Model model, HttpServletRequest request) {

		// 업무 단건
		BusinessVO bussVO = businessService.businessSelect(bussNo);
		model.addAttribute("businessVO", bussVO);
		
		// 업무 참여자 list
		List<MemberVO> list = businessMemberService.bussMemberList(bussNo);
		model.addAttribute("businessMemberList", list);
		
		int prjtNo = sessionService.getProjectNo(request);
		// 회사 멤버 list -> 프로젝트 참여자 list
		List<MemberVO> projectMemberList = memberService.projectMemberList(prjtNo);
		model.addAttribute("projectMemberList", projectMemberList);

		//종속 변경할 업무 리스트
		List<BusinessVO> busineesNameList = businessService.bussinessNameList(prjtNo);
		model.addAttribute("businessNameList", busineesNameList);
		 
		return "modal/modal-business";
	}

	@PostMapping("/updateBusiness")
	@ResponseBody
	public void updateBusiness(@RequestBody FormVO formVO) {
		
		//업무 수정 
		businessService.modifyBusiness(formVO.getBusiness());
		//종속 변경
		businessService.updateBusiness(formVO.getBusiness()); 
		
		//참여자 변경
		businessMemberService.deleteBusinessMember(formVO);
		businessMemberService.insertBusinessMember(formVO);
	
	}
}
