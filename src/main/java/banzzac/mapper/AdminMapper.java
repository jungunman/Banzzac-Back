package banzzac.mapper;

import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.DashBoardDTO;
import banzzac.dto.MemberDTO;
import banzzac.dto.PageDTO;
import banzzac.dto.PaymentSuccessDTO;
import banzzac.dto.ReportDTO;
import banzzac.dto.SalesManagementDTO;


@Mapper
public interface AdminMapper {


	
	/***************** 어매성 ************************/

	/** 7일 전까지 결제 내역 전체결제내역, 환불 승인된 결제내역:환불금액 */
	@Select("SELECT date_range.date AS daily_range, "
			 + "COALESCE(sum(CASE WHEN r.approve = 1 THEN ps.total_amount ELSE 0 END),0) AS refund_status, "
			 + "COALESCE(SUM(ps.total_amount), 0) AS total_amount, "
			 + "COALESCE(SUM(ps.quantity), 0) AS quantity, "			
			 + "COALESCE(count(ps.partner_order_id), 0) AS order_cnt "
			 + "FROM ( "
			+ "SELECT DATE_SUB(CURRENT_DATE(), INTERVAL (t*10 + u) DAY) AS date "
			+ "FROM "
				+ "(SELECT 0 AS t "
				+ "UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL "
				+ "SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9)"
			+ " AS tens, "
				+ "(SELECT 0 AS u UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL "
				+ "SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS units "
				+ "WHERE DATE_SUB(CURRENT_DATE(), INTERVAL (t*10 + u) DAY) BETWEEN DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY) AND CURRENT_DATE() "
				+ ")"
			+ " AS date_range "
			+ "LEFT JOIN paymentSuccess ps ON DATE(ps.approved_at) = date_range.date "
			+ "left join refund r ON r.partner_order_id = ps.partner_order_id "
			+ "GROUP BY date_range.date "
			+ "ORDER BY date_range.date DESC")
	   public ArrayList<SalesManagementDTO> dailySales(); 
	   
	   /** 주간 결제 내역 */ 
	   @Select("WITH RECURSIVE date_range AS ( "
	   		+ "  SELECT  "
	   		+ "    DATE(CONCAT(#{year}, '-', #{month}, '-01')) AS date, "
	   		+ "    YEAR(CONCAT(#{year}, '-', #{month}, '-01')) AS year, "
	   		+ "    MONTH(CONCAT(#{year}, '-', #{month}, '-01')) AS month, "
	   		+ "    WEEK(CONCAT(#{year}, '-', #{month}, '-01')) AS week_number "
	   		+ "  UNION ALL "
	   		+ "  SELECT  "
	   		+ "    DATE_ADD(date, INTERVAL 1 DAY), "
	   		+ "    YEAR(DATE_ADD(date, INTERVAL 1 DAY)), "
	   		+ "    MONTH(DATE_ADD(date, INTERVAL 1 DAY)), "
	   		+ "    WEEK(DATE_ADD(date, INTERVAL 1 DAY)) "
	   		+ "  FROM date_range "
	   		+ "  WHERE  "
	   		+ "    DATE_ADD(date, INTERVAL 1 DAY) < CONCAT(#{year}, '-', #{month}, '-01') + INTERVAL 1 MONTH "
	   		+ "), "
	   		+ "payment_info AS ( "
	   		+ "  SELECT  "
	   		+ "    WEEK(p.approved_at) AS week_number,  "
	   		+ "    COALESCE(COUNT(p.partner_order_id), 0) AS order_cnt, "
	   		+ "    COALESCE(SUM(p.quantity), 0) AS quantity, "
	   		+ "    COALESCE(SUM(p.total_amount), 0) AS total_amount, "
	   		+ "    COALESCE(SUM(CASE WHEN r.approve = 1 THEN p.total_amount ELSE 0 END), 0) AS refund_status "
	   		+ "  FROM  "
	   		+ "    paymentsuccess p  "
	   		+ "  LEFT JOIN  "
	   		+ "    refund r ON p.partner_order_id = r.partner_order_id "
	   		+ "  WHERE  "
	   		+ "    YEAR(p.approved_at) = #{year} "
	   		+ "    AND MONTH(p.approved_at) = #{month} "
	   		+ "  GROUP BY  "
	   		+ "    WEEK(p.approved_at) "
	   		+ ") "
	   		+ "SELECT  "
	   		+ "  dr.week_number as ranking, "
	   		+ "  COALESCE(pi.order_cnt, 0) AS order_cnt, "
	   		+ "  COALESCE(pi.quantity, 0) AS quantity, "
	   		+ "  COALESCE(pi.total_amount, 0) AS total_amount, "
	   		+ "  COALESCE(pi.refund_status, 0) AS refund_status "
	   		+ "FROM  "
	   		+ "  date_range dr "
	   		+ "LEFT JOIN  "
	   		+ "  payment_info pi ON dr.week_number = pi.week_number "
	   		+ "group by  dr.year, dr.month, dr.week_number "
	   		+ "ORDER BY  "
	   		+ "  dr.year, dr.month, dr.week_number; "
	   		+ "  ")
	   public ArrayList<SalesManagementDTO> weeklySales(int year, int month);
	   
