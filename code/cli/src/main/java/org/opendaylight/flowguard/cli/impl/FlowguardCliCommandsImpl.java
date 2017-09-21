/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.flowguard.cli.api.FlowguardCliCommands;

public class FlowguardCliCommandsImpl implements FlowguardCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(FlowguardCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public FlowguardCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("FlowguardCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}