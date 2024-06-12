package banzzac.service.impl;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import banzzac.dto.MemberDTO;
import banzzac.service.SalesListService;


@Service
public class Chart implements SalesListService{

	@Override
	public ArrayList<MemberDTO> execute(MemberDTO dto) {
		
		System.out.println("판매 관리 - 서비스 실행");
		
		return null;
	}
}
