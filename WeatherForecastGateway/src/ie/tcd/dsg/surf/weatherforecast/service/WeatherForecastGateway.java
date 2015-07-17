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
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 *
 */
@Path("weather")
public class WeatherForecastGateway {

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_XML)
	public String getWeather() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.ALL);
	}

	@GET
	@Path("temperature")
	@Produces(MediaType.APPLICATION_XML)
	public String getTemperature() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.TEMPERATURE);
	}

	@GET
	@Path("humidity")
	@Produces(MediaType.APPLICATION_XML)
	public String getHumidity() {
		return WeatherForecast.getInstance().queryAllResources(WeatherMeasurement.HUMIDITY);
	}

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
