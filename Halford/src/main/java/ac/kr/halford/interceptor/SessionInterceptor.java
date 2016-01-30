package ac.kr.halford.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionInterceptor extends HandlerInterceptorAdapter {
	
	private Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("session interceptor");
		
		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("id") == null) {
			logger.info("no session");
			response.sendRedirect(request.getContextPath() + "/");
			return false;
		} else {
			logger.info(session.getAttribute("id").toString());
		}
		
		return super.preHandle(request, response, handler);
	}

}
