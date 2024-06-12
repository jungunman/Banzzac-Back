package banzzac.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.MemberDTO;
import banzzac.dto.ReportDTO;

@Mapper
public interface MemberMapper {
	
	
	/** 신규 회원가입*/
	@Insert("insert into member "
			+"(id,pwd,gender,age,img,walking_style,nickname,phone) values "
			+"(#{id},#{pwd},#{gender},#{age},#{img},#{walkingStyleStr},#{nickname},#{phone})")
	@Options(useGeneratedKeys=true, keyProperty="no")
	int createMember(MemberDTO dto);
 
	
	/** 로그인한 회원의 정보 불러오기 */
	@Select("select "
			+ "no,id,gender,age,img,walking_style,nickname,"
			+ "phone,cnt,isGrant,quantity,status_message,date,temperature "
			+ "from member where id = #{id}")
	@Result(property = "walkingStyleStr",column = "walking_style" )
	ArrayList<MemberDTO> memberInfo(MemberDTO dto);
	
	MemberDTO afterCreat();
	/** 회원 개인정보 수정 */
	@Update("update `member` set pwd=#{pwd}, img=#{img},status_message = #{statusMessage}, "
			+ "walking_style=#{walkingStyleStr}, nickname=#{nickname}, phone=#{phone} where id = #{id}")
	int modifyMember(MemberDTO dto);

	/** 상태 메시지 수정 */
	@Update("update `member` "
			+ "set status_message = #{statusMessage} "
			+ "where id = #{id}")
	int modifyStatus(MemberDTO dto);
	

	/** 리뷰 받은 후 온도, 리뷰 받은 수 변경 
	  Temperature = Temperature + review_score*20 / cnt = cnt+review insert 갯수
	 */
	//@Update("update `member` set Temperature=#{Temperature} , cnt=#{cnt} where member table의 id = review table의 your_id일 경우")
	int modifyOndo(MemberDTO dto);
	
	/** 회원탈퇴 시 권한 0으로 변경 */
	@Update("update `member` set isGrant = 0 where id = #{id} && pwd=#{pwd}")
	int withdrawMember(MemberDTO dto);

	
	/**
	 * 멤버가 멤버를 신고하는 SQL 작성자 : 정운만
	 * */
	
	@Insert("INSERT INTO report"
			+ " (member_id, reported_id, report_reason)"
			+ " VALUES"
			+ " (#{memberId},#{reportedId},#{reportReason})")
	public int reportMember(ReportDTO dto);

	
	
	

}
		