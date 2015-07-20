/*
 * This file is part of ComposedRoutingService.
 * 
 * ComposedRoutingService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ComposedRoutingService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ComposedRoutingService. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.composedrouting.service;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public interface  ComposedRoutingService {

	public static final String SID = "ComposedRoutingService";
	public static final String DESCRIPTION = "Composed Routing Service";
	
	public String getRoute();
	
}