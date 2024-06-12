package banzzac.rest;

import java.net.URI;
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

import banzzac.dto.DogDTO;
import banzzac.dto.MemberDTO;
import banzzac.jwt.MemberDetail;
import banzzac.mapper.DogMapper;
import banzzac.mapper.MemberMapper;
import banzzac.utill.CommonResponse;
import jakarta.annotation.Resource;


@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	@Resource
	MemberMapper memMapper;
	
	@Resource
	DogMapper dogMapper;
	
	
	@GetMapping()
	ResponseEntity<CommonResponse<ArrayList<MemberDTO>>> myProfile(MemberDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		
		System.out.println("myProfile" + myId);
		dto.setId(myId.getId());
		return CommonResponse.success(memMapper.memberInfo(dto));
	}


	@PostMapping()
	ResponseEntity<CommonResponse<ArrayList<MemberDTO>>> modifyMyProfile(@RequestBody MemberDTO dto,Authentication auth) {	
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		if(memMapper.modifyMember(dto)>=1) {
			ArrayList<MemberDTO> res = memMapper.memberInfo(dto);
			System.out.println("myProfile 수정 성공" + res);
			
			return CommonResponse.success(res);
		}
		return CommonResponse.error(HttpStatus.BAD_REQUEST,"MyProfile Modify Failed", "개인정보 수정 실패");
	}
	
	@PostMapping("status")
	ResponseEntity<CommonResponse<ArrayList<MemberDTO>>> modifyStatus(@RequestBody MemberDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		if(memMapper.modifyStatus(dto)>=1) {
			ArrayList<MemberDTO> res = memMapper.memberInfo(dto);
			System.out.println("상태메세지 수정 성공" + dto.getStatusMessage());
			return CommonResponse.success(res);
		}
		return CommonResponse.error(HttpStatus.BAD_REQUEST,"StatusMessage Modify Failed", "상태메시지 수정 실패");
	}
	
	@GetMapping("logout")
	ResponseEntity<CommonResponse<Object>> logout(){
		// 로그아웃 시 react 에서 token 지우기
		URI uri = URI.create("http://localhost:5173/login"); 
		return ResponseEntity.status(302).location(uri).build();			
	}
	
	@PostMapping("withdraw")
	ResponseEntity<CommonResponse<Object>> withdrawMember(@RequestBody MemberDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		System.out.println(dto);
		if(memMapper.withdrawMember(dto)>=1) {
			System.out.println("탈퇴성공 main페이지로 redirect");
			URI uri = URI.create("http://localhost:5173/login"); 
			
			return ResponseEntity.status(302).location(uri).build();
		}else {
			System.out.println("탈퇴 실패");
			return CommonResponse.error(HttpStatus.BAD_REQUEST,"Member Withdraw Failed","탈퇴 실패");
		}
	}
	
	@GetMapping("dog")
	ResponseEntity<CommonResponse<ArrayList<DogDTO>>> dogList(DogDTO dto, Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		ArrayList<DogDTO> res = dogMapper.list(dto);
		System.out.println("반려견 전체 리스트"+res);
		return CommonResponse.success(res);
	}

		
	@PostMapping("dog")
	ResponseEntity<CommonResponse<ArrayList<DogDTO>>> addDog(@RequestBody DogDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		DogDTO res = dogMapper.checkDog(dto);
		
		if(res==null) {
			dogMapper.createDog(dto);
			return CommonResponse.success(dogMapper.list(dto));	
		}else {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "Dog Add Failed", "같은 정보 반려견 존재");
		}	
	}

	@PostMapping("dog/{name}")
	ResponseEntity<CommonResponse<ArrayList<DogDTO>>> modifyDog(@RequestBody DogDTO dto, @PathVariable String name,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		dto.setName(name);
		System.out.println("반려견 수정");
		if (dogMapper.modifyDog(dto)>=1) {			
			return CommonResponse.success(dogMapper.list(dto));
		}else {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "Dog Modify Failed", "반려견 수정 실패");
		}
	}

	@GetMapping("dog/delete/{name}")
	ResponseEntity<CommonResponse<ArrayList<DogDTO>>> deleteDog(DogDTO dto,Authentication auth){
		MemberDetail myId = (MemberDetail)auth.getPrincipal();
		dto.setId(myId.getId());
		System.out.println("반려견 삭제" + dto);
		if(dogMapper.deleteDog(dto)>=1) {
			return CommonResponse.success(dogMapper.list(dto));
		}else {
			return CommonResponse.error(HttpStatus.BAD_REQUEST, "Dog Delete Failed", "반려견 삭제 실패");
		}
	}

	
}
