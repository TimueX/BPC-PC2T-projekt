package cz.vut.fekt.project.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cz.vut.fekt.project.model.Analyst;
import cz.vut.fekt.project.model.CooperationLevel;
import cz.vut.fekt.project.model.Security;
import cz.vut.fekt.project.model.Worker;
import cz.vut.fekt.project.model.WorkerType;

public class DatabaseService {

    private static final String URL = "jdbc:sqlite:data.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    private static final String INSERT_WORKER =
            "INSERT INTO workers VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_RELATION =
            "INSERT INTO relations VALUES (?, ?, ?)";

    public static void init() {
        try (Connection conn = connect();
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS workers(
                    id INTEGER PRIMARY KEY,
                    type TEXT,
                    name TEXT,
                    surname TEXT,
                    year INTEGER
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS relations(
				    id1 INTEGER,
				    id2 INTEGER,
				    level TEXT,
				    PRIMARY KEY (id1, id2),
				    CHECK (id1 < id2)
				)
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(Registry reg) {
        try (Connection conn = connect()) {

            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {

                st.execute("DELETE FROM workers");
                st.execute("DELETE FROM relations");

                try (PreparedStatement ps = conn.prepareStatement(INSERT_WORKER)) {
                    for (Worker w : reg.getAll()) {
                        ps.setInt(1, w.getId());
                        ps.setString(2, w.getType().name());
                        ps.setString(3, w.getName());
                        ps.setString(4, w.getSurname());
                        ps.setInt(5, w.getBirthYear());
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(INSERT_RELATION)) {
                    for (Worker w : reg.getAll()) {
                        for (var e : w.getRelations().entrySet()) {
                            Worker other = e.getKey();

                            if (w.getId() < other.getId()) {
                                ps.setInt(1, w.getId());
                                ps.setInt(2, other.getId());
                                ps.setString(3, e.getValue().name());
                                ps.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Registry reg) {
    		reg.clear();
        try (Connection conn = connect()) {

            Map<Integer, Worker> temp = new HashMap<>();

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM workers");

            while (rs.next()) {

	            	WorkerType wt;
	            	try {
	            	    wt = WorkerType.valueOf(rs.getString("type"));
	            	} catch (IllegalArgumentException e) {
	            	    System.out.println("Neznamy typ, preskakujem riadok");
	            	    continue;
	            	}
	            	
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                int year = rs.getInt("year");
                int id = rs.getInt("id");

                Worker w;

                if (wt == WorkerType.ANALYST) {
                    w = new Analyst(id, name, surname, year);
                } else {
                    w = new Security(id, name, surname, year);
                }

                reg.addLoadedWorker(w);
                temp.put(id, w);
            }

            rs = st.executeQuery("SELECT * FROM relations");

            while (rs.next()) {
                int id1 = rs.getInt("id1");
                int id2 = rs.getInt("id2");

                CooperationLevel lvl;

                try {
                    lvl = CooperationLevel.valueOf(rs.getString("level").toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Neplatna hodnota vztahu, preskakujem riadok");
                    continue;
                }

                Worker w1 = temp.get(id1);
                Worker w2 = temp.get(id2);

                if (w1 == null || w2 == null) {
                    System.out.println("Preskakujem neplatny vztah: " + id1 + " - " + id2);
                    continue;
                }

                reg.addRelation(w1.getId(), w2.getId(), lvl);
            }
            reg.updateNextId();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}