package org.david.rain.dao.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.david.rain.dao.CommonDao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "commonDao")
public class CommonDaoImpl implements CommonDao {

	@Resource(name = "jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	@Resource(name = "transactionTemplate")
	private TransactionTemplate transactionTemplate;

	/**
	 * 
	 * @Title: addOrUpdateForTransaction
	 * @Description: TODO sql_param and sql_user 添加事务管理
	 * @param sql
	 * @param sql1
	 * @param o
	 * @return
	 * @throws Exception
	 */
	@Override
	public int addOrUpdateForTransaction(String sql, String sql1, String sql2,
			List<List<Object>> userCondition, String[]... o) throws Exception {
		Integer state = -1;
		PlatformTransactionManager manager = transactionTemplate
				.getTransactionManager();
		TransactionStatus statu = manager
				.getTransaction(new DefaultTransactionDefinition());
		try {
			for (int i = 0; i < o[1].length; i++) {
				Object[] inserto = { o[1][i], o[2][i], i };
				jdbcTemplate.update(sql, inserto);
			}
			for (int i = 0; i < o[0].length; i++) {
				Object[] inserto = { o[0][i] };
				jdbcTemplate.update(sql1, inserto);
			}
			for (int i = 0; i < userCondition.size(); i++) {
				Object[] inserto = userCondition.get(i).toArray();
				jdbcTemplate.update(sql2, inserto);
			}
		} catch (Exception e) {
			statu.setRollbackOnly();
			throw e;
		}
		state = 2;
		manager.commit(statu);
		return state;
	}

	@Override
	public int addOrUpdateForTransactionOneTable(String sql, String sqlselect,
			String[] o) throws Exception {
		Integer state = -1;
		PlatformTransactionManager manager = transactionTemplate
				.getTransactionManager();
		TransactionStatus statu = manager
				.getTransaction(new DefaultTransactionDefinition());
		try {
			for (int i = 0; i < o.length; i++) {

				Object[] inserto = { o[i] };
				if (jdbcTemplate.queryForObject(sqlselect, inserto,
						Integer.class) == 0)
					jdbcTemplate.update(sql, inserto);
			}
		} catch (Exception e) {
			statu.setRollbackOnly();
			throw e;
		}
		state = 2;
		manager.commit(statu);
		return state;
	}

	@Override
	public int addOrUpdate(String sql, Object[] o) {
		return jdbcTemplate.update(sql, o);
	}

	@Override
	public int queryForInt(String sql, Object[] o) {
		return jdbcTemplate.queryForObject(sql, o, Integer.class);
	}

	@Override
	public List queryList(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List queryList(String sql, Object[] o) {
		return jdbcTemplate.queryForList(sql, o);
	}

	/**
	 * 查询对象列表方法，切记返回结果的列名要与Bean的属性名完全一致！！！
	 */
	@Override
	public <T> List<T> queryObjList(String sql, Object[] args,
			final Class<T> clazz) {
		return jdbcTemplate.query(sql, args, new RowMapper<T>() {

			@Override
			public T mapRow(ResultSet rs, int arg1) throws SQLException {
				T result = null;
				ResultSetMetaData rsmd = rs.getMetaData();
				List<String> columns = getColumnList(rsmd); //获取数据rs的列名
				try {
					result = clazz.newInstance();
					BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
					PropertyDescriptor[] pds = beanInfo
							.getPropertyDescriptors();
					for (PropertyDescriptor pd : pds) {//迭代bean的所有属性
						Method setMethod = pd.getWriteMethod();
						if (setMethod != null) { //存在set方法
							String propertyName = pd.getName();							
							if(columns.contains(propertyName.toLowerCase())){
								BeanUtils.setProperty(result, propertyName,
										rs.getObject(propertyName));//往反射的bean中赋值
							}							
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return result;
			}

		});
	}

    /**
     * 获取返回数据集的所有列名
     * @param rsmd
     * @return
     */
	private List<String> getColumnList(ResultSetMetaData rsmd) {
		int columnCount = 0;
		List<String> columnList = null;
		try {
			columnCount = rsmd.getColumnCount();
			if (columnCount > 0) {
				columnList = new ArrayList<String>();
				for (int i = 1; i <= columnCount; i++) {
					String columnname = rsmd.getColumnName(i);
					columnList.add(columnname.toLowerCase());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnList;
	}
	@Override
	public <T> T queryObj(String sql, Object[] args, final Class<T> clazz) {
		return jdbcTemplate.query(sql, new ResultSetExtractor<T>() {

			@Override
			public T extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				T result = null;
				if (rs.next()) {
					try {
						result = clazz.newInstance();
						BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
						PropertyDescriptor[] pds = beanInfo
								.getPropertyDescriptors();
						for (PropertyDescriptor pd : pds) {
							Method setMethod = pd.getWriteMethod();
							if (setMethod != null) {
								String propertyName = pd.getName();
								BeanUtils.setProperty(result, propertyName,
										rs.getObject(propertyName));
							}
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				return result;
			}

		});
	}
	
	@Override
	public <T> List<T> queryForList(String sql, Object[] args,
			Class<T> elementType) {
		return jdbcTemplate.queryForList(sql, args, elementType);
	}
	
	@Override
	public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, args, rowMapper);
	}
	
	@Override
	public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) {
		return jdbcTemplate.batchUpdate(sql, pss);
	}

	@Override
	public void execSql(String sqlString) {
		// TODO Auto-generated method stub
		jdbcTemplate.execute(sqlString);
	}
}
