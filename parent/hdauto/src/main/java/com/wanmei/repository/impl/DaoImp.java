package com.david.web.pppppp.repository.impl;

import com.david.web.pppppp.common.CommonList;
import com.david.web.pppppp.common.search.Search;
import com.david.web.pppppp.entity.HdTable;
import com.david.web.pppppp.repository.DaoValidater;
import com.david.web.pppppp.repository.Idao;
import com.david.web.pppppp.repository.SqlResolver;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作接口
 * User: chenzhiyi
 * Date: 12-11-20
 * Time: 上午11:44
 */
@Qualifier("hdManagerDao")
@Component
public class DaoImp implements Idao {
    private QueryRunner runner;
    private DataSource dataSource;
    private static final Log LOG = LogFactory.getLog(DaoImp.class);

    @Autowired
    public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

   /* public DaoImp() {
        this.runner = new QueryRunner(dataSource);
    }*/

    /**
     * @return
     */
    @Override
    public QueryRunner getQueryRunner() {
        if (runner == null) {
            synchronized (this) {
                if (runner == null)
                    runner = new QueryRunner(dataSource);
            }
        }
        return runner;
    }

    /**
     * 插入新数据
     *
     * @param object
     * @return
     */
    @Override
    public int insert(Object object) throws SQLException {
        runner = new QueryRunner(dataSource);
        SqlResolver resolver = new SqlResolver(object);
        return runner.update(resolver.insertSql(), resolver.paramValues());
    }

    @Override
    public boolean batchInsert(String sql, Object[][] objects) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * 数据库增insert删delete改update操作执行
     *
     * @param sql
     * @return int 影响数据库行数
     */
    @Override
    public int update(String sql) throws SQLException {
        runner = new QueryRunner(dataSource);
        return runner.update(sql);
    }


    /**
     * 数据库增insert删delete改update操作执行
     *
     * @param sql
     * @param paramValues 参数列表的值
     * @return int 影响数据库行数
     */
    @Override
    public int update(String sql, Object... paramValues) throws SQLException {
        runner = new QueryRunner(dataSource);
        return runner.update(sql, paramValues);
    }


    /**
     * 根据id查询对象
     *
     * @param clazz 类需要有HdTable注解
     * @param id
     * @return
     */
    @Override
    public <T> T queryObject(Class<T> clazz, Integer id) throws SQLException {
        runner = new QueryRunner(dataSource);
        DaoValidater.assertHasHdTable(clazz);
        ResultSetHandler<T> handler = new BeanHandler<T>(clazz);
        String tableName = clazz.getAnnotation(HdTable.class).value();
        return runner.query("select * from " + tableName + " where id=?", handler, id);
    }


    /**
     * 根据sql查询单个对象
     *
     * @param clazz
     * @param sql
     * @return
     */
    @Override
    public <T> T queryObject(Class<T> clazz, String sql) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler<T> handler = new BeanHandler<T>(clazz);
        return runner.query(sql, handler);
    }

    /**
     * 根据sql查询单个对象
     *
     * @param clazz
     * @param sql
     * @param paramValues
     * @return
     */
    @Override
    public <T> T queryObject(Class<T> clazz, String sql, Object... paramValues) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler<T> handler = new BeanHandler<T>(clazz);
        return runner.query(sql, handler, paramValues);
    }


    public <T> T queryScalar(String sql) throws SQLException {
        ResultSetHandler<T> handler = new ScalarHandler<T>();
        return runner.query(sql, handler);
    }

    /**
     * 根据Sql查询单个字段值
     *
     * @param sql select count(xxx)/sum(xxx)/id/username from .....
     * @return count()函数返回Long sum()函数返回BigDecimal 其他返回字段对应的类型
     */
    public <T> T queryScalar(String sql, Object... paramValues) throws SQLException {
        ResultSetHandler<T> handler = new ScalarHandler<T>();
        return runner.query(sql, handler, paramValues);
    }

    /**
     * 需要Connection 根据Sql查询单个字段值  应用事务控制
     *
     * @param conn
     * @param sql  select count(xxx)/sum(xxx)/id/username from .....
     * @return count()函数返回Long sum()函数返回BigDecimal 其他返回字段对应的类型
     */
    public <T> T queryScalar(Connection conn, String sql) throws SQLException {
        ResultSetHandler<T> handler = new ScalarHandler<T>();
        return runner.query(conn, sql, handler);
    }

    /**
     * 需要Connection 根据Sql查询单个字段值  应用事务控制
     *
     * @param conn
     * @param sql  select count(xxx)/sum(xxx)/id/username from .....
     * @return count()函数返回Long sum()函数返回BigDecimal 其他返回字段对应的类型
     */
    public <T> T queryScalar(Connection conn, String sql, Object... paramValues) throws SQLException {
        ResultSetHandler<T> handler = new ScalarHandler<T>();
        return runner.query(conn, sql, handler, paramValues);
    }


    /**
     * 查询数据列表
     *
     * @param clazz
     * @param sql
     * @return
     */
    @Override
    public <T> List<T> queryObjects(Class<T> clazz, String sql) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler<List<T>> handler = new BeanListHandler<T>(clazz);
        return runner.query(sql, handler);
    }


    /**
     * 该方法还没有测试
     *
     * @param sql
     * @param <T>
     * @return
     * @throws java.sql.SQLException
     */
    public <T> List<T> queryOneColumnList(String sql) throws SQLException {
        ColumnListHandler<T> hander = new ColumnListHandler<T>(1);
        return runner.query(sql, hander);
    }


    /**
     * 查询数据列表
     *
     * @param clazz
     * @param sql
     * @return
     */
    @Override
    public <T> List<T> queryObjects(Class<T> clazz, String sql, Object... paramValues) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler<List<T>> handler = new BeanListHandler<T>(clazz);
        return runner.query(sql, handler, paramValues);
    }


    /**
     * 查询数量SQL
     *
     * @param sql 必须是查询数量的 select count(1)....
     * @return
     */
    @Override
    public long queryCount(String sql) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler handler = new ScalarHandler();
        return (Long) runner.query(sql, handler);
    }


    /**
     * 查询数量SQL
     *
     * @param sql 必须是查询数量的 select count(1)....
     * @return
     */
    @Override
    public long queryCount(String sql, Object... paramValues) throws SQLException {
        runner = new QueryRunner(dataSource);
        ResultSetHandler handler = new ScalarHandler();
        return (Long) runner.query(sql, handler, paramValues);
    }


    /**
     * 分页查询
     *
     * @param search
     * @param clazz
     * @return
     */
    @Override
    public <T> CommonList<T> pagination(Search search, Class<T> clazz) {
        runner = new QueryRunner(dataSource);
        int recNum = 0; //查询的总页数；
        if (search.getPageNo() <= 0) {
            search.setPageNo(1);
        }
        List<T> objects = null;
        search.setWhetherPage(true);
        try {
            String countsql = search.builderCountSql();
            String objectssql = search.builderSearchSql();
            if (search.getWhereParas().size() == 0) {
                recNum = (int) queryCount(countsql);
                objects = queryObjects(clazz, objectssql); //得到记录集合
            } else {
                recNum = (int) queryCount(countsql, search.getWhereParas().toArray());
                objects = queryObjects(clazz, objectssql, search.getWhereParas().toArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CommonList<T> commonList = new CommonList<T>(search.getSearchStr(), recNum, search.getPageNo(), search.getPageSize());
        if (objects != null) {
            commonList.addAll(objects);
        }
        return commonList;
    }

}
