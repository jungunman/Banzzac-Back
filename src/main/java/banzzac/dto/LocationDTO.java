package banzzac.dto;

import org.springframework.stereotype.Component;


import lombok.Data;

@Data
@Component
public class LocationDTO {
	
	private MemberDTO member;
	//현재 내 위치를 가져옵니다.
	private Double longitude, latitude;
	//북동쪽 범위
	private Double rangeNorth;
	private Double rangeEast;
	//남서쪽 범위
	private Double rangeSouth;
	private Double rangeWest;
	
}
