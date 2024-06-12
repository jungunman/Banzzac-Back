package banzzac.controll;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuperPathController {
	
	@GetMapping("/")
	public String goToDashBoard() {
		return "redirect:/dashboard";
	}
	
	
}
