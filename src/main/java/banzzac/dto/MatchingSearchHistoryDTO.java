package banzzac.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;
@Data
@Component
public class MatchingSearchHistoryDTO {
    private String memberId;
    private String searchedMemberId;
    private String searchDateStr;
    private Date searchDate;
    
    
	public void setSearchDateStr(String searchDateStr) {
		this.searchDateStr = searchDateStr;
		try {
			this.searchDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(searchDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
		this.searchDateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(searchDateStr);
	}
    
    
    
}
