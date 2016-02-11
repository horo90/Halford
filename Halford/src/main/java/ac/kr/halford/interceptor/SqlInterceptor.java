package ac.kr.halford.interceptor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
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
		
//		InputStream stream = null;
//		
//		stream = Resources.getResourceAsStream("")
		
		
		
		SqlSource sqlSource = ms.getSqlSource(); 
		if (sqlSource instanceof DynamicSqlSource) {
			logger.info("dynamic");
		} else if (sqlSource instanceof StaticSqlSource) {
			logger.info("static");
		} else if (sqlSource instanceof RawSqlSource) {
			logger.info("raw");
		} else if (sqlSource instanceof ProviderSqlSource) {
			logger.info("provider");
		}
		logger.info("param size : {}", sqlSource.getBoundSql(param).getParameterMappings().size());
		
		
		
		logger.info(boundSql.getSql());
		
		
		
//		String dq = getFullSqlQuery(boundSql, param);
//		System.out.println(boundSql.getSql());
		
//		if (removeAttributes(boundSql.getSql(), dq)) return invocation.proceed();  
//		else return new ArrayList<Object>();
		return invocation.proceed();
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
		
		// 占쏙옙占쏙옙 占쏙옙 클占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙占쏙옙 占싱울옙占싹깍옙 占쏙옙占쏙옙占쏙옙 占싱삼옙占쏙옙 占쏙옙占쏙옙.
		// 占쏙옙占쏙옙 占싹놂옙占쏙옙 占쏙옙占� 占쏙옙占� sql 占쏙옙占쏙옙占� 占쌉뤄옙占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙틴占쏙옙 占쏙옙占쏙옙 x
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
		
		// 추가적으로 preparedstatement + bind variable을 사용할 경우, 이런식으로 만들면 소용이 없는 경우
		// 이거 분명히 수정의 여지가 있음. 특히 'x' 잡는 것이 매우 불안.
		String stringReg = "=\\s*'([^']*)'";
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

}
