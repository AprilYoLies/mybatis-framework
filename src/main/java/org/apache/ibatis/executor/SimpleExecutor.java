/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Clinton Begin
 */
public class SimpleExecutor extends BaseExecutor {

  public SimpleExecutor(Configuration configuration, Transaction transaction) {
    super(configuration, transaction);
  }

  @Override
  public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
      stmt = prepareStatement(handler, ms.getStatementLog());
      return handler.update(stmt);
    } finally {
      closeStatement(stmt);
    }
  }
  // 获取真正的连接，设置隔离级别和自动提交信息，根据条件创建 Statement,设置超时信息,设置 FetchSize，从 boundSql 获取 parameterMappings，然后将每一项对应的值设置到 ps 中
  @Override // 从 Statement 获取 ResultSet，将其封装为 ResultSetWrapper，构建 DefaultResultHandler，通过它得到结果集，从 ResultSetWrapper 拿到原始的结果集，根据条件跳过必要的行数，创建结果承载对象，然后完成结果的填充返回， 将 ResultContext 中的结果添加到 list 中，最后将 list 中的结果添加到 multipleResults
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql); // 通过参数构建了 RoutingStatementHandler
        stmt = prepareStatement(handler, ms.getStatementLog()); // 获取真正的连接，设置隔离级别和自动提交信息，根据条件创建 Statement,设置超时信息,设置 FetchSize，从 boundSql 获取 parameterMappings，然后将每一项对应的值设置到 ps 中
        return handler.query(stmt, resultHandler);  // 从 Statement 获取 ResultSet，将其封装为 ResultSetWrapper，构建 DefaultResultHandler，通过它得到结果集，从 ResultSetWrapper 拿到原始的结果集，根据条件跳过必要的行数，创建结果承载对象，然后完成结果的填充返回， 将 ResultContext 中的结果添加到 list 中，最后将 list 中的结果添加到 multipleResults
    } finally {
      closeStatement(stmt);
    }
  }

  @Override
  protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
    Statement stmt = prepareStatement(handler, ms.getStatementLog());
    stmt.closeOnCompletion();
    return handler.queryCursor(stmt);
  }

  @Override
  public List<BatchResult> doFlushStatements(boolean isRollback) {
    return Collections.emptyList();
  }
  // 获取真正的连接，设置隔离级别和自动提交信息，根据条件创建 Statement,设置超时信息,设置 FetchSize，从 boundSql 获取 parameterMappings，然后将每一项对应的值设置到 ps 中
  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    Connection connection = getConnection(statementLog);  // 获取真正的连接，设置隔离级别和自动提交信息，如果启动了 statementLog，则创建连接代理类，这将会打印日志信息
    stmt = handler.prepare(connection, transaction.getTimeout()); // 根据条件创建 Statement,设置超时信息,设置 FetchSize
    handler.parameterize(stmt); // 从 boundSql 获取 parameterMappings，然后将每一项对应的值设置到 ps 中
    return stmt;
  }

}
