package com.totallytot.ao

import net.java.ao.Entity
import net.java.ao.Preload

@Preload
interface MonitoredGroup extends Entity {

    void setMonitoredGroup(String key)

    String getMonitoredGroup()
}