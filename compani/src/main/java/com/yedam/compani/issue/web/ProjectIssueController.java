package com.yedam.compani.issue.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yedam.compani.issue.file.service.IssueFileService;
import com.yedam.compani.issue.file.service.IssueFileVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.yedam.compani.issue.service.IssueService;
import com.yedam.compani.issue.service.IssueVO;
import com.yedam.compani.paging.SearchDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProjectIssueController {

	private final IssueService issueService;
	private final IssueFileService issueFileService;

	@GetMapping("/project/issues/{prjtNo}")
	public String projectIssueList(@PathVariable int prjtNo, String search, String keyword,
			@RequestParam(required = false, defaultValue = "1") int pageNum, Model model) {
		PageInfo<IssueVO> issues = new PageInfo<>(issueService.getProjectIssueList(pageNum, search, keyword, prjtNo),
				8);
		Page<IssueVO> vo = issueService.getProjectIssueList(pageNum, search, keyword, prjtNo);

		model.addAttribute("projectIssue", issues);
		model.addAttribute("prjoectIssueList", vo);
		model.addAttribute("search", search);

		return "project/project-issue";
	}

	// 프로젝트 내 이슈 리스트 조회 (Ajax)
	@GetMapping("/project/aissues/{prjtNo}")
	@ResponseBody
	public Map<String, Object> projectIssue(@PathVariable int prjtNo, String search, String keyword,
			@RequestParam(required = false, defaultValue = "1") int pageNum) {
		PageInfo<IssueVO> issues = new PageInfo<>(issueService.getProjectIssueList(pageNum, search, keyword, prjtNo),
				8);
		Page<IssueVO> vo = issueService.getProjectIssueList(pageNum, search, keyword, prjtNo);
		Map<String, Object> map = new HashMap<>();

		map.put("pissue", issues);
		map.put("projectIssueList", vo);

		return map;
	}

	// 프로젝트 내 이슈 단건 조회 + 해당 이슈에 대한 모든 파일 조회
	@GetMapping("/project/issues/{prjtNo}/{issuNo}")
	public String projectIssueSelect(@PathVariable final int prjtNo, @PathVariable final int issuNo, Model model) {

		IssueVO vo = issueService.findIssueById(issuNo);
		model.addAttribute("issueInfo", vo);

		List<IssueFileVO> list = issueFileService.findAllFileByIssuNo(issuNo);
		model.addAttribute("issueFile", list);

		return "project/project-issue-info";
	}

	// 프로젝트게시판 내 이슈 삭제
	@PostMapping("/project/issues/del")
	@ResponseBody
	public String projectIssueDelete(@RequestParam final int issuNo) {
		issueService.deleteIssue(issuNo);
		return "project/project-issue";
	}
}
