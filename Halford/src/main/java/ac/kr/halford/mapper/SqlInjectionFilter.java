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
	
	// dq->ddq limit filter ����
	
	public static boolean isSQLi (String FQ, String DQ) {
		
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
		
		// �̷��� �ϴ°� ������, �׳� string compare�ϴ°� ������ �� �𸣰ڴ�.
		// �����ϱ�δ� string compare�� ������.
		// byte �迭�� ���̸� �̿��ص� ����.
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
		
		// params�� model class�� Map class�� �ִ� �Ӽ��� object �迭�� �ٲٱ� ������ String or ���� �迭 ���̴�.
		// �̷������� �Ϸ��� ���� Map�̳� Model class�� ����� �ʿ�� ������, mybatis ��ݿ��� �ٲٴ� ���� �̷� ���°� �ƴ�.
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
