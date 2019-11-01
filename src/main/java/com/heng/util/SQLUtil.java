package com.heng.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SQLUtil {

    private MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();

    private SQLUtil(String sql){
        SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stat = parser.parseStatement();
        stat.accept(visitor);
    }


    public Set<TableStat.Name> getTable(){
        return visitor.getTables().keySet();
    }

    public Collection<TableStat.Column> getField(){
        return visitor.getColumns();
    }

    public List<TableStat.Condition> getConditions(){
        List<TableStat.Condition> conditions = visitor.getConditions();
        return conditions;
    }


    public static class SQLUtilBuilder{

        public static SQLUtil build(String sql) {
            SQLUtil sqlUtil = new SQLUtil(sql);
            Set<TableStat.Name> set = sqlUtil.getTable();
            if (set.size() <= 0 || set.size() > 1){
                try {
                    throw new SQLException("es的类型只能为1");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return sqlUtil;
        }
    }

    interface OperatorType{
        String LIKE = "LIKE";
        String EQUAL = "=";
        String IN = "IN";
    }
}
