/*
 * This file is part of ComposedRoutingClient.
 * 
 * ComposedRoutingClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ComposedRoutingClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ComposedRoutingClient. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.composedrouting.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import eu.linksmart.composedrouting.service.ComposedRoutingServiceLocator;
import eu.linksmart.composedrouting.service.ComposedRoutingServicePortType;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class ComposedRoutingClient {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ComposedRoutingServiceLocator locator = new ComposedRoutingServiceLocator();
		try {
			ComposedRoutingServicePortType composedRoutingService = locator.getComposedRoutingServicePort();
			String result = composedRoutingService.getRoute();
			System.out.println(result);
		} catch(RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
