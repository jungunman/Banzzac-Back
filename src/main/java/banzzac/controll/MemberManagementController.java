package banzzac.controll;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import banzzac.dto.MemberDTO;
import banzzac.dto.PageDTO;
import banzzac.dto.ReportDTO;
import banzzac.mapper.AdminMapper;
import banzzac.service.MemberManagementListService;
import banzzac.service.MemberManagementService;
import banzzac.utill.CommonLayout;
import banzzac.utill.SelectTitle;
import jakarta.annotation.Resource;

@Controller
@RequestMapping("/management")
public class MemberManagementController {
	
	@Resource
	private PageDTO pageDTO;
	
	@Resource
	private AdminMapper mapper;
	
	@Autowired
	private ApplicationContext applicationContext;
	
//	@ModelAttribute
//	public CommonLayout getCommonData(CommonLayout cl, SelectTitle title) {
//		cl.setFolder("member");
//		title.selectTitle(cl.getService());
//		return cl;
//	}
	
	@ModelAttribute
	public PageDTO getCommonData(@Param("currentPage") PageDTO paging) {
		pageDTO = paging;
		return pageDTO;
	}
	
	/** Service 인터페이스를 상속받아서 Implement를 만들고 그 안에서 비즈니스 로직을 작성하신 후 결과 값을 Return 해주시면 됩니다
	 * */
	
	/*****신고 회원 목록 보기*****/
	@GetMapping("report")
	public String getReportMemberList(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("report");
		cl.setService("report");
		title.selectTitle("report");
		
		ArrayList<ReportDTO> res = mapper.getReportMemberList(pageDTO);
		
		mm.addAttribute("mainData", res);
		
		return "template";
	}
	
	/*****신고 상세보기*****/
	@GetMapping("reportDetail/{no}")
	public String reportDetail(Model mm, CommonLayout cl, SelectTitle title, @PathVariable int no) {
		cl.setFolder("report");
		cl.setService("reportDetail");
		title.selectTitle("report");
		
		System.out.println("신고하기 상세보기" + no);
		ReportDTO dto = mapper.reportDetail(no);
		mm.addAttribute("report", dto);
		
		return "template";
	}
	
	/*****신고 관리자 답변 추가 *****/
	@PostMapping("reportDetail/modify/{no}")
	public String modifyReportDetail(@ModelAttribute ReportDTO dto, @PathVariable int no) {
		dto.setReportNo(no);
		System.out.println("신고하기 관리자 답변 추가" + dto.getReportNo());
		mapper.modifyReportDetail(dto);
		
		return "redirect:/management/reportDetail/"+dto.getReportNo();
	}
	
	/*****신고된 유저 정지*****/
	@GetMapping("reportDetail/suspend/{id}/{no}")
	public String suspendMember(@PathVariable String id, @PathVariable int no) {
		System.out.println("유저 정지하기" + id);
		
		mapper.suspendMember(id);
		mapper.modifyReportStatus(no);
		
		return "redirect:/management/report";
	}
	
	/*****정지 회원 목록*****/
	@GetMapping("suspend")
	public String suspendMemberList(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("member");
		cl.setService("suspendList");
		title.selectTitle("suspend");
		System.out.println("정지 회원 목록");
		ArrayList<MemberDTO> res = mapper.getSuspendMemberList(pageDTO);
		mm.addAttribute("suspendList", res);
		return "template";
	}
	
	/*****유저 정지 해제하기*****/
	@GetMapping("changeSuspend/{id}")
	public String changeSuspend(@PathVariable String id) {
		System.out.println("유저 정지해제" + id);
		
		mapper.changeSuspendMember(id);
		
		return "redirect:/management/suspend";
	}
	
	/*****탈퇴 회원 목록*****/
	@GetMapping("withdrawal")
	public String withdrawalMemberList(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("member");
		cl.setService("withdrawalList");
		title.selectTitle("withdrawal");
		System.out.println("탈퇴 회원 목록");
		ArrayList<MemberDTO> res = mapper.getWithdrawalMemberList(pageDTO);
		mm.addAttribute("withdrawalList", res);
		return "template";
	}
	//**회원리스트*/
	@GetMapping("member")
	public String memberList(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("member");
		cl.setService("member");
		title.selectTitle("member");
		
		ArrayList<MemberDTO> res = mapper.member(pageDTO);
		mm.addAttribute("memberList", res);
		
		return "template";
	}
	//**회원정보상세보기*/
	@GetMapping("memberDetail/{no}")
	public String memberDetail(Model mm, CommonLayout cl, SelectTitle title, @PathVariable int no) {
		cl.setFolder("member");
		cl.setService("memberDetail");
		title.selectTitle("member");
		
		System.out.println("memberDetail" + no);
		MemberDTO dto = mapper.memberDetail(no);
		System.out.println("memberDetail" + dto);
		
		mm.addAttribute("member", dto);
		
		return "template";
	}
	
	//**신규멤버검수*/
	@GetMapping("newMembers")
	public String newmember(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("member");
		cl.setService("newMembers");
		title.selectTitle("newMember");
		
		ArrayList<MemberDTO> res = mapper.newmember(pageDTO);
		mm.addAttribute("newMembers", res);
		
		return "template";
	}
	//*신규멤버검수승인*/
	@GetMapping("approval/{id}")
	public String approval(@PathVariable String id) {
		System.out.println("신규유저승인" + id);
		
		mapper.approval(id);
		
		return "redirect:/management/newMembers";
	}
	
	//*신규멤버검수거절*/
	@GetMapping("refuse/{id}")
	public String refuse(@PathVariable String id) {
		System.out.println("신규유저거부" + id);
			
		mapper.refuse(id);
			
		return "redirect:/management/newMembers";
	}
	

	
	@GetMapping("periodMember")
	public String periodMember(Model mm, CommonLayout cl, SelectTitle title) {
		cl.setFolder("member");
		cl.setService("periodMember");
		title.selectTitle("periodMember");
		
		System.out.println("periodMember 진입");
		return "template";
	}

	
	//**기간별 신규멤버조회*/
	@GetMapping("periodMemberReg")
	public String periodMemberReg(@RequestParam(value="startDate") String startDate, @RequestParam(value="endDate") String endDate, RedirectAttributes redirectAttributes) {
	    ArrayList<MemberDTO> res = mapper.periodMember(startDate, endDate);
	    redirectAttributes.addFlashAttribute("member", res);
	    System.out.println("periodMemberReg 진입");
	    return "redirect:/management/periodMember";
	}
	
		
}
