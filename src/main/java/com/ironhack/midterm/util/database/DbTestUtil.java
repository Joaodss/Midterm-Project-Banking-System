package com.ironhack.midterm.util.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbTestUtil {


  public static void resetAutoIncrementColumns(ApplicationContext applicationContext, String... tableNames) throws SQLException {
    DataSource dataSource = applicationContext.getBean(DataSource.class);
    String resetSqlTemplate = getResetSqlTemplate(applicationContext);
    try (Connection dbConnection = dataSource.getConnection()) {
      for (String resetSqlArgument : tableNames) {
        try (Statement statement = dbConnection.createStatement()) {
          String resetSql = String.format(resetSqlTemplate, resetSqlArgument);
          statement.execute(resetSql);
        }
      }
    }
  }

  private static String getResetSqlTemplate(ApplicationContext applicationContext) {
    //Read the SQL template from the properties file
    Environment environment = applicationContext.getBean(Environment.class);
    return environment.getRequiredProperty("test.reset.sql.template");
  }

}
