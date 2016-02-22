package ac.kr.halford.constants;

public class LoginSql {

	public static final String addMember = "INSERT INTO member (id, password) VALUES (?, ?)";
	
	public static final String findMember = "SELECT DISTINCT id, password FROM member WHERE id=? AND password=?";
	
}
