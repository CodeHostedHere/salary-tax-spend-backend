package fyi.incomeoutcome.salarytaxspend.util;

import fyi.incomeoutcome.salarytaxspend.data.Role;

public class RoleUtil {

    public static String getFullRoleTitle(Role role){
        if (role.getSeniority().equals("")){
            return String.format("%s %s", role.getSeniority(), role.getRoleName());
        } else{
            return role.getRoleName();
        }
    }

}
