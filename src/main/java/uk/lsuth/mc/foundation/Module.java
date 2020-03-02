package uk.lsuth.mc.foundation;

import java.util.HashMap;
import java.util.List;

public interface Module
{
    List<FoundationCommand> getCommands();

    HashMap<String,Object> getTemplateData();
}
