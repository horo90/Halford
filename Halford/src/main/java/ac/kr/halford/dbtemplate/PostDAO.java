package ac.kr.halford.dbtemplate;

import java.util.List;
import java.util.Map;

import ac.kr.halford.model.PostModel;

public interface PostDAO {
	public int addPost(PostModel post);
	
	public int deleteCertainPost(PostModel post);
	
	public int updateCertainPost(PostModel post);
	
	public PostModel findCertainPost(PostModel post);
	public List<PostModel> findPosts(Map<String, Object> map);
	public int getPostCount();
}
