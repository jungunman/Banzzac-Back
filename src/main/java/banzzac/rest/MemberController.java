package banzzac.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import banzzac.dto.DogDTO;
import banzzac.dto.MatchingDTO;
import banzzac.dto.MemberDTO;
import banzzac.dto.ReportDTO;
import banzzac.mapper.DogMapper;
import banzzac.mapper.MatchingMapper;
import banzzac.mapper.MemberMapper;
import banzzac.utill.CommonResponse;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/member")
public class MemberController {
	
	@Resource
	MemberMapper mapper;
	
	@Resource
	DogMapper domapper;
	@Resource
	MatchingMapper matchingMapper;
	
	//*회원가입*/
	@GetMapping("createMember")
	void createMemberForm() {
		System.out.println("createMemberForm 진입");
	}

	//*회원가입레그*/
	@PostMapping("createMember")
	ResponseEntity<CommonResponse<MemberDTO>> createMemberReg(@RequestBody MemberDTO dto) {
		System.out.println("createMemberReg 진입");
		
		System.out.println(dto);
	
		mapper.createMember(dto);
		MatchingDTO matchingDTO = new MatchingDTO();
		matchingDTO.setNo(dto.getNo());
		matchingMapper.insertMatchingCondition(matchingDTO);
		return CommonResponse.success(dto);
	}
	
	//*반려견정보 입력 */
	@GetMapping("createDog")
	void createDogForm() {
		System.out.println("createDogForm 진입");
	}
	//*반려견정보 입력레그*/
	@PostMapping("createDog")
	ResponseEntity<CommonResponse<DogDTO>> createDogReg(@RequestBody DogDTO dto) {
		System.out.println("createDogReg 진입"+dto);
		domapper.createDog(dto);
	
		return CommonResponse.success(dto);
	}
	
	
	
	/**
	 * 멤버 신고하는 기능
	 * */
	@PostMapping("/report")
	public ResponseEntity<CommonResponse<ReportDTO>> reportMember(@RequestBody ReportDTO dto){
		System.out.println("dto : " + dto);
		if(mapper.reportMember(dto) >= 1 ) {
			return CommonResponse.success(dto) ;
		}else {
			return CommonResponse.error(HttpStatus.NOT_FOUND, "Not found Member", "신고 대상이 없습니다.");
		}
	}
	
}
