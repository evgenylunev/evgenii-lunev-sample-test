package com.epam.elunev.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.dao.DatastoreDAOInterface;
import com.epam.elunev.dao.WeatherDAOInterface;
import com.epam.elunev.entities.Person;
import com.epam.elunev.entities.WeatherClass;
import com.epam.elunev.entities.WeatherProto.WeatherList;
import com.google.inject.Inject;

/**
 * @author evgenii.lunev
 *
 */

@Path("/rest")
public class RestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);
	
	@Inject
	private WeatherDAOInterface weatherDAO;
	
	@Inject
	private DatastoreDAOInterface datastoreDAO;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
    @Path("weather/{count}")
	public List<WeatherClass> returnWeather(@PathParam("count") String count){
		LOGGER.info("!!!weather method called");
		int numberOfRows = 10;
		if(count != null)
			try{
				numberOfRows = Integer.valueOf(count);
			}catch(NumberFormatException ex){
				LOGGER.info("Please, provide valid number of rows.");
			}		
		//return Response.status(Status.OK).entity(responseObject).type("application/json").build();
		return weatherDAO.getAllWeather(numberOfRows);
	}
	

	@GET
	//@Named("protobuf")
	@Produces("application/x-protobuf")
    @Path("protoweather/{count}")
	public WeatherList getProtoWeather(@PathParam("count") String count){
		LOGGER.info("!!!proto method called");
		int numberOfRows = 10;
		if(count != null)
			try{
				numberOfRows = Integer.valueOf(count);
			}catch(NumberFormatException ex){
				LOGGER.info("Please, provide valid number of rows.");
			}
		
		return weatherDAO.getAllProtoWeather(numberOfRows);
	}
	
	@PUT
	@Produces({MediaType.APPLICATION_JSON})
    @Path("datastore/add")
	public Person datastoreAdd(Person person){
		LOGGER.info("PUT method datastoreAdd name:" + person.getName());
		return datastoreDAO.add(person);
	
	}	
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
    @Path("datastore/update")
	public Person datastoreUpdate(Person person){
		LOGGER.info("POST method datastore update name:" + person.getName() + " id:" + person.getId() + " description:" + person.getDescription());
		return datastoreDAO.update(person);
	
	}
	
	@DELETE
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
    @Path("datastore/delete")
	public Person datastoreDeleteById(Person person){
		LOGGER.info("DELETE method datastoreDeleteByName key string:" + person.getId());
		datastoreDAO.delete(person);
		return null;
	
	}	

	@GET
	@Produces({MediaType.APPLICATION_JSON})
    @Path("datastore/list")
	public List<Person> datastoreList(){
		
		return datastoreDAO.list();
	
	}
}
