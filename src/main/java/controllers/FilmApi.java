package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.FilmDAO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Film;
import models.FilmsList;

@WebServlet("/filmapi")
public class FilmApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String type = request.getParameter("type");
		String id = request.getParameter("id");
		response.setContentType("application/" + type); // server to client

		FilmDAO dao = new FilmDAO();
		
		// get single film data
		if (id != null) {
			Film film = new Film();
			film = dao.getFilmByID(Integer.parseInt(id));
			out.write(gson.toJson(film));
		// get all films data
		} else {
			ArrayList<Film> allFilms = dao.getAllFilms();
			String allFilmsBaseOnType = "";

			// set response base on different type
			if (type.equals("json")) {
				Gson gson = new Gson();
				allFilmsBaseOnType = gson.toJson(allFilms);			
			} else if (type.equals("xml")) {
				FilmsList films = new FilmsList(allFilms);
				StringWriter sw = new StringWriter();

				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(FilmsList.class);
					Marshaller m = jaxbContext.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					
					m.marshal(films, sw);
					allFilmsBaseOnType = sw.toString();
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			out.write(allFilmsBaseOnType);
		}
		out.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		FilmDAO dao = new FilmDAO();
		Film film = new Film();
		
		// get new film data from request
	    StringBuilder buffer = new StringBuilder();
		// 處理「'」標點符號問題
		BufferedReader reader = request.getReader();
		String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();

	    // transfer to java object
	    Gson gson = new Gson();
	    film = gson.fromJson(data, Film.class);
	
		try {
			boolean result = dao.insertFilm(film);
			
			if (result) {
				out.write("Film inserted!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		out.close();
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		FilmDAO dao = new FilmDAO();
		Film film = new Film();
		
		// get updated film data from request
		StringBuilder buffer = new StringBuilder();
		// 處理「'」標點符號問題
		BufferedReader reader = request.getReader();
		String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    // transfer to java object
	    Gson gson = new Gson();
	    film = gson.fromJson(data, Film.class);
		
//		BufferedReader reader = request.getReader();
//		String type = request.getContentType();
//		if (type.equals("application/json")) {
//			Gson gson = new Gson();
//			film = gson.fromJson(reader, Film.class);
//			System.out.println("json: " + film.toString());
//		} else if (type.equals("application/xml")) {
//			try {
//				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
//				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//				film = (Film) jaxbUnmarshaller.unmarshal(reader);
//				System.out.println("xml: " + film.toString());
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		try {
			boolean result = dao.updateFilm(film);
			
			if (result) {
				out.write("Film updated!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		FilmDAO dao = new FilmDAO();
		Film film = new Film();
		int id = Integer.parseInt(request.getParameter("id"));
		
//		BufferedReader reader = request.getReader();
//		String type = request.getContentType();
//		
//		if (type.equals("application/json")) {
//			Gson gson = new Gson();
//			film = gson.fromJson(reader, Film.class);
//			id = film.getId();
//		} else if (type.equals("application/xml")) {
//			try {
//				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
//				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//				film = (Film) jaxbUnmarshaller.unmarshal(reader);
//				id = film.getId();
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		try {
			boolean result = dao.deleteFilmById(id);
			
			if (result) {
				out.write("Film deleted!");
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}