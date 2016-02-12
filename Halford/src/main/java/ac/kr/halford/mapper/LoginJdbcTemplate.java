package ac.kr.halford.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ac.kr.halford.model.MemberModel;

public class LoginJdbcTemplate extends JdbcDaoSupport implements LoginDAO {
	
	
	private static Logger logger = LoggerFactory.getLogger(LoginJdbcTemplate.class);

	@Override
	public void addMember(MemberModel member) {
		// 여기 ? 안하고, 직접 때려넣으면  new Object 안해도 되고, 이 경우가 매우 취약한 경우 일듯.
		
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.addMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		if (SqlInjectionFilter.isSQLi(fq, dq)) {
			this.getJdbcTemplate().update(dq);
		}
		
		//나중에는 return value를 줘야할듯
	}

	@Override
	public MemberModel findMember(MemberModel member) {
		
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.findMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		//****************************************
		//	정상 쿼리에 대해
		//	resultset이 있는 경우, 해당하는 model 리턴.
		//	resultset이 없으면, empty model 리턴.
		//	
		//	sqli가 감지된 경우, null 리턴.
		//	본 애플리케니션에서 공톤으로 적용
		//****************************************
		if (SqlInjectionFilter.isSQLi(fq, dq)) {
			
			try {
				return this.getJdbcTemplate().queryForObject(dq, new RowMapper<MemberModel> () {
					
					@Override
					public MemberModel mapRow(ResultSet rs, int rowNum) throws SQLException {
						MemberModel member = new MemberModel(rs.getString("id"), rs.getString("password"));
						member.setEmpty(false);
						return member;
					}

				});
			} catch (EmptyResultDataAccessException e) {
				member = new MemberModel();
				member.setEmpty(true);
				return member;
			}
		} else return null;
		
		
	}

}
