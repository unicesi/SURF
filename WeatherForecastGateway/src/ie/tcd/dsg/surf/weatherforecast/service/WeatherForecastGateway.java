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
package ie.tcd.dsg.surf.weatherforecast.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ie.tcd.dsg.surf.weatherforecast.gateway.WeatherForecast;
import ie.tcd.dsg.surf.weatherforecast.utils.WeatherMeasurement;

/**
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
@Path("weather")
public class WeatherForecastGateway {

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_XML)
	public String getWeather() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.ALL);
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("temperature")
	@Produces(MediaType.APPLICATION_XML)
	public String getTemperature() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.TEMPERATURE);
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("humidity")
	@Produces(MediaType.APPLICATION_XML)
	public String getHumidity() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.HUMIDITY);
	}

	/**
	 * 
	 * @param measurement
	 * @param id
	 * @return
	 */
	@GET
	@Path("{measurement}/{id}")
	public Response getMeasurement(@PathParam("measurement") String measurement, @PathParam("id") String id) {
		Response response;
		String xml = WeatherForecast.getInstance().queryResource(id, measurement);;
		if (xml != null) {
			if (xml.startsWith("<")) {
				response = Response.ok(xml, MediaType.APPLICATION_XML).build();
			} else {
				response = getErrorResponse(xml.substring(0, 4), xml);
			}
		} else {
			String notFoundMessage = "<h1>Ooops!</h1><br /><h2>MOTE not found :(</h2>";
			response = Response.status(Response.Status.NOT_FOUND).entity(notFoundMessage).type(MediaType.TEXT_HTML).build();
		}
		return response;
	}
	
	/**
	 * 
	 * @param responseCode
	 * @param message
	 * @return
	 */
	private Response getErrorResponse(String responseCode, String message) {
		Response response;
		switch (responseCode) {
		case "4.00":
		case "4.02":
			response = Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.01":
			response = Response.status(Response.Status.UNAUTHORIZED).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.03":
			response = Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.04":
		case "4.05":
			response = Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.06":
		case "4.13":
			response = Response.status(Response.Status.NOT_ACCEPTABLE).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.12":
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.15":
			response = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;

		default:
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		}
		return response;
	}
	
}
