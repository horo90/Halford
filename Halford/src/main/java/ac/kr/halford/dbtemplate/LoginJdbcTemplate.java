package ac.kr.halford.dbtemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.kr.halford.constants.LoginSql;
import ac.kr.halford.model.MemberModel;

public class LoginJdbcTemplate extends JdbcDaoSupport implements LoginDAO {
	
	
	private static Logger logger = LoggerFactory.getLogger(LoginJdbcTemplate.class);
	
	

	@Override
	public int addMember(MemberModel member) {
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.addMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			this.getJdbcTemplate().update(dq);
			return 0;
		} else return 1;
	}

	@Override
	public MemberModel findMember(MemberModel member) {
		
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.findMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		logger.info("filter : {}", filter);
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			
			
			List<MemberModel> list = this.getJdbcTemplate().query(dq, new RowMapper<MemberModel> () {
					
					@Override
					public MemberModel mapRow(ResultSet rs, int rowNum) throws SQLException {
						MemberModel member = new MemberModel(rs.getString("id"), rs.getString("password"));
						member.setEmpty(false);
						return member;
					}

			});
			
			if (list.size() == 0) {
				member = new MemberModel();
				member.setEmpty(true);
				return member;
			} else {
				return list.get(0);
			}
			
		} else return null;
		
		
	}

}
