package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.CastingRecipe;

/**
 * Fired when an item is cast in the casting table.
 * If consumeCast is set to true, the cast will be destroyed.
 */
public abstract class SmelteryCastedEvent extends Event
{
    public final CastingRecipe recipe;
    public ItemStack output;
    public boolean consumeCast;

    public SmelteryCastedEvent(CastingRecipe recipe, ItemStack output)
    {
        this.recipe = recipe;
        this.consumeCast = recipe.consumeCast;
        this.output = output;
    }

    public static class CastingTable extends SmelteryCastedEvent
    {
        public CastingTable(CastingRecipe recipe, ItemStack output)
        {
            super(recipe, output);
        }
    }

    public static class CastingBasin extends SmelteryCastedEvent
    {
        public CastingBasin(CastingRecipe recipe, ItemStack output)
        {
            super(recipe, output);
        }
    }
}
