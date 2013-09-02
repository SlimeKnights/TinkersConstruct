package tconstruct.worldgen.village;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import tconstruct.common.TContent;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class TVillageTrades implements IVillageTradeHandler
{

    @Override
    public void manipulateTradesForVillager (EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        if (villager.getProfession() == 78943)
        {
            for (int num = 14; num < 18; num++)
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Block.pistonBase, num), new ItemStack(TContent.oreBerrySecond, 1, 0)));
            for (int num = 23; num < 26; num++)
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Block.pistonBase, num), new ItemStack(TContent.oreBerry, 1, 0)));
            for (int num = 23; num < 26; num++)
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Block.pistonBase, num), new ItemStack(TContent.oreBerry, 1, 2)));
            for (int num = 23; num < 26; num++)
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Block.pistonBase, num), new ItemStack(TContent.oreBerry, 1, 3)));
            for (int num = 30; num < 34; num++)
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Block.pistonBase, num), new ItemStack(TContent.oreBerry, 1, 1)));
        }
    }

}
