package com.epam.elunev.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.dao.WeatherDAOInterface;
import com.epam.elunev.entities.WeatherClass;
import com.google.inject.Inject;

/**
 * @author evgenii.lunev
 *
 */

@Path("/epam/rest")
public class RestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);
	
	@Inject
	private WeatherDAOInterface weatherDAO;
	
	@GET
    //@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
    @Path("weather/{count}")
	public List<WeatherClass> returnWeather(@PathParam("count") String count){

		int numberOfRows = 10;
		if(count != null)
			try{
				numberOfRows = Integer.valueOf(count);
			}catch(NumberFormatException ex){
				LOGGER.info("Please, provide valid number of rows.");
			}
		List <WeatherClass> responseObject = weatherDAO.getAllWeather(numberOfRows);			
		
		//return Response.status(Status.OK).entity(responseObject).type("application/json").build();
		return responseObject;
	}
}
