package demo.springbootthymeleaf;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
public class IndexController {
	
	@RequestMapping("/")
	public String index() {
		
		//return "index";
		return "redirect:/springboot-thymeleaf/login";
	}
	
	
	/*
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "login"; 
	}
	 */
	 
	
	
	

}
