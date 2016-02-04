package ac.kr.halford.interceptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query",  args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
	@Signature(type = Executor.class, method = "query",  args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlInterceptor implements Interceptor {
	private Logger logger = LoggerFactory.getLogger(SqlInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[]        args     = invocation.getArgs();
		MappedStatement ms       = (MappedStatement)args[0];
		Object          param    = (Object)args[1];
		BoundSql        boundSql = ms.getBoundSql(param);
		String dq = getFullSqlQuery(boundSql, param);
		
		if (removeAttributes(boundSql.getSql(), dq)) return invocation.proceed();  
		else return new ArrayList<Object>();
		
//		System.out.println("====================================");
//		System.out.println(invocation.getMethod().getName());
//		System.out.println(ms.getId());
//		System.out.println(boundSql.getSql());
//		System.out.println(param);
//		System.out.println(sql);
//		System.out.println("====================================");
		
		
		// if the sql is normal, return invocation.proceed.
		
		
		// if function detect that the sql is abnormal, return empty ArrayList.
//		return new ArrayList<Object>();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		
		
	}
	
	private String getFullSqlQuery (BoundSql boundSql, Object param) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String sql = boundSql.getSql();
		
		// 현재 모델 클래스나 맵 형식의 매개변수를 이용하기 때문에 이상이 없음.
		// 정수 하나의 경우 모든 sql 사용자 입력을 대입하는 루틴은 구현 x
		if (param != null) {
			if (param instanceof String) sql = sql.replaceFirst("\\?", "'"+param.toString()+"'");
			else if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) sql = sql.replaceFirst("\\?", param.toString());
			else if (param instanceof Map) {
				List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
				
				for (ParameterMapping parameterMapping : parameterMappingList) {
					String propertyValue = parameterMapping.getProperty();
					Object value = ((Map)param).get(propertyValue);
					
					if (value instanceof String) sql = sql.replaceFirst("\\?", "'"+value.toString()+"'");
					else sql = sql.replaceFirst("\\?", value.toString());
				}
			} else {
				List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
				Class<? extends Object> paramClass = param.getClass();
				
				for (ParameterMapping parameterMapping : parameterMappingList) {
					String propertyValue = parameterMapping.getProperty();
					Field field = paramClass.getDeclaredField(propertyValue);
					field.setAccessible(true);
					Class<?> javaType = parameterMapping.getJavaType();
					
					if (javaType == String.class) sql = sql.replaceFirst("\\?", "'"+field.get(param).toString()+"'"); 
					else sql = sql.replaceFirst("\\?", field.get(param).toString());
				}
			}
		}
		
		return sql;
	}
	
	private boolean removeAttributes(String FQ, String DQ) {
		
		String stringReg = "=\\s*'([^']*)'";
//		String stringReg = "=\\s*'.*'";
		String numberReg = "=\\s*[+-]?(\\d*)(\\.\\d*)?";
		String likeReg = "(?i)like\\s*'([^']*)'";
		String limitReg = "(?i)limit\\s*(\\d*),\\s*(\\d*)";
		String quotesReg = "'([^']*)'";
		String NumEqNumReg = "[+-]?(\\d*)(\\.\\d*)?=[+-]?(\\d*)(\\.\\d*)?";
		String reg = stringReg + "|" + numberReg;
		
		logger.info("fq : {}", FQ);
		logger.info("dq : {}", DQ);
		
		String Fdq = FQ.replaceAll("\\?", "");
		Fdq = Fdq.replaceAll(limitReg, "limit , ");
		String Ddq = DQ.replaceAll(reg, "=");
		Ddq = Ddq.replaceAll(likeReg, "like ");
		Ddq = Ddq.replaceAll(quotesReg, "");
		Ddq = Ddq.replaceAll(NumEqNumReg, "=");
		Ddq = Ddq.replaceAll(limitReg, "limit , ");
//		String DDQ = DQ.replaceAll(stringReg, "=''");
//		DDQ = DDQ.replaceAll(numberReg, "=");
//		DDQ = DDQ.replaceAll(likeReg, "like ''");
		
		logger.info("fdq : {}", Fdq);
		logger.info("ddq : {}", Ddq);
		
		byte[] FdqBytes = Fdq.getBytes();
		byte[] DdqBytes = Ddq.getBytes();
		byte[] result = new byte[FdqBytes.length]; 
		boolean check = false;
		
		for (int i = 0;i < FdqBytes.length;++i) {
			result[i] = (byte)(FdqBytes[i] ^ DdqBytes[i]);
			if (result[i] != 0x00) {
				check = true;
				break;
			}
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