	   /** 월별 결제 내역 => 달별 환불금액 가져오는 쿼리 수정하기*/
	   @Select("SELECT  "
	         + "    p.year_num AS year, "
	         + "    m.month_number AS month, "
	         + "    COALESCE(p.order_num, 0) AS order_cnt, "
	         + "    COALESCE(SUM(p.quantity), 0) AS quantity, "
	         + "    COALESCE(SUM(p.total_amount), 0) AS total_amount, "
			 + "	COALESCE(sum(CASE WHEN p.approve = 1 THEN p.total_amount ELSE 0 END),0) AS refund_status "
	         + "FROM ( "
	         + "    SELECT DISTINCT YEAR(approved_at) AS year_num  "
	         + "    FROM paymentSuccess "
	         + "    WHERE YEAR(approved_at) = #{year} "
	         + ") AS years "
	         + "CROSS JOIN ( "
	         + "    SELECT 1 AS month_number UNION ALL "
	         + "    SELECT 2 UNION ALL "
	         + "    SELECT 3 UNION ALL "
	         + "    SELECT 4 UNION ALL "
	         + "    SELECT 5 UNION ALL "
	         + "    SELECT 6 UNION ALL "
	         + "    SELECT 7 UNION ALL "
	         + "    SELECT 8 UNION ALL "
	         + "    SELECT 9 UNION ALL "
	         + "    SELECT 10 UNION ALL "
	         + "    SELECT 11 UNION ALL "
	         + "    SELECT 12 "
	         + ") AS m "
	         + "LEFT JOIN ( "
	         + "    SELECT  "
	         + "        MONTH(ps.approved_at) AS month_number, "
	         + "        YEAR(ps.approved_at) AS year_num, "
	         + "        COUNT(ps.partner_order_id) AS order_num, "
	         + "        SUM(ps.quantity) AS quantity, "
	         + "        SUM(ps.total_amount) AS total_amount, "
	         +"			r.approve as approve "		
	         + "    FROM paymentSuccess ps "
			 + "	left join refund r ON r.partner_order_id = ps.partner_order_id "
	         + "    WHERE YEAR(approved_at) = #{year} "
	         + "    GROUP BY month_number "
	         + ") AS p ON years.year_num = p.year_num AND m.month_number = p.month_number "
	         + "WHERE (years.year_num = YEAR(CURDATE()) AND m.month_number <= MONTH(CURDATE())) "
	         + "   OR (years.year_num < YEAR(CURDATE())) "
	         + "GROUP BY p.year_num, m.month_number "
	         + "ORDER BY p.year_num DESC, m.month_number DESC;")
	   public ArrayList<SalesManagementDTO> montlySales(int year);
	   
