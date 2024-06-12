package banzzac.rest;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import banzzac.dto.MemberDTO;
import banzzac.dto.ReviewDTO;
import banzzac.dto.WalkDiaryDTO;
import banzzac.jwt.MemberDetail;
import banzzac.mapper.ReviewMapper;
import banzzac.utill.CommonResponse;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/walk")
public class WalkController {

	@Resource
	ReviewMapper mapper;

	/**4. 산책 다이어리 리스트 가져오기 */
	@GetMapping("diaries")
	public ResponseEntity<CommonResponse<ArrayList<WalkDiaryDTO>>> getWalkingDiary(Authentication auth){
		
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		MemberDTO info = new MemberDTO();
		info.setId(myId.getId());
		
		return CommonResponse.success(mapper.getWalkList(info));
	}
	
	/**1. 약속 잡기 추가*/
	@PostMapping("promise")
	public ResponseEntity<CommonResponse<WalkDiaryDTO>> insertPromise(@RequestBody WalkDiaryDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setSessionId(myId.getId());
		if(mapper.createPromise(dto) >= 1) {
			return CommonResponse.success(mapper.getInsertPromise(dto));			
		}
		
		return CommonResponse.error(HttpStatus.BAD_REQUEST, "400.1", "약속잡기 실패");
	}
	
	/**2. 승낙 거절*/
	@PostMapping("promise/{promiseStatus}")
	public ResponseEntity<CommonResponse<String>> updatePromiseStatus(Authentication auth,@RequestBody WalkDiaryDTO dto ,@PathVariable Integer promiseStatus){
		
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setSessionId(myId.getId());
		
		if(mapper.updatePromiseStatus(dto) == 0 ) {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "error - 404.2", "요청에 실패했습니다.");
		}
		dto.setPromiseStatus(promiseStatus);
		String msg = "";
		System.out.println("promiseStatus : "+dto);
		
		if(dto.getPromiseStatus() == 0) {
			return CommonResponse.error(HttpStatus.OK, "거절", "상대가 산책 약속을 거절했습니다.");
		}else {
			msg = "산책을 승낙하셨습니다.";
			
			//리뷰 테이블 생성
			//3 .
			mapper.createReviews(dto);
		}
		
		return CommonResponse.success(msg);
	}
	
	/**5. 평가하기 */
	@PostMapping("appraisal")
	public ResponseEntity<CommonResponse<ArrayList<WalkDiaryDTO>>> appraiseWalker(@RequestBody ReviewDTO dto, Authentication auth){
		
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		MemberDTO info = new MemberDTO();
		info.setId(myId.getId());
		dto.setMyId(info.getId());
		if(mapper.registerReview(dto) >= 1) {
			return CommonResponse.success(mapper.getWalkList(info));			
		}
		
		return CommonResponse.error(HttpStatus.BAD_REQUEST,"400.1", "평가하기 실패");
		
	
	}
	
	/**6. 리뷰 수정 후 내용만 수정*/
	@PostMapping("review/{no}")
	public ResponseEntity<CommonResponse<ReviewDTO>> modifyReview(@RequestBody ReviewDTO dto,Authentication auth,@PathVariable int no){
		
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setMyId(myId.getId());
		dto.setNo(no);
		
		if(mapper.modify(dto) >= 1) {
			return CommonResponse.success(mapper.showAfterModifyReviewQuery(dto));			
		}
		
		return CommonResponse.error(HttpStatus.BAD_REQUEST, "400.1", "리뷰 수정 실패");
		
	}
	
	
	
	/**7. 리뷰 삭제 */
	@GetMapping("review/{no}/delete")
	public ResponseEntity<CommonResponse<Integer>> deleteReview(Authentication auth, @PathVariable int no){
		ReviewDTO dto = new ReviewDTO();
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setMyId(myId.getId());
		dto.setNo(no);
		System.out.println("delete Review dto : " + dto);
		int res = mapper.delete(dto);
		
		if(res >= 1) {
			return CommonResponse.success(1);			
		}
		
		return CommonResponse.error(HttpStatus.BAD_REQUEST, "400.1", "리뷰 삭제 실패");
	}
	
	
}
