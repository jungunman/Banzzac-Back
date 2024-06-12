package banzzac.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.DogDTO;
import banzzac.dto.MatchingDTO;
import banzzac.dto.MemberDTO;

@Mapper
public interface MatchingMapper {

	/** 회원가입 시 insertMachingCondition이 기본 값으로 설정 될 수 있도록 만들어야 합니다.
	 * no = 회원 넘버
	 * */
	@Insert("insert into matching_conditions (no) values (#{no})")
	public int insertMatchingCondition(MatchingDTO dto);
	
	/**회원이 산책 조건을 설정할 때 불러올 값입니다.
	 * no = 회원 넘버
	 * */
	@Select("select"
			+ " style as walking_style_str,"
			+ " no,"
			+ " dog_nature as dog_nature_str,"
			+ " size, amount_of_activity,"
			+ " want_matching,"
			+ " gender,"
			+ " age_range_start,"
			+ " age_range_end "
			+ " from matching_conditions where no = #{no}")
	public MatchingDTO showMatchingCondition(MatchingDTO dto);
	
	
	/**회원이 설정한 매칭 조건을 업데이트 합니다.
	 * no = 회원 넘버
	 * */
	@Update("Update matching_conditions set"
			+ " style = #{walkingStyleStr},"
			+ " age_range_start = #{ageRangeStart},"
			+ " age_range_end=#{ageRangeEnd},"
			+ " gender=#{gender},"
			+ " size=#{size},"
			+ " dog_nature=#{dogNatureStr},"
			+ " amount_of_activity=#{amountOfActivity},"
			+ " want_matching=#{wantMatching} where no=#{no}")
	public int updateMatchingCondition(MatchingDTO dto);
	
	
	/** 
	 * 경우에 따라 분기를 나눠 주셔야 합니다.
	 * 유저가 계정을 삭제할 때, 정지 당할 때, 매칭을 원하지 않을 때로 나뉩니다.
	 * 유저가 계정을 삭제할 때, 정지 당할 때는 wantMatching에 0을 넣어주세요.
	 * 유저가 매칭을 원할 때, 원하지 않을 때는 Front에서 받아온 값으로 설정합니다.
	 * */
	@Update("Update matching_conditions set"
			+ " want_matching = #{wantMatching} where no=#{no}")
	public int updateWantMatching(MatchingDTO dto);
	
	/**
	 * 회원 조건에 부합하는 사람을 뽑아오는 sql 문입니다.
	 * 넘어오는 값은 null이 없어야 하며, 반환되는 결과 값은 memberDTO에 연결됩니다.
	 * 결과 처리 후, result에서 getMatchingConditionsDogs를 반환합니다.
	 * 반환된 값은 dogDTOs에 자동 결합됩니다.
	 * 참고 ) 한 번 검색된 회원은 중복하여 나타나지 않는 쿼리입니다.
	 * 나중에는 회원이 위치한 곳의 범위를 가져야 할 수 있습니다.
	 * 변경 예상 => Matching DTO에 범위 설정용 Location DTO 추가.
	 * Location 테이블과 Join.
	 * */
	@Select("<script>"
		    + "SELECT m.no as no,"
		    + " m.gender as gender,"
		    + " m.walking_style as walking_style,"
		    + " m.age as age,"
		    + " m.temperature as temperature,"
		    + " m.cnt as cnt,"
		    + " m.id as id,"
		    + " m.img as img,"
		    + " m.nickname as nickname,"
		    + " d.*"
		    + " FROM member m"
		    + " JOIN dog d"
		    + " ON m.id = d.id"
		    + " JOIN matching_conditions mc"
		    + " ON m.no = mc.no"
		    + " WHERE"
		    + " m.age BETWEEN #{ageRangeStart} AND #{ageRangeEnd}"
		    + " <if test='gender != null'>"
		     	+" AND m.gender = #{gender}"
		    + " </if>"
		    + " <if test='walkingStyle != null and walkingStyle.size() > 0'>"
		    	+" AND ("
		    + " <foreach item='style' collection='walkingStyle' separator=' or '>"
		    	+"  m.walking_style LIKE CONCAT('%', #{style}, '%') "
		    + " </foreach>"
		    	+")"
		    + " </if>"
		    + " <if test='size != null'>"
		     	+" AND d.size = #{size}"
		    + " </if>"
		    + " <if test='dogNature != null and dogNature.size() > 0'>"
		     	+" AND ("
		    + " <foreach item='nature' collection='dogNature' separator=' or '>"
		     	+" d.personality LIKE CONCAT('%', #{nature}, '%')"
		    + " </foreach>"
		    + " )"
		    + " </if>"
		    + " <if test='amountOfActivity != null'>"
		    	+ " AND d.activity = #{amountOfActivity}"
		    + " </if>"
		    + " AND m.isGrant = 1"
		    //+ " AND mc.want_matching = 1 "
		    + " AND m.id NOT IN (SELECT searched_member_id"
		    					+ " FROM matching_search_history"
		    					+ " WHERE member_id = #{memberId})"
		    + " </script>")
	 @Results({
	        @Result(property = "memberDTO.no", column = "no"),
	        @Result(property = "memberDTO.gender", column = "gender"),
	        @Result(property = "memberDTO.age", column = "age"),
	        @Result(property = "memberDTO.temperature", column = "temperature"),
	        @Result(property = "memberDTO.cnt", column = "cnt"),
	        @Result(property = "memberDTO.id", column = "id"),
	        @Result(property = "memberDTO.img", column = "img"),
	        @Result(property = "memberDTO.walkingStyleStr", column = "walking_style"),
	        @Result(property = "memberDTO.nickname", column = "nickname"),
	        @Result(property = "dogDTOs", column = "id",
	            javaType = ArrayList.class,
	            many = @Many(select = "banzzac.mapper.MatchingMapper.getMatchingConditionsDogs"))
	    })
	public ArrayList<MatchingDTO> searchMatchingMembers(MatchingDTO matchingDTO);
	
	
	/**
	 * searchMatchingMembers(MatchingDTO matchingDTO) 가 실행되고 결과값으로 실행되는 메소드입니다.*/
	@Select("SELECT * FROM dog WHERE id = #{memberId}")
	
    ArrayList<DogDTO> getMatchingConditionsDogs(String memberId);
	
	/**
	 * 매칭 검색이 끝나면 실행 해야하는 메소드입니다.
	 * 매칭 검색이 없었을 경우를 유효성 검사하여 이 메소드를 실행 해야 합니다.
	 * 이 메소드는 한 번 검색된 회원은 추천하지 않게 하는 메소드입니다.
	 * */
	@Insert("INSERT INTO matching_search_history (member_id, searched_member_id) VALUES (#{memberId}, #{searchedMemberId})")
	public int insertSearchedHistory(String memberId, String searchedMemberId);
}

