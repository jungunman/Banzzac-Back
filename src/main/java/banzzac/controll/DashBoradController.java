package banzzac.controll;


import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import banzzac.dto.DashBoardDTO;
import banzzac.dto.MemberDTO;
import banzzac.dto.PageDTO;
import banzzac.mapper.AdminMapper;
import banzzac.service.MemberManagementService;
import banzzac.utill.CommonLayout;
import banzzac.utill.SelectTitle;
import jakarta.annotation.Resource;

@Controller
@RequestMapping("/dashboard")
public class DashBoradController {
	
	/** 페이징 사용 법 ==> 
	 * 1. 페이징이 필요한 메소드 매개변수에 PageDTO 선언 
	 * 2. @PathVariable을 쓰든, param을 쓰든 이름을 무조건 currentPage로 작성 
	 * 3. 해당 테이블의 총 갯수를 구해오는 매퍼 작성
	 * 4. pageDTO.setTotalPage에 셋팅
	 * 5. List 가져오는 매퍼에 limit #{searchNo}, #{listCnt} 추가
	 * 6. HTML 원하는 부위에 
	 <div th:replace="~{inc/paging(${pageDTO.currentPage},
	  	${pageDTO.prevBlock}, ${pageDTO.nextBlock},
	   	${pageDTO.minPage}, ${pageDTO.maxPage},
	    ${pageDTO.isNextBtn} )}"></div> 
	   	추가 하면 페이징 적용 완료
	   	이후 페이지 가져오는 갯수를 늘리고 싶을경우 ?listCnt=원하는 값 으로 가져오게 만드시면 됩니다.
	   	*/
	@Resource
	private PageDTO pageDTO;
	@Resource
	private DashBoardDTO dashBoardDTO;
	@Resource
	private AdminMapper mapper;
	
	@ModelAttribute
	public CommonLayout getCommonData(CommonLayout cl , SelectTitle title) {
		cl.setFolder("dash");
		cl.setService("board");
		title.selectTitle(cl.getService());
		System.out.println(title);
		return cl;
	}
	@ModelAttribute
	public PageDTO getCommonData(@Param("currentPage") PageDTO paging) {
		pageDTO = paging;
		return pageDTO;
	}
	
	@GetMapping("")
	public String goToDashBoard(Model mm) {
		mm.addAttribute("sales", mapper.calculateDailyPay());
		mm.addAttribute("event", mapper.getTodayEvent());
		mm.addAttribute("register", mapper.getTodayRegister());
		mm.addAttribute("report", mapper.getOutstandingReport());
		mm.addAttribute("refund", mapper.getTodayRefund());
		mm.addAttribute("rank", mapper.getWeekPaymentRank());
		return "template";
	}
	
	
	
}
