package ac.kr.halford.service;

import java.util.List;

import ac.kr.halford.model.PostModel;
import ac.kr.halford.util.Pager;

public interface PostService {
	public PostModel findCertainPost(PostModel post);
	public List<PostModel> findPosts(int current);
	
	public void addPost(PostModel post);
	
	public void updatePost(PostModel post);
	
	public void deletePost(PostModel post);
	
	public void setPostModel(PostModel post);
	public void setPager(Pager pager);
}