	   @Select("select year(p.approved_at) as year "
	         + "from paymentsuccess p "
	         + "group by year "
	         + "order by p.approved_at desc   ")
	   public ArrayList<SalesManagementDTO> selectYear();
	   
	   @Select("SELECT YEAR(approved_at) AS year,"
	         + "       SUM(quantity) AS total_quantity,"
	         + "       SUM(total_amount) AS total_amount,"
	         + "       COUNT(partner_order_id) AS order_count "
	         + "FROM paymentSuccess "
	         + "GROUP BY YEAR(approved_at); ")
	   public ArrayList<SalesManagementDTO> yearSales();
	   
	   
	   @Select("SELECT  "
	         + "  partner_user_id as user_id, "
	         + "  ranking, "
	         + "  total_amount "
	         + "FROM ( "
	         + "    SELECT  "
	         + "        p.partner_user_id, "
	         + "		sum(total_amount) as total_amount,"
	         + "        MONTH(approved_at) as month_num, "
	         + "        year(approved_at) as year_num, "
	         + "        RANK() OVER (PARTITION by month(p.approved_at) ORDER BY SUM(p.total_amount) DESC) AS ranking "
	         + "    FROM  "
	         + "        paymentsuccess p "
	         + "      where not exists ( "
	         + "         select partner_order_id  "
	         + "         from refund r "
	         + "         where r.partner_order_id = p.partner_order_id"
	         + "				and r.approve != 0)"
	         + "    GROUP BY  "
	         + "        p.partner_user_id,month_num "
	         + "     having year_num = #{year} and month_num=#{month} "
	         + "    order by month_num,year_num desc,ranking asc "
	         + ") AS ranked_table "
	         + "where ranking<=3")
	   public ArrayList<SalesManagementDTO> ranking(int year, int month);
	   
	   @Select("  SELECT  "
	   		+ "        p.partner_user_id as user_id, "
	   		+ "        p.partner_order_id as order_id, "
	   		+ "		   p.quantity as quantity, p.total_amount as total_amount,"
	   		+ "        p.approved_at as pay_date, "
	   		+ "		   MONTH(p.approved_at) as month,"
	   		+ "        year(p.approved_at) as year"
	   		+ "    FROM paymentsuccess p "
	   		+ "		where not exists (  "
	   		+ "			select r.partner_order_id   "
	   		+ "			from refund r  "
	   		+ "			where r.partner_order_id = p.partner_order_id  "
	   		+ "			and r.approve != 0  ) "
	   		+ "     having year = #{year} and month= #{month} "
	   		+ "    order by pay_date desc")
	   public ArrayList<SalesManagementDTO> paymentList(int year, int month);
	   
	   @Select("select "
	         + "r.partner_order_id as order_id,"
	         + "r.reason as reason,"
	         + "r.approve as refund_status,"
	         + "r.refund_request_date as refund_request,"
	         + "r.approve_time as refund_approve,"
	         + "p.quantity as quantity,"
	         + "p.total_amount as total_amount,"
	         + "p.tid as tid,"
	         + "p.partner_user_id as user_id,"
	         + "p.approved_at as pay_date "
	         + "from refund r "
	         + "join paymentsuccess p  "
	         + "on r.partner_order_id = p.partner_order_id  "
	         + "where r.approve = #{refundStatus} "
	         + "order by r.refund_request_date desc")
	   public ArrayList<SalesManagementDTO> refund(int refundStatus);

