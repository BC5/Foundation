package uk.lsuth.mc.foundation;

import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;

public interface Module
{
    List<FoundationCommand> getCommands();

    List<Listener> getListeners();

    HashMap<String,Object> getTemplateData();
}