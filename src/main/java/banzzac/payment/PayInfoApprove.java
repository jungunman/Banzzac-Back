package banzzac.payment;

import lombok.Data;

@Data
public class PayInfoApprove {
	
	private String tid;
	private String nextRedirect_mobile_url;
	private String next_redirect_pc_url;

}
