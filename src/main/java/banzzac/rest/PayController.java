package banzzac.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import banzzac.dto.MemberDTO;
import banzzac.dto.PaymentSuccessDTO;
import banzzac.dto.RefundDTO;
import banzzac.jwt.MemberDetail;
import banzzac.mapper.PaymentMapper;
import banzzac.payment.PayInfoApprove;
import banzzac.payment.PaySuccessInfo;
import banzzac.utill.CommonResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/payment")
public class PayController {

	@Resource
	PaymentMapper mapper;
	
	private PayInfoApprove payInfoApprove;
	private PaySuccessInfo paySuccessInfo;
	private int orderId;
	
	@PostMapping("ready")
	public ResponseEntity<CommonResponse<Object>> readyToPay(@RequestBody PaymentSuccessDTO paymentSuccessDto,Authentication auth){
		System.out.println("카카오페이 결제 준비 요청");	
		 
		 orderId = (int) (Math.random() * Integer.MAX_VALUE);
		 PaymentSuccessDTO dto = mapper.checkOrderId(orderId); // 동일한 주문번호 check		 
		
		 while(dto!=null) {	 // 동일한 주문번호가 있을 경우
		 	orderId = (int) (Math.random() * Integer.MAX_VALUE);
		 	if(dto.getPartnerOrderId()!=orderId) {
		 		break;
		 	}
		 }
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		System.out.println("My Id : "+myId);
		paymentSuccessDto.setPartnerOrderId(orderId);
		paymentSuccessDto.setPartnerUserId(myId.getId());
		 
		HttpHeaders headers = new HttpHeaders(); 
		RestTemplate restTemplate = new RestTemplate();
		 		
	    headers.set("Authorization", "SECRET_KEY DEV363D27AC1786201E1E1E880CD565F7F19A499"); // secret key 숨기기
		headers.set("Content-Type", "application/json"); //jason 형태로 보내기
		
		
		// kakao 에 요청할 Body
		Map<String, String> params = new HashMap<>(); //key-value 형태로 저장
		params.put("cid", "TC0ONETIME");
		params.put("partner_order_id", orderId+"");
		params.put("partner_user_id", paymentSuccessDto.getPartnerUserId());
		params.put("item_name", "매칭권");
		params.put("quantity",paymentSuccessDto.getQuantity()+"");
		params.put("total_amount", paymentSuccessDto.getTotalAmount()+"");
		params.put("tax_free_amount", "0");
		params.put("approval_url", "http://localhost/api/payment/success/"+orderId+"");	//결제 승인 url
		params.put("cancel_url", "http://localhost/api/payment/cancel/"+orderId+"");		//결제 취소 시 보여질 페이지 -> 주문페이지
		params.put("fail_url", "http://localhost/api/payment/fail/"+orderId+"");		//결제 실패 시 보여질 페이지 -> 주문페이지
					
		// Header + Body
		HttpEntity<Map<String, String>> transform = new HttpEntity<>(params, headers);
		
		// kakao 로 준비 요청하기 -> PayInfoApprove.class : response로 받아올 class
		payInfoApprove = restTemplate.postForObject("https://open-api.kakaopay.com/online/v1/payment/ready", transform, PayInfoApprove.class);
		paymentSuccessDto.setTid(payInfoApprove.getTid());
		
		if(payInfoApprove != null && mapper.insertPayment(paymentSuccessDto)>=1) {
			System.out.println("카카오결제 url요청");

			return CommonResponse.success(payInfoApprove);	
		}else {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "Pay Request Failed", "결제 요청 실패");
		}
	}
	
	 
	 // 결제 성공 시 pgToken 값 받아오기
	 @GetMapping("success/{partnerOrderId}")
	 public ResponseEntity<CommonResponse<Object>> paySuccess(@RequestParam("pg_token") String pgToken, @PathVariable int partnerOrderId) {		 
		System.out.println("결제 준비 성공 -> 승인 요청"); 
		
		HttpHeaders headers = new HttpHeaders(); 
		RestTemplate restTemplate = new RestTemplate();

		headers.set("Authorization", "SECRET_KEY DEV363D27AC1786201E1E1E880CD565F7F19A499");
		headers.set("Content-Type", "application/json"); //jason 형태로 보내기
		
		PaymentSuccessDTO dto = mapper.detail(partnerOrderId);

		Map<String, String> params = new HashMap<>(); 
		params.put("cid", "TC0ONETIME");
		params.put("tid", dto.getTid());
		params.put("pg_token",pgToken);
		params.put("partner_order_id", dto.getPartnerOrderId()+"");
		params.put("partner_user_id", dto.getPartnerUserId());
	
		HttpEntity<Map<String, String>> transform = new HttpEntity<>(params, headers);
		
		// 결제 승인 요청 + 결제 성공 정보 받아오기
		paySuccessInfo = restTemplate.postForObject("https://open-api.kakaopay.com/online/v1/payment/approve", transform, PaySuccessInfo.class);
		
		if(paySuccessInfo != null) {
			// 결제 성공 return 값 -> 결제성공 table update
			mapper.paySuccess(paySuccessInfo.getTid(),paySuccessInfo.getAid(),paySuccessInfo.getApproved_at(),paySuccessInfo.getPayment_method_type(),partnerOrderId);
			// 결제 성공 후 매칭권 갯수 변경
			mapper.modifyMatchingQuantity(dto);
			System.out.println(paySuccessInfo.getApproved_at());
			
			// 결제 성공 후 redirect 페이지
			String address = "http://localhost:5173/payment/list";
			URI uri = URI.create(address);
			
			return ResponseEntity.status(302).location(uri).build();
			
		}else {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "Pay Approve Failed", "결제 승인 실패");
		}

	 }
	 
	 
	 @GetMapping(path = {"fail/{partnerOrderId}","/cancel/{partnerOrderId}"})
	 public ResponseEntity<Object> payFail(PaymentSuccessDTO dto){
		 System.out.println("결제 요청 실패 or 취소");
		
		// db에서 삭제하기
		mapper.delete(dto);

		// 결제 실패, 취소 후 redirect 페이지
		String address = "http://localhost:5173/profile";
		URI uri = URI.create(address);

		return ResponseEntity.status(302).location(uri).build();
	 }
	 
	 /** 결제 내역 보기 */
	 @GetMapping()
	 public ResponseEntity<CommonResponse<ArrayList<PaymentSuccessDTO>>> payList(PaymentSuccessDTO dto,Authentication auth){
		System.out.println("결제 내역 : 환불신청건 제외");
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setPartnerUserId(myId.getId());

		return CommonResponse.success(mapper.myPayList(dto));
	 }

	 /** 환불 신청 */
	 @PostMapping("refund/insert")
	 public ResponseEntity<CommonResponse<ArrayList<RefundDTO>>> insertRefund(@RequestBody RefundDTO dto,PaymentSuccessDTO payDto,Authentication auth){		
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		 System.out.println("환불 신청 "+dto);
		 payDto.setPartnerOrderId(dto.getPartnerOrderId());
		 payDto.setPartnerUserId(myId.getId());
		 
		 if(mapper.insertRefund(dto.getPartnerOrderId(),dto.getReason(), myId.getId())>=1) {
			mapper.minusQuantity(payDto);
			 System.out.println("환불신청 성공"+dto);
			 return CommonResponse.success(mapper.myRefundList(myId.getId()));
		 }else {
			 return CommonResponse.error(HttpStatus.BAD_REQUEST, "Refund Insert Failed", "환불 신청 실패");
		 }
	 }
	 
	 /** 환불 신청 내역 */
	 @GetMapping("refund")
	 public ResponseEntity<CommonResponse<ArrayList<RefundDTO>>> myRefundList(RefundDTO dto,Authentication auth){
		 System.out.println("환불 신청 내역 ");
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		 ArrayList<RefundDTO> res = mapper.myRefundList(myId.getId());
		 System.out.println(res);
		 return CommonResponse.success(res);
	 }
 
	 /** 환불 신청 사유 수정 */
	 @PostMapping("refund")
	 public ResponseEntity<CommonResponse<ArrayList<RefundDTO>>> modifyRefund(@RequestBody RefundDTO dto,Authentication auth){
		 System.out.println("환불 사유 수정");
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		 if(dto.getApprove()==2) {
			 System.out.println("승인 대기 중");
			if(mapper.modifyRefund(dto,myId.getId())>=1) {
				return CommonResponse.success(mapper.myRefundList(myId.getId()));
			}else {
				return CommonResponse.error(HttpStatus.BAD_REQUEST, "Refund Reason Modify Failed", "환불사유 수정 실패");
			} 
		 }else {
			 return CommonResponse.error(HttpStatus.BAD_REQUEST, "Refund Approve Finish", "환불 대기 상태 아님");
		 } 
	 }
	 
	 /** 환불 취소 */
	 @GetMapping("refund/cancel/{partnerOrderId}")
	 public ResponseEntity<CommonResponse<ArrayList<RefundDTO>>> cancelRefund(RefundDTO dto,PaymentSuccessDTO pdto, @PathVariable String partnerOrderId,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		 System.out.println("환불 취소");

		 pdto.setPartnerOrderId(dto.getPartnerOrderId());
		 pdto.setPartnerUserId(myId.getId());
		 
		 if(mapper.cancelRefund(dto)>=1) {
			 mapper.plusQuantity(pdto);
			 return CommonResponse.success(mapper.myRefundList(myId.getId()));
		 }else {
			 return CommonResponse.error(HttpStatus.BAD_REQUEST, "Refund Cancel Failed", "환불 취소 실패");
		 }
	 }
	
}
