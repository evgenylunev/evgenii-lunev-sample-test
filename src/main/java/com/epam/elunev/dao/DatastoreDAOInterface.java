/**
 * 
 */
package com.epam.elunev.dao;

import java.util.List;

import com.epam.elunev.entities.Person;
import com.google.appengine.api.datastore.Entity;
import com.google.inject.ImplementedBy;

/**
 * @author evgenii.lunev
 *
 */
@ImplementedBy(DatastoreDAOImplementation.class)
public interface DatastoreDAOInterface {

	public List<Person> list();
	public Person add(Person person);
	public Person update(Person person);
	public void delete(Person person);
}
