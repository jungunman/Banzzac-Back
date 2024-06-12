package banzzac.payment;

import java.util.Date;

import lombok.Data;

@Data
public class PaySuccessInfo {
	private String aid;
	private String tid;
	private String payment_method_type;
	private Object card_info; // push 알림 보내줄거면
	private int quantity;
	private Date approved_at;

}
