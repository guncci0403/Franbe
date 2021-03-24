package kr.or.ddit.project.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.common.model.PageVo;
import kr.or.ddit.common.model.SearchVo;
import kr.or.ddit.contract.model.MeetingVo;
import kr.or.ddit.contract.service.ContractService;
import kr.or.ddit.project.model.PAttendVo;
import kr.or.ddit.project.model.ProjectVo;
import kr.or.ddit.project.service.ProjectService;
import kr.or.ddit.user.model.MessageVo;
import kr.or.ddit.user.model.UserVo;
import kr.or.ddit.user.service.MessageService;
import kr.or.ddit.user.service.UserService;

@Controller
@RequestMapping("project")
public class ProjectController {

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@Resource(name = "projectService")
	private ProjectService projectService;

	@Resource(name = "contractService")
	private ContractService contractService;
	
	@Resource(name = "messageService")
	private MessageService messageService;

	@Resource(name = "userService")
	private UserService userService;

	@RequestMapping("selectProject")
	public String project(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int pageSize,
			Model model, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		PageVo pageVo = new PageVo(page, pageSize);

		model.addAllAttributes(projectService.PagingProject(pageVo));

		return "t/project/projectList";
	}

	@RequestMapping(path = "insertProject", method = RequestMethod.GET)
	public String insertproject(Model model, UserVo userVo, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		return "t/project/insertProject";
	}

	@RequestMapping(path = "insertProject", method = RequestMethod.POST)
	public String insertproject(Model model, ProjectVo projectVo, String ps_no) {
		String str = ps_no;
		String[] array = str.split(",");

		int insertCnt = projectService.insertProject(projectVo);
		for (int i = 0; i < array.length; i++) {
			String ps = array[i];
			projectService.insertPskill(ps);
		}
		if (insertCnt == 1) {
			return "redirect:/project/selectProject";
		}
		return "t/project/insertProject";
	}

