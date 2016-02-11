package ac.kr.halford.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ac.kr.halford.model.PostModel;

public class PostJdbcTemplate extends JdbcDaoSupport implements PostDAO {
	
	
//	@Autowired
//	private DataSource dataSource;
//	private JdbcTemplate jdbcTemplate;
	
	

	@Override
	public void addPost(PostModel post) {
		this.getJdbcTemplate().update(PostSql.addPost, new Object[] {post.getMemberId(), post.getTitle(), post.getContents(), post.getDate()});
	}

	@Override
	public void deleteCertainPost(PostModel post) {
		this.getJdbcTemplate().update(PostSql.deleteCertainPost, new Object[] {post.getPostId()});
		
	}

	@Override
	public void updateCertainPost(PostModel post) {
		this.getJdbcTemplate().update(PostSql.updateCertainPost, new Object[] {post.getTitle(), post.getContents(), post.getDate(), post.getPostId()});
		
	}

	@Override
	public PostModel findCertainPost(PostModel post) {
		
		return this.getJdbcTemplate().queryForObject(PostSql.findCertainPost, new Object[] {post.getPostId()}, 
													new RowMapper<PostModel> () {

														@Override
														public PostModel mapRow(ResultSet rs, int rowNum) throws SQLException {
															PostModel post = new PostModel();
															post.setPostId(rs.getInt("post_id"));
															post.setMemberId(rs.getString("member_id"));
															post.setTitle(rs.getString("title"));
															post.setContents(rs.getString("contents"));
															post.setDate(rs.getString("post_date"));
															return post;
														}
			
		});
	}

	@Override
	public List<PostModel> findPosts(Map<String, Object> map) {
		
		return this.getJdbcTemplate().query(PostSql.findPosts, new Object[] {map.get("current")}, 
													new RowMapper<PostModel> () {

														@Override
														public PostModel mapRow(ResultSet rs, int rowNum) throws SQLException {
															PostModel post = new PostModel();
															post.setPostId(rs.getInt("post_id"));
															post.setMemberId(rs.getString("member_id"));
															post.setTitle(rs.getString("title"));
															post.setContents(rs.getString("contents"));
															post.setDate(rs.getString("post_date"));
															return post;
														}
			
		});
	}

	@Override
	public int getPostCount() {
		
		return this.getJdbcTemplate().queryForObject(PostSql.getPostCount, new Object[]{}, Integer.class);
	}

}
