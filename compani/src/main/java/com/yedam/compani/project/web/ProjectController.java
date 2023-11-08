package com.yedam.compani.project.web;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yedam.compani.business.service.BusinessService;
import com.yedam.compani.business.service.BusinessVO;
import com.yedam.compani.issue.service.IssueService;
import com.yedam.compani.issue.service.IssueVO;
import com.yedam.compani.member.feedback.service.MemberFeedbackService;
import com.yedam.compani.member.feedback.service.MemberFeedbackVO;
import com.yedam.compani.member.service.MemberService;
import com.yedam.compani.member.service.MemberVO;
import com.yedam.compani.project.member.service.ProjectMemberService;
import com.yedam.compani.project.member.service.ProjectMemberVO;
import com.yedam.compani.project.service.ProjectFormVO;
import com.yedam.compani.project.service.ProjectService;
import com.yedam.compani.project.service.ProjectVO;
import com.yedam.compani.project.status.service.ProjectStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;
	private final BusinessService businessService;
	private final ProjectMemberService projectMemberService;
	private final MemberFeedbackService memberFeedbackService;
	private final IssueService issueService;
	private final MemberService memberService;
	private final ProjectStatusService projectStatusService;

	@GetMapping("/project/home/{prjtNo}")
	public String projectHome(@PathVariable Integer prjtNo, Model model, HttpServletRequest request) {
		List<List<String>> businessStateList = businessService.getBusinessStateList(prjtNo);
		List<Map<Object, Object>> businessLevelList = businessService.getBusinessAndLevelList(prjtNo);
		List<Map<Object, Object>> memberStatusList = projectMemberService.getBusinessCompleteStatus(prjtNo);
		

		model.addAttribute("businessStateList", businessStateList);
		model.addAttribute("businessLevelList", businessLevelList);
		model.addAttribute("memberStatusList", memberStatusList);

		// 헤더 단건 조회
		Map<Object, Object> projectVO = projectService.projectSelect(prjtNo);
		request.getSession().setAttribute("projectVO", projectVO);
		request.getSession().setAttribute("prjtNo", prjtNo);
		
		return "project/project-home";
	}
	
	@GetMapping("/project/modal/{prjtNo}")
	public String projectModal(@PathVariable Integer prjtNo, Model model, HttpServletRequest request) {
		// 프로젝트 모달 수정 - 참여자 리스트
		List<Map<String,String>> prjtMemberList =  projectMemberService.projectMemberList(prjtNo);
		model.addAttribute("projectMemberList", prjtMemberList);
		
		// 프로젝트 모달 수정 - 회사 멤버 리스트
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loginInfo");
		String membId = memberVO.getMembId();
		String coCd = memberVO.getCoCd();
		
		ProjectMemberVO projectMemberVO = projectMemberService.projectMemberSelect(prjtNo, membId);
	
		model.addAttribute("projectMemberVO", projectMemberVO);
		
		List<MemberVO> memberList = memberService.prjtMemberList(prjtNo,coCd);
		model.addAttribute("memberList", memberList);
		
		return "modal/modal-project";
	}


	@GetMapping("home")
	public String mainhomeList(Model model, HttpServletRequest request) {
	
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loginInfo");
		List<ProjectVO> list = projectService.getProjectList(memberVO);
		model.addAttribute("projectList", list);
		List<MemberFeedbackVO> list2 = memberFeedbackService.getMemberFeedbackList(memberVO);
		model.addAttribute("memberFeedbackList", list2);
		List<BusinessVO> list3 = businessService.getBusinessList(memberVO);
		model.addAttribute("businessList", list3);
		List<IssueVO> list4 = issueService.getIssueList(memberVO);
		model.addAttribute("issueList", list4);

		return "home";
	}

	@PostMapping("ProjectStateAjax")
	@ResponseBody
	public Map<String, Object> ProjectStateAjax(ProjectVO projectVO, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loginInfo");
		projectVO.setMembId(memberVO.getMembId());
		System.out.println(projectVO);
		List<ProjectVO> List = projectService.getProjectStateList(projectVO);
		map.put("projectStateList", List);
		
		return map;
	}

	@PostMapping("favAjax")
	@ResponseBody
	public Map<String, Object> favAjax(ProjectVO projectVO, HttpServletRequest request) {

		projectService.updateFavorite(projectVO);
		Map<String, Object> map = new HashMap<>();
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loginInfo");
		projectVO.setMembId(memberVO.getMembId());

		List<ProjectVO> projectStateList = projectService.getProjectStateList(projectVO);
		map.put("project", projectStateList);
		
		return map;
	}

	//프로젝트 등록 - 실행시 
	@PostMapping("/insertProject")
	@ResponseBody
	public Map<String, Object> insertProject(@RequestBody ProjectFormVO formVO, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		projectService.insertProject(formVO.getProject());
		projectMemberService.insertProjectMember(formVO);
		
		// project 추가 후 sidebar project list 재설정
		setProjectSidebarList(request);

		return map;
	}

	@PostMapping("/updateProject")
	@ResponseBody
	public void updateProject(@RequestBody ProjectFormVO formVO, HttpServletRequest request) {
		ProjectVO projectVO = formVO.getProject();
		projectService.updateProject(projectVO);
		
		// 프로젝트 완료 시, 통계 측정
		if (projectVO.stateCheckEnd()) {
			projectStatusService.insert(projectVO.getPrjtNo());
		}
		
		projectMemberService.deleteProjectMember(formVO);
		projectMemberService.insertProjectMember(formVO);

		// 프로젝트 수정 시 사이드바 재설정
		setProjectSidebarList(request);
	}
	
	//회사 프로젝트 게시판 
	@GetMapping("/company/project/{cocd}")
	public String projectBoard(Model model) {
		
		return "company/company-project";
	}

	private void setProjectSidebarList(HttpServletRequest request) {
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loginInfo");
		List<Map<Object, Object>> projectList = projectService.getProjectAndMemberList(memberVO.getMembId());
		request.getSession().setAttribute("projectList", projectList);
	}
}
