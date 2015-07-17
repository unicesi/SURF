/*
 * This file is part of WeatherForecastGateway.
 * 
 * WeatherForecastGateway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * WeatherForecastGateway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with WeatherForecastGateway. If not, see <http://www.gnu.org/licenses/>.
 */
package ie.tcd.dsg.surf.weatherforecast.utils;

/**
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public enum WeatherMeasurement {

	/**
	 * 
	 */
	ALL("all"),
	/**
	 * 
	 */
	TEMPERATURE("temperature"),
	/**
	 * 
	 */
	HUMIDITY("humidity");

	/**
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private WeatherMeasurement(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static WeatherMeasurement getByValue(String value) throws IllegalArgumentException {
		for (WeatherMeasurement weatherMeasurement : values()) {
			if (weatherMeasurement.getValue().equals(value)) {
				return weatherMeasurement;
			}
		}
		throw new IllegalArgumentException(value + " is not a valid weather measurement.");
	}
}
