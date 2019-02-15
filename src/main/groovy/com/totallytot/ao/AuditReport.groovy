package com.totallytot.ao

import net.java.ao.Entity
import net.java.ao.Preload

@Preload
interface AuditReport extends Entity{

    void setSpaceKey(String spaceKey)

    String getSpaceKey()

    void setGroup(String group)

    String getGroup()

    void setPermission(String permission)

    String getPermission()

    void setViolator(String userName)

    String getViolator()

    void setDate(String date)

    String getDate()
}