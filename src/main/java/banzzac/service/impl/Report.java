package banzzac.service.impl;

import java.util.ArrayList;

import banzzac.dto.PageDTO;
import banzzac.dto.ReportDTO;
import banzzac.mapper.AdminMapper;
import banzzac.service.ReportMemberListService;
import jakarta.annotation.Resource;

public class Report implements ReportMemberListService {

	@Resource
	AdminMapper mapper;
	
	@Override
	public ArrayList<ReportDTO> execute() {
		
		
		
		ArrayList<ReportDTO> res = mapper.getReportMemberList(null); 
		
		return res;
	}
}
