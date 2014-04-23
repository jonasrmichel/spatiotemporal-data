package stdata.simulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public class MovingObjectDatabase implements IMovingObjectDatabase {

	/** Database settings. */
	public static final String PG_URL = "jdbc:postgresql://localhost/";
	public static final String PG_DATABASE = "moving_object_database";
	public static final String PG_USER = "postgres";
	public static final String PG_PASSWORD = "postgres";
	public static final String PG_TEMPLATE = "template_postgis";

	/** The simulation's table name. */
	String simTable;

	/** JDBC database connection. */
	Connection conn = null;

	public MovingObjectDatabase(String simTable) {
		this.simTable = simTable;

		initialize();
	}

	private void initialize() {
		try {
			try {
				// attempt to connect to the moving object database
				if (SimulationManager.verbose)
					Util.report(MovingObjectDatabase.class,
							"connecting to moving object database");

				conn = DriverManager.getConnection(PG_URL + PG_DATABASE,
						PG_USER, PG_PASSWORD);

			} catch (SQLException ex) {
				if (SimulationManager.verbose)
					Util.report(MovingObjectDatabase.class,
							"moving object database does not exist");

				conn = createDatabase();
			}

			// (re)create simulation table
			createSimulationTable();

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	private Connection createDatabase() throws SQLException {
		Connection conn = null;
		Statement st = null;
		try {
			if (SimulationManager.verbose)
				Util.report(MovingObjectDatabase.class,
						"creating moving object database");

			conn = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD);
			st = conn.createStatement();

			String sqlCreateDatabase = "CREATE DATABASE " + PG_DATABASE
					+ " WITH TEMPLATE " + PG_TEMPLATE;
			st.executeUpdate(sqlCreateDatabase);

			if (SimulationManager.verbose)
				Util.report(MovingObjectDatabase.class,
						"database created successfully");

		} catch (SQLException ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (st != null)
					st.close();

				if (conn != null)
					conn.close();

				return DriverManager.getConnection(PG_URL + PG_DATABASE,
						PG_USER, PG_PASSWORD);

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	private void createSimulationTable() {
		Statement st = null;
		try {
			if (SimulationManager.verbose)
				Util.report(MovingObjectDatabase.class,
						"(re)creating simulation moving object table");

			// drop table
			String sqlDropTable = "DROP TABLE IF EXISTS " + simTable;

			// table schema
			String sqlCreateTable = "CREATE TABLE " + simTable + " "
					+ "(id SERIAL NOT NULL, "
					+ "identifier INTEGER NOT NULL, " + "type VARCHAR(50), "
					+ "timestamp INTEGER NOT NULL, " + "PRIMARY KEY " + "(id)"
					+ ")";

			// geometry spatial column
			String sqlAddGeometryColumn = "SELECT AddGeometryColumn('"
					+ simTable + "', 'location', 4326, 'POINT', 2)";

			// index for WGS84 (planar degree) projection
			String sqlCreateIndexGist4326 = "CREATE INDEX " + simTable
					+ "_location_gist_4326 " + "ON " + simTable + " "
					+ "USING GIST " + "( location )";

			// functional index for transformations to metric projection
			String sqlCreateIndexGist26986 = "CREATE INDEX " + simTable
					+ "_location_gist_26986 " + "ON " + simTable + " "
					+ "USING GIST " + "( ST_Transform(location, 26986) )";

			// index for identifier-timestamp combinations
			String sqlCreateIndexIdentifierTimestamp = "CREATE INDEX "
					+ simTable + "_identifier_timestamp " + "ON " + simTable
					+ " (identifier, timestamp DESC)";

			st = conn.createStatement();
			st.executeUpdate(sqlDropTable);
			st.executeUpdate(sqlCreateTable);
			st.execute(sqlAddGeometryColumn);
			st.executeUpdate(sqlCreateIndexGist4326);
			st.executeUpdate(sqlCreateIndexGist26986);
			st.executeUpdate(sqlCreateIndexIdentifierTimestamp);

			if (SimulationManager.verbose)
				Util.report(MovingObjectDatabase.class,
						"table (re)created successfully");

		} catch (SQLException ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (st != null)
					st.close();

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/** Shuts down the moving object database connection. */
	public void shutdown() {
		if (SimulationManager.verbose)
			Util.report(MovingObjectDatabase.class, "shutting down");

		try {
			if (conn != null)
				conn.close();

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	/* IMovingObjectDatabase interface implementation. */

	@Override
	public void updatePosition(int identifier, String type, int timestamp,
			Geoshape location) {
		Statement st = null;
		try {
			String sql = "INSERT INTO " + simTable
					+ " (identifier, type, timestamp, location) VALUES ("
					+ Integer.toString(identifier) + ", '" + type + "', "
					+ Integer.toString(timestamp)
					+ ", ST_PointFromText('POINT("
					+ Float.toString(location.getPoint().getLongitude()) + " "
					+ Float.toString(location.getPoint().getLatitude())
					+ ")', 4326) )";

			st = conn.createStatement();
			st.executeUpdate(sql);

		} catch (SQLException ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (st != null)
					st.close();

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public List<Integer> getNearbyObjects(String type, Geoshape location,
			double distance) {
		Statement st = null;
		ResultSet rs = null;
		List<Integer> nearby = null;
		try {
			String sql = "SELECT DISTINCT ON (identifier) identifier FROM "
					+ simTable + " WHERE " + "type='" + type + "' "
					+ "AND ST_DWithin(ST_Transform(ST_PointFromText('POINT("
					+ Float.toString(location.getPoint().getLongitude()) + " "
					+ Float.toString(location.getPoint().getLatitude())
					+ ")', 4326), 26986), ST_Transform(location, 26986), "
					+ Double.toString(distance) + ") "
					+ "ORDER BY identifier, timestamp DESC";

			st = conn.createStatement();
			rs = st.executeQuery(sql);

			nearby = new ArrayList<Integer>();
			while (rs.next())
				nearby.add(rs.getInt("identifier"));

		} catch (SQLException ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();

				if (st != null)
					st.close();

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return nearby;
	}

	public static void main(String[] args) {
		MovingObjectDatabase mod = new MovingObjectDatabase("test");
		mod.shutdown();
	}
}
