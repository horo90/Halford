package ac.kr.halford.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ac.kr.halford.model.MemberModel;

public class LoginJdbcTemplate extends JdbcDaoSupport implements LoginDAO {
	
//	@Autowired
//	private DataSource dataSource;
//	private JdbcTemplate jdbcTemplate;

	@Override
	public void addMember(MemberModel member) {
		// ���� ? ���ϰ�, ���� ����������  new Object ���ص� �ǰ�, �� ��찡 �ſ� ����� ��� �ϵ�.
		this.getJdbcTemplate().update(LoginSql.addMember, new Object[]{member.getId(), member.getPassword()});
		
	}

	@Override
	public MemberModel findMember(MemberModel member) {
		
		return this.getJdbcTemplate().queryForObject(LoginSql.findMember, new Object[]{member.getId(), member.getPassword()}, 
														new RowMapper<MemberModel> () {

															@Override
															public MemberModel mapRow(ResultSet rs, int rowNum) throws SQLException {
																MemberModel member = new MemberModel(rs.getString("id"), rs.getString("password"));
																return member;
															}
			
		});
	}

}
