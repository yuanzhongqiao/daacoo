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
package com.daacoo.file.manager.connector.data;

/**
 *
 */
public class EventCommandData {

    private Class<? extends IEventHandler> eventListener;
    private PluginInfo pluginInfo;

    /**
     * constructor with class parameter.
     *
     * @param eventListener name of plugin command class
     */
    public EventCommandData(final Class<? extends IEventHandler> eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * @return the eventListener
     */
    public final Class<? extends IEventHandler> getEventListener() {
        return eventListener;
    }

    /**
     * @param eventListener the eventListener to set
     */
    public final void setEventListener(final Class<? extends IEventHandler> eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * @return the pluginInfo
     */
    public final PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    /**
     * @param pluginInfo the pluginInfo to set
     */
    public final void setPluginInfo(final PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
    }
}
