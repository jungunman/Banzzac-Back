package banzzac.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class ReviewDTO {
	
	private int no,reviewScore;
	private String myId, yourId,content,regDateStr;
	private Date regDate;
	
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
		this.regDateStr = new SimpleDateFormat("yyyy-MM-dd").format(regDate);
	}
}
