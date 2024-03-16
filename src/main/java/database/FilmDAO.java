package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Film;

import java.sql.*;


public class FilmDAO {
	
	Film oneFilm = null;
	Connection c = null;
    Statement s = null;
	String user = "lijiawen";
    String password = "dreswAlt4";
    // Note none default port used, 6306 not 3306
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/"+user;

	public FilmDAO() {}

	
	private Statement openConnection(){
		// loading jdbc driver for mysql
		try{
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch(Exception e) { System.out.println(e); }

		// connecting to database
		try{
			// connection string for demos database, username demos, password demos
 			c = DriverManager.getConnection(url, user, password);
		    s = c.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return s;
    }

	private void closeConnection(){
		try {
			if (s != null) {
				s.close();
			}
			if (c != null) {
				c.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Film getNextFilm(ResultSet rs){
    	Film thisFilm=null;
		try {
			thisFilm = new Film(
					rs.getInt("id"),
					rs.getString("title"),
					rs.getInt("year"),
					rs.getString("director"),
					rs.getString("stars"),
					rs.getString("review"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisFilm;		
	}

	/**
	 * Retrieve all Films
	 * @return Collection of all films found in the mysql database
	 */
   public ArrayList<Film> getAllFilms(){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films";
		    ResultSet rs1 = s.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    s.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
   }

   public Film getFilmByID(int id){
	   
		openConnection();
		oneFilm=null;
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films where id="+id;
		    ResultSet rs1 = s.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    }

		    s.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return oneFilm;
   }
   
   public boolean insertFilm(Film f) throws SQLException {
	   boolean b = false;
	   try {
		   String sql = "insert into films (title, year, director, stars, review) values ("
		   		+ "'" + f.getTitle() + "','" 
				+ f.getYear() + "','" 
				+ f.getDirector() + "','" 
				+ f.getStars() + "','" 
				+ f.getReview()
				+ "');";
		   
		   System.out.println(sql);
		   b = openConnection().execute(sql);
		   closeConnection();
		   b = true;
	   } catch (SQLException s) {
		   throw new SQLException("Film Not Added");
	   }
	   
	   return b;
   }
   
   public boolean updateFilm(Film f) throws SQLException {
	   boolean b = false;
	   try {
		   String sql = 
				   "update films set title = '" + f.getTitle() + "',"
				   + " year = " +  f.getYear() + ","
				   + " director = '" +  f.getDirector() + "',"
				   + " stars = '" +  f.getStars() + "',"
				   + " review = '" +  f.getReview() + "'"
				   + " where id = " + f.getId();
		   
		   System.out.println(sql);
		   b = openConnection().execute(sql);
		   closeConnection();
		   b = true;
	   } catch (SQLException e) {
		   throw new SQLException("Film Not Updated");
	   }
	   
	   return b;
   }
   
   public boolean deleteFilmById(int id) throws SQLException {
	   boolean b = false;
	   try {
		   String sql = "delete from films where id="+id;
		   
		   System.out.println(sql);
		   b = openConnection().execute(sql);
		   closeConnection();
		   b = true;
	   } catch (SQLException e) {
		   throw new SQLException("Film Not Deleted");
	   }
	   
	   return b;
   }
   
}
