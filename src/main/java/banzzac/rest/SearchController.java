package banzzac.rest;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import banzzac.dto.LocationDTO;
import banzzac.mapper.SearchMapper;
import banzzac.utill.CommonResponse;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/search")
public class SearchController {
	
	@Resource
	SearchMapper mapper;
	
	@PostMapping("members")
	public ResponseEntity<CommonResponse<ArrayList<LocationDTO>>> getLocations(@RequestBody LocationDTO dto){
		System.out.println(dto);
		ArrayList<LocationDTO> res = mapper.getMemberLocations(dto);
		System.out.println("DB dto : "+res);
		return CommonResponse.success(res);
	}
	
}
