package ac.kr.halford;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ac.kr.halford.model.MemberModel;
import ac.kr.halford.service.LoginService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	public String home(Model model) {
		logger.info("main page");
		
		MemberModel member = new MemberModel();
		model.addAttribute("member", member);
		
		return "home";
	}
	
	@RequestMapping(value = "/joinPage.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String joinPage (HttpServletRequest request, Model model) {
		logger.info("join page");
		
		MemberModel member = new MemberModel();
		
		model.addAttribute("member", member);
		
		return "join/join";
	}
	
	@RequestMapping(value = "/join.do", method = RequestMethod.POST)
	public String join (@ModelAttribute MemberModel member, HttpServletRequest request, Model model) {
		logger.info("join process");
		
		loginService.join(member);
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String login (@ModelAttribute MemberModel member, HttpServletRequest request, Model model) {
		logger.info("login process");
		
		if (loginService.login(member) != null) {
			logger.info("loing success");
			
			return "redirect:/boardPage.do";
		} else {
			logger.info("login failed");
			return "redirect:/";
		}
	}
	
	@RequestMapping(value = "/boardPage.do", method={RequestMethod.GET, RequestMethod.POST})
	public String boardPage (HttpServletRequest reqeust, Model model) {
		logger.info("boardPage");
		
		return "board/board";
	}
}
