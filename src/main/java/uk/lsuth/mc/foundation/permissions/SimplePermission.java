package uk.lsuth.mc.foundation.permissions;

import org.bukkit.OfflinePlayer;

public interface SimplePermission
{
    boolean permitted(OfflinePlayer player, String permission);

    void permit(OfflinePlayer player, String permission);

    void deny(OfflinePlayer player, String permission);

    void groupPermit(Group group, String permission);

    void groupDeny(Group group, String permission);

    String fetchPrefix();
    String fetchSuffix();
    String fetchChatColour();

    void joinGroup(Group group, OfflinePlayer player);
    void leaveGroup(Group group, OfflinePlayer player);

    void createGroup(Group group);
    void disbandGroup(Group group);

    Group[] getGroups();
}
