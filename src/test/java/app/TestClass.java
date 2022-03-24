package app;

import app.imports.XmlImporter;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.concurrent.atomic.AtomicLong;

public class TestClass {

    @Test
    public void testSettings(){
        XmlImporter.Settings sett = XmlImporter.Settings.builder().build();
        System.out.println();
    }

    @Test
    public void testAtomicLong() throws InterruptedException {
        AtomicLong res = new AtomicLong();

        Runnable increment = () -> {
            StringBuilder str = new StringBuilder();
            str.append(res.get() + "\n");
            res.getAndAdd(1);
            str.append(res.get() + "\n");
            str.append("---");
            System.out.println(str.toString());
        };
        for(int i = 0; i<10;++i){
            new Thread(increment).start();
        }


        System.out.println("Result = " + res.get());

        Thread.sleep(1000);
        System.out.println("Result = " + res.get());
    }


    @Test
    public void test() throws SQLException, NoSuchMethodException {
//        TestClass.class.getMethod("test").
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        stackTraceElements[stackTraceElements.length-1];
//        Thread.currentThread().

        Connection conn = getConnection();
        conn.setAutoCommit(false);
        PreparedStatement statement = conn.prepareStatement("select * from uniq_nums");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            System.out.println(resultSet.getInt(1));
        }

        statement = conn.prepareStatement("insert into uniq_nums values (?)");
        statement.setInt(1, 555);
        System.out.println(statement.executeUpdate());

        conn.commit();
        resultSet.close();
        statement.close();
        conn.close();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String pass = "chronit53142";
        return DriverManager.getConnection(url, user, pass);
    }
//
//    @Test
//    public void test() throws IOException, SQLException, InterruptedException {
//        Connection conn = getConnection();
//        conn.setAutoCommit(false);
//        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//
//        PreparedStatement select = conn.prepareStatement("select * from nums");
//        ResultSet resultSet = select.executeQuery();
//        while(resultSet.next()){
//            System.out.print(resultSet.getString("n") + ", ");
//        }
//        System.out.println();
//        System.out.println("------New Transaction");
////        startNewTransaction();
//        Thread.sleep(20_000);
//
//        System.out.println("------After");
//
//        PreparedStatement insert = conn.prepareStatement("insert into nums values(1123)");
//        insert.executeUpdate();
//        select = conn.prepareStatement("select * from nums");
//        resultSet = select.executeQuery();
//        while(resultSet.next()){
//            System.out.print(resultSet.getString("n") + ", ");
//        }
//        System.out.println();
//        conn.commit();
//        conn.close();
//    }
//
//    private void startNewTransaction() throws SQLException {
//        Connection conn = getConnection();
//        conn.setAutoCommit(false);
//        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//        PreparedStatement select = conn.prepareStatement("select * from nums");
//        ResultSet resultSet = select.executeQuery();
//        while(resultSet.next()){
//            System.out.print(resultSet.getString("n") + ", ");
//        }
//        System.out.println();
//        select = conn.prepareStatement("select * from nums");
//        select.executeQuery();
////        PreparedStatement insert = conn.prepareStatement("insert into nums values(1)");
//////        PreparedStatement insert = conn.prepareStatement("insert into uniq_nums values (104);");
////        System.out.println("Insert = " + insert.executeUpdate());
//        conn.commit();
//    }
//
//    @Test
//    public void testEmptyResultSet() throws SQLException {
//        Connection conn = getConnection();
//        PreparedStatement select =
//                conn.prepareStatement("select 1 where exists(select * from nums where n = 324435) " +
//                        "union all " +
//                        "select 1 where exists(select * from nums where n = 1)");
//        ResultSet resultSet = select.executeQuery();
//        while(resultSet.next()){
//            System.out.println(resultSet.getString(1));
//        }
//        conn.close();
//
//    }
//
//    @Test
//    public void testPreparedSelect() throws SQLException {
//        Connection conn = getConnection();
//        String[] uniqCol = new String[]{"id","num"};
//        StringBuilder query = new StringBuilder();
//        List<Map<String, Object>> values =
//                List.of(Map.of("id",3, "num",1123),
//                        Map.of("id", "nextval('nums_with_id_id_seq'::regclass)", "num",1),
//                        Map.of("id", 2, "num",20));
//        String tableName = "nums_with_id";
//        for(int i = 0; i<values.size();++i) {
//            query.append("select case exists(select * from " + tableName + " where ");
//            for (String col : uniqCol) {
//                query.append("((").append("(" + tableName + "." + col + " is null)::integer + (? is null)::integer = 2").append(")");
//                query.append(" or ").append("(").append(" " + tableName + "." + col +" = ? ").append("))");
//                query.append(" and ");
//            }
//            query.append(" true) when True then 1 else 0 end\n").append(" union all\n");
//        }
//        query.append(" select 0");
//        System.out.println(query);
//        PreparedStatement preparedStatement = conn.prepareStatement(query.toString());
//        for(int i = 0; i < values.size(); ++i){
////            preparedStatement.setString(i * 3 + 1, tableName);
//            for(int j = 0; j<uniqCol.length;++j){
////                preparedStatement.set(i*5 + 2,tableName + "." + uniqCol[j]);
//                preparedStatement.setObject((i*uniqCol.length + j) * 2 + 1, values.get(i).get(uniqCol[j]));
////                preparedStatement.setObject(i*5 + 4,tableName + "." + uniqCol[j]);
//                preparedStatement.setObject((i*uniqCol.length + j) * 2+ 2, values.get(i).get(uniqCol[j]));
//            }
//        }
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while(resultSet.next()){
//            System.out.println(resultSet.getString(1));
//        }
//
//
//
//    }
//
//    @Test
//    public void testRegClass() throws SQLException {
//        Connection conn = getConnection();
//        String query = "select * from nums_with_id where id = 'nextval('nums_with_id_id_seq'::regclass)'::integer";
//        PreparedStatement statement = conn.prepareStatement(query);
//        ResultSet resultSet = statement.executeQuery();
//        System.out.println();
//    }
//
//    @Test
//    public void testService(){
//
//
//
//    }


}
