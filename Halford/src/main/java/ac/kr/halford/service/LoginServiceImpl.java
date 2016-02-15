package ac.kr.halford.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.kr.halford.constants.Messages;
import ac.kr.halford.dbtemplate.LoginJdbcTemplate;
import ac.kr.halford.model.MemberModel;

@Service("LoginService")
@Transactional
public class LoginServiceImpl implements LoginService {
	
	private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private LoginJdbcTemplate loginJdbcTemplate;

	@Override
	public int join(MemberModel member) {
		logger.info("LoginService-join");
		
		return loginJdbcTemplate.addMember(member);
	}

	@Override
	public MemberModel login(MemberModel member) {
		logger.info("LoginService-login");
		logger.info(member.toString());
		
		member = loginJdbcTemplate.findMember(member);
		
		if (member != null) {	// ���� ����
			if (!member.isEmpty()) {
				ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
				HttpSession session = attr.getRequest().getSession();
				session.setAttribute("id", member.getId());
			} else ;	// �������� login ����
			
		} else ;	//sqli
		
		return member;
	}

	@Override
	public void logout() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		session.setAttribute(Messages.idKey, null);
	}

}
