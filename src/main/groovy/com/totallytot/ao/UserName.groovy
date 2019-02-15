package com.totallytot.ao

import net.java.ao.Entity
import net.java.ao.Preload

@Preload
interface UserName extends Entity{

    void setUserName(String userName)

    String getUserName()

}