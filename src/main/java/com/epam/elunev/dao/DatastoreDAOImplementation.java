/**
 * 
 */
package com.epam.elunev.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.entities.Person;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Index.IndexState;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
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
		Map<Index, IndexState> indexMap = datastore.getIndexes();
		LOGGER.info("index map size:" + indexMap.size());
		for(Index index : indexMap.keySet()){
			LOGGER.info("index id:" + index.getId() + " index kind:" + index.getKind() + " string:" + index.toString());
		}
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
		Map<Index, IndexState> indexMap = datastore.getIndexes();
		LOGGER.info("index map size:" + indexMap.size());
		Transaction transaction = datastore.beginTransaction();
		try{
			Key customerKey = KeyFactory.createKey(INDEX_NAME, person.getName());

			Date date = new Date();
			Entity entity = new Entity(INDEX_NAME, customerKey);
			entity.setProperty("name", person.getName());
			entity.setProperty("description", person.getDescription());
			entity.setProperty("date", date);
			datastore.put(entity);
			transaction.commit();
			LOGGER.info("entity:" + entity.getKey().getId());
			return Person.createPersonFromEntity(entity);
		}finally{
			if(transaction.isActive())
				transaction.rollback();
		}
	}
	
	private void putEntityToDatastore(Entity personEntity, Person person){
		personEntity.setProperty("name", person.getName());
		personEntity.setProperty("description", person.getDescription());
		personEntity.setProperty("date", new Date());
		datastore.put(personEntity);
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#update(com.epam.elunev.entities.Person)
	 */
	@Override
	public Person update(Person person) {

		Entity personEntity = null;
		Transaction transaction = datastore.beginTransaction();
		try{
			try {
				personEntity = datastore.get(KeyFactory.stringToKey(person.getId()));
			} catch (EntityNotFoundException e) {
				LOGGER.info("creating new enttity");
				Key customerKey = KeyFactory.createKey(INDEX_NAME, person.getName());
				personEntity = new Entity(INDEX_NAME, customerKey);
			}
			putEntityToDatastore(personEntity, person);
			transaction.commit();
		}finally{
			if(transaction.isActive())
			transaction.rollback();
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

			
		return Person.createPersonFromEntity(personEntity);
		//}
		//return person;
	}

	/* (non-Javadoc)
	 * @see com.epam.elunev.dao.DatastoreDAOInterface#delete(com.epam.elunev.entities.Person)
	 */
	@Override
	public void delete(Person person) {		
		Transaction transaction = datastore.beginTransaction();
		try {
			datastore.delete(KeyFactory.stringToKey(person.getId()));	
			transaction.commit();
		}finally{
			if(transaction.isActive())
			transaction.rollback();
		}
	}

}
