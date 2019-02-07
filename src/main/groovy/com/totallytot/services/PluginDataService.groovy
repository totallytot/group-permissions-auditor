package com.totallytot.services

interface PluginDataService {

    void setIgnoredSpace(String spaceKey)

    Set<String> getIgnoredSpaces()

    void removeIgnoredSpace(String spaceKey)

    void setMonitoredGroup(String group)

    Set<String> getMonitoredGroups()

    void removeMonitoredGroup(String group)

    Boolean isEmailActive()

    void activateEmail(Boolean active)

    Boolean isPermissionRemovalActive()

    void activatePermissionRemoval(Boolean active)

    void setNotificationReceiver(String userName)

    Set<String> getNotificationReceivers()

    void removeNotificationReceiver(String userName)

}