package banzzac.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;

@Data
public class SalesManagementDTO {
	private int orderCnt,totalAmount, quantity;
	private Date dailyRange;
	private int month;
	private int year;
	private int refundStatus, orderId;
	private String reason, userId, tid;
	private Date refundRequest, refundApprove, payDate;
	private String refundRequestStr, refundApproveStr, payDateStr,dailyRangeStr;
	private int ranking;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	
	public void setRefundRequest(Date refundRequest) {
		this.refundRequest = refundRequest;
		this.refundRequestStr = sdf.format(refundRequest);
	}
	public void setRefundApprove(Date refundApprove) {
		this.refundApprove = refundApprove;
		this.refundApproveStr = sdf.format(refundApprove);
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
		this.payDateStr = sdf.format(payDate);
	}
	public void setRefundRequestStr(String refundRequestStr) {
		this.refundRequestStr = refundRequestStr;
		try {
			this.refundRequest = sdf.parse(refundRequestStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setRefundApproveStr(String refundApproveStr) {
		this.refundApproveStr = refundApproveStr;
		try {
			this.refundApprove = sdf.parse(refundApproveStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setPayDateStr(String payDateStr) {
		this.payDateStr = payDateStr;
		try {
			this.payDate = sdf.parse(payDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setDailyRange(Date dailyRange) {
		this.dailyRange = dailyRange;
		this.dailyRangeStr = new SimpleDateFormat("yyyy-MM-dd").format(dailyRange);
	}

	public void setDailyRangeStr(String dailyRangeStr) {
		this.dailyRangeStr = dailyRangeStr;
		try {
			this.dailyRange = new SimpleDateFormat("yyyy-MM-dd").parse(dailyRangeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
