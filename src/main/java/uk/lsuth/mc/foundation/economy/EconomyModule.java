package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.language.LanguageManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EconomyModule implements Module, Economy
{
    private static final DecimalFormat dformat = new DecimalFormat("###,###.##");

    private final String currencyName;
    private final String currencyNamePlural;
    private final String insufficientFunds;
    private final String currencySymbol;

    private LanguageManager lmgr;
    private DataManager dmgr;

    private static double round(double v)
    {
        BigDecimal bd = new BigDecimal(Double.toString(v));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public EconomyModule(FoundationCore core)
    {
        Map<String,String> languageStrings = lmgr.getStrings("econ");
        this.lmgr = core.getLmgr();
        this.dmgr = core.getDmgr();

        currencyName = languageStrings.get("currencyName");
        currencyNamePlural = languageStrings.get("currencyNamePlural");
        currencySymbol = languageStrings.get("currencySymbol");
        insufficientFunds = languageStrings.get("insufficientFunds");
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getOfflinePlayer(String username)
    {
        return Bukkit.getOfflinePlayer(username);
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> cmds = new ArrayList<FoundationCommand>();

        cmds.add(new Mint(this,lmgr.getCommandStrings("mint")));
        cmds.add(new Balance(this,lmgr.getCommandStrings("bal")));
        cmds.add(new Invoice(this,lmgr.getCommandStrings("invoice")));
        cmds.add(new Transfer(this,lmgr.getCommandStrings("transfer"),dmgr));

        return cmds;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listenerList = new ArrayList<Listener>();
        listenerList.add(new EconomyListener(this,lmgr.getStrings("econ")));
        return listenerList;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        HashMap<String,Object> data = new HashMap<String, Object>();
        data.put("balance",0d);
        return data;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Foundation Economy";
    }

    @Override
    public boolean hasBankSupport()
    {
        return false;
    }

    @Override
    public int fractionalDigits()
    {
        return 2;
    }

    @Override
    public String format(double v)
    {
        return currencySymbol + dformat.format(v);
    }

    @Override
    public String currencyNamePlural()
    {
        return currencyNamePlural;
    }

    @Override
    public String currencyNameSingular()
    {
        return currencyName;
    }


    //Player accounts are created on login. Check if player has logged in.

    @Override @Deprecated
    public boolean hasAccount(String s)
    {
        return hasAccount(getOfflinePlayer(s));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer)
    {
        return dmgr.playerExists(offlinePlayer);
    }

    @Override @Deprecated
    public boolean hasAccount(String s, String s1)
    {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s)
    {
        return hasAccount(offlinePlayer);
    }

    @Override @Deprecated
    public double getBalance(String s)
    {
        return getBalance(getOfflinePlayer(s));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer)
    {
        Document pdoc = dmgr.fetchData(offlinePlayer).getPlayerDocument();
        return (double) pdoc.get("balance");
    }

    @Override @Deprecated
    public double getBalance(String s, String s1)
    {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s)
    {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v)
    {
        return has(getOfflinePlayer(s),v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v)
    {
        double bal = getBalance(offlinePlayer);
        return !(v > bal);
    }

    @Override
    public boolean has(String s, String s1, double v)
    {
        return has(s,v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v)
    {
        return has(offlinePlayer,v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v)
    {
        return withdrawPlayer(getOfflinePlayer(s),v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v)
    {
        Document pdoc = dmgr.fetchData(offlinePlayer).getPlayerDocument();
        double bal = (double) pdoc.get("balance");

        if(bal >= v)
        {
            pdoc.replace("balance",round(bal-v));
            return new EconomyResponse(v,bal-v, EconomyResponse.ResponseType.SUCCESS,null);
        }
        else
        {
            return new EconomyResponse(0,bal, EconomyResponse.ResponseType.FAILURE, insufficientFunds);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v)
    {
        return withdrawPlayer(s,v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v)
    {
        return withdrawPlayer(offlinePlayer,v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v)
    {
        return depositPlayer(getOfflinePlayer(s),v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v)
    {
        Document pdoc = dmgr.fetchData(offlinePlayer).getPlayerDocument();
        double bal = (double) pdoc.get("balance");

        pdoc.replace("balance",round(bal+v));
        return new EconomyResponse(v,bal+v, EconomyResponse.ResponseType.SUCCESS,null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v)
    {
        return depositPlayer(s,v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v)
    {
        return depositPlayer(offlinePlayer,v);
    }

    //PAST THIS SECTION IS FOR BANKS. THIS PLUGIN DOES NOT SUPPORT BANKS.

    @Override
    public EconomyResponse createBank(String s, String s1)
    {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer)
    {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s)
    {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s)
    {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v)
    {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v)
    {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v)
    {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1)
    {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer)
    {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1)
    {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer)
    {
        return null;
    }

    @Override
    public List<String> getBanks()
    {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s)
    {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer)
    {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1)
    {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s)
    {
        return false;
    }

    public static double moneyParse(String string) throws NumberFormatException
    {
        int l = string.length();

        if(l-1 < 0)
        {
            return Double.parseDouble(string);
        }

        if(string.charAt(l-1) == 'k')
        {
            double amnt = Double.parseDouble(string.substring(0,l-1));
            return amnt * 1000;
        }
        else if(string.charAt(l-1) == 'M')
        {
            double amnt = Double.parseDouble(string.substring(0,l-1));
            return amnt * 1000000;
        }
        else
        {
            return Double.parseDouble(string);
        }
    }
}
