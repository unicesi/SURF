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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Thread that polls the routes discovery server for the discovered routes, determines if new routes
 * are available and notifies the WeatherForecast (see ie.tcd.dsg.surf.weatherforecast.gateway.WeatherForecast).
 * If no new routes were found, it retries again in 10 seconds.
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class DiscoveryThread extends Thread {

	/**
	 * Observer that will be notified if new routes are available.
	 * @see ie.tcd.dsg.surf.weatherforecast.gateway.WeatherForecast
	 */
	private WeatherForecast weatherForecast;
	/**
	 * The routes discovery server's URI.
	 */
	private String discoveryServerURI;
	/**
	 * Flag to stop the thread.
	 */
	private boolean stopRequested;
	/**
	 * Set of discovered routes. It is continually updated while the thread runs. 
	 * A route is represented as an IP address.
	 */
	private Set<String> discoveredRoutes;
	
	/**
	 * Builds a new discovery thread instance.
	 * 
	 * @param weatherForecast Observer that will be notified if new routes are available.
	 * @param discoveryServerURI The routes discovery server's URI.
	 */
	public DiscoveryThread(WeatherForecast weatherForecast, String discoveryServerURI) {
		super();
		this.weatherForecast = weatherForecast;
		this.discoveryServerURI = discoveryServerURI;
		this.stopRequested = false;
		this.discoveredRoutes = new HashSet<>();
	}
	
	/**
	 * Stops the current discovery thread.
	 */
	public void stopDiscovery() {
		this.stopRequested = true;
	}
	
	/**
	 * Returns the set of discovered routes.
	 * 
	 * @return A set of discovered routes represented as strings.
	 */
	public Set<String> getDiscoveredRoutes() {
		return this.discoveredRoutes;
	}
	
	/**
	 * Polls the routes discovery server for the discovered routes, determines if new routes
	 * are available and notifies the WeatherForecast (see ie.tcd.dsg.surf.weatherforecast.gateway.WeatherForecast).
	 * If no new routes were found, it retries again in 10 seconds.
	 * The resource provided by the discovery server is expected to be of type <code>application/xml</code>.
	 */
	@Override
	public void run() {
		super.run();
		// Build a REST client.
		Client client = ClientBuilder.newClient();
		// Set the client's target URI to the discovery server.
		WebTarget serviceWebTarget = client.target(this.discoveryServerURI);
		// Set the expected response type to application/xml
		Invocation.Builder invocationBuilder = serviceWebTarget.request(MediaType.APPLICATION_XML_TYPE);
		// Poll the discovery server until the discovery thread is requested to stop.
		while (!stopRequested) {
			System.out.println("[INFO{dt}] Requesting XML from " + this.discoveryServerURI);
			// Send a GET request to the discovery server.
			Response response = invocationBuilder.get();
			// Validate the response has content.
			if (response.getStatus() == 200) {
				System.out.println("[INFO{dt}] Response received. Processing XML...");
				// If the response has content, get the content from the response as a string.
				String xmlResponse = response.readEntity(String.class);
				// Process the XML retrieved.
				boolean newRoutesFound = processResponse(xmlResponse);
				// Validate new routes were found.
				if (newRoutesFound) {
					System.out.println("[INFO{dt}] New routes found. Notifying...");
					// If new routes were found, notify the observer.
					this.weatherForecast.notifyNewRoutesFound();
				} else {
					// If no new routes were found, do nothing.
					System.out.println("[INFO{dt}] No new routes were found. Trying again in 10 seconds.");
				}
				printDiscoveredRoutes();
			} else {
				// If the response has no content, display the HTTP error code.
				System.out.println("[ERROR{dt}] Failed with HTTP error code: " + response.getStatus());
			}
			// Wait 10 seconds to poll the discovery server again.
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * Parses the given XML in search for <code>route</code> tags and adds the new routes found to the
	 * set of discovered routes.
	 * 
	 * @param xml The XML to be processed.
	 * @return If one or more new routes were found.
	 */
	private boolean processResponse(String xml) {
		boolean newRoutesFound = false;
		// Build an XML document parser with the given XML as input source and parse it.
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
		    documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    InputSource inputSource = new InputSource();
		    inputSource.setCharacterStream(new StringReader(xml));
		    try {
		        Document document = documentBuilder.parse(inputSource);
		        document.getDocumentElement().normalize();
		        
		        // Retrieve all elements with the tag <route></route>.
		        NodeList routes = document.getElementsByTagName("route");
		        
		        // For each element retrieve its text content, which represents a route and 
		        // attempt adding it to the set discovered routes. If the route was added
		        // a new route has been found.
		        for (int i = 0; i < routes.getLength(); i++) {
		        	Node nNode = routes.item(i);
		        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    			Element route = (Element) nNode;
		    			boolean added = this.discoveredRoutes.add(route.getTextContent());
		    			if (added && !newRoutesFound) {
		    				System.out.println("[INFO{dt}] New discovered route: " + route.getTextContent());
		    				newRoutesFound = true;
		    			}
		        	}
		        }
		    } catch (SAXException e) {
		    	e.printStackTrace();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();
		}
		return newRoutesFound;
	}
	
	private void printDiscoveredRoutes() {
		System.out.println("[INFO] Discovered routes:");
		for (String discoveredRoute : discoveredRoutes) {
			System.out.println(discoveredRoute);
		}
	}
	
}
