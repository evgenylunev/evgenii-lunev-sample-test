/**
 * 
 */
package com.epam.elunev.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.entities.Person;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.inject.Singleton;

/**
 * @author evgenii.lunev
 *
 */
@Singleton
public class DatastoreDAOImplementation implements DatastoreDAOInterface {
	
	private DatastoreService datastore;
	private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreDAOImplementation.class);
	private static final String INDEX_NAME = "Persons";

	public DatastoreDAOImplementation() {
		super();
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#list()
	 */
	@Override
	public List<Person> list() {
		List<Person> result = new ArrayList<>();
		Query query = new Query(INDEX_NAME).addSort("date", Query.SortDirection.DESCENDING);
		List<Entity> entityList = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
		for(Entity entity : entityList){
			result.add(Person.createPersonFromEntity(entity));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#add()
	 */
	@Override
	public Person add(Person person) {
		Key customerKey = KeyFactory.createKey(INDEX_NAME, person.getName());
        
		Date date = new Date();
        Entity entity = new Entity(INDEX_NAME, customerKey);
        entity.setProperty("name", person.getName());
        entity.setProperty("description", person.getDescription());
        entity.setProperty("date", date);
        datastore.put(entity);
        LOGGER.info("entity:" + entity.getKey().getId());
        return Person.createPersonFromEntity(entity);
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#update(com.epam.elunev.entities.Person)
	 */
	@Override
	public Person update(Person person) {
		String name = person.getName();
		String description = person.getDescription();
		
		Entity personEntity = null;;
		try {
			personEntity = datastore.get(KeyFactory.stringToKey(person.getId()));
		} catch (EntityNotFoundException e) {
			LOGGER.info("creating new enttity");
			Key customerKey = KeyFactory.createKey(INDEX_NAME, name);
			personEntity = new Entity(INDEX_NAME, customerKey);
		}
		//if(person.getOriginalName() != null){
		//	String originalName =  person.getOriginalName();
//actual filter query implementation
		//	Query query = new Query(INDEX_NAME);
		//	Filter nameFilter =  new FilterPredicate("name", FilterOperator.EQUAL, name);
		//	query.setFilter(nameFilter);
		//	PreparedQuery pq = datastore.prepare(query);
		//	for (Entity result : pq.asIterable()) {
		//}

			personEntity.setProperty("name", name);
			personEntity.setProperty("description", description);
			personEntity.setProperty("date", new Date());
			datastore.put(personEntity);
			return Person.createPersonFromEntity(personEntity);
		//}
		//return person;
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#delete(com.epam.elunev.entities.Person)
	 */
	@Override
	public void delete(Person person) {			
        datastore.delete(KeyFactory.stringToKey(person.getId()));		
	}

}
