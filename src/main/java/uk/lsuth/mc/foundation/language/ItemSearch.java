package uk.lsuth.mc.foundation.language;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;

public class ItemSearch
{
    ArrayList<String> itemids;
    final boolean lowCPU;
    int[] index;

    public ItemSearch(boolean lowCPU)
    {
        this.lowCPU = lowCPU;
        index = new int[26];
        itemids = new ArrayList<>();

        //Populate ArrayList
        for(Material m :Material.values())
        {
            String materialString = m.toString().toLowerCase();
            if(!materialString.contains("legacy"))
            {
                itemids.add(materialString);
            }
        }

        //Sort alphabetically
        itemids.sort(Comparator.naturalOrder());

        //Build lowcpu index
        int i = 0;
        for(String str: itemids)
        {
            int l = str.charAt(0) - 'a';
            if(index[l] == 0 && l!=0) index[l] = i;
            i++;
        }

    }

    public ArrayList<String> search(String in)
    {
        if(lowCPU) return alphabeticalSearch(in);
        else return deepSearch(in);
    }

    /**
     * Does not search within item IDs. Uses prebuilt index to start search at the first letter of input
     * @param in
     * @return
     */
    public ArrayList<String> alphabeticalSearch(String in)
    {
        if(in == null) return null;
        if(in.length() == 0) return null;

        in = in.toLowerCase();
        int l = in.charAt(0) - 'a';

        ArrayList<String> results = new ArrayList<>();
        int currentsim = 0;

        for(int i = index[l]; i < index[l+1]; i++)
        {
            String str = itemids.get(i);
            int sim = similarity(in,str);
            if(currentsim < sim)
            {
                results.clear();
                results.add(str);
            }
            else if(currentsim == sim)
            {
                results.add(str);
            }
        }
        return results;
    }

    /**
     * Iterates through the entire array. Searches within strings using .contains() and appends additional results to lowcpu.
     * @param in
     * @return
     */
    public ArrayList<String> deepSearch(String in)
    {
        if(in == null) return null;
        if(in.length() == 0) return null;

        ArrayList<String> simpleResults = alphabeticalSearch(in);
        ArrayList<String> results = (ArrayList<String>) itemids.clone();

        for(int i = 0; i < results.size(); i++)
        {
            if(!results.get(i).contains(in))
            {
                results.remove(i);
                i--;
            }
        }

        if(simpleResults.size() != results.size())
        {
            for(String i : results)
            {
                if(!simpleResults.contains(i))
                {
                    simpleResults.add(i);
                }
            }
        }

        return simpleResults;
    }

    public static int similarity(String subject, String comparator)
    {
        char[] s1c = subject.toCharArray();
        char[] s2c = comparator.toCharArray();

        int similarity = 0;

        for(int i = 0; i < s1c.length; i++)
        {
            if(i > s2c.length-1) break;
            if(s1c[i] == s2c[i]) similarity++;
            else break;
        }

        return similarity;
    }
}
