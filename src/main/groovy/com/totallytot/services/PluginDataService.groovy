package com.totallytot.services

interface PluginDataService {

    void setIgnoredSpace(String spaceKey)

    Set<String> getIgnoredSpaces()

    void removeIgnoredSpace(String spaceKey)

    void setMonitoredGroup(String group)

    Set<String> getMonitoredGroups()

    void removeMonitoredGroup(String group)

    boolean isEmailActive()

    void activateEmail(boolean active)
}