package banzzac.service.impl;


import java.util.ArrayList;

import org.springframework.stereotype.Service;

import banzzac.dto.MemberDTO;
import banzzac.mapper.AdminMapper;
import banzzac.service.MemberManagementListService;
import jakarta.annotation.Resource;

@Service
public class NewMembers implements MemberManagementListService{
	
	@Resource
	private AdminMapper mapper;
	
	@Override
	public ArrayList<MemberDTO> execute(MemberDTO dto) {
		
		System.out.println("서비스 진입");
		
		return null;
	}
}
