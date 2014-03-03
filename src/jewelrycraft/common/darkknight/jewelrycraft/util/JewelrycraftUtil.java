package common.darkknight.jewelrycraft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import common.darkknight.jewelrycraft.item.ItemList;

public class JewelrycraftUtil
{
    public static ArrayList<ItemStack> modifiers = new ArrayList<ItemStack>();
    public static ArrayList<ItemStack> jewel = new ArrayList<ItemStack>();
    public static ArrayList<ItemStack> jewelry = new ArrayList<ItemStack>();
    public static ArrayList<ItemStack> metal = new ArrayList<ItemStack>();
    public static ArrayList<String> jamcraftPlayers = new ArrayList<String>();
    public static HashMap<ItemStack, ItemStack> combinations = new HashMap<ItemStack, ItemStack>();

    public static void addStuff()
    {
        //Modifiers
        modifiers.add(new ItemStack(Block.chest));
        modifiers.add(new ItemStack(Block.torchWood));
        modifiers.add(new ItemStack(Item.book));
        modifiers.add(new ItemStack(Item.sugar));
        modifiers.add(new ItemStack(Item.feather));
        modifiers.add(new ItemStack(Item.bed));
        modifiers.add(new ItemStack(Item.pickaxeIron));
        modifiers.add(new ItemStack(Item.blazePowder));
        modifiers.add(new ItemStack(Item.eyeOfEnder));
        modifiers.add(new ItemStack(Item.potion, 1, 8270));

        //Jewels
        jewel.add(new ItemStack(Block.obsidian));
        jewel.add(new ItemStack(Item.diamond));
        jewel.add(new ItemStack(Item.emerald));
        jewel.add(new ItemStack(Item.enderPearl));
        jewel.add(new ItemStack(Item.netherStar));

        //Jewelry
        jewelry.add(new ItemStack(ItemList.ring));
    }
    
    public static void addSpecialCombinations()
    {
        combinations.put(new ItemStack(Item.enderPearl), new ItemStack(Block.chest));
        combinations.put(new ItemStack(Item.enderPearl), new ItemStack(Item.bed));
        combinations.put(new ItemStack(Block.obsidian), new ItemStack(Item.eyeOfEnder));
        combinations.put(new ItemStack(Item.netherStar), new ItemStack(Block.chest));
        //An ender pearl with any modifier that is not a chest or bed
        combinations.put(new ItemStack(Item.enderPearl), new ItemStack(Item.itemsList.length, 0, 0));
    }
    
    public static void jamcrafters()
    {
        jamcraftPlayers.add("allout58");
        jamcraftPlayers.add("ChewBaker");
        jamcraftPlayers.add("domi1819");
        jamcraftPlayers.add("founderio");
        jamcraftPlayers.add("Ironhammer354");
        jamcraftPlayers.add("isomgirls6");
        jamcraftPlayers.add("jmjmjm439");
        jamcraftPlayers.add("Joban");
        jamcraftPlayers.add("KJ4IPS");
        jamcraftPlayers.add("Mitchellbrine");
        jamcraftPlayers.add("MrComputerGhost");
        jamcraftPlayers.add("MrKol999");
        jamcraftPlayers.add("Resinresin");
        jamcraftPlayers.add("sci4me");
        jamcraftPlayers.add("sor1n");
        jamcraftPlayers.add("theminecoder");
        jamcraftPlayers.add("YSPilot");
        jamcraftPlayers.add("direwolf20");
    }
    
    public static void addMetals()
    {
        int index = 0, index2 = 0;
        while(index < OreDictionary.getOreNames().length)
        {
            while(index2 < OreDictionary.getOres(OreDictionary.getOreNames()[index]).size())
            {
                if(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2).getUnlocalizedName().toLowerCase().contains("ingot") && !JewelrycraftUtil.metal.contains(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2)))
                    metal.add(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2));
                index2++;
            }
            index2 = 0;
            index++;
        }   
        metal.add(new ItemStack(Item.ingotGold));
        metal.add(new ItemStack(Item.ingotIron));
    }

    public static boolean isModifier(ItemStack item)
    {
        Iterator<ItemStack> i = modifiers.iterator();

        while (i.hasNext())
        {
            ItemStack temp = i.next();
            if (temp.itemID == item.itemID && temp.getItemDamage() == item.getItemDamage())
                return true;
        }
        return false;
    }

    public static boolean isJewel(ItemStack item)
    {
        Iterator<ItemStack> i = jewel.iterator();

        while (i.hasNext())
        {
            ItemStack temp = i.next();
            if (temp.itemID == item.itemID && temp.getItemDamage() == item.getItemDamage())
                return true;
        }
        return false;
    }

    public static boolean isJewelry(ItemStack item)
    {
        Iterator<ItemStack> i = jewelry.iterator();

        while (i.hasNext())
        {
            ItemStack temp = i.next();
            if (temp.itemID == item.itemID && temp.getItemDamage() == item.getItemDamage())
                return true;
        }
        return false;
    }
}
