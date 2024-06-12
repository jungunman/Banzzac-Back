package banzzac.service;

import java.util.ArrayList;

import banzzac.dto.MemberDTO;

public interface SalesListService {
					//세일즈 DTO로 바꾸세요
	public ArrayList<MemberDTO> execute(MemberDTO dto);
}
