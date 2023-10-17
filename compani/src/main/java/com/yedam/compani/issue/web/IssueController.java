package com.yedam.compani.issue.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.yedam.compani.issue.service.IssueService;
import com.yedam.compani.issue.service.IssueVO;
import com.yedam.compani.paging.SearchDto;

@Controller
public class IssueController {

	@Autowired
	IssueService issueService;

	@GetMapping("/ModalIssueList")
	public String modalIssueList(@ModelAttribute SearchDto search, @ModelAttribute SearchDto keyword,
			@RequestParam(required = false, defaultValue = "1") int pageNum, Model model) throws Exception {
		PageInfo<IssueVO> issues = new PageInfo<>(issueService.getIssueList(pageNum, search), 8);
		model.addAttribute("issue", issues);
		model.addAttribute("issues", issueService.getIssueList(pageNum, search));
		model.addAttribute("search", search);
		model.addAttribute("keyword", keyword);
		System.out.println(issueService.getIssueList(pageNum, search));
		return "index";
	}

}
