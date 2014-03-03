package common.darkknight.jewelrycraft.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import common.darkknight.jewelrycraft.block.BlockList;
import common.darkknight.jewelrycraft.item.ItemList;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingRecipes
{
    private static boolean isInitialized = false;
    
    public static void preInit(FMLPreInitializationEvent e)
    {
        if (!isInitialized)
        {
            GameRegistry.addRecipe(new ItemStack(ItemList.thiefGloves), "x x", "yxy", "yxy", 'x', ItemList.shadowIngot, 'y', new ItemStack(Block.cloth, 1, 15));
            GameRegistry.addRecipe(new ItemStack(ItemList.clayMolds, 1, 0), "xx", 'x', Item.clay);
            GameRegistry.addRecipe(new ItemStack(ItemList.clayMolds, 1, 1), " x ", "x x", " x ", 'x', Item.clay);
            GameRegistry.addRecipe(new ItemStack(BlockList.molder), "x x", "xxx", 'x', Block.cobblestone);
            GameRegistry.addRecipe(new ItemStack(BlockList.smelter), "xyx", "x x", "xzx", 'x', Block.cobblestone, 'y', Item.bucketEmpty, 'z', Item.bucketLava);
            GameRegistry.addRecipe(new ItemStack(BlockList.jewelCraftingTable), "xxx", "y y", "y y", 'x', Block.planks, 'y', Block.cobblestone);
            GameRegistry.addRecipe(new ItemStack(BlockList.displayer, 2), " x ", "xxx", "yyy", 'x', Item.ingotIron, 'y', Block.blockEmerald);
            GameRegistry.addRecipe(new ItemStack(BlockList.shadowBlock, 1), "xxx", "xxx", "xxx", 'x', ItemList.shadowIngot);
            GameRegistry.addSmelting(BlockList.shadowOre.blockID, new ItemStack(ItemList.shadowIngot), 1.5f);
            FurnaceRecipes.smelting().addSmelting(ItemList.clayMolds.itemID, 0, new ItemStack(ItemList.molds, 1, 0), 0.85F);
            FurnaceRecipes.smelting().addSmelting(ItemList.clayMolds.itemID, 1, new ItemStack(ItemList.molds, 1, 1), 0.85F);
            
            isInitialized = true;
        }
    }
}
