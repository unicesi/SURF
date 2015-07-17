package ie.tcd.dsg.surf.weatherforecast.gateway;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import ie.tcd.dsg.surf.weatherforecast.utils.WeatherMeasurement;

public class WeatherForecast {

	private static final String moteServerURI = "coap://localhost:5683";
	private static final String COAP_DISCOVERY_PATH = "/.well-known/core";

	private static WeatherForecast weatherForecast;
	
	private DiscoveryThread discoveryThread;
	private CoapClient coapClient;
	private Set<WebLink> resources;
	private Set<String> discoveredRoutes;

	private WeatherForecast() {
		this.coapClient = new CoapClient();
		this.discoveredRoutes = new HashSet<>();
		this.resources = new HashSet<>();
	}
	
	public static WeatherForecast getInstance() {
		if (weatherForecast == null) {
			weatherForecast = new WeatherForecast();
		}
		return weatherForecast;
	}

	public int discoverAllResources() {
		this.coapClient.setURI(moteServerURI);
		Set<WebLink> discoveredResources = this.coapClient.discover();
		for (WebLink webLink : discoveredResources) {
			if (!webLink.getURI().endsWith(COAP_DISCOVERY_PATH)) {
				this.resources.add(webLink);
			}
		}
		return this.resources.size();
	}
	
	public void discoverAllResources(String discoveryServerURI) {
		this.discoveryThread = new DiscoveryThread(this, discoveryServerURI);
		this.discoveryThread.start();
	}
	
	public void notifyNewRoutesFound() {
		Set<String> discoveredRoutes = this.discoveryThread.getDiscoveredRoutes();
		for (String route : discoveredRoutes) {
			if (this.discoveredRoutes.add(route)) {
				discoverAllResourcesInRoute(route);
			}
		}
	}
	
	private void discoverAllResourcesInRoute(String route) {
		this.coapClient.setURI("coap://[" + route + "]:5683");
		Set<WebLink> discoveredResources = this.coapClient.discover();
		for (WebLink webLink : discoveredResources) {
			if (!webLink.getURI().endsWith(COAP_DISCOVERY_PATH)) {
				this.resources.add(webLink);
			}
		}
	}
	
	public int getResourcesDiscovered() {
		return this.resources.size();
	}
	
	public void stopDiscoveryThread() {
		this.discoveryThread.stopDiscovery();
	}
	
	public String queryResource(String id, String measurement) {
		WebLink resource = new WebLink("/" + id + (measurement.equals(WeatherMeasurement.ALL) ? "" : "/" + measurement));
		String response = null;
		if (resources.contains(resource)) {
			coapClient.setURI(resource.getURI());
			CoapResponse coapResponse = coapClient.get();
			if (coapResponse != null) {
				if (coapResponse.getCode() == ResponseCode.CONTENT) {
					response = coapResponse.getResponseText();
				} else {
					response = coapResponse.getCode().toString();
				}
			}
		}
		return response;
	}
	
	public String queryAllResources(WeatherMeasurement measurement) {
		String response = null;
		if (resources.size() > 1) {
			response = "<measurements>";
			if (measurement == WeatherMeasurement.ALL) {
				for (WebLink resource : resources) {
					String resourceURI = resource.getURI();
					if (!resourceURI.endsWith("/.well-known/core")) {
						if (!resourceURI.endsWith(WeatherMeasurement.TEMPERATURE.getValue()) && !resourceURI.endsWith(WeatherMeasurement.HUMIDITY.getValue())) {
							coapClient.setURI(resourceURI);
							CoapResponse coapResponse = coapClient.get();
							if (coapResponse != null) {
								if (coapResponse.getCode() == ResponseCode.CONTENT) {
									response += coapResponse.getResponseText();
								} else {
									response += ""
											+ "<error>"
												+ "<uri>" + resourceURI + "</uri>"
												+ "<code>" + coapResponse.getCode() + "</code>"
											+ "</error>";
								}
							} else {
								response += ""
										+ "<error>"
											+ "<uri>" + resourceURI + "</uri>"
											+ "<message>" + "No response from resource" + "</message>"
										+ "</error>";
							}
						}
					}
				}
			} else {
				for (WebLink resource : resources) {
					String resourceURI = resource.getURI();
					if (!resourceURI.endsWith("/.well-known/core")) {
						if (resourceURI.endsWith(measurement.getValue())) {
							coapClient.setURI(resource.getURI());
							CoapResponse coapResponse = coapClient.get();
							if (coapResponse != null) {
								if (coapResponse.getCode() == ResponseCode.CONTENT) {
									response += coapResponse.getResponseText();
								} else {
									response += ""
											+ "<error>"
												+ "<uri>" + resourceURI + "</uri>"
												+ "<message>" + coapResponse.getCode() + "</message>"
											+ "</error>";
								}
							} else {
								response += ""
										+ "<error>"
											+ "<uri>" + resourceURI + "</uri>"
											+ "<message>" + "No response from resource" + "</message>"
										+ "</error>";
							}
						}
					}
				}
			}
			response += "</measurements>";
		}
		return response;
	}

}
