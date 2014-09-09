package tconstruct.library.crafting;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.*;

public class DryingRackRecipes
{
    public static ArrayList<DryingRecipe> recipes = new ArrayList<DryingRecipe>();

    public static void addDryingRecipe (Object input, int time, Object output)
    {
        ItemStack inputItem = null;
        ItemStack outputItem = null;

        if (input instanceof ItemStack)
            inputItem = (ItemStack) input;
        else if (input instanceof Item)
            inputItem = new ItemStack((Item) input, 1, 0);
        else if (input instanceof Block)
            inputItem = new ItemStack((Block) input, 1, 0);
        else
            throw new RuntimeException("Drying recipe input is invalid!");

        if (output instanceof ItemStack)
            outputItem = (ItemStack) output;
        else if (output instanceof Item)
            outputItem = new ItemStack((Item) output, 1, 0);
        else if (output instanceof Block)
            outputItem = new ItemStack((Block) output, 1, 0);
        else
            throw new RuntimeException("Drying recipe output is invalid!");

        recipes.add(new DryingRecipe(inputItem, time, outputItem));
    }

    public static int getDryingTime (ItemStack input)
    {
        for (DryingRecipe r : recipes)
        {
            if (r.matches(input))
                return r.time;
        }

        return -1;
    }

    public static ItemStack getDryingResult (ItemStack input)
    {
        for (DryingRecipe r : recipes)
        {
            if (r.matches(input))
                return r.getResult();
        }

        return null;
    }

    public static class DryingRecipe
    {
        public final int time;
        public final ItemStack input;
        public final ItemStack result;

        DryingRecipe(ItemStack input, int time, ItemStack result)
        {
            this.time = time;
            this.input = input;
            this.result = result;
        }

        public boolean matches (ItemStack input)
        {
            return ItemStack.areItemStacksEqual(this.input, input);
        }

        public ItemStack getResult ()
        {
            return result.copy();
        }
    }
}
