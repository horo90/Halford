package ac.kr.halford.util;

public class Pager {
	private int currentPage;
	private int startPage;
	private int endPage;
	private int totalPage;
	private boolean hasPre;
	private boolean hasPost;
	public static final int maxPageNum = 5;
	
	public Pager() {}

	public Pager(int currentPage, int startPage, int endPage, int totalPage, boolean hasPre, boolean hasPost) {
		this.currentPage = currentPage;
		this.startPage = startPage;
		this.endPage = endPage;
		this.totalPage = totalPage;
		this.hasPre = hasPre;
		this.hasPost = hasPost;
	}

	public int getCurrentPage() {	return currentPage;}
	public void setCurrentPage(int currentPage) {	this.currentPage = currentPage;}
	public int getStartPage() {	return startPage;}
	public void setStartPage(int startPage) {	this.startPage = startPage;}
	public int getEndPage() {	return endPage;}
	public void setEndPage(int endPage) {	this.endPage = endPage;}
	public boolean isHasPre() {	return hasPre;}
	public void setHasPre(boolean hasPre) {	this.hasPre = hasPre;}
	public boolean isHasPost() {	return hasPost;}
	public void setHasPost(boolean hasPost) {	this.hasPost = hasPost;}
	public int getTotalPage() {	return totalPage;}
	public void setTotalPage(int totalPage) {	this.totalPage = totalPage;}

	public void setPager (int totalPostCount) {
		this.totalPage = totalPostCount % 10 > 0 ? (int)(totalPostCount/10) + 1 : (int)(totalPostCount/10);
		this.startPage = this.currentPage - 2 <= 0 ? 1 : this.currentPage - 2;
		this.endPage = this.currentPage + 2 > this.totalPage ? this.totalPage : this.currentPage + 2;
		
		this.hasPre = this.startPage > 1 ? true : false;
		this.hasPost = this.endPage < this.totalPage ? true : false;
	}

	@Override
	public String toString() {
		return "Pager [currentPage=" + currentPage + ", startPage=" + startPage + ", endPage=" + endPage
				+ ", totalPage=" + totalPage + ", hasPre=" + hasPre + ", hasPost=" + hasPost + "]";
	}
}