	   @Update("UPDATE refund r "
	   		+ "join paymentsuccess p "
	   		+ "on r.partner_order_id  = p.partner_order_id "
	   		+ "SET approve = #{refundStatus},"
	        + "approve_time = CASE "
	        + "  WHEN #{refundStatus} = 0 THEN sysdate() "
	        + "  WHEN #{refundStatus} = 1 THEN #{refundApprove} "
	        + "END "
	   		+ "where r.partner_order_id=#{orderId}")
	   int checkRefund(SalesManagementDTO dto);
	   

	   
	   @Update("update `member` m  "
				+ "join paymentsuccess p "
				+ "on m.id = p.partner_user_id "
				+ "set m.quantity = m.quantity + ("
				+ "select p.quantity "
				+ "from paymentsuccess p "
				+ "where p.partner_order_id = #{orderId}) "
				+ "where p.partner_user_id =  #{userId}")
		int plusQuantity(SalesManagementDTO dto);
	
	/************************ 어매성 ************************/
  

	
	/*############ 건들면 ################### 리쌍훈 ################ 물어요 ################*/
	
	// 신고 받은 회원 리스트 가져오기
	@Select(" <script>"
			+ "SELECT r.* "
			+ "FROM report r "
			+ "ORDER BY r.report_status, r.report_time DESC "
			+ " <if test='searchNo != null' >"
			+ " limit #{searchNo}, #{listCnt} "
			+ " </if>"
			+ " <if test='searchNo == null' >"
			+ " limit 0,#{listCnt}"
			+ " </if>"
			+ " </script>")
	public ArrayList<ReportDTO> getReportMemberList(PageDTO dto);
	
	// 신고 받은 회원 명수 가져오기
	@Select("select count(*) from report")
	public int getTotalReportCount();
	
	// 신고 회원 정보 상세보기
	@Select("SELECT * "
			+ "FROM report "
			+ "where report_no = #{no} ")
	public ReportDTO reportDetail(int no);
	
	
	// 회원 정지하기
	@Update("UPDATE member SET isGrant = 1 WHERE id = #{id}")
	public int suspendMember(String id);
	
	
	// 신고받은 회원 정지시키기
	@Update("UPDATE report "
			+ "SET report_admin_answer = #{reportAdminAnswer} , report_status = 2 "
			+ "WHERE report_no  = #{reportNo}")
	public int modifyReportDetail(ReportDTO dto);
	
	
	// 신고 처리 상태 처리 완료 변경
	@Update("UPDATE report "
			+ "SET report_status = 2 "
			+ "WHERE report_no = #{no} ")
	public int modifyReportStatus(int no);
	
	
	// 정지 회원 목록 리스트
	@Select("select * from member where isGrant = 1")
	public ArrayList<MemberDTO> getSuspendMemberList(PageDTO dto);
	
	
	// 정지 해제
	@Update("UPDATE member "
			+ "SET isGrant = 2 "
			+ "WHERE id = #{id} ")
	public int changeSuspendMember(String id);
	
	// 탈퇴 회원 목록 리스트
	@Select("select * from member where isGrant = 0")
	public ArrayList<MemberDTO> getWithdrawalMemberList(PageDTO dto);
	

	//######################################   리쌍훈 끝
	

    // 성재 시작 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

	//** 일반 멤버 리스트*/
	@Select("select * from member where isGrant = 1 ")
	public ArrayList<MemberDTO> member(PageDTO dto);
	
	//** 멤버상세정보*/
	@Select("select * from member where no =#{no}")
	public MemberDTO memberDetail(int no);
	

	//** 검수 멤버 리스트*/
	@Select("select * from member where isGrant = 2 ")
	public ArrayList<MemberDTO> newmember(PageDTO dto);
	
	//** 검수 멤버 승인*/
	@Update("UPDATE member "
			+ "SET isGrant = 3 "
			+ "WHERE id = #{id} "
			+ "ORDER BY date")
	public int approval(String id);
	
	//** 검수 멤버 거절*/
	@Delete("delete from member where id = #{id}")
	public int refuse(String id);
	
