package ac.kr.halford;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ac.kr.halford.model.MemberModel;
import ac.kr.halford.model.PostModel;
import ac.kr.halford.service.LoginService;
import ac.kr.halford.service.PostService;
import ac.kr.halford.util.Pager;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private PostService postService;
	
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	public String home(HttpServletRequest request, Model model) {
		logger.info("main page");
		
		MemberModel member = new MemberModel();
		model.addAttribute("member", member);
		
		
		if (request.getRequestURI().equals(request.getContextPath()+"/") && request.getSession().getAttribute("id") != null) {
			return "redirect:/boardPage.do";
		}
		
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
	public String join (@ModelAttribute MemberModel member, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		logger.info("join process");
		
		if (loginService.join(member) != 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isSqli", true);
			map.put("message", "SQL injection 공격이 감지되었습니다.");
			redirectAttributes.addFlashAttribute("map", map);
		}
		
		return "redirect:/";
	}
	
	// 나중에 문자열 모두 상수화 시켜야함.
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String login (@ModelAttribute MemberModel member, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
		logger.info("login process");
		
		Map<String, Object> map = new HashMap<String, Object>();
		member = loginService.login(member);
		
		if (member != null) {
			if (!member.isEmpty()) {
				logger.info("loing success");
				map.put("isSqli", false);
				redirectAttributes.addFlashAttribute("map", map);
				return "redirect:/boardPage.do";
			} else {
				logger.info("login failed");
				map.put("isSqli", false);
				map.put("message", "해당 id와 password에 일치하는 정보가 없습니다.");
				redirectAttributes.addFlashAttribute("map", map);
				return "redirect:/";
			}
		} else {
			logger.info("detecting sqli");
			map.put("isSqli", true);
			map.put("message", "SQL injection 공격이 감지되었습니다.");
			redirectAttributes.addFlashAttribute("map", map);
			return "redirect:/";
		}
	}
	
	@RequestMapping(value = "/logout.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String logout (HttpServletRequest request) {
		logger.info("logout process");
		
		loginService.logout();
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/boardPage.do", method={RequestMethod.GET, RequestMethod.POST})
	public String boardPage (HttpServletRequest request) {
		logger.info("boardPage");
		int current = 0;
		Pager pager = new Pager();
		
		if (request.getParameter("current") != null) {
			current = Integer.parseInt(request.getParameter("current"));
		} else current = 1;
		pager.setCurrentPage(current);
		
		postService.setPager(pager);
		logger.info("pager : " + pager.toString());
		
		List<PostModel> postList = postService.findPosts(current);
		
		if (postList == null) logger.info("postList is null");
		else logger.info("postList is not null");
		request.setAttribute("postList", postList);
		request.setAttribute("pager", pager);
		
		return "board/board";
	}
	
	@RequestMapping(value = "/postPage.do", method={RequestMethod.GET, RequestMethod.POST})
	public String postPage (HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		logger.info("postPage-work={}", request.getParameter("work"));
		//********************************
		//	work == 1 : write a new post
		//	work == 2 : show the post
		//	work == 3 : modify the post
		//********************************
		int work= 0;
		int postId = -1;
		try {
			work = Integer.parseInt(request.getParameter("work"));
		} catch (NumberFormatException e1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isSqli", false);
			map.put("message", "비정상적인 접근입니다.");
			redirectAttributes.addFlashAttribute("map", map);
			return "redirect:/boardPage.do";
		}
		
		request.setAttribute("work", work);
		
		PostModel post = new PostModel();
		
		if (work == 1) {
			postService.setPostModel(post);
			model.addAttribute("post", post);
		} else {
			try {
				postId = Integer.parseInt(request.getParameter("id"));
			} catch (NumberFormatException e1) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isSqli", false);
				map.put("message", "비정상적인 접근입니다.");
				redirectAttributes.addFlashAttribute("map", map);
				return "redirect:/boardPage.do";
			}
			
			if (work == 3) {														//	new post & modify the post
				post.setPostId(postId);
				post = postService.findCertainPost(post);
				
				if (post.isEmpty()) {								// postId가 숫자 이지만, db 안에 존재하지 않는 것을 사용할 경우.
					post.setContents("해당하는 게시물이 없습니다.");
				}
				
				model.addAttribute("post", post);
			} else if (work  == 2) {												// show the post
					postId = Integer.parseInt(request.getParameter("id"));
					
					post.setPostId(postId);
					
					post = postService.findCertainPost(post);
					
					if (post.isEmpty()) {								// postId가 숫자 이지만, db 안에 존재하지 않는 것을 사용할 경우.
						logger.info("해당 게시물 x");
						post.setContents("해당하는 게시물이 없습니다.");
					}
					request.setAttribute("post", post);
				
			} else {
				// abnormal access
			}
		}
		
		return "board/post";
	}
	
	@RequestMapping(value = "/post.do", method={RequestMethod.POST, RequestMethod.GET})
	public String post (@ModelAttribute PostModel post, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		logger.info("post");
		logger.info(post.toString());
		
		int work = 0;
		try {
			work = Integer.parseInt(request.getParameter("work"));
		} catch (NumberFormatException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isSqli", false);
			map.put("message", "비정상적인 접근입니다.");
			redirectAttributes.addFlashAttribute("map", map);
			return "redirect:/boardPage.do";
		}
		
		logger.info("work : {}", work);
		if (work == 1) {				//	new post
			postService.setPostModel(post);
			if (postService.addPost(post) != 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isSqli", true);
				map.put("message", "SQL injection 공격이 감지되었습니다.");
				redirectAttributes.addFlashAttribute("map", map);
			}
		} else if (work == 3) {			//	modify post
			if (postService.updatePost(post) != 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isSqli", true);
				map.put("message", "SQL injection 공격이 감지되었습니다.");
				redirectAttributes.addFlashAttribute("map", map);
			}
		} else if (work == 4) {			// delete post
			if (request.getParameter("id") != null) {
				int postId;
				try {
					postId = Integer.parseInt(request.getParameter("id"));
					post = new PostModel();
					post.setPostId(postId);
					if (postService.deletePost(post) != 0) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("isSqli", true);
						map.put("message", "SQL injection 공격이 감지되었습니다.");
						redirectAttributes.addFlashAttribute("map", map);
					}
				} catch (NumberFormatException e) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("isSqli", false);
					map.put("message", "비정상적인 접근입니다.");
					redirectAttributes.addFlashAttribute("map", map);
				}
				
			} else {
				//	abnormal access
			}
		} else {
			//	abnormal access
		}
		
		return "redirect:/boardPage.do";
	}
}
