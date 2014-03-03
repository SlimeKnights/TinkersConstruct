package tconstruct.worldgen.village;

import java.util.ArrayList;
import java.util.List;
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

    private final List<ItemStack> allowedIngredients = new ArrayList<ItemStack>();
    private final int max = 17;
    private final int min = 7;

    public TVillageTrades()
    {
        super();

        // vanilla blocks
        allowedIngredients.add(new ItemStack(Block.pistonBase, 64));
        allowedIngredients.add(new ItemStack(Block.pistonStickyBase, 64));

        // tconstruct blocks
        allowedIngredients.add(new ItemStack(TContent.barricadeBirch, 64));
        allowedIngredients.add(new ItemStack(TContent.barricadeJungle, 64));
        allowedIngredients.add(new ItemStack(TContent.barricadeOak, 64));
        allowedIngredients.add(new ItemStack(TContent.barricadeSpruce, 64));
        allowedIngredients.add(new ItemStack(TContent.punji, 64));
        allowedIngredients.add(new ItemStack(TContent.toolStationWood, 3, 0));
        allowedIngredients.add(new ItemStack(TContent.toolStationWood, 3, 1));
        allowedIngredients.add(new ItemStack(TContent.toolStationWood, 3, 5));
        allowedIngredients.add(new ItemStack(TContent.toolStationWood, 3, 10));
        for (int sc = 0; sc < 4; sc++)
        {
            allowedIngredients.add(new ItemStack(TContent.landmine, 64, sc));
        }
    }

    @Override
    public void manipulateTradesForVillager (EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        if (villager.getProfession() == 78943)
        {
            ItemStack ingredient;
            ItemStack ingredient2;
            ItemStack result;

            for (int sc = 8; sc < 12; sc++)
            {
                int num = getNextInt(random, min, max);

                ingredient = getIngredient(random, num);
                if (ingredient.stackSize < 13)
                {
                    ingredient2 = getIngredient(random, ingredient);
                }
                else
                {
                    ingredient2 = null;
                }
                result = new ItemStack(TContent.oreBerry, calcStackSize(ingredient, ingredient2), sc);
                // adds iron, gold, copper & tin orebushes to the recipe list
                recipeList.addToListWithCheck(new MerchantRecipe(ingredient, ingredient2, result));

                if (sc == 8)
                {
                    // adds alumine orebush to the recipe list
                    result = new ItemStack(TContent.oreBerrySecond, calcStackSize(ingredient, ingredient2), sc);
                    recipeList.addToListWithCheck(new MerchantRecipe(ingredient, ingredient2, result));
                }
            }
        }
    }

    private int calcStackSize (ItemStack ingredient, ItemStack ingredient2)
    {
        if (ingredient == null)
            return 1;
        int num = ingredient.stackSize;
        if (ingredient2 != null)
            num += ingredient2.stackSize;

        return Math.max(1, Math.round((num - 5) / 4));
    }

    private ItemStack getIngredient (Random random, ItemStack ingredient)
    {
        int sc;
        ItemStack is;
        int tries = 0;
        while (true)
        {
            sc = getNextInt(random, 0, allowedIngredients.size() - 1);
            is = allowedIngredients.get(sc);

            if (is.itemID != ingredient.itemID || is.getItemDamage() != ingredient.getItemDamage())
                break;

            tries++;
            if (tries == 5)
                return null;
        }
        int num = getNextInt(random, 0, Math.min(is.stackSize, max - ingredient.stackSize));
        return is.copy().splitStack(num);
    }

    private ItemStack getIngredient (Random random, int num)
    {
        int sc = getNextInt(random, 0, allowedIngredients.size() - 1);
        ItemStack item = allowedIngredients.get(sc);
        return item.copy().splitStack(Math.min(num, item.stackSize));
    }

    private int getNextInt (Random random, int min, int max)
    {
        return random.nextInt(Math.max(1, (max - min) + 1)) + min;
    }
}
