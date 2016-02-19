/**
 * 
 */
package com.epam.elunev.entities;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;


/**
 * @author evgenii.lunev
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Person {
	
	private String id;
	private String name;
	private String description;
	private Date date;
	
	public Person() {
		super();
	}
	
	public static Person createPersonFromEntity(Entity entity){
		Person person = new Person();
		person.setId(KeyFactory.keyToString(entity.getKey()));
		person.setName((String) entity.getProperty("name"));
		person.setDate((Date) entity.getProperty("date"));
		person.setDescription((String) entity.getProperty("description"));
		return person;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
