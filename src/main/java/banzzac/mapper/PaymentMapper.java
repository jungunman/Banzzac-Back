package banzzac.mapper;

import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.MemberDTO;
import banzzac.dto.PaymentSuccessDTO;
import banzzac.dto.RefundDTO;

@Mapper
public interface PaymentMapper {
	
	/** 내가 결제한 리스트 -> 세션아이디 넣기 */
	@Select("select "
			+ "partner_order_id,payment_method_type,quantity,total_amount,approved_at "
			+ "from paymentSuccess p "
			+ "where partner_user_id= #{partnerUserId} "
			+ "	   AND NOT EXISTS ("
			+ "    	SELECT 1"
			+ "    	FROM refund r"
			+ "    	WHERE r.partner_order_id = p.partner_order_id )"
			+ "order by approved_at desc")
	ArrayList<PaymentSuccessDTO> myPayList(PaymentSuccessDTO dto);
	
	/** 결제 시 매칭권 갯수 변경
	 * 보유하고 있던 매칭권 수 + 구매한 매칭권 수 */
	@Update("update `member` m "
			+ "join paymentsuccess p"
			+ " on m.id = p.partner_user_id "
			+ "set m.quantity=m.quantity+p.quantity "
			+ "where p.partner_order_id = #{partnerOrderId};")
	int modifyMatchingQuantity(PaymentSuccessDTO dto);
	
	
	/** 결제 요청 시 orderId 생성 -> db에 같은 orderId가 있는 지 확인하고 
	 * 같은 주문번호가 없을 때까지 orderId를 생성하기 위한 sql문 */
	@Select("select partner_order_id from paymentSuccess where partner_order_id=#{partnerOrderId}")
	PaymentSuccessDTO checkOrderId(int partnerOrderId);
	
	
	/** 결제 처음 요청 시 db에 추가 */
	@Insert("insert into paymentSuccess "
			+ "(partner_order_id,partner_user_id,quantity,total_amount,tid) "
			+ "values "
			+ "(#{partnerOrderId},#{partnerUserId},#{quantity},#{totalAmount},#{tid})")
	int insertPayment(PaymentSuccessDTO dto);
	
	
	/** 결제 성공 시 db 추가 로 update */
	@Update("update paymentSuccess set "
			+ "tid=#{tid}, aid=#{aid}, approved_at=sysdate(), payment_method_type=#{paymentMethodType}"
			+ "where partner_order_id = #{partnerOrderId}")
	int paySuccess(String tid, String aid, Date approvedAt, String paymentMethodType, int partnerOrderId);
	
	
	/** 카카오에 결제 승인에 필요한 값 select */
	@Select("select partner_order_id,partner_user_id,tid from paymentSuccess where partner_order_id = #{partnerOrderId}")
	PaymentSuccessDTO detail(int partnerOrderId);
	
	
	/** 결제 : 취소/수정 시 db에서 삭제 */
	@Delete("delete from paymentsuccess where partner_order_id= #{partnerOrderId}")
	int delete(PaymentSuccessDTO dto);
	
	
	/** 환불 신청 가능 조건 
	 	: 결제내역 존재, 현재로부터 7일 이내, 구매id = login id, 남아있는 매칭권 >= 구매한 매칭권, 환불신청하지 않은 주문건 */
	@Insert("INSERT INTO refund (partner_order_id, reason, refund_request_date) "
			+ "    SELECT #{partnerOrderId}, #{reason}, sysdate() "
			+ "    FROM paymentsuccess p "
			+ "    JOIN `member`  m "
			+ "    on p.partner_user_id = m.id "
			+ "    WHERE DATEDIFF(sysdate(), p.approved_at) <= 7 "
			+ "    AND p.partner_user_id = #{partnerUserId} "
			+ "    AND m.quantity >= p.quantity  "
			+ "	   AND p.partner_order_id = #{partnerOrderId} "
			+ "	   AND NOT EXISTS ("
			+ "    	SELECT #{partnerOrderId}"
			+ "    	FROM refund r"
			+ "    	WHERE r.partner_order_id = p.partner_order_id)")
	int insertRefund(int partnerOrderId, String reason, @Param("partnerUserId") String partnerUserId);
	
	/** 환불 신청 시 member 테이블의 quantity 수량 빼기 */
	@Update("update `member` m  "
			+ "join paymentsuccess p "
			+ "on m.id = p.partner_user_id "
			+ "set m.quantity = m.quantity -("
			+ "select p.quantity "
			+ "from paymentsuccess p "
			+ "join refund r "
			+ "on p.partner_order_id = r.partner_order_id "
			+ "where r.partner_order_id = #{partnerOrderId}) "
			+ "where p.partner_user_id =  #{partnerUserId}")
	int minusQuantity(PaymentSuccessDTO dto);
	
	
	/** 환불 사유 수정 : 환불 대기 중인 건에 한해 */
	@Update("update refund r "
			+ "join paymentsuccess p "
			+ "on r.partner_order_id  = p.partner_order_id "
			+ "set r.reason = #{reason} "
			+ "where "
			+ "r.partner_order_id = #{partnerOrderId} && "
			+ "p.partner_user_id = #{partnerUserId} && "
			+ "r.approve = 2")
	int modifyRefund(RefundDTO refundDTO, @Param("partnerUserId") String partnerUserId);
	
	
	/** 내가 신청한 환불 리스트 */
	@Select("select "
			+ "r.partner_order_id,r.reason,r.refund_request_date ,r.approve,p.quantity, p.total_amount, r.approve_time "		
			+ "from refund r "
			+ "join paymentsuccess p "
			+ "on r.partner_order_id = p.partner_order_id "
			+ "where "
			+ "p.partner_user_id = #{partnerUserId} "
			+ "order by r.refund_request_date desc" )
	ArrayList<RefundDTO> myRefundList(String partnerUserId);

	
	/** 환불 취소 */
	@Delete("delete from refund where partner_order_id = #{partnerOrderId}")
	int cancelRefund(RefundDTO dto);
	
	@Update("update `member` m  "
			+ "join paymentsuccess p "
			+ "on m.id = p.partner_user_id "
			+ "set m.quantity = m.quantity + ("
			+ "select p.quantity "
			+ "from paymentsuccess p "
			+ "where p.partner_order_id = #{partnerOrderId}) "
			+ "where p.partner_user_id =  #{partnerUserId}")
	int plusQuantity(PaymentSuccessDTO dto);
}
