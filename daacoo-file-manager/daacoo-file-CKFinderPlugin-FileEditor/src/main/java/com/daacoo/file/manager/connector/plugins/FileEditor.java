/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.daacoo.file.manager.connector.plugins;

import com.daacoo.file.manager.connector.configuration.Events;
import com.daacoo.file.manager.connector.configuration.Plugin;

public class FileEditor extends Plugin {

    @Override
    public void registerEventHandlers(Events events) {
        events.addEventHandler(Events.EventTypes.BeforeExecuteCommand, SaveFileCommand.class);

    }
}
