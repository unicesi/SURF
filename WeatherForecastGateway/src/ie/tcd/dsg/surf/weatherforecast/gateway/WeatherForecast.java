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
 * CoAP client that queries CoAP resources providing temperature and humidity information from discovered MOTEs.
 * This class is a singleton.
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class WeatherForecast {

	/**
	 * Test URI to discover CoAP resources.
	 */
	private static final String MOTE_SERVER_URI = "coap://localhost:5683";
	/**
	 * Test CoAP server name.
	 */
	private static final String MOTE_SERVER_ADDRESS = "localhost";
	/**
	 * CoAP discovery path. It is used when filtering discovered CoAP resources. 
	 */
	private static final String COAP_DISCOVERY_PATH = "/.well-known/core";
	/**
	 * Weather resource path. It is used when filtering discovered CoAP resources.
	 */
	private static final String WEATHER_RESOURCE_PATH = "sht11";

	/**
	 * Singleton instance.
	 */
	private static WeatherForecast weatherForecast;
	
	/**
	 * Reference to the discovery thread instance.
	 */
	private DiscoveryThread discoveryThread;
	/**
	 * The CoAP resources discovered stored as (IP address, Set of CoAP resources) pairs.
	 */
	private Map<String, Set<WebLink>> resources;
	/**
	 * The discovered routes. A route is represented as an IP address.
	 */
	private Set<String> discoveredRoutes;

	/**
	 * Builds a new WeatherForecast instance. Access to the constructor is
	 * restricted to allow only one instance to exist.
	 */
	private WeatherForecast() {
		this.discoveredRoutes = new HashSet<>();
		this.resources = new HashMap<>();
	}
	
	/**
	 * Returns the WeatherForecast instance. If no instance exists, one is created.
	 * 
	 * @return The WeatherForecast instance.
	 */
	public static WeatherForecast getInstance() {
		if (weatherForecast == null) {
			weatherForecast = new WeatherForecast();
		}
		return weatherForecast;
	}

	/**
	 * Discovers all resources in the Test Server and returns the number of discovered resources.
	 * 
	 * @return The number of resources discovered in the Test Server.
	 */
	public int discoverAllResources() {
		// Build a CoAP client.
		CoapClient coapClient = new CoapClient();
		// Set the CoAP client's target URI to the Test Server URI.
		coapClient.setURI(MOTE_SERVER_URI);
		System.out.println("[INFO] CoAP client set to URI: " + coapClient.getURI());
		// Discover resources.
		Set<WebLink> discoveredResources = coapClient.discover();
		// Filter the discovered resources.
		if (discoveredResources != null) {
			Set<WebLink> filteredResources = new HashSet<>();
			for (WebLink webLink : discoveredResources) {
				if (!webLink.getURI().endsWith(COAP_DISCOVERY_PATH)) {
					filteredResources.add(webLink);
					System.out.println("[INFO] Resource discovered in path: " + webLink);
				}
			}
			this.resources.put(MOTE_SERVER_ADDRESS, filteredResources);
		}
		return this.resources.size();
	}
	
	/**
	 * Creates and starts a new discovery thread with the given discovery server URI.
	 * 
	 * @param discoveryServerURI The discovery server that will be used.
	 */
	public void discoverAllResources(String discoveryServerURI) {
		this.discoveryThread = new DiscoveryThread(this, discoveryServerURI);
		this.discoveryThread.start();
	}
	
	/**
	 * Retrieves discovered routes from the discovery thread and for each one discovers its resources.
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
	 * Discover all resources in the given route. A route is represented as an IP address.
	 * 
	 * @param route The IP address where the lookup will be directed to. It may be an IPv4 or 
	 * IPv6 address. In the case of an IPv6 address, it may contain square brackets ([ and ])
	 * but if it doesn't they are added automatically.
	 */
	private void discoverAllResourcesInRoute(String route) {
		// Build a CoAP client.
		CoapClient coapClient = new CoapClient();
		// If the given route is an IPv6 address, check if it contains square brackets.
		// If it doesn't then add them.
		if (route.contains(":")) {
			if (!route.startsWith("[")) {
				route = "[" + route;
			}
			if (!route.endsWith("]")) {
				route += "]";
			}
		}
		// Set the CoAP client's target URI with the given route.
		coapClient.setURI("coap://" + route + ":5683");
		System.out.println("[INFO] CoAP client set to URI: " + coapClient.getURI());
		// Discover resources.
		Set<WebLink> discoveredResources = coapClient.discover();
		// Filter the discovered resources.
		Set<WebLink> filteredResources = new HashSet<>();
		if (discoveredResources != null) {
			for (WebLink discoveredResource : discoveredResources) {
				if (discoveredResource != null) {
					if (!discoveredResource.getURI().endsWith(COAP_DISCOVERY_PATH)) {
						filteredResources.add(discoveredResource);
						System.out.println("[INFO] Resource discovered in path: " + discoveredResource);
					}
				} else {
					System.out.println("[WARNING] WebLink is null.");
				}
			}
		} else {
			System.out.println("[INFO] No resources in route: " + route);
		}
		this.resources.put(route, filteredResources);
	}
	
	/**
	 * Returns the total number of routes that have been discovered.
	 * 
	 * @return The total number of routes that have been discovered.
	 */
	public int getRoutesDiscovered() {
		return this.resources.size();
	}
	
	/**
	 * Stops the discovery thread.
	 */
	public void stopDiscoveryThread() {
		this.discoveryThread.stopDiscovery();
	}
	
	/**
	 * Queries the MOTE with the given ID for the given measurement.
	 * 
	 * @param id The ID of the MOTE.
	 * @param measurement The requested measurement.
	 * @return The response from the MOTE if it was found and it responded with the expected measurement.
	 * A CoAP response code otherwise.
	 */
	public String queryResource(String id, String measurement) {
		// Build a CoAP resource path with the given MOTE ID and measurement.
		WebLink resource = new WebLink("/" + id + (measurement.equals(WeatherMeasurement.ALL) ? "" : "/" + measurement));
		String response = null;
//		// Validate if the resource path exists in the collection of discovered CoAP resources.
//		if (resources.containsKey(resource)) {
//			// If the resource exists, get its host IP address.
//			String route = resources.get(resource);
//			// Build a CoAP client.
//			CoapClient coapClient = new CoapClient();
//			// Set the CoAP client's target URI with the hosts IP address and resource path.
//			coapClient.setURI("coap://" + route + ":5683" + resource.getURI());
//			// Perform a GET request.
//			CoapResponse coapResponse = coapClient.get();
//			if (coapResponse != null) {
//				// If the response has content, return it. Otherwise return the CoAP response code.
//				if (coapResponse.getCode() == ResponseCode.CONTENT) {
//					response = coapResponse.getResponseText();
//				} else {
//					response = coapResponse.getCode().toString();
//				}
//			}
//		}
//		return response;
		return "Sorry, this operation is no longer available.";
	}
	
	/**
	 * Queries all discovered MOTEs for the given measurement. The response is an XML with the results of the queries to the individual
	 * motes.
	 * 
	 * @param measurement The required measurement: temperature, humidity or all.
	 * @return An XML with the results of the queries. May include <code>error</code> tags if the resource failed to provide content.
	 * The schema of the XML is as follows:<br />
	 * <pre>
	 * {@code
	 * <measurements>
	 * 		<mote uri="CoAP URI to the mote">
	 * 			<temperature unit="The unit of measurement>Temperature reading value</temperature>
	 * 			<humidity unit="The unit of measurement">Humidity reading value</humidity>
	 * 			<value>Any other sensor value</value>
	 * 		</mote>
	 * 		<error>
	 * 	 		<uri>The URI to the resource</uri>
	 * 			<code>The CoAP response code returned</code>
	 * 		</error>
	 * </measurements>
	 * }
	 * </pre>
	 */
	public String queryAllResources(WeatherMeasurement measurement) {
		String response = null;
		if (this.resources.size() > 0) {
			response = "<resources>";
			for (String route : this.resources.keySet()) {
				Set<WebLink> resources = this.resources.get(route);
				for (WebLink resource : resources) {
					String resourceURI = resource.getURI();
//					if (!resourceURI.endsWith(COAP_DISCOVERY_PATH)) {
//						if (!resourceURI.endsWith(WeatherMeasurement.TEMPERATURE.getValue()) && !resourceURI.endsWith(WeatherMeasurement.HUMIDITY.getValue())) {
							// Build a CoAP resource URI with the resource's route and path.
							String coapURI = "coap://" + route + ":5683" + resource.getURI();
							// Build a CoAP client.
							CoapClient coapClient = new CoapClient();
							// Set the CoAP client's target URI with the CoAP resource URI.
							coapClient.setURI(coapURI);
							// Perform a GET request.
							CoapResponse coapResponse = coapClient.get();
							response += "<resource uri=\"" + coapURI + "\">";
							if (coapResponse != null) {
								// If the response has content, retrieve it, otherwise get the CoAP response code, and build an XML to return.
								if (coapResponse.getCode() == ResponseCode.CONTENT) {
									// If the response comes from the temperature and humidity resource split the retrieved values accordingly
									// and add them to the XML.
									// Otherwise use a generic tag to capture the response contents.
									if (resourceURI.endsWith(WEATHER_RESOURCE_PATH)) {
										String[] resourceValues = coapResponse.getResponseText().split(";");
										String temperature = "<temperature unit=\"ºC\">" + resourceValues[0] + "</temperature>";
										String humidity = "<humidity unit=\"%\">" + resourceValues[1] + "</humidity>";
										if (measurement == WeatherMeasurement.ALL) {
											response += temperature + humidity;
										} else if (measurement == WeatherMeasurement.TEMPERATURE) {
											response += temperature;
										} else if (measurement == WeatherMeasurement.HUMIDITY) {
											response += humidity;
										} else {
											response += ""
													+ "<error>"
														+ "<message>Measurement " + measurement + "is not valid. Try again.</message>"
													+ "</error>";
										}
									} else {
										response += "<value>" + coapResponse.getResponseText() + "</value>";
									}
									
								} else {
									response += ""
											+ "<error>"
												+ "<coapcode>" + coapResponse.getCode() + "</coapcode>"
												+ "<message>" + coapResponse.getCode().name() + "</message>"
											+ "</error>";
								}
							} else {
								response += ""
										+ "<error>"
											+ "<message>" + "No response from resource" + "</message>"
										+ "</error>";
							}
							response += "</resource>";
//						}
//					}
				}
			}
			response += "</resources>";
		}
		return response;
	}

}
