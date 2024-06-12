package banzzac.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class WalkDiaryDTO {
	private String sessionId;
	private int no;
	private int promiseStatus;
	private int memberNo;
    private String comment;
    private String memberNickname;
    private String memberImg;
    private String memberId;
    private String reviewScore;
    private String reviewContent;    
    private String dogName;
    private String dogImg;
    
    
    private String startWalkTimeStr;
    private String endWalkTimeStr;
    private Date startWalkTime;
    private Date endWalkTime;
    
    
	public void setStartWalkTimeStr(String startWalkTimeStr) {
		this.startWalkTimeStr = startWalkTimeStr;
		try {
			this.startWalkTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startWalkTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setStartWalkTime(Date startWalkTime) {
		this.startWalkTime = startWalkTime;
		this.startWalkTimeStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(startWalkTime);
	}

	
	public void setEndWalkTimeStr(String endWalkTimeStr) {
		this.endWalkTimeStr = endWalkTimeStr;
		try {
			this.endWalkTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endWalkTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setEndWalkTime(Date endWalkTime) {
		this.endWalkTime = endWalkTime;
		this.endWalkTimeStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(endWalkTime);
	}
    
    
}
