package banzzac.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import banzzac.dto.ChatDTO;
import banzzac.dto.ChatroomDTO;

@Mapper
public interface ChatMapper {

	@Select("SELECT "
			+ "    cm.*, "
			+ "    sender.nickname AS senderNickname, "
			+ "    receiver.nickname AS receiverNickname "
			+ "FROM  "
			+ "    chat_message cm "
			+ "INNER JOIN  "
			+ "    member sender ON cm.sender_id = sender.id "
			+ "INNER JOIN  "
			+ "    member receiver ON cm.receiver_id = receiver.id "
			+ "WHERE "
			+ "    cm.chatroom_no = #{chatroomNo} "
			+ "ORDER BY "
			+ "    cm.send_time")
	ArrayList<ChatDTO> getChatList(int chatroomNo); 
	
	
	
	@Insert("insert into chat_message "
			+ "(sender_id, receiver_id, message, send_time, send_img, chatroom_no) values "
			+ "(#{senderId},#{receiverId},#{message}, sysdate(), #{sendImg}, #{chatroomNo} ) ")
	int insertChat(ChatDTO dto);
	
	@Update("update chatroom set last_message = #{message} , last_message_sendtime = #{sendTime} where chatroom_no = #{chatroomNo} ")
	int changeLastMessage(ChatDTO dto);
	
	@Update("update chat_message set is_read = 0 where receiver_id = #{userId} and chatroom_no  = #{chatroomNo}")
	int changeIsRead(String userId, int chatroomNo);
	
	
	@Select("SELECT "
			+ "    c.*, "
			+ "    CASE "
			+ "        WHEN c.room_member_1 = #{userId} THEN m2.id "
			+ "        WHEN c.room_member_2 = #{userId} THEN m1.id "
			+ "    END AS opponent_id, "
			+ "    CASE "
			+ "        WHEN c.room_member_1 = #{userId} THEN m2.nickname"
			+ "        WHEN c.room_member_2 = #{userId} THEN m1.nickname"
			+ "    END AS member_nickname, "
			+ "    CASE "
			+ "        WHEN c.room_member_1 = #{userId} THEN m2.img "
			+ "        WHEN c.room_member_2 = #{userId} THEN m1.img "
			+ "    END AS member_img, "
			+ "    CASE "
			+ "        WHEN c.room_member_1 = #{userId} THEN d2.name "
			+ "        WHEN c.room_member_2 = #{userId} THEN d1.name "
			+ "    END AS dog_name, "
			+ "    CASE "
			+ "        WHEN c.room_member_1 = #{userId} THEN d2.img "
			+ "        WHEN c.room_member_2 = #{userId} THEN d1.img "
			+ "    END AS dog_img, "
			+ "    COUNT(cm.is_read) AS unread_messages_count "
			+ "FROM "
			+ "    chatroom c "
			+ "LEFT OUTER JOIN "
			+ "    member m1 ON m1.id = c.room_member_1 "
			+ "LEFT OUTER JOIN "
			+ "    member m2 ON m2.id = c.room_member_2 "
			+ "LEFT OUTER JOIN "
			+ "    dog d1 ON d1.id = c.room_member_1 "
			+ "LEFT OUTER JOIN "
			+ "    dog d2 ON d2.id = c.room_member_2 "
			+ "LEFT OUTER JOIN "
			+ "    chat_message cm ON cm.chatroom_no = c.chatroom_no AND cm.is_read = 1 "
			+ "WHERE "
			+ "    c.room_member_1 = #{userId} OR c.room_member_2 = #{userId} "
			+ "GROUP BY "
			+ "    c.chatroom_no "
			+ "ORDER BY "
			+ "    c.last_message_sendtime DESC " )
	ArrayList<ChatroomDTO> getChatroomList(String userId); 
	
	
	@Insert("insert into chatroom "
			+ "(room_member_1, room_member_2) values "
			+ "(#{roomMember1},#{roomMember1} ) ")
	int insertChatroom(ChatroomDTO dto);
	
	@Update("UPDATE chatroom "
			+ "SET "
			+ "    room_member_1 = CASE WHEN room_member_1 = #{id} THEN NULL ELSE room_member_1 END, "
			+ "    room_member_2 = CASE WHEN room_member_2 = #{id} THEN NULL ELSE room_member_2 END "
			+ "WHERE "
			+ "    chatroom_no = #{no} ")
	int outChatroom(String id, int no);
	
	@Select("select * from chatroom where chatroom_no = #{chatroomNo}")
	ChatroomDTO getChatroomMember(int chatroomNo);
	
	@Update("update friend set block = 0 where id = #{memberId} and friend_id = #{oppId}")
	int blockUser(String memberId, String oppId);
	
}