	@RequestMapping(path="viewProject", method=RequestMethod.GET)
	public String viewproject(Model model, int p_code, HttpSession session) {      // () 안에 있는건 받아오는 값들
		model.addAttribute("alarmList", messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if(((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList", projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if(((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList", projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();
		
		PAttendVo pattend = new PAttendVo();
		pattend.setP_code(p_code);
		pattend.setUser_id(user_id);
  
		model.addAttribute("project", projectService.viewProject(p_code));
		model.addAttribute("applicant", projectService.applicantCnt(p_code));
		model.addAttribute("pstate", projectService.selectPstate(pattend));
		model.addAttribute("pskill", projectService.listPskill(p_code));
  
		return "t/project/viewProject";
	}

	@RequestMapping(path = "insertLike", method = RequestMethod.POST)
	public String insertlike(Model model, PAttendVo pattendVo, RedirectAttributes ra) {

//		logger.debug("pattend : {}", pattendVo);

		int insertCnt = projectService.insertLike(pattendVo);
//		logger.debug("cnt : {}", insertCnt);
		if (insertCnt == 1) {
			ra.addFlashAttribute("msg", "관심등록되었습니다.");
			return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
		}
		return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
	}

	@RequestMapping(path = "deleteLike", method = RequestMethod.POST)
	public String deletelike(Model model, PAttendVo pattendVo, RedirectAttributes ra) {

		logger.debug("delete패턴vo : {}", pattendVo);
		int deleteCnt = projectService.deletelike(pattendVo);

		if (deleteCnt == 1) {
			ra.addFlashAttribute("msg", "삭제되었습니다.");
			return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
		}

		return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
	}

	@RequestMapping(path = "insertApply", method = RequestMethod.POST)
	public String insertapply(Model model, PAttendVo pattendVo, RedirectAttributes ra) {

		logger.debug("pattend : {}", pattendVo);

		int insertCnt = projectService.insertApply(pattendVo);
		logger.debug("cnt : {}", insertCnt);
		if (insertCnt == 1) {
			ra.addFlashAttribute("msg", "지원완료.");
			return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
		}
		return "t/project/projectList";
	}

	@RequestMapping(path = "updateApply", method = RequestMethod.POST)
	public String updateapply(Model model, PAttendVo pattendVo, RedirectAttributes ra) {

		logger.debug("pattend : {}", pattendVo);

		int updateCnt = projectService.updateApply(pattendVo);
		logger.debug("cnt : {}", updateCnt);
		if (updateCnt == 1) {
			ra.addFlashAttribute("msg", "지원완료.");
			return "redirect:/project/viewProject?p_code=" + pattendVo.getP_code();
		}
		return "project/updateapply";
	}

	@RequestMapping("searchProject")
	public String searchProject(Model model, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, String sT, String kW, RedirectAttributes ra,
			HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		logger.debug("kW : {} , sT : {} ", kW, sT);
		Map<String, Object> map = null;

		PageVo vo = new PageVo(page, pageSize, kW);
		if (kW.equals("") || sT.equals("")) {
			ra.addAttribute("page", page);
			ra.addAttribute("pageSize", pageSize);
			return "redirect:/project/selectProject";
		} else {
			if (sT.equals("i")) {
				map = projectService.searchProjectid(vo);
			} else if (sT.equals("p")) {
				map = projectService.searchProjectnm(vo);
			}
			int cnt = (int) map.get("cnt");
			model.addAttribute("projectList", map.get("projectList"));
			model.addAttribute("pagination", (int) Math.ceil((double) cnt / pageSize));
			model.addAttribute("pageVo", vo);
			model.addAttribute("sT", sT);
			model.addAttribute("kW", kW);
			return "t/project/projectList";
		}
	}

	// 지원자 모집중 관심 등록 리스트
	@RequestMapping("selectLikeList")
	public String selectlikelist(Model model, ProjectVo projectvo, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}

		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();

		model.addAttribute("likeList", projectService.selectLike(user_id));

		return "t/project/selectLikeList";
	}

	// 지원자 모집중 관심 등록 리스트 마감된 프로젝트 삭제
	@RequestMapping(path = "deleteLikeList", method = RequestMethod.POST)
	public String deletelikeList(Model model, PAttendVo pattendVo, RedirectAttributes ra) {

		int deleteCnt = projectService.deletelike(pattendVo);

		if (deleteCnt == 1) {
			ra.addFlashAttribute("msg", "삭제되었습니다.");
			return "redirect:/project/selectLikeList";
		}
		return "t/project/selectLikeList";
	}

	// 지원내역 리스트
	@RequestMapping("applyList")
	public String applyList(Model model, ProjectVo projectvo, HttpSession session) {
		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();
		model.addAttribute("alarmList", messageService.alarmMessage(user_id));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}

		model.addAttribute("applyList", projectService.applyList(user_id));

		return "t/project/applyList";
	}

	@RequestMapping(path = "deleteApplyList", method = RequestMethod.POST)
	public String deletapplyList(Model model, PAttendVo pattendVo, RedirectAttributes ra) {
		int deleteCnt = projectService.deletelike(pattendVo);

		if (deleteCnt == 1) {
			ra.addFlashAttribute("msg", "삭제되었습니다.");
			return "redirect:/project/applyList";
		}
		return "project/applyList";
	}

	@RequestMapping("ingProjectList")
	public String ingprojectlist(Model model, ProjectVo projectvo, HttpSession session) {

		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();
		model.addAttribute("alarmList", messageService.alarmMessage(user_id));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		String purpose = ((UserVo) session.getAttribute("S_USER")).getPurpose();
		logger.debug("ㅎㅎ : {}", purpose);
		if (purpose.equals("C")) {
			model.addAttribute("ingProjectList", projectService.ingProjectListC(user_id));
			logger.debug("나는 C : {}", purpose);
		} else {
			model.addAttribute("ingProjectList", projectService.ingProjectListP(user_id));
			logger.debug("나는 P : {}", purpose);
		}
		return "t/project/ingProjectList";
	}

	@RequestMapping("finishProjectList")
	public String finishprojectlist(Model model, ProjectVo projectvo, HttpSession session) {

		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();
		model.addAttribute("alarmList", messageService.alarmMessage(user_id));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		String purpose = ((UserVo) session.getAttribute("S_USER")).getPurpose();

		if (purpose.equals("C")) {
			model.addAttribute("finishProjectList", projectService.finishProjectListC(user_id));
		} else {
			model.addAttribute("finishProjectList", projectService.finishProjectListP(user_id));
		}
		return "t/project/finishProjectList";
	}

	@RequestMapping("beforeProjectList")
	public String beforeprojectlist(Model model, ProjectVo projectvo, HttpSession session) {

		String user_id = ((UserVo) session.getAttribute("S_USER")).getUser_id();
		model.addAttribute("alarmList", messageService.alarmMessage(user_id));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		model.addAttribute("beforeprojectList", projectService.beforeProject(user_id));

		return "t/project/beforeProjectList";
	}

	/**
	 * 수진
	 */
	// 지원자 모집중
	@RequestMapping("selectUserProject")
	public String selectUserProject(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, String user_id, Model model, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		ProjectVo pageVo = new ProjectVo(page, pageSize, user_id);
		model.addAllAttributes(projectService.selectUserProject(pageVo));
		return "t/project/recruitment";
	}

	// 지원자 조회
	@RequestMapping("viewPattendUser")
	public String viewPattendUser(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, int p_code, String user_id, Model model,
			RedirectAttributes ra, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		PageVo pageVo = new PageVo(page, pageSize);
		pageVo.setP_code(p_code);

		List<UserVo> userList = (List<UserVo>) projectService.viewPattendUser(pageVo).get("userList");
		Map<String, Object> map = projectService.viewPattendUser(pageVo);
		if (userList.isEmpty()) {
			ra.addFlashAttribute("msg", "지원자가 없습니다.");
			return "redirect:/project/selectUserProject?user_id=" + user_id;
		}

		model.addAllAttributes(projectService.viewPattendUser(pageVo));
		return "t/project/recruitmentUser";

	}

	// 마감완료 변경 - 상태 04
	@RequestMapping(path = "updateProjectState", method = RequestMethod.GET)
	public String updateProjectState(Model model, ProjectVo projectVo, RedirectAttributes ra) {
		int updateCnt = projectService.updateProjectState(projectVo);
		if (updateCnt == 1) {
			ra.addFlashAttribute("msg", "프로젝트가 마감되었습니다.");
			return "redirect:/project/selectUserProject?user_id=" + projectVo.getUser_id();
		}
		return "t/project/selectUserProject";
	}

	// 지원자 수락
	@RequestMapping(path = "projectOk", method = RequestMethod.POST)
	public String projectOk(Model model, PAttendVo pattendVo, String user_nm, RedirectAttributes ra, HttpSession session, MeetingVo meeting) {
		String client = ((UserVo)session.getAttribute("S_USER")).getUser_id();
		logger.debug(client);
		int updateCnt = projectService.updateProjectOk(new PAttendVo(pattendVo.getP_code(), pattendVo.getUser_id()));
		if (updateCnt == 1) {
			meeting.setPtn_id(pattendVo.getUser_id());
			contractService.insertContract(meeting);
			logger.debug(" 여기 : {} ", meeting);
			
			ra.addFlashAttribute("msg", user_nm + "님 수락 되었습니다.");
			projectService.projectStateMsg(new MessageVo(
					"안녕하세요 클라이언트 수락 요청입니다. <br> 프로젝트에 참가하게 되었습니다  <div class='font-icon-list p-2 border mx-1 mb-2'>"
							+ "<a href='http://localhost:80/project/viewProject?p_code=" + pattendVo.getP_code()
							+ "'>참가하는 프로젝트 보기</a></div>",
					pattendVo.getUser_id(), client));
		}
		return "redirect:/project/viewPattendUser?p_code=" + pattendVo.getP_code();
	}

	// 지원자 거절
	@RequestMapping(path = "projectNo", method = RequestMethod.POST)
	public String projectNo(Model model, PAttendVo pattendVo, HttpSession session, RedirectAttributes ra) {
		
		String client = ((UserVo)session.getAttribute("S_USER")).getUser_id();
		
		int updateCnt = projectService.updateProjectNo(new PAttendVo(pattendVo.getP_code(), pattendVo.getUser_id()));
		if (updateCnt == 1) {
			ra.addFlashAttribute("msg", "거절 되었습니다.");
			projectService.projectStateMsg(
					new MessageVo("안타깝지만 요청한 프로젝트에 거절되었습니다.<br> 다음에 다시 지원 부탁드립니다.", pattendVo.getUser_id(), client));
		}
		return "redirect:/project/viewPattendUser?p_code=" + pattendVo.getP_code();
	}

	// 필터 검색 - 금액
	@RequestMapping(path = "searchFilterPrice", method = RequestMethod.GET)
	public String searchFilterPrice(Model model, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "0") String st,
			@RequestParam(defaultValue = "") String end, String state, RedirectAttributes ra, HttpSession session) {
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		if(st.equals("0") && end.equals("") ) {
			return "redirect:/project/selectProject";
		}
		PageVo vo = new PageVo(page, pageSize);
		if (!st.equals("0") && end.equals("") ) {
			logger.debug("1");
			vo.setStart(st+ "0000");
			model.addAttribute("state", "price");
			model.addAllAttributes(projectService.searchFilterPrice(vo));
//			return "t/project/projectSearch";
		} else if (!st.equals("") && !end.equals("")) {
			vo.setStart(st+"0000");
			vo.setEnd(end+"0000");
			logger.debug("2");
			model.addAttribute("state", "price");
			model.addAllAttributes(projectService.searchFilterPrice(vo));
//			return "t/project/projectSearch";
		} else if ((st.equals("0") && !end.equals(""))) {
			vo.setStart(st+"0000");
			vo.setEnd(end+"0000");
			logger.debug("3");
			model.addAttribute("state", "price");
			model.addAllAttributes(projectService.searchFilterPrice(vo));
		}
//		model.addAttribute("st", st.substring(0, st.length() - 4));
//		model.addAttribute("end", end.substring(0, end.length() - 4));
		logger.debug("st : {} , end : {} ", st, end);
		return "t/project/projectSearch";
//		}
	}

	// 필터 검색 - 기간
	@RequestMapping(path = "searchFilterPreiod", method = RequestMethod.GET)
	public String searchFilterPreiod(Model model, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "0") String st,
			@RequestParam(defaultValue = "") String end, String state, RedirectAttributes ra, HttpSession session) {

		logger.debug("st : {} , end : {} ", st, end);

		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		PageVo vo = new PageVo(page, pageSize);
		if (st.equals("") && end.equals("")) {
			logger.debug("1");
			vo.setStart(st);
			model.addAttribute("state", "preiod");
			model.addAllAttributes(projectService.searchFilterPreiod(vo));
//			return "t/project/projectSearch";
		} else if (!st.equals("") && !end.equals("")) {
			vo.setStart(st);
			vo.setEnd(end);
			logger.debug("2");
			model.addAttribute("state", "preiod");
			model.addAllAttributes(projectService.searchFilterPreiod(vo));
//			return "t/project/projectSearch";
		} else if ((!st.equals("") && end.equals("") || st.equals("0") && !end.equals(""))) {
			vo.setStart(st);
			vo.setEnd(end);
			logger.debug("3");
			model.addAttribute("state", "preiod");
			model.addAllAttributes(projectService.searchFilterPreiod(vo));
		}
		return "t/project/projectSearch";
	}

