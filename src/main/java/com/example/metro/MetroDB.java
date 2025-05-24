package com.example.metro;


import java.sql.ResultSet;
import java.sql.SQLException;


public class MetroDB extends DBConnection{

    MetroDB(String DBName, String DBUser, String DBPassword) throws ClassNotFoundException, SQLException {
        super(DBName, DBUser, DBPassword);

    }

    public ResultSet StationConnectionsQueryResult() throws SQLException {
        String query = "SELECT S1.XCoordinate, S1.YCoordinate, S2.XCoordinate, S2.YCoordinate, L.LineColor\n" +
                        "FROM connections AS C\n" +
                        "JOIN stations AS S1 ON C.Station1ID = S1.StationID \n" +
                        "JOIN stations AS S2 ON C.Station2ID = S2.StationID \n" +
                        "JOIN lines AS L ON C.LineID = L.LineID;";
        return QueryResult(query);
    }

    public ResultSet StationsQueryResult() throws SQLException {
        String query = "SELECT * FROM stations;";
        return QueryResult(query);
    }

    public ResultSet ConnectionsTravelTimeQueryResult() throws SQLException {
        String query = "SELECT Station1ID, Station2ID, TravelTime FROM connections;";
        return QueryResult(query);
    }

    public ResultSet StationsCountQueryResult() throws SQLException {
        String query = "SELECT COUNT(StationID) FROM stations;";
        return QueryResult(query);
    }

    public ResultSet IdByStationQueryResult(String StationName) throws SQLException {
        String query = "SELECT StationID FROM stations\n" +
                        "WHERE StationName = '" + StationName + "';";
        return QueryResult(query);
    }

    public ResultSet StationByIdQueryResult(int StationId) throws SQLException {
        String query = "SELECT StationName FROM stations\n" +
                        "WHERE StationId = " + StationId + ";";
        return QueryResult(query);
    }

    public ResultSet CoordinatesByIdQueryResult(int StationId) throws SQLException {
        String query = "SELECT XCoordinate, YCoordinate FROM stations\n" +
                        "WHERE StationId = " + StationId + ";";
        return QueryResult(query);
    }

    public ResultSet StationConnectionsByIDQueryResult(int Station1Id, int Station2d) throws SQLException {
        String query = "SELECT S1.XCoordinate, S1.YCoordinate, S2.XCoordinate, S2.YCoordinate, L.LineColor\n" +
                        "FROM connections AS C\n" +
                        "INNER JOIN stations AS S1 ON C.Station1ID = S1.StationID OR C.Station2ID = S1.StationID\n" +
                        "INNER JOIN stations AS S2 ON C.Station2ID = S2.StationID OR C.Station1ID = S2.StationID\n" +
                        "INNER JOIN lines AS L ON C.LineID = L.LineID\n" +
                        "WHERE S1.StationID = " + Station1Id + " AND S2.StationID = " + Station2d + ";";
        return QueryResult(query);
    }


}
