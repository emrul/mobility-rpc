/**
 * Copyright 2011 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.mobilityrpc.quickstart;

import com.googlecode.mobilityrpc.MobilityRPC;
import com.googlecode.mobilityrpc.controller.MobilityController;
import com.googlecode.mobilityrpc.network.ConnectionIdentifier;
import com.googlecode.mobilityrpc.quickstart.util.NetworkUtil;

/**
 * A quick way to programmatically start the mobility-rpc library to listen for incoming connections, using default
 * settings. The library will listen for incoming connections on port 5739, on all network interfaces detected on the
 * machine.
 * <p/>
 * <b>Usage</b><br/>
 * Usage of this class is fairly straightforward. The {@link #start()} and {@link #stop()} methods start the
 * library listening for incoming connections, and shut it down, respectively. Once the library has been started, the
 * {@link #getMobilityController()} method returns the {@link MobilityController} object, which is responsible for
 * managing all aspects of the library, and provides the main API of the library.
 * <p/>
 * Applications which start the library via this class should remember to call {@link #stop()} when the application is
 * shutting down (or being undeployed/redeployed), to have the library stop any threads it started and unbind from
 * network ports.
 * <p/>
 * This class is provided for convenience. Applications needing to override default settings can initialize the library
 * via the library's main API, through {@link com.googlecode.mobilityrpc.MobilityRPC}, which allows custom settings
 * to be specified.
 * <p/>
 * @author Niall Gallagher
 */
public class EmbeddedServer {
    // NOTE: This class has bad code smell - static state etc.
    // The purpose is just to provide the simplest API possible for "quickstart" purposes.

    private static final int DEFAULT_PORT = 5739;

    private static volatile MobilityController instance = null;

    /**
     * Starts the mobility-rpc library to listen for incoming connections on port 5739 on all network interfaces
     * detected on the local machine.
     * <p/>
     * Once this method returns, the library is initialised, and the {@link #getMobilityController()} method will
     * return the {@link MobilityController} object responsible for managing all aspects of the library, and providing
     * the main API of the library.
     * @throws IllegalStateException If the library is already started
     */
    public static synchronized void start() {
        MobilityController mobilityController = instance;
        if (mobilityController != null) {
            throw new IllegalStateException("EmbeddedServer is already running");
        }
        // Create a new MobilityController...
        mobilityController = MobilityRPC.newController();

        // Detect and bind to all network interfaces...
        for (String networkAddress : NetworkUtil.getAllNetworkInterfaceAddresses()) {
            mobilityController.getConnectionManager().bindConnectionListener(
                    new ConnectionIdentifier(networkAddress, DEFAULT_PORT));
        }
        // Done...
        instance = mobilityController;
    }

    /**
     * Shuts down the instance of the mobility-rpc library which was created via the {@link #start()} method. If the
     * library was never initialized via that method, this method does nothing.
     */
    public static synchronized void stop() {
        MobilityController mobilityController = instance;
        if (mobilityController != null) {
            mobilityController.destroy();
            instance = null;
        }
    }

    /**
     * @return the MobilityController object responsible for managing all aspects of the library instance started via
     * thev{@link #start()} method, and providing the main API of the library
     * @throws IllegalStateException If the library has not been started via the {@link #start()} method
     */
    public static MobilityController getMobilityController() {
        MobilityController mobilityController = instance;
        if (mobilityController == null) {
            throw new IllegalStateException("EmbeddedServer has not been started");
        }
        return mobilityController;
    }

}
