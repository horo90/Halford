package ac.kr.halford.mapper;

import java.util.List;
import java.util.Map;

import ac.kr.halford.model.PostModel;

public interface PostDAO {
public void addPost(PostModel post);
	
	public void deleteCertainPost(PostModel post);
	
	public void updateCertainPost(PostModel post);
	
	public PostModel findCertainPost(PostModel post);
	public List<PostModel> findPosts(Map<String, Object> map);
	public int getPostCount();
}
