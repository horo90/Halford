package ac.kr.halford;

import java.util.List;

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
	public String postPage (HttpServletRequest request, Model model) {
		logger.info("postPage-work={}", request.getParameter("work"));
		//********************************
		//	work == 1 : write a new post
		//	work == 2 : show the post
		//	work == 3 : modify the post
		//********************************
		int work= 0;
		
		if (request.getParameter("work") != null) {
			work = Integer.parseInt(request.getParameter("work"));
			request.setAttribute("work", work);
			
			PostModel post = new PostModel();
			
			if (work == 1 || work == 3) {														//	new post & modify the post
				
				if (work == 1) {
					postService.setPostModel(post);
					
				} else {
					if (request.getParameter("id") != null) {
						int postId = Integer.parseInt(request.getParameter("id"));
						post.setPostId(postId);
						post = postService.findCertainPost(post);
						logger.info(post.toString());
					} else {
						//	abnormal access
					}
				}
				
				model.addAttribute("post", post);
			} else if (work  == 2) {												// show the post
				if (request.getParameter("id") != null) {
					int postId = Integer.parseInt(request.getParameter("id"));
					post.setPostId(postId);
					
					post = postService.findCertainPost(post);
					logger.info(post.toString());
					//null chceck
					request.setAttribute("post", post);
				} else {
					//	abnormal access
				}
			} else {
				// abnormal access
			}
		}
		
		return "board/post";
	}
	
	@RequestMapping(value = "/post.do", method={RequestMethod.POST, RequestMethod.GET})
	public String post (@ModelAttribute PostModel post, HttpServletRequest request) {
		logger.info("post");
		logger.info(post.toString());
		
		if (request.getParameter("work") != null) {
			int work = Integer.parseInt(request.getParameter("work"));
			logger.info("work : {}", work);
			if (work == 1) {				//	new post
				postService.setPostModel(post);
				postService.addPost(post);
			} else if (work == 3) {			//	modify post
				postService.updatePost(post);
			} else if (work == 4) {			// delete post
				if (request.getParameter("id") != null) {
					int postId = Integer.parseInt(request.getParameter("id"));
					post = new PostModel();
					post.setPostId(postId);
					postService.deletePost(post);
				} else {
					//	abnormal access
				}
			} else {
				//	abnormal access
			}
		} else {
			//abnormal access
		}
		
		return "redirect:/boardPage.do";
	}
}
