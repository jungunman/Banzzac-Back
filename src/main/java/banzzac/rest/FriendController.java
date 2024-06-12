package banzzac.rest;


import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Update;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import banzzac.dto.FriendDTO;
import banzzac.dto.MemberDTO;
import banzzac.jwt.MemberDetail;
import banzzac.mapper.FriendMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

	
	@Resource
	FriendMapper mapper;
	
	/** 친구리스트*/
	@GetMapping("list")
	public List<FriendDTO> friendList(Authentication auth) {
		MemberDetail dto = (MemberDetail)auth.getPrincipal();
		System.out.println("principal dto : " + dto);
		List<FriendDTO> res = mapper.list(dto.getId());
		return res;
	}
	
	/** 차단친구리스트*/
	@GetMapping("blockList")
	public List<FriendDTO> friendblockList(HttpSession session) {
		MemberDTO dto = (MemberDTO)session.getAttribute("member");
		List<FriendDTO> res = mapper.blockList(dto.getId());
		return res;
	}
		
	/** 즐겨찾기 친구리스트*/
	@GetMapping("favoriteList")
	public List<FriendDTO> favoriteList(Authentication auth) {
		MemberDetail dto = (MemberDetail)auth.getPrincipal();
		
		List<FriendDTO> res = mapper.favoriteList(dto.getId());
		return res;
	}
	
	/** 친구 삭제  */
	@GetMapping("delete/{friendId}")
	Object delete(FriendDTO dto,@PathVariable String friendId,HttpSession session) {
		MemberDTO memDTO = (MemberDTO)session.getAttribute("member");
		dto.setId(memDTO.getId());
		mapper.delete(dto);
		return mapper.list(memDTO.getId());
	}
	
	//** 친구프로필 */
	
	@GetMapping("friendProfile/{friendId}")
	public MemberDTO friendProfile(@PathVariable String friendId){
		MemberDTO res = mapper.friendProfile(friendId);
		return res;
	}
	
	/** 친구차단  */
	@GetMapping("friendBlock/{friendId}")
	Object friendBlock(FriendDTO dto,@PathVariable String friendId,HttpSession session) {
		
		MemberDTO memDTO = (MemberDTO)session.getAttribute("member");
		dto.setId(memDTO.getId());
		mapper.friendBlock(dto);
		return mapper.list(memDTO.getId());
	}
	
	/** 친구차단해제  */
	@GetMapping("friendUnBlock/{friendId}")
	Object friendUnBlock(FriendDTO dto,@PathVariable String friendId,HttpSession session) {
		MemberDTO memDTO = (MemberDTO)session.getAttribute("member");
		dto.setId(memDTO.getId());
		mapper.friendUnBlock(dto);
		System.out.println("차단해제");
		return mapper.blockList(memDTO.getId());
	
	}
	
	/** 친구즐겨찾기 추가  */
	@GetMapping("friendFavorite/{friendId}")
	Object friendFavorite(FriendDTO dto, @PathVariable String friendId, Authentication auth) {
		
		MemberDetail memDTO = (MemberDetail)auth.getPrincipal();
		dto.setId(memDTO.getId());
		mapper.friendFavorite(dto);	
		
		return mapper.list(memDTO.getId());
	}
	
	/** 친구즐겨찾기 해제  */
	@GetMapping("friendUnFavorite/{friendId}")
	Object friendUnFavorite(FriendDTO dto,@PathVariable String friendId,HttpSession session) {
		MemberDTO memDTO = (MemberDTO)session.getAttribute("member");
		dto.setId(memDTO.getId());
		mapper.friendUnFavorite(dto);
		return mapper.list(memDTO.getId());
	}
	
}