	//** 기간별 신규멤버조회*/
	@Select("SELECT * FROM member WHERE date BETWEEN STR_TO_DATE(#{startDate}, '%Y-%m-%d') AND STR_TO_DATE(#{endDate}, '%Y-%m-%d') ")
	public ArrayList<MemberDTO> periodMember(@Param("startDate") String startDate, @Param("endDate") String endDate);

	// 성재 끝 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

	// 정운만 시작#############################################

	@Select("SELECT date_range.date AS daily_range, "
			+ "COALESCE(SUM(ps.total_amount), 0) AS total_amount "
			+ "FROM ( "
			+ "SELECT DATE_SUB(CURRENT_DATE(), INTERVAL (t*10 + u) DAY) AS date "
			+ "FROM "
				+ "(SELECT 0 AS t "
				+ "UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL "
				+ "SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9)"
			+ " AS tens, "
				+ "(SELECT 0 AS u UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL "
				+ "SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS units "
				+ "WHERE DATE_SUB(CURRENT_DATE(), INTERVAL (t*10 + u) DAY) BETWEEN DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY) AND DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) "
				+ ")"
			+ " AS date_range "
			+ "LEFT JOIN paymentSuccess ps"
			+ " ON DATE(ps.approved_at) = date_range.date "
			+ "GROUP BY date_range.date"
			+ " ORDER BY date_range.date DESC")
	public ArrayList<SalesManagementDTO> calculateDailyPay();
	
	@Select("select (select count(*) from member where Date(date) = curdate() and isGrant = 2) as today_register, "
				+ "	(select count(*) from report where report_status != 2 ) as report_count, "
				+ "	(select count(*) from member where Date(date) = curdate() and isGrant = 0) as today_withdrawn_member, "
				+ "	(select count(*) from refund where approve = 2) as refund_count")
	public DashBoardDTO getTodayEvent();
	
	@Select(" SELECT m.id AS member_id, m.nickname AS nickname, MAX(d.name) AS dog_name"
			+ " FROM member m"
			+ " JOIN dog d ON d.id = m.id"
			+ " WHERE DATE_FORMAT(`date`, '%Y-%m-%d') = CURDATE() AND m.isGrant = 2"
			+ " GROUP BY m.id, m.nickname"
			+ " limit 0,5")
	public ArrayList<DashBoardDTO> getTodayRegister();
	
	@Select("select r.report_no as report_no,"
			+ " m.id as member_id,"

			+ " m2.id as reported_id ,"
			+ " r.report_reason as reason"
			+ " from report r"
			+ " join `member` m "
			+ " on r.member_id = m.id "
			+ " join `member` m2 "
			+ " on r.reported_id = m2.`id` "
			+ " where r.report_status != 2"
			+ " order by report_time desc "
			+ " limit 0,5")
	public ArrayList<DashBoardDTO> getOutstandingReport();
	
	
	@Select("select r.partner_order_id as partner_order_id,"
			+ "p.total_amount  as total_amount ,"
			+ "m.id as member_id,"
			+ "r.reason as reason "
			+ " from refund r"
			+ " join paymentsuccess p "
			+ " on r.partner_order_id = p.partner_order_id "
			+ " join `member` m "
			+ " on m.id = p.partner_user_id "
			+ " where Date(refund_request_date) = curdate() and approve = 2"
			+ " limit 0,5")
	public ArrayList<DashBoardDTO> getTodayRefund();
	
	
	@Select("select sum(p.total_amount) as total_amount, "
			+ "p.partner_user_id as member_id , "
			+ "m.nickname as nickname "
			+ "from paymentsuccess p "
			+ "join `member` m "
			+ "on p.partner_user_id = m.id "
			+ "where p.approved_at  >= date_sub(curdate(),interval 1 week) "
			+ "group by partner_user_id "
			+ "order by total_amount desc "
			+ "limit 0,5")
	public ArrayList<DashBoardDTO> getWeekPaymentRank();
	
	// 정운만 끝 ##############################################
	
	

}
