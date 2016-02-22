package ac.kr.halford;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ac.kr.halford.constants.Messages;
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
		model.addAttribute(Messages.memberKey, member);
		
		
		if (request.getRequestURI().equals(request.getContextPath()+"/") && request.getSession().getAttribute("id") != null) {
			return "redirect:/boardPage.do";
		}
		
		return "home";
	}
	
	@RequestMapping(value = "/joinPage.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String joinPage (HttpServletRequest request, Model model) {
		logger.info("join page");
		
		MemberModel member = new MemberModel();
		
		model.addAttribute(Messages.memberKey, member);
		
		return "join/join";
	}
	
	@RequestMapping(value = "/join.do", method = RequestMethod.POST)
	public String join (@ModelAttribute MemberModel member, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		logger.info("join process");
		logger.info(member.toString());
		
		if (loginService.join(member) != 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Messages.isSqliKey, true);
			map.put(Messages.messageKey, Messages.detectSqli);
			redirectAttributes.addFlashAttribute(Messages.mapKey, map);
		}
		
		return "redirect:/";
	}
	
	// ���߿� ���ڿ� ��� ���ȭ ���Ѿ���.
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String login (@ModelAttribute MemberModel member, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
		logger.info("login process");
		logger.info(member.toString());
		
		Map<String, Object> map = new HashMap<String, Object>();
		member = loginService.login(member);
		
		if (member != null) {
			if (!member.isEmpty()) {
				logger.info("loing success");
				return "redirect:/boardPage.do";
			} else {
				logger.info("login failed");
				map.put(Messages.isSqliKey, false);
				map.put(Messages.messageKey, Messages.noMember);
				redirectAttributes.addFlashAttribute(Messages.mapKey, map);
				return "redirect:/";
			}
		} else {
			logger.info("detecting sqli");
			map.put(Messages.isSqliKey, true);
			map.put(Messages.messageKey, Messages.detectSqli);
			redirectAttributes.addFlashAttribute(Messages.mapKey, map);
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
		
		if (request.getParameter(Messages.currentkey) != null) {
			current = Integer.parseInt(request.getParameter(Messages.currentkey));
		} else current = 1;
		pager.setCurrentPage(current);
		
		postService.setPager(pager);
		logger.info("pager : " + pager.toString());
		
		List<PostModel> postList = postService.findPosts(current);
		
		request.setAttribute(Messages.postListKey, postList);
		request.setAttribute(Messages.pagerKey, pager);
		
		return "board/board";
	}
	
	@RequestMapping(value = "/postPage.do", method={RequestMethod.GET, RequestMethod.POST})
	public String postPage (HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		logger.info("postPage-work={}", request.getParameter(Messages.workKey));
		//********************************
		//	work == 1 : write a new post
		//	work == 2 : show the post
		//	work == 3 : modify the post
		//********************************
		int work= 0;
		int postId = -1;
		try {
			work = Integer.parseInt(request.getParameter(Messages.workKey));
		} catch (NumberFormatException e1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Messages.isSqliKey, false);
			map.put(Messages.messageKey, Messages.abnormalAccess);
			redirectAttributes.addFlashAttribute(Messages.mapKey, map);
			return "redirect:/boardPage.do";
		}
		
		request.setAttribute(Messages.workKey, work);
		
		PostModel post = new PostModel();
		
		if (work == 1) {
			postService.setPostModel(post);
			model.addAttribute(Messages.postKey, post);
		} else {
			try {
				postId = Integer.parseInt(request.getParameter(Messages.idKey));
			} catch (NumberFormatException e1) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(Messages.isSqliKey, false);
				map.put(Messages.messageKey, Messages.abnormalAccess);
				redirectAttributes.addFlashAttribute(Messages.mapKey, map);
				return "redirect:/boardPage.do";
			}
			
			if (work == 3) {
				post.setPostId(postId);
				post = postService.findCertainPost(post);
				
				if (post.isEmpty()) post.setContents(Messages.noPost);
				
				model.addAttribute(Messages.postKey, post);
			} else if (work  == 2) {												// show the post
					post.setPostId(postId);
					post = postService.findCertainPost(post);
					
					if (post.isEmpty()) post.setContents(Messages.noPost);
					
					request.setAttribute(Messages.postKey, post);
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
			work = Integer.parseInt(request.getParameter(Messages.workKey));
		} catch (NumberFormatException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Messages.isSqliKey, false);
			map.put(Messages.messageKey, Messages.abnormalAccess);
			redirectAttributes.addFlashAttribute(Messages.mapKey, map);
			return "redirect:/boardPage.do";
		}
		
		logger.info("work : {}", work);
		if (work == 1) {				//	new post
			postService.setPostModel(post);
			if (postService.addPost(post) != 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(Messages.isSqliKey, true);
				map.put(Messages.messageKey, Messages.detectSqli);
				redirectAttributes.addFlashAttribute(Messages.mapKey, map);
			}
		} else if (work == 3) {			//	modify post
			if (postService.updatePost(post) != 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(Messages.isSqliKey, true);
				map.put(Messages.messageKey, Messages.detectSqli);
				redirectAttributes.addFlashAttribute(Messages.mapKey, map);
			}
		} else if (work == 4) {			// delete post
			if (request.getParameter(Messages.idKey) != null) {
				int postId;
				try {
					postId = Integer.parseInt(request.getParameter(Messages.idKey));
					post = new PostModel();
					post.setPostId(postId);
					if (postService.deletePost(post) != 0) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(Messages.isSqliKey, true);
						map.put(Messages.messageKey, Messages.detectSqli);
						redirectAttributes.addFlashAttribute(Messages.mapKey, map);
					}
				} catch (NumberFormatException e) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(Messages.isSqliKey, false);
					map.put(Messages.messageKey, Messages.abnormalAccess);
					redirectAttributes.addFlashAttribute(Messages.mapKey, map);
				}
				
			} else {
				//	abnormal access
			}
		} else {
			//	abnormal access
		}
		
		return "redirect:/boardPage.do";
	}
	
	@RequestMapping(value = "/filterToggle.do", method = RequestMethod.GET)
	public String toggleFilter (HttpServletRequest request) {
		logger.info("toggle");
		
		HttpSession session = request.getSession();
		
		// if filter is off, 0
		// if reg filter is on, 1
		// if quote filter is on, 2
		try {
			int filter = Integer.parseInt(request.getParameter("filter"));
			logger.info(filter + "");
			loginService.updateFilter(filter);
			session.setAttribute(Messages.filterKey, filter);
		} catch (NumberFormatException e) {
			
			//
		}
		
//		loginService.updateFilter();
//		int filter = loginService.findFilter();
//		session.setAttribute(Messages.filterKey, filter);
		
		return "redirect:/";
	}
}
