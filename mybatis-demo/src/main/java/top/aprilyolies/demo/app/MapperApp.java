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
public class MapperApp {
  public static void main(String[] args) throws IOException {
    SqlSessionFactory sessionFactory = buildSessionFactory(buildDataSource());
    UserMapper mapper = sessionFactory.openSession().getMapper(UserMapper.class); // 从 knownMappers 中获取对应的代理工厂，然后根据代理工厂构建相应接口的代理类
    User user = mapper.getUserById(6);
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
  private static SqlSessionFactory buildSessionFactory(DataSource dataSource) {
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("development", transactionFactory, dataSource); // 构建 Environment，持有了 TransactionFactory 和 DataSource
    Configuration configuration = new Configuration(environment); // 完成了一系列 class 的别名映射，持有了 Environment
    configuration.addMapper(UserMapper.class);
    return new SqlSessionFactoryBuilder().build(configuration);
  }
}
