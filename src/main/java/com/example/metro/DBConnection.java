package com.example.metro;

import java.sql.*;

public class DBConnection {

    private Connection DB_Connection = null;
    private String DB_Name;
    private String DB_User;
    private String DB_Password;

    public String getDB_User() {
        return DB_User;
    }

    public String getDB_Name() {
        return DB_Name;
    }

    public Connection getDB_Connection() {
        return DB_Connection;
    }

    public void setDB_Name(String DBName) {
        this.DB_Name = DBName;
    }

    public void setDB_User(String DBUser) {
        this.DB_User = DBUser;
    }

    public void setDB_Password(String DBPassword) {
        this.DB_Password = DBPassword;
    }

    DBConnection(String DBName, String DBUser, String DBPassword) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        this.DB_Name = DBName;
        this.DB_User = DBUser;
        this.DB_Password = DBPassword;
        Connect();
    }

    public Connection Connect() throws SQLException {

        this.DB_Connection = DriverManager.getConnection(this.DB_Name, this.DB_User, this.DB_Password);

        if (this.DB_Connection != null) {
            System.out.println("Successful connection to the database " + this.DB_Name);
        } else {
            System.out.println("Connection to the database has failed" + this.DB_Name);
        }

        return this.DB_Connection;

    }

    public ResultSet QueryResult(String query) throws SQLException {
        Statement statement = this.DB_Connection.createStatement();
        return statement.executeQuery(query);
    }
}
