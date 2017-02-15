package net.sf.odinms.client.anticheat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.TimerManager;

public class CheatingOffensePersister {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CheatingOffensePersister.class);
    private final static CheatingOffensePersister INSTANCE = new CheatingOffensePersister();
    private Set<CheatingOffenseEntry> toPersist = new LinkedHashSet<CheatingOffenseEntry>();

    private CheatingOffensePersister() {
        //TimerManager.getInstance().register(new PersistingTask(), 61000);
    }

    public static CheatingOffensePersister getInstance() {
        return INSTANCE;
    }

    public void persistEntry(CheatingOffenseEntry coe) {
        synchronized (toPersist) {
            toPersist.remove(coe);
            toPersist.add(coe);
        }
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            CheatingOffenseEntry[] offenses;
            synchronized (toPersist) {
                offenses = toPersist.toArray(new CheatingOffenseEntry[toPersist.size()]);
                toPersist.clear();
            }

            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement insertps = con.prepareStatement("INSERT INTO cheatlog (cid, offense, count, lastoffensetime, param) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateps = con.prepareStatement("UPDATE cheatlog SET count = ?, lastoffensetime = ?, param = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                for (CheatingOffenseEntry offense : offenses) {
                    String parm = offense.getParam() == null ? "" : offense.getParam();
                    if (offense.getDbId() == -1) {
                        insertps.setInt(1, offense.getChrfor().getId());
                        insertps.setString(2, offense.getOffense().name());
                        insertps.setInt(3, offense.getCount());
                        insertps.setTimestamp(4, new Timestamp(offense.getLastOffenseTime()));
                        insertps.setString(5, parm);
                        insertps.setQueryTimeout(1);
                        try
                        {
                            insertps.executeUpdate();           
                        } catch (SQLException e){}
                        ResultSet rs = insertps.getGeneratedKeys();
                        if (rs.next()) {
                            offense.setDbId(rs.getInt(1));
                        }
                        insertps.close();
                        rs.close();
                    } else {
                        updateps.setInt(1, offense.getCount());
                        updateps.setTimestamp(2, new Timestamp(offense.getLastOffenseTime()));
                        updateps.setString(3, parm);
                        updateps.setInt(4, offense.getDbId());
                        updateps.setQueryTimeout(1);
                        try
                        {
                            updateps.executeUpdate();           
                        } catch (SQLException e){}
                        updateps.close();
  
                    }
                }
                con.close();
            } catch (SQLException e) {
                log.error("error persisting cheatlog", e);
            }
        }
    }
}