package com.totallytot.ao

import net.java.ao.Entity
import net.java.ao.Preload

@Preload
interface IgnoredSpace extends Entity {

    void setIgnoredSpaceKey(String key)

    String getIgnoredSpaceKey()
}