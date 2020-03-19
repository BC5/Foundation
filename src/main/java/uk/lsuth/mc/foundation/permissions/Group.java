package uk.lsuth.mc.foundation.permissions;

import java.util.ArrayList;

public class Group
{
    public Group parent;
    ArrayList<String> permissions;
    String groupName;

    String groupPrefix;
    String groupSuffix;
    String groupChatColour;


    public Group(String groupName)
    {
        this.groupName = groupName;
    }

    public Group(String groupName, Group parent)
    {
        this.groupName = groupName;
        this.parent = parent;
    }

    public String getGroupPrefix()
    {
        return groupPrefix;
    }

    public void setGroupPrefix(String groupPrefix)
    {
        this.groupPrefix = groupPrefix;
    }

    public String getGroupSuffix()
    {
        return groupSuffix;
    }

    public void setGroupSuffix(String groupSuffix)
    {
        this.groupSuffix = groupSuffix;
    }

    public String getGroupChatColour()
    {
        return groupChatColour;
    }

    public void setGroupChatColour(String groupChatColour)
    {
        this.groupChatColour = groupChatColour;
    }

    public Group getParent()
    {
        return parent;
    }

    public ArrayList<String> getPermissions()
    {
        return permissions;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
