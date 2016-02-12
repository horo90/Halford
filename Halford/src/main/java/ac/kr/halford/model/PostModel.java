package ac.kr.halford.model;

public class PostModel extends SuperModel {
	private int postId;
	private int parentPostId;
	private String memberId;
	private String title;
	private String contents;
	private String date;
	
	public PostModel() {}

	public PostModel(int postId, int parentPostId, String memberId, String title, String contents, String date) {
		this.postId = postId;
		this.parentPostId = parentPostId;
		this.memberId = memberId;
		this.title = title;
		this.contents = contents;
		this.date = date;
	}

	public int getPostId() {	return postId;}
	public void setPostId(int postId) {	this.postId = postId;}
	public int getParentPostId() {	return parentPostId;}
	public void setParentPostId(int parentPostId) {	this.parentPostId = parentPostId;}
	public String getMemberId() {	return memberId;}
	public void setMemberId(String memberId) {	this.memberId = memberId;}
	public String getTitle() {	return title;}
	public void setTitle(String title) {	this.title = title;}
	public String getContents() {	return contents;}
	public void setContents(String contents) {	this.contents = contents;}
	public String getDate() {	return date;}
	public void setDate(String date) {	this.date = date;}

	@Override
	public String toString() {
		return "PostModel [postId=" + postId + ", parentPostId=" + parentPostId + ", memberId=" + memberId + ", title="
				+ title + ", contents=" + contents + ", date=" + date + "]";
	}
}
