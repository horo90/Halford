package ac.kr.halford.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.kr.halford.dbtemplate.LoginJdbcTemplate;
import ac.kr.halford.model.MemberModel;

@Service("LoginService")
@Transactional
public class LoginServiceImpl implements LoginService {
	
	private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private LoginJdbcTemplate loginJdbcTemplate;
//	private LoginMapper loginMapper;

	@Override
	public int join(MemberModel member) {
		logger.info("LoginService-join");
		
		return loginJdbcTemplate.addMember(member);
//		loginMapper.addMember(member);
	}

	@Override
	public MemberModel login(MemberModel member) {
		logger.info("LoginService-login");
		logger.info(member.toString());
		
//		member = loginMapper.findMember(member);
		member = loginJdbcTemplate.findMember(member);
		
		if (member != null) {	// 정상 쿼리
			if (!member.isEmpty()) {
				ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
				HttpSession session = attr.getRequest().getSession();
				session.setAttribute("id", member.getId());
			} else ;	// 정상적인 login 실패
			
		} else ;	//sqli
		
		return member;
	}

	@Override
	public void logout() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		session.setAttribute("id", null);
	}

}
