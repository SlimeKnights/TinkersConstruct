package mods.tinker.tconstruct.worldgen.village;

import java.util.Random;

import mods.tinker.tconstruct.TContent;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class TVillageTrades implements IVillageTradeHandler
{

    @Override
    public void manipulateTradesForVillager (EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        if (villager.getProfession() == 78943)
        {
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 1, 23), new ItemStack(TContent.materials, 1, 20)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 4, 23), new ItemStack(Item.arrow, 4)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 1, 23), new ItemStack(TContent.materials, 1, 0)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 1, 23), new ItemStack(TContent.materials, 1, 21)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 5, 23), new ItemStack(TContent.materials, 1, 1)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 1, 23), new ItemStack(TContent.materials, 1, 22)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 2, 23), new ItemStack(TContent.materials, 1, 2)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 1, 23), new ItemStack(TContent.materials, 1, 19)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 64, 23), new ItemStack(TContent.materials, 1, 6)));
            recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(TContent.materials, 64, 23), new ItemStack(TContent.materials, 1, 7)));
        }
    }

}
