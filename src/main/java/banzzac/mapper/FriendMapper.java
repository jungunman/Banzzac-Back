package banzzac.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.FriendDTO;
import banzzac.dto.MemberDTO;


@Mapper
public interface FriendMapper {

//    	//* 친구리스트*/
	@Select("SELECT "
	        + "    f.id, "
	        + "    f.friend_id, "
	        + "    f.block, "
	        + "    m.nickname AS m_nickname, "
	        + "    m.img AS m_img, "
	        + "    m.status_message AS m_status_message, "
	        + "    MIN(d.name) AS dog_name, "  
	        + "    MIN(d.img) AS dog_img "
	        + "FROM "
	        + "    member m "
	        + "JOIN "
	        + "    friend f ON m.id = f.friend_id "
	        + "JOIN "
	        + "    dog d ON m.id = d.id "
	        + "WHERE "
	        + "    f.id = #{id} AND (f.block = 1) "
	        + "GROUP BY "
	        + "    f.id, f.friend_id, f.block, m.nickname, m.img, m.status_message")
	List<FriendDTO> list(String id);
	
	//* 차단 친구 리스트 */
	@Select("SELECT "
	        + "    f.id, "
	        + "    f.friend_id, "
	        + "    f.block, "
	        + "    m.nickname AS m_nickname, "
	        + "    m.img AS m_img, "
	        + "    m.status_message AS m_status_message, "
	        + "    MIN(d.name) AS dog_name, "  
	        + "    MIN(d.img) AS dog_img " 
	        + "FROM "
	        + "    member m "
	        + "JOIN "
	        + "    friend f ON m.id = f.friend_id "
	        + "JOIN "
	        + "    dog d ON m.id = d.id "
	        + "WHERE "
	        + "    f.id = #{id} AND f.block = 0 "
	        + "GROUP BY "
	        + "    f.id, f.friend_id, f.block, m.nickname, m.img, m.status_message")
	List<FriendDTO> blockList(String id);
	
	//* 즐겨찾기 친구 리스트*/
	@Select("SELECT "
	        + "    f.id, "
	        + "    f.friend_id, "
	        + "    f.block, "
	        + "    m.nickname AS m_nickname, "
	        + "    m.img AS m_img, "
	        + "    m.status_message AS m_status_message, "
	        + "    MIN(d.name) AS dog_name, "  
	        + "    MIN(d.img) AS dog_img "  
	        + "FROM "
	        + "    member m "
	        + "JOIN "
	        + "    friend f ON m.id = f.friend_id "
	        + "JOIN "
	        + "    dog d ON m.id = d.id "
	        + "WHERE "
	        + "    f.id = #{id} AND f.block = 2 "
	        + "GROUP BY "
	        + "    f.id, f.friend_id, f.block, m.nickname, m.img, m.status_message")
	List<FriendDTO> favoriteList(String id);
	
	//* 친구차단 */
	@Update("update friend set block=0 where id= #{id} and friend_id= #{friendId}") //차단
	int friendBlock(FriendDTO dto);
	
	//* 친구차단해제 */
	@Update("update friend set block=1 where id=#{id} and friend_id= #{friendId}") //차단해제
	int friendUnBlock(FriendDTO dto);
	
	
	//* 즐겨찾기친구추가 */
	@Update("update friend set block=2 where id=#{id} and friend_id= #{friendId}") //즐찾
	int friendFavorite(FriendDTO dto);
	
	//* 즐겨찾기친구해제 */
	@Update("update friend set block=1 where id=#{id} and friend_id= #{friendId}") //즐찾해제
	int friendUnFavorite(FriendDTO dto);
	
	//*친구삭제*/
	@Delete("delete from friend where id=#{id} and friend_id=#{friendId}")
	int delete(FriendDTO dto);
	
	//*친구프로필*/
	@Select("select * from member where id=#{friendId}")
	@Result(property = "walkingStyleStr",column = "walking_style" )
	MemberDTO friendProfile(String friendId);
	
	
	
	
	
		
}
		