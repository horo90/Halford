package ac.kr.halford.dbtemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ac.kr.halford.constants.CommonSql;
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
		
		int filter = SqlInjectionFilter.isFiltered(this.getJdbcTemplate());
		
		if (dq != null && (filter == 0 || ((filter == 1 && !SqlInjectionFilter.isSQLiR(fq, dq)) || (filter == 2 && !SqlInjectionFilter.isSQLiQ(fq, dq))))) {
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
		
//		boolean filter = SqlInjectionFilter.isFiltered(this.getJdbcTemplate());
		int filter = SqlInjectionFilter.isFiltered(this.getJdbcTemplate());
		logger.info("filter : {}", filter);
		
		if (dq != null && (filter == 0 || ((filter == 1 && !SqlInjectionFilter.isSQLiR(fq, dq)) || (filter == 2 && !SqlInjectionFilter.isSQLiQ(fq, dq))))) {
			
//			try {
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
//			} catch (BadSqlGrammarException e) {return null;}
		} else return null;
		
		
	}

	@Override
	public int findFilter() {
		
//		if (this.getJdbcTemplate().queryForObject(CommonSql.findFilter, Integer.class) == 0) return false;
//		else return true;
		return this.getJdbcTemplate().queryForObject(CommonSql.findFilter, Integer.class);
	}

	@Override
	public void updateFilter(int filter) {
		this.getJdbcTemplate().update(CommonSql.updateFilter, new Object[] {filter});
//		if (this.getJdbcTemplate().queryForObject(CommonSql.findFilter, Integer.class) == 0) {
//			this.getJdbcTemplate().update(CommonSql.updateFilter, new Object[] {1});
//		} else this.getJdbcTemplate().update(CommonSql.updateFilter, new Object[] {0});
		
	}

}
