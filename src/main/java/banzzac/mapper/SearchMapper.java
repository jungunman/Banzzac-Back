package banzzac.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import banzzac.dto.LocationDTO;

@Mapper
public interface SearchMapper {

	
	/** 내 현재 위치한 멤버들의 정보를 불러옵니다. 이것으로 회원의 프로필 보기를 구성하면 됨.*/
	  @Select("select l.*, "
	            + "m.no as member_no, m.id as member_id, m.gender as member_gender, m.img as member_img, "
	            + "m.walking_style as member_walking_style, m.nickname as member_nickname, "
	            + "m.Temperature as member_Temperature, m.cnt as member_cnt "
	            + "from location as l "
	            + "left join `member` m "
	            + "on l.`no` = m.`no` "
	            + "where latitude > #{rangeSouth} and latitude  < #{rangeNorth} "
	            + "and longitude > #{rangeWest} and longitude < #{rangeEast} "
	            + "and is_hide = 1")
	    @Results({
	        @Result(property = "member.no", column = "member_no"),
	        @Result(property = "member.id", column = "member_id"),
	        @Result(property = "member.gender", column = "member_gender"),
	        @Result(property = "member.img", column = "member_img"),
	        @Result(property = "member.walkingStyleStr", column = "member_walking_style"),
	        @Result(property = "member.nickname", column = "member_nickname"),
	        @Result(property = "member.temperature", column = "member_Temperature"),
	        @Result(property = "member.cnt", column = "member_cnt")
	    })
	public ArrayList<LocationDTO> getMemberLocations(LocationDTO dto);
	
}
