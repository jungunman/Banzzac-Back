package banzzac.utill;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString
public class SelectTitle {
	
	@Getter
	private String title;
	
	
	public void selectTitle(String serviceName) {
		String members = "회원 관리 - ";
		String sales = "매출 관리 - ";
		Map<String, String> titles = new LinkedHashMap<>();

		titles.put("newMembers", members+"신규 회원 검수");		
		titles.put("daily", sales+"일별 매출");
		titles.put("weekly", sales+"주간 매출");
		titles.put("monthly", sales+"월간 매출");
		titles.put("paymentList", sales+"결제회원 리스트");
		titles.put("refundList", sales+"환불신청 리스트");
		titles.put("refuse", sales+"환불 거절 리스트");
		titles.put("approve", sales+"환불 승인 리스트");

		titles.put("board", "대시보드");	
		titles.put("daily", sales+"일별 매출");
		titles.put("weekly", sales+"주간 매출");
		titles.put("monthly", sales+"월간 매출");
		titles.put("newMembers", members+"신규 회원 검수");

		titles.put("chart", sales+"매출");
		titles.put("report", "신고");
		titles.put("suspend", "정지");
		titles.put("withdrawal", "탈퇴");

		titles.put("member", "기존회원");
		titles.put("newMember", "신규회원");
		titles.put("periodMember", "기간별회원");
		
		this.title = titles.get(serviceName);
		
	}
	
	
}
