package ac.kr.halford.constants;

public class PostSql {

	public static final String addPost = "INSERT INTO post (member_id, title, contents, post_date) VALUES (?, ?, ?, ?)";
	
	public static final String deleteCertainPost = "DELETE FROM post WHERE post_id=?";
	
	public static final String updateCertainPost = "UPDATE post SET title=?, contents=?, post_date=? WHERE post_id=?";
	
	public static final String findCertainPost = "SELECT post_id, member_id, title, contents, post_date FROM post WHERE post_id=?";
	
	public static final String findPosts = "SELECT post_id, member_id, title, contents, post_date FROM post ORDER BY post_id DESC LIMIT ?, 10";
	
	public static final String getPostCount = "SELECT DISTINCT count(*) FROM post";
}
