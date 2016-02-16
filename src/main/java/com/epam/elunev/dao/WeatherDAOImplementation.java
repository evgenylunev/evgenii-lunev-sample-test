package com.epam.elunev.dao;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.entities.WeatherClass;
import com.epam.elunev.entities.WeatherProto;
import com.epam.elunev.entities.WeatherProto.WeatherItem;
import com.epam.elunev.entities.WeatherProto.WeatherList;
import com.epam.elunev.entities.WeatherProto.WeatherList.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
//import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author evgenii.lunev
 *
 */

@Singleton
public class WeatherDAOImplementation implements WeatherDAOInterface {
	
	//@Inject
	//protected EntityManager entityManager;
	
	private Bigquery bigquery;
	private static final Logger LOGGER = LoggerFactory.getLogger(WeatherDAOImplementation.class);
	//private static final String QUERY_STRING = "SELECT station_number, year, month, day FROM [publicdata:samples.gsod]  where year=2003 and month=3 and day=14 LIMIT 2";
	private static final String QUERY_STRING = "SELECT table1.station_number as station_number, table1.year as year, table1.month as month, first_day as day, rain1 as rain " 
	 + "FROM (select station_number, year, month, day as first_day, mean_temp as mean_temp1, rain as rain1 from [publicdata:samples.gsod] where station_number=726506) as table1 " 
    + "join each (select station_number, year, month, (day + 1) as second_day, mean_temp as mean_temp2,  rain as rain2 from [publicdata:samples.gsod] where station_number=726506) as table2  "
   +  "on first_day = second_day and table1.month = table2.month and table1.year = table2.year "
   + "where rain1 != rain2 "
   + "order by table1.year desc, table1.month desc, first_day desc";// LIMIT 20";
	private String project_id;
	private  String scope;
	private String serviceAccountId;
	private String P12File;
	
	@Inject
	public WeatherDAOImplementation(@Named("project_id") String project_id, 
			@Named("scope") String scope, 
			@Named("serviceAccountId") String serviceAccountId, 
			@Named("P12File") String P12File) {
		super();
		this.project_id = project_id;
		this.scope = scope;
		this.serviceAccountId = serviceAccountId;
		this.P12File = P12File;
		try {
			this.bigquery = createAuthorizedClient();
		} catch (IOException e) {
			LOGGER.error("IOException, cause:" + e.getCause());
		} catch (GeneralSecurityException e) {
			LOGGER.error("GeneralSecurityException, cause:" + e.getCause());
		}
		LOGGER.info("WeatherDAOImplementation bigquery initialized");
	}

	
	@Override
	public WeatherList getAllProtoWeather(Integer numberOfRows) {
		
		
		Builder builder = WeatherProto.WeatherList.newBuilder();
		for(TableRow row: runPredefinedQuery(numberOfRows)){
			TableCell[] cellsArray = new TableCell[row.getF().size()];
			cellsArray = row.getF().toArray(cellsArray);
			WeatherItem w = WeatherProto.WeatherItem.newBuilder()
					.setStationnumber(Long.valueOf( (String) cellsArray[0].getV()))
					.setYear(Integer.valueOf((String) cellsArray[1].getV()))
					.setMonth(Integer.valueOf((String) cellsArray[2].getV()))
					.setDay(Integer.valueOf((String) cellsArray[3].getV()))
					.setRain(Boolean.valueOf((String) cellsArray[4].getV()))
					.build();
			builder.addList(w);
		}
		return builder.build();
	}
	
	@Override
	public List<WeatherClass> getAllWeather(Integer numberOfRows) {
		
		List<WeatherClass> result = new ArrayList<>();
		//LOGGER.error("qeuryRes size:" + queryRes.size());
		for(TableRow row: runPredefinedQuery(numberOfRows)){
			
			WeatherClass wc = WeatherClass.createWeatherClassFromTAbleCells(row.getF());
			result.add(wc);
		}
		return result;
	}
	

	private List<TableRow> runPredefinedQuery(Integer numberOfRows){
		List<TableRow> queryRes = null;
		String queryString = QUERY_STRING + " LIMIT " + numberOfRows;
		try {
			queryRes = executeQuery(queryString, project_id);
			if(queryRes == null)
				queryRes = new ArrayList<TableRow>();
				
		} catch (IOException e) {
			LOGGER.error("IOException, cause:" + e.getCause());
			 e.printStackTrace();
		}
		return queryRes;
	}
	
	 private Bigquery createAuthorizedClient() throws IOException, GeneralSecurityException {
		    // Create the credential
		 HttpTransport TRANSPORT = new NetHttpTransport();
		    JsonFactory JSON_FACTORY = new JacksonFactory();
		 GoogleCredential credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
			        .setJsonFactory(JSON_FACTORY)
			        .setServiceAccountId(serviceAccountId)
			        .setServiceAccountScopes(Arrays.asList(new String[] {scope}))
			        .setServiceAccountPrivateKeyFromP12File(new File(P12File))
			        .build();

		 bigquery = new Bigquery.Builder(TRANSPORT, JSON_FACTORY, credential)
				        .setApplicationName(project_id) // any name seems to do it??
				        .setHttpRequestInitializer(credential).build();
		 
			    return bigquery;
		    
		  }
		  
		  /**
		   * Executes the given query synchronously.
		   *
		   * @param querySql the query to execute.
		   * @param bigquery the Bigquery service object.
		   * @param projectId the id of the project under which to run the query.
		   * @return a list of the results of the query.
		   * @throws IOException if there's an error communicating with the API.
		   */
		  private List<TableRow> executeQuery(String querySql,  String projectId)
		      throws IOException {
		    QueryResponse query = bigquery.jobs().query(
		        projectId,
		        new QueryRequest().setQuery(querySql))
		        .execute();

		    // Execute it
		    GetQueryResultsResponse queryResult = bigquery.jobs().getQueryResults(
		        query.getJobReference().getProjectId(),
		        query.getJobReference().getJobId()).execute();

		    return queryResult.getRows();
		  }

}
