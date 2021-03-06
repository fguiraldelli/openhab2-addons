/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.somfytahoma.internal.handler;

import static org.openhab.binding.somfytahoma.internal.SomfyTahomaBindingConstants.*;

import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SomfyTahomaAwningHandler} is responsible for handling commands,
 * which are sent to one of the channels of the awning thing.
 *
 * @author Ondrej Pecta - Initial contribution
 */
@NonNullByDefault
public class SomfyTahomaAwningHandler extends SomfyTahomaBaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SomfyTahomaAwningHandler.class);

    public SomfyTahomaAwningHandler(Thing thing) {
        super(thing);
        stateNames.put(CONTROL, "core:DeploymentState");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received command {} for channel {}", command, channelUID);
        if (!CONTROL.equals(channelUID.getId())) {
            return;
        }

        if (RefreshType.REFRESH.equals(command)) {
            updateChannelState(channelUID);
        } else {
            String cmd = getTahomaCommand(command.toString());
            if (COMMAND_MY.equals(cmd)) {
                String executionId = getCurrentExecutions();
                if (executionId != null) {
                    //Check if the awning is moving and MY is sent => STOP it
                    cancelExecution(executionId);
                } else {
                    sendCommand(COMMAND_MY, "[]");
                }
            } else {
                String param = COMMAND_SET_DEPLOYMENT.equals(cmd) ? "[" + command.toString() + "]" : "[]";
                sendCommand(cmd, param);
            }
        }
    }

    private String getTahomaCommand(String command) {
        switch (command) {
            case "OFF":
            case "DOWN":
                return COMMAND_DOWN;
            case "ON":
            case "UP":
                return COMMAND_UP;
            case "STOP":
                return COMMAND_MY;
            default:
                return COMMAND_SET_DEPLOYMENT;
        }
    }
}
