package ac.kr.halford.interceptor;

import java.lang.reflect.Field;
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
		String sql = getFullSqlQuery(boundSql, param);
		
		System.out.println("====================================");
		System.out.println(invocation.getMethod().getName());
		System.out.println(ms.getId());
		System.out.println(boundSql.getSql());
		System.out.println(param);
		System.out.println(sql);
		System.out.println("====================================");
		
//		Object[] args = invocation.getArgs();
//		MappedStatement ms = (MappedStatement) args[0];
//		Map<String, String> params = (Map<String, String>)args[1];
//		BoundSql boundSql = ms.getBoundSql(params);
//		StringBuilder sql = new StringBuilder(boundSql.getSql());
//		
//		for (ParameterMapping param : boundSql.getParameterMappings()) {
//			String property = param.getProperty();
//			int index = sql.indexOf("?");
//			sql.replace(index, index + 1, "'" + params.get(property) + "'");
//		}
//		
//		Object proceed = invocation.proceed();
//		
//		logger.info("{} ==>  Preparing: {}", ms.getId(), sql.toString());
//		if (proceed != null && proceed instanceof List) {
//			List<Map<String, String>> list = (List<Map<String, String>>) proceed;
//			if (list.size() > 0) {
//				logger.info("{} ==>     Result: {}", ms.getId(), list.get(0));
//			}
//			
//			logger.info("{} <==      Total: {}", ms.getId(), list.size());
//		}
		
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
		
		if (param != null) {
			if (param instanceof String) sql = sql.replaceFirst("\\?", "'"+param.toString()+"'");
			else if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) sql = sql.replaceFirst("\\?", param.toString());
			else if (param instanceof Map) {
				List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
				for (ParameterMapping parameterMapping : parameterMappingList) {
					String propertyValue = parameterMapping.getProperty();
					Object value = ((Map)param).get(propertyValue);
					if (value instanceof String) sql = sql.replaceFirst("\\?", "'"+value.toString()+"'");
					else sql = sql.replaceFirst("\\?", param.toString());
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

}
