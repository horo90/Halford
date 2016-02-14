package ac.kr.halford.dbtemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ac.kr.halford.constants.PostSql;
import ac.kr.halford.model.PostModel;

public class PostJdbcTemplate extends JdbcDaoSupport implements PostDAO {
	
	private static Logger logger = LoggerFactory.getLogger(PostJdbcTemplate.class);
	

	@Override
	public int addPost(PostModel post) {
		Object[] params = new Object[] {post.getMemberId(), post.getTitle(), post.getContents(), post.getDate()};
		String fq = PostSql.addPost;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			this.getJdbcTemplate().update(dq);
			return 0;
		} else return 1;
	}

	@Override
	public int deleteCertainPost(PostModel post) {
		Object[] params = new Object[] {post.getPostId()};
		String fq = PostSql.deleteCertainPost;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			this.getJdbcTemplate().update(dq);
			return 0;
		} else return 1;
	}

	@Override
	public int updateCertainPost(PostModel post) {
		Object[] params = new Object[] {post.getTitle(), post.getContents(), post.getDate(), post.getPostId()};
		String fq = PostSql.updateCertainPost;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			this.getJdbcTemplate().update(dq);
			return 0;
		} else return 1;
	}

	@Override
	public PostModel findCertainPost(PostModel post) {
		Object[] params = new Object[] {post.getPostId()};
		String fq = PostSql.findCertainPost;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {
			//if want to get single row, It's correct using queryForObject() method -> only return single row.
			// but in this case, for developing vulnerable web application, use query() method-> allow return multi rows. 
			List<PostModel> list = this.getJdbcTemplate().query(dq, new RowMapper<PostModel> () {
				@Override
				public PostModel mapRow(ResultSet rs, int rowNum) throws SQLException {
					PostModel post = new PostModel();
					post.setPostId(rs.getInt("post_id"));
					post.setMemberId(rs.getString("member_id"));
					post.setTitle(rs.getString("title"));
					post.setContents(rs.getString("contents"));
					post.setDate(rs.getString("post_date"));
					post.setEmpty(false);
					return post;
				}

			});
			if (list.size() == 0) {
				post = new PostModel();
				post.setEmpty(true);
				return post;
			} else {
				return list.get(0);
			}
			
		} else return null;
	}

	@Override
	public List<PostModel> findPosts(Map<String, Object> map) {
		
		Object[] params = new Object[] {map.get("current")};
		String fq = PostSql.findPosts;
		String dq = SqlInjectionFilter.getBoundSql(fq, params);
		
		logger.info(dq);
		
		boolean filter = SqlInjectionFilter.isFiltered();
		
		if (!filter || (filter && !SqlInjectionFilter.isSQLi(fq, dq))) {			try {
				return this.getJdbcTemplate().query(dq, 
						new RowMapper<PostModel> () {

							@Override
							public PostModel mapRow(ResultSet rs, int rowNum) throws SQLException {
								PostModel post = new PostModel();
								post.setPostId(rs.getInt("post_id"));
								post.setMemberId(rs.getString("member_id"));
								post.setTitle(rs.getString("title"));
								post.setContents(rs.getString("contents"));
								post.setDate(rs.getString("post_date"));
								post.setEmpty(false);
								return post;
							}

				});
			} catch (EmptyResultDataAccessException e) {
				
				return new ArrayList<PostModel>();
			}
		} else return null;
	}

	@Override
	public int getPostCount() {
		
		return this.getJdbcTemplate().queryForObject(PostSql.getPostCount, Integer.class);
	}

}
