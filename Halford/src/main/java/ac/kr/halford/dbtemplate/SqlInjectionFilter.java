package ac.kr.halford.dbtemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import ac.kr.halford.constants.CommonSql;

public class SqlInjectionFilter {
	
	private static Logger logger = LoggerFactory.getLogger(SqlInjectionFilter.class);

	private static final String stringFilter = "=\\s*'([^']*)'";
	private static final String numberFilter = "=\\s*[+-]?(\\d*)(\\.\\d*)?";
	private static final String likeFilter = "(?i)like\\s*'([^']*)'";
	private static final String limitFilter = "(?i)limit\\s*(\\d*),\\s*(\\d*)";
	private static final String quotesFilter = "'([^']*)'";
	private static final String NumEqNumFilter = "[+-]?(\\d*)(\\.\\d*)?=[+-]?(\\d*)(\\.\\d*)?";
	
//	public static boolean isFiltered (JdbcTemplate jdbcTemplate) {
//		if (jdbcTemplate.queryForObject(CommonSql.findFilter, Integer.class) == 0) return false;
//		else return true;
//	}
	
	public static int isFiltered (JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.queryForObject(CommonSql.findFilter, Integer.class);
	}
	
	
	// dq->ddq limit filter ����
	
	public static boolean isSQLiR (String FQ, String DQ) {
		logger.info("executing reg filter");
		logger.info("fq : {}", FQ);
		logger.info("dq : {}", DQ);
		
		String Fdq = FQ.replaceAll("\\?", "");
		Fdq = Fdq.replaceAll(limitFilter, "LIMIT , ");
		
		String Ddq = DQ.replaceAll(stringFilter + "|" + numberFilter, "=");
		Ddq = Ddq.replaceAll(likeFilter, "LIKE ");
		Ddq = Ddq.replaceAll(quotesFilter, "");									// �̰� ������ ���� �ɸ�.
		Ddq = Ddq.replaceAll(NumEqNumFilter, "=");
		Ddq = Ddq.replaceAll(limitFilter, "LIMIT , ");
		
		logger.info("fdq : {}", Fdq);
		logger.info("ddq : {}", Ddq);
		
		byte[] FdqBytes = Fdq.getBytes();
		byte[] DdqBytes = Ddq.getBytes();
		byte[] result = new byte[FdqBytes.length]; 
		boolean check = false;
		
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
		
		if (check) {
			logger.info("abnormal sql");
			return true;
		}
		logger.info("normal sql");
		return false;
	}
	
	public static boolean isSQLiQ (String FQ, String DQ) {
		logger.info("executing quotation filter");
		String Fdq = null;
		String Ddq = null;
		
//		Fdq = removeAttr(FQ);
		Fdq = FQ.replace("?", "");
		Ddq = removeAttr(DQ);
		
		logger.info("Fdq : {}", Fdq);
		logger.info("Ddq : {}", Ddq);
		
		if (Fdq.compareTo(Ddq) == 0) {
			return false;
		} else return true;
		
	}
	
	private static String removeAttr (String query) {
		boolean quote = false;
		String result = "";
		
		for (int i = 0;i < query.length();++i) {
			char ch = query.charAt(i);
			
			if (!quote && ch == '\'') {
				quote = true;
			} else if (quote && ch == '\'') {
				quote = false;
			} else if (!quote) {
				result += ch;
			}
		}
		
		return result;
	}
	
	public static String getBoundSql (String sql, Object[] params) {
		
		if (params.length > 0) {
			for (Object value : params) {
				if (value == null) {
					return null;
				} else if (value.getClass() == String.class) {
					sql = sql.replaceFirst("\\?", "'"+value.toString()+"'");
				} else {
					sql = sql.replaceFirst("\\?", value.toString());
				}
			}
		}
		
		return sql;
	}
}
