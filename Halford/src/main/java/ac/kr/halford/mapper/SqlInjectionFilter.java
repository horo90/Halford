package ac.kr.halford.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlInjectionFilter {
	
	private static Logger logger = LoggerFactory.getLogger(SqlInjectionFilter.class);

	private static final String stringFilter = "=\\s*'([^']*)'";
	private static final String numberFilter = "=\\s*[+-]?(\\d*)(\\.\\d*)?";
	private static final String likeFilter = "(?i)like\\s*'([^']*)'";
	private static final String limitFilter = "(?i)limit\\s*(\\d*),\\s*(\\d*)";
	private static final String quotesFilter = "'([^']*)'";
	private static final String NumEqNumFilter = "[+-]?(\\d*)(\\.\\d*)?=[+-]?(\\d*)(\\.\\d*)?";
	
	// dq->ddq limit filter 오류
	
	public static boolean isSQLi (String FQ, String DQ) {
		
		logger.info("fq : {}", FQ);
		logger.info("dq : {}", DQ);
		
		String Fdq = FQ.replaceAll("\\?", "");
		Fdq = Fdq.replaceAll(limitFilter, "LIMIT , ");
		
		String Ddq = DQ.replaceAll(stringFilter + "|" + numberFilter, "=");
		Ddq = Ddq.replaceAll(likeFilter, "LIKE ");
		Ddq = Ddq.replaceAll(quotesFilter, "");									// 이거 때문에 조금 걸림.
		Ddq = Ddq.replaceAll(NumEqNumFilter, "=");
		Ddq = Ddq.replaceAll(limitFilter, "LIMIT , ");
		
		logger.info("fdq : {}", Fdq);
		logger.info("ddq : {}", Ddq);
		
		byte[] FdqBytes = Fdq.getBytes();
		byte[] DdqBytes = Ddq.getBytes();
		byte[] result = new byte[FdqBytes.length]; 
		boolean check = false;
		
		// 이렇게 하는게 빠른지, 그냥 string compare하는게 빠른지 잘 모르겠다.
		// 간단하기로는 string compare가 간단함.
		// byte 배열의 길이를 이용해도 가능.
		for (int i = 0;;++i) {
			if (i < FdqBytes.length && i < DdqBytes.length) {
				result[i] = (byte)(FdqBytes[i] ^ DdqBytes[i]);
				if (result[i] != 0x00) {
					check = true;
					break;
				}
			} else if (i >= FdqBytes.length && i < DdqBytes.length) {
				check = true;
				break;
			} else break;
			
		}
		
		//	check true -> abnormal sql
		//	check false -> normal sql
		//	return false -> abnormal sql
		//	return true -> normal sql
		if (check) {
			logger.info("abnormal sql");
			return false;
		}
		logger.info("normal sql");
		return true;
	}
	
	public static String getBoundSql (String sql, Object[] params) {
		
		// params는 model class나 Map class에 있는 속성을 object 배열로 바꾸기 때문에 String or 숙자 계열 뿐이다.
		// 이런식으로 하려면 굳이 Map이나 Model class를 사용할 필요는 없지만, mybatis 기반에서 바꾸다 보니 이런 형태가 됐다.
		if (params.length > 0) {
			for (Object value : params) {
				if (value.getClass() == String.class) {
					sql = sql.replaceFirst("\\?", "'"+value.toString()+"'");
				} else {
					sql = sql.replaceFirst("\\?", value.toString());
				}
			}
		}
		
		return sql;
	}
}
