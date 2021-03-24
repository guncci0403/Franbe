package kr.or.ddit.manage.service;

import java.util.List;

import kr.or.ddit.project.model.ProjectVo;
import kr.or.ddit.user.model.UserVo;

public interface ManageService {
	
	// 클라이언트, 파트너스 리스트 조회
	List<UserVo> selectUserList(String purpose);
	
	// 검수 중인 프로젝트 조회
	List<ProjectVo> requestProjectList();
	
	// 프로젝트 승인
	int approvalProject(int p_code);
	
	// 프로젝트 거부
	int refusalProject(int p_code);
}