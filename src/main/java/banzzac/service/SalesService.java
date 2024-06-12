package banzzac.service;


import banzzac.dto.MemberDTO;
import banzzac.mapper.AdminMapper;
import jakarta.annotation.Resource;

public interface SalesService {
	
					//세일즈 DTO로 바꾸세요
	public MemberDTO execute(MemberDTO dto);
}