	// 필터 검색 - 페이징
	@RequestMapping("searchFilter")
	public String searchFilterPaging(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "0") String st,
			@RequestParam(defaultValue = "") String end, String p_filed, String state) {

		logger.debug("st : {} , end : {} ", st, end);
		// 필터 - 가격 검색
		if (state.equals("price")) {
			logger.debug("진입 1");
			return "redirect:/project/searchFilterPrice?page=" + page + "&pageSize=" + pageSize + "&st=" + st + "&end="
					+ end + "&state=" + state;
		} // 체크박스 필터일 때
		else if (state.equals("filed")) {
			return "redirect:/project/searchFilterpfiled?page=" + page + "&pageSize=" + pageSize + "&p_filed=" + p_filed
					+ "&state=" + state;
		} // 필터 - 기간 검색
		else {
			return "redirect:/project/searchFilterPreiod?page=" + page + "&pageSize=" + pageSize + "&st=" + st + "&end="
					+ end + "&state=" + state;
		}
	}

	// 필터 체크박스 검색
	@RequestMapping(path = "searchFilterpfileds", method = RequestMethod.GET)
	public String searchFilterpfiled(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize, String p_filed, String state, Model model,
			HttpSession session) {
		logger.debug("p_filed{}", p_filed);
		model.addAttribute("alarmList",
				messageService.alarmMessage(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("C")) {
			model.addAttribute("pList",
					projectService.ingProjectListC(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		} else if (((UserVo) session.getAttribute("S_USER")).getPurpose().equals("P")) {
			model.addAttribute("pList",
					projectService.ingProjectListP(((UserVo) session.getAttribute("S_USER")).getUser_id()));
		}
		if (p_filed == null || p_filed.equals("")) {
			return "redirect:/project/selectProject";
		} else {
			String[] p_fileds = p_filed.split(",");
			SearchVo searchVo = new SearchVo(page, pageSize, p_fileds.length);
			if (p_fileds.length == 1) {
				searchVo.setValue1(p_fileds[0]);
				logger.debug("한개 : {}", searchVo);
			} else if (p_fileds.length == 2) {
				searchVo.setValue1(p_fileds[0]);
				searchVo.setValue2(p_fileds[1]);
				logger.debug("두개 : {}", searchVo);
			} else if (p_fileds.length == 3) {
				searchVo.setValue1(p_fileds[0]);
				searchVo.setValue2(p_fileds[1]);
				searchVo.setValue3(p_fileds[2]);
				logger.debug("세개 : {}", searchVo);
			} else if (p_fileds.length == 4) {
				searchVo.setValue1(p_fileds[0]);
				searchVo.setValue2(p_fileds[1]);
				searchVo.setValue3(p_fileds[2]);
				searchVo.setValue4(p_fileds[3]);
				logger.debug("네개 : {}", searchVo);
			}
			model.addAttribute("state", state);
			model.addAttribute("chk", p_filed);
			model.addAllAttributes(projectService.searchFilterPfileds(searchVo));
		}
		return "t/project/projectSearch";
	}

	// 사인 페이지
	@RequestMapping("signPage")
	public String signPage() {

		return "t/project/siginPage";
	}

	@RequestMapping(path="requestSend", method = RequestMethod.GET)
	public String requestSend(HttpSession session, Model model) {
		logger.debug("여기들어옴");
		model.addAttribute("projectTList", projectService.requestProjectList(((UserVo) session.getAttribute("S_USER")).getUser_id()));
				
		return "jsonView";
	}
	
	@RequestMapping(path="requestSend", method = RequestMethod.POST)
	public String requestSend(int p_code, String user_id, Model model) {
		
		int cnt = projectService.requestSend(new PAttendVo(p_code, user_id));
		model.addAttribute("cnt", cnt);
		logger.debug("요청전송cnt : {} ", cnt);
		return "jsonView";
	}
}