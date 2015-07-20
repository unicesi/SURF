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
/**
 * ComposedRoutingService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package eu.linksmart.composedrouting.service;

public interface ComposedRoutingService extends javax.xml.rpc.Service {
    public java.lang.String getComposedRoutingServicePortAddress();

    public eu.linksmart.composedrouting.service.ComposedRoutingServicePortType getComposedRoutingServicePort() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.composedrouting.service.ComposedRoutingServicePortType getComposedRoutingServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
