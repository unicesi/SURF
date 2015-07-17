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
package ie.tcd.dsg.surf.weatherforecast.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import ie.tcd.dsg.surf.weatherforecast.utils.WeatherMeasurement;

/**
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class WeatherForecast {

	/**
	 * 
	 */
	private static final String MOTE_SERVER_URI = "coap://localhost:5683";
	/**
	 * 
	 */
	private static final String MOTE_SERVER_ADDRESS = "localhost";
	/**
	 * 
	 */
	private static final String COAP_DISCOVERY_PATH = "/.well-known/core";

	/**
	 * 
	 */
	private static WeatherForecast weatherForecast;
	
	/**
	 * 
	 */
	private DiscoveryThread discoveryThread;
	/**
	 * 
	 */
	private CoapClient coapClient;
	/**
	 * 
	 */
	private Map<WebLink, String> resources;
	/**
	 * 
	 */
	private Set<String> discoveredRoutes;

	/**
	 * 
	 */
	private WeatherForecast() {
		this.coapClient = new CoapClient();
		this.discoveredRoutes = new HashSet<>();
		this.resources = new HashMap<>();
	}
	
	/**
	 * 
	 * @return
	 */
	public static WeatherForecast getInstance() {
		if (weatherForecast == null) {
			weatherForecast = new WeatherForecast();
		}
		return weatherForecast;
	}

	/**
	 * 
	 * @return
	 */
	public int discoverAllResources() {
		this.coapClient.setURI(MOTE_SERVER_URI);
		System.out.println("[INFO] CoAP client set to URI: " + coapClient.getURI());
		Set<WebLink> discoveredResources = this.coapClient.discover();
		for (WebLink webLink : discoveredResources) {
			if (!webLink.getURI().endsWith(COAP_DISCOVERY_PATH)) {
				this.resources.put(webLink, MOTE_SERVER_ADDRESS);
				System.out.println("[INFO] Resource discovered in path: " + webLink);
			}
		}
		return this.resources.size();
	}
	
	/**
	 * 
	 * @param discoveryServerURI
	 */
	public void discoverAllResources(String discoveryServerURI) {
		this.discoveryThread = new DiscoveryThread(this, discoveryServerURI);
		this.discoveryThread.start();
	}
	
	/**
	 * 
	 */
	public void notifyNewRoutesFound() {
		Set<String> discoveredRoutes = this.discoveryThread.getDiscoveredRoutes();
		for (String route : discoveredRoutes) {
			if (this.discoveredRoutes.add(route)) {
				System.out.println("[INFO] Discovering resources in route: " + route + " ...");
				discoverAllResourcesInRoute(route);
			}
		}
	}
	
	/**
	 * 
	 * @param route
	 */
	private void discoverAllResourcesInRoute(String route) {
		this.coapClient.setURI("coap://[" + route + "]:5683");
		System.out.println("[INFO] CoAP client set to URI: " + coapClient.getURI());
		Set<WebLink> discoveredResources = this.coapClient.discover();
		if (discoveredResources != null) {
			for (WebLink webLink : discoveredResources) {
				if (webLink != null) {
					if (!webLink.getURI().endsWith(COAP_DISCOVERY_PATH)) {
						if (webLink.getURI().endsWith("sht11")) {
							this.resources.put(webLink, route);
							System.out.println("[INFO] Resource discovered in path: " + webLink);
						}
					}
				} else {
					System.out.println("[WARNING] WebLink is null.");
				}
			}
		} else {
			System.out.println("[INFO] No resources in route: " + route);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResourcesDiscovered() {
		return this.resources.size();
	}
	
	/**
	 * 
	 */
	public void stopDiscoveryThread() {
		this.discoveryThread.stopDiscovery();
	}
	
	/**
	 * 
	 * @param id
	 * @param measurement
	 * @return
	 */
	public String queryResource(String id, String measurement) {
		WebLink resource = new WebLink("/" + id + (measurement.equals(WeatherMeasurement.ALL) ? "" : "/" + measurement));
		String response = null;
		if (resources.containsKey(resource)) {
			String route = resources.get(resource);
			coapClient.setURI("coap://[" + route + "]:5683" + resource.getURI());
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
	
	/**
	 * 
	 * @param measurement
	 * @return
	 */
	public String queryAllResources(WeatherMeasurement measurement) {
		String response = null;
		if (this.resources.size() > 1) {
			response = "<measurements>";
			if (measurement == WeatherMeasurement.ALL) {
				for (WebLink resource : resources.keySet()) {
					String resourceURI = resource.getURI();
					String route = resources.get(resource);
					if (!resourceURI.endsWith("/.well-known/core")) {
						if (!resourceURI.endsWith(WeatherMeasurement.TEMPERATURE.getValue()) && !resourceURI.endsWith(WeatherMeasurement.HUMIDITY.getValue())) {
							if (resourceURI.endsWith("sht11")) {
								String coapURI = "coap://[" + route + "]:5683" + resource.getURI();
								coapClient.setURI(coapURI);
								CoapResponse coapResponse = coapClient.get();
								if (coapResponse != null) {
									if (coapResponse.getCode() == ResponseCode.CONTENT) {
										response += "<mote uri=\"" + coapURI + "\">";
										String[] resourceValues = coapResponse.getResponseText().split(";");
										response += "<temperature unit=\"ºC\">" + resourceValues[0] + "</temperature>"
												+ "<humidity unit=\"%\">" + resourceValues[1] + "</humidity>";
										response += "</mote>";
//										response += coapResponse.getResponseText() + "\n";
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
				}
			} else {
				for (WebLink resource : resources.keySet()) {
					String resourceURI = resource.getURI();
					String route = resources.get(resource);
					if (!resourceURI.endsWith("/.well-known/core")) {
//						if (resourceURI.endsWith(measurement.getValue())) {
							String coapURI = "coap://[" + route + "]:5683" + resource.getURI();
							coapClient.setURI(coapURI);
							CoapResponse coapResponse = coapClient.get();
							if (coapResponse != null) {
								if (coapResponse.getCode() == ResponseCode.CONTENT) {
									response += "<mote uri=\"" + coapURI + "\">";
									String[] resourceValues = coapResponse.getResponseText().split(";");
									if (measurement == WeatherMeasurement.TEMPERATURE) {
										response += "<temperature>" + resourceValues[0] + "</temperature>";
									} else {
										response += "<humidity>" + resourceValues[1] + "</humidity>";
									}
									response += "</mote>";
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
//						}
					}
				}
			}
			response += "</measurements>";
		}
		return response;
	}

}
