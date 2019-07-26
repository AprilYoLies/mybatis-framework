package top.aprilyolies.demo.app;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import top.aprilyolies.demo.mapper.UserMapper;
import top.aprilyolies.demo.pojo.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @Author EvaJohnson
 * @Date 2019-07-25
 * @Email g863821569@gmail.com
 */
public class SqlSessionApp {
  public static void main(String[] args) throws IOException {
    SqlSession session = buildSessionFactory(buildDataSource(), UserMapper.class).openSession();  // 构建 DefaultSqlSession，DefaultSqlSession 持有了 configuration、executor、autoCommit 信息
    User user = session.selectOne("top.aprilyolies.demo.mapper.UserMapper.getUserById", 6); // 获取连接，查询结果，返回结果集
    System.out.println(user);
  }

  // 构建 datasource，硬编码使用 DruidDataSource
  private static DataSource buildDataSource() {
    DruidDataSource dataSource = null;
    try {
      // Config dataSource
      dataSource = new DruidDataSource();
      dataSource.setUrl("jdbc:mysql://localhost:3306/common");
      dataSource.setUsername("root");
      dataSource.setPassword("kuaile1..");
      dataSource.init();
      return dataSource;
    } catch (SQLException e) {
      return dataSource;
    }
  }

  // 构建 SqlSessionFactory，用于和数据库进行交互
  private static SqlSessionFactory buildSessionFactory(DataSource dataSource, Class clazz) {
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("development", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(clazz); // 根据 config，type 构建 MapperAnnotationBuilder，MapperAnnotationBuilder 尝试根据 mapper 类型，加载对应的 mapper.xml，然后完成解析,缓存正在处理的 type 信息，处理 CacheNamespace、CacheNamespaceRef 注解，针对 type 的方法进行处理，将映射相关的一些声明通过 assistant 构建为 MappedStatement，然后缓存到 configuration 中
    return new SqlSessionFactoryBuilder().build(configuration);
  }
}
