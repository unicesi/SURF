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
package ie.tcd.dsg.surf.weatherforecast.motemock.server;

import java.net.SocketException;

import org.eclipse.californium.core.CoapServer;
import ie.tcd.dsg.surf.weatherforecast.motemock.resources.MoteMock;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class MoteServer extends CoapServer {

	/**
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        try {
            MoteServer server = new MoteServer();
            server.start();
        } catch (SocketException e) {
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }
    
    /**
     * 
     * @throws SocketException
     */
    public MoteServer() throws SocketException {
        add(new MoteMock());
    }
    
}
