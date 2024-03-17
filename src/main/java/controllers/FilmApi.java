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
		Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		String type = request.getParameter("type");
		response.setContentType("application/" + type); // server to client
		
		// retrieve data from database
		FilmDAO dao = new FilmDAO();
		ArrayList<Film> allFilms = dao.getAllFilms();
		
		String allFilmsBaseOnType = "";
		// set response base on different type
		if (type.equals("json")) {
			allFilmsBaseOnType = gson.toJson(allFilms);
			System.out.println("test");
			
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
		out.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		FilmDAO dao = new FilmDAO();
		Film film = new Film();
		PrintWriter out = response.getWriter();
//		StringReader stringReader = new StringReader();
		
		// 處理「'」標點符號問題
		BufferedReader reader = request.getReader();
		String type = request.getContentType();
		System.out.println("reader: " + reader);
		if (type.equals("application/json")) {
			Gson gson = new Gson();
			film = gson.fromJson(reader, Film.class);
			System.out.println("JSON film title: " + film.getTitle());
		} else if (type.equals("application/xml")) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				film = (Film) jaxbUnmarshaller.unmarshal(reader);
				System.out.println("XML film title: " + film.getTitle());
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		try {
			boolean result = dao.insertFilm(film);
			
			if (result) {
				System.out.println("res: " + result);
				out.write("Film inserted!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		out.close();
	}
}