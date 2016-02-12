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
		// ���� ? ���ϰ�, ���� ����������  new Object ���ص� �ǰ�, �� ��찡 �ſ� ����� ��� �ϵ�.
		
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.addMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		if (SqlInjectionFilter.isSQLi(fq, dq)) {
			this.getJdbcTemplate().update(dq);
		}
		
		//���߿��� return value�� ����ҵ�
	}

	@Override
	public MemberModel findMember(MemberModel member) {
		
		Object[] params = new Object[]{member.getId(), member.getPassword()};
		String fq = LoginSql.findMember;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		//****************************************
		//	���� ������ ����
		//	resultset�� �ִ� ���, �ش��ϴ� model ����.
		//	resultset�� ������, empty model ����.
		//	
		//	sqli�� ������ ���, null ����.
		//	�� ���ø��ɴϼǿ��� �������� ����
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
