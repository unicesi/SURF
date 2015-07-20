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
 * RESTful web service providing a REST API to query temperature and humidity information from MOTEs that 
 * provide it as a CoAP resource.
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
@Path("weather")
public class WeatherForecastGateway {

	/**
	 * Returns an XML with both temperature and humidity measurements from the discovered MOTEs.
	 * 
	 * @return An XML with both temperature and humidity measurements from the discovered MOTEs.
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_XML)
	public String getWeather() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.ALL);
	}

	/**
	 * Returns an XML with temperature measurements from the discovered MOTEs.
	 * 
	 * @return An XML with temperature measurements from the discovered MOTEs.
	 */
	@GET
	@Path("temperature")
	@Produces(MediaType.APPLICATION_XML)
	public String getTemperature() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.TEMPERATURE);
	}

	/**
	 * Returns an XML with humidity measurements from the discovered MOTEs.
	 * 
	 * @return An XML with humidity measurements from the discovered MOTEs.
	 */
	@GET
	@Path("humidity")
	@Produces(MediaType.APPLICATION_XML)
	public String getHumidity() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.HUMIDITY);
	}

	/**
	 * Returns an XML with the specified measurement for the MOTE matching the given ID.
	 * 
	 * @param measurement The required measurement: temperature, humidity or all.
	 * @param id The ID of the MOTE.
	 * @return An XML with the specified measurement for the MOTE matching the given ID.
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
	 * Maps CoAP response codes to HTTP response codes and returns the HTTP response.
	 * 
	 * @param responseCode The CoAP response code.
	 * @param message The message to be added to the response.
	 * @return An HTTP response with the matched HTTP response code for the given CoAP response code.
	 */
	private Response getErrorResponse(String responseCode, String message) {
		Response response;
		switch (responseCode) {
		// CoAP client side errors:
		case "4.00": // CoAP Bad request
		case "4.02": // CoAP Bad option
			response = Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.01": // CoAP Unauthorized
			response = Response.status(Response.Status.UNAUTHORIZED).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.03": // CoAP Forbidden
			response = Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.04": // CoAP Not Found
		case "4.05": // CoAP Method Not Allowed
			response = Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.06": // CoAP Not Acceptable
		case "4.13": // CoAP Request Entity Too Large
			response = Response.status(Response.Status.NOT_ACCEPTABLE).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.12": // CoAP Precondition Failed
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		case "4.15": // CoAP Unsupported Content-Format
			response = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;

		default: // All CoAP's server side errors are defaulted to HTTP 500 Internal Server Error.
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build();
			break;
		}
		return response;
	}
	
}
