package ac.kr.halford.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlInjectionFilter {
	
	private static Logger logger = LoggerFactory.getLogger(SqlInjectionFilter.class);

	private final String stringFilter = "=\\s*'([^']*)'";
	private final String numberFilter = "=\\s*[+-]?(\\d*)(\\.\\d*)?";
	private final String likeFilter = "(?i)like\\s*'([^']*)'";
	private final String limitFilter = "(?i)limit\\s*(\\d*),\\s*(\\d*)";
	private final String quotesFilter = "'([^']*)'";
	private final String NumEqNumFilter = "[+-]?(\\d*)(\\.\\d*)?=[+-]?(\\d*)(\\.\\d*)?";
	
	
	public static boolean isSQLi (String FQ, String DQ) {
		
		logger.info("fq : {}", FQ);
		logger.info("dq : {}", DQ);
		
		
//		String Fdq = FQ.replaceAll("\\?", "");
//		Fdq = Fdq.replaceAll(limitReg, "limit , ");
//		String Ddq = DQ.replaceAll(reg, "=");
//		Ddq = Ddq.replaceAll(likeReg, "like ");
//		Ddq = Ddq.replaceAll(quotesReg, "");
//		Ddq = Ddq.replaceAll(NumEqNumReg, "=");
//		Ddq = Ddq.replaceAll(limitReg, "limit , ");
		
//		String Fdq = FQ.replaceAll("\\?", "");
		
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
	
}
