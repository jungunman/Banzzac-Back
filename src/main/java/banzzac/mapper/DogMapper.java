package banzzac.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.DogDTO;

@Mapper
public interface DogMapper {
	//** 반려견  등록*/
	@Insert("insert into dog "
			+"(id,name,age,img,gender,weight,neutrification,size,kind,personality,activity) "
			+ "values "
			+"(#{id},#{name},#{age},#{img},#{gender},#{weight},#{neutrification},#{size},#{kind},#{personality},#{activity})"
			)
	int createDog(DogDTO dto);
	
	
	/** 내 반려견 전체 리스트 불러오기  -> id = sessionID */
	@Select("select * from dog where id = #{id}")
	ArrayList<DogDTO> list(DogDTO dto);
	
	
	/** 반려견 추가 시 같은 정보가 있는지 확인 */
	@Select("select id,name from dog where id = #{id} && name = #{name}")
	DogDTO checkDog(DogDTO dto);
	
	
	/** 반려견 수정 -> id = sessionID && name = #{name} */
	@Update("update dog set "
			+ "age=#{age}, img=#{img}, weight=#{weight}, "
			+ "neutrification=#{neutrification}, size=#{size}, "
			+ "personality=#{personality}, activity=#{activity} "
			+ "where id = #{id} && name = #{name}")
	int modifyDog(DogDTO dto);

	
	/** 반려견 삭제 -> id = sessionID && name = #{name} */
	@Delete("delete from dog where id = #{id} && name = #{name}")
	int deleteDog(DogDTO dto);
	
}
		