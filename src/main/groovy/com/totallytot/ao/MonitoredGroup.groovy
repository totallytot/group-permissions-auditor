package com.totallytot.ao

import net.java.ao.Entity

interface MonitoredGroup extends Entity {

    void setMonitoredGroup(String key)

    String getMonitoredGroup()
}