package com.yedam.compani.project.status.service.impl;

import com.yedam.compani.project.status.mapper.ProjectStatusMapper;
import com.yedam.compani.project.status.service.ProjectStatusService;
import com.yedam.compani.project.status.service.ProjectStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectStatusServiceImpl implements ProjectStatusService {

    @Autowired
    ProjectStatusMapper projectStatusMapper;

    @Override
    public boolean insert(int prjtNo) {
        return (projectStatusMapper.insert(prjtNo) == 1);
    }

    @Override
    public ProjectStatusVO getProjectStatus(Integer prjtNo) {
        return projectStatusMapper.selectProjectStatus(prjtNo);
    }
}
