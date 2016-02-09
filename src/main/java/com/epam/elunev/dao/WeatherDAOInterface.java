package com.epam.elunev.dao;

import java.util.List;

import com.epam.elunev.entities.WeatherClass;
import com.google.inject.ImplementedBy;

/**
 * @author evgenii.lunev
 *
 */

@ImplementedBy(WeatherDAOImplementation.class)
public interface WeatherDAOInterface {

	public List<WeatherClass> getAllWeather(Integer numberOfRows);
}
