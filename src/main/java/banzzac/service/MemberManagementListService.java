package banzzac.service;

import java.util.ArrayList;

import banzzac.dto.MemberDTO;

public interface MemberManagementListService {

	public ArrayList<MemberDTO> execute(MemberDTO dto);
	
}
