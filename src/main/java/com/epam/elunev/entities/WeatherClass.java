package com.epam.elunev.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.services.bigquery.model.TableCell;

/**
 * @author evgenii.lunev
 *
 */

@Entity
@XmlRootElement
public class WeatherClass {
	
	private long stationNumber;
	private int year;
	private int month;
	private int day;
	private boolean rain;
	
	public WeatherClass() {
		super();
	}

	public static WeatherClass createWeatherClassFromTAbleCells(List<TableCell> cells){
		WeatherClass wc = new WeatherClass();
		TableCell[] cellsArray = new TableCell[cells.size()];
		cellsArray = cells.toArray(cellsArray);
		wc.setStationNumber(Long.valueOf( (String) cellsArray[0].getV()));
		wc.setYear(Integer.valueOf((String) cellsArray[1].getV()));
		wc.setMonth(Integer.valueOf((String) cellsArray[2].getV()));
		wc.setDay(Integer.valueOf((String) cellsArray[3].getV()));
		wc.setRain(Boolean.valueOf((String) cellsArray[4].getV()));
		return wc;
	}
	
	public long getStationNumber() {
		return stationNumber;
	}

	public void setStationNumber(long stationNumber) {
		this.stationNumber = stationNumber;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isRain() {
		return rain;
	}

	public void setRain(boolean rain) {
		this.rain = rain;
	}
	
}
