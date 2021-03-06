/*
 * This file is part of MoteMock.
 * 
 * MoteMock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MoteMock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MoteMock. If not, see <http://www.gnu.org/licenses/>.
 */
package ie.tcd.dsg.surf.weatherforecast.motemock.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class MoteMock extends CoapResource {
	
	/**
	 * 
	 */
	private TemperatureResource temperatureResource;
	/**
	 * 
	 */
	private HumidityResource humidityResource;

	/**
	 * 
	 */
	public MoteMock() {
		super("m1");
		this.getAttributes().setTitle("MOTE M1 Mock");
		this.registerWeatherResources();
	}
	
	/**
	 * 
	 */
	private void registerWeatherResources() {
		this.temperatureResource = new TemperatureResource(this);
		this.humidityResource = new HumidityResource(this);
		this.add(this.temperatureResource, this.humidityResource);
	}
	
	/**
	 * 
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		String payload = ""
    			+ "<mote id=\"m1\">"
    				+ "<temperature path=\"" + this.temperatureResource.getURI() + "\" unit=\"ºC\">22</temperature>"
    				+ "<humidity path=\"" + this.humidityResource.getURI() + "\" unit=\"%\">83</humidity>"
    			+ "</mote>";
        exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_XML);
	}
}
