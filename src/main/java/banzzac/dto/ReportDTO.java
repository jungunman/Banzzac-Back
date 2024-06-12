package banzzac.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component
public class ReportDTO {
	private int reportNo;
	private String reportReason;
	private Date reportTime;
	private int reportStatus;
	private String reportAdminAnswer;
	private String memberId, reportedId;
	
	public String getReportTimeStr() {
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      return sdf.format(reportTime);
	   }
	
	
}
