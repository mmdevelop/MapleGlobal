
package net.sf.odinms.database;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.lang.ref.WeakReference;


public class DatabaseConnection {

    //private static ThreadLocal<Connection> con = new ThreadLocalConnection();
    private static SharedPoolDataSource ds = null;
    private final static Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    private static Properties props = null;
    public static HashMap<Connection, Long> allConnections = new HashMap<Connection, Long>();

    public static Connection getConnection() {
        if (props == null) {
            throw new RuntimeException("DatabaseConnection not initialized");
        }
        if (ds == null)
        {
            DriverAdapterCPDS cpds = new DriverAdapterCPDS();
            try {
                cpds.setDriver(props.getProperty("driver"));
            } catch (ClassNotFoundException e) {
            	log.error("ERROR", e);
                //e.printStackTrace();
            }
            cpds.setUrl(props.getProperty("url"));
            cpds.setUser(props.getProperty("user"));
            cpds.setPassword(props.getProperty("password"));
            cpds.setTimeBetweenEvictionRunsMillis(7000);
            cpds.setMinEvictableIdleTimeMillis(25000);
            //cpds.setMaxConnLifetimeMillis(200);

            SharedPoolDataSource tds = new SharedPoolDataSource();
            tds.setConnectionPoolDataSource(cpds);
            tds.setDefaultTimeBetweenEvictionRunsMillis(7000);
            tds.setDefaultMinEvictableIdleTimeMillis(25000);
            tds.setMaxConnLifetimeMillis(60000);
            //tds.setMaxActive(10);
            //tds.setMaxWait(50);
            ds = tds;       	
        }
        try
        {
            Connection conn = ds.getConnection();
            Long time = System.currentTimeMillis();
            allConnections.put(conn, time);
            LinkedList<Connection> toRemove = new LinkedList<Connection>();
            synchronized(allConnections)
            {
                
                for (Connection c : allConnections.keySet())   
                {
                    //;
                    if (c.isClosed())       
                    {
                        toRemove.add(c);
                    }
                    else if ((time - allConnections.get(c) > 60000))
                            {
                                try {
                                    synchronized(c)
                                    {
                                        c.close();  
                                        ;
                                    }
                                } catch (SQLException e)
                                {
                                    ;
                                }
                                toRemove.add(c);
                            }
                }
                
            }
            for (Connection tc : toRemove)
            {
                allConnections.remove(tc);
            }
            return conn;            
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isInitialized() {
        return props != null;
    }

    public static void setProps(Properties aProps) {
        props = aProps;
    }

    public static void closeAll() throws SQLException {
    }

}



//
//
//package net.sf.odinms.database;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.Properties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.lang.ref.WeakReference;
//
//public class DatabaseConnection {
//
//    private static ThreadLocal<Connection> con = new ThreadLocalConnection();
//    private final static Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
//    private static Properties props = null;
//
//    public static Connection getConnection() {
//        if (props == null) {
//            throw new RuntimeException("DatabaseConnection not initialized");
//        }
//        );
//        return con.get();
//    }
//
//    public static boolean isInitialized() {
//        return props != null;
//    }
//
//    public static void setProps(Properties aProps) {
//        props = aProps;
//    }
//
//    public static void closeAll() throws SQLException {
//        //for (Weak con : ThreadLocalConnection.allConnections) {
//            //con.close();
//        //}
//    }
//
//    private static class ThreadLocalConnection extends ThreadLocal<Connection> {
//
//        public static Collection<Connection> allConnections = new LinkedList<Connection>();
//
//        @Override
//        protected Connection initialValue() {
//            String driver = props.getProperty("driver");
//            String url = props.getProperty("url");
//            String user = props.getProperty("user");
//            String password = props.getProperty("password");
//            try {
//                Class.forName(driver); // Touch the mysql driver.
//            } catch (ClassNotFoundException e) {
//                log.error("ERROR", e);
//            }
//            try {
//                Connection con = DriverManager.getConnection(url, user, password);
//                allConnections.add(con);
//                return con;
//            } catch (SQLException e) {
//                log.error("ERROR", e);
//                return null;
//            }
//        }
//    }
//}
