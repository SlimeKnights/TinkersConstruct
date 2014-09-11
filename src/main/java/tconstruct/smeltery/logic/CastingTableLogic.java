package tconstruct.smeltery.logic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.event.SmelteryCastEvent;
import tconstruct.library.event.SmelteryCastedEvent;

public class CastingTableLogic extends CastingBlockLogic
{
    public CastingTableLogic()
    {
        super(TConstructRegistry.getTableCasting());
    }

    @Override
    public SmelteryCastEvent getCastingEvent(CastingRecipe recipe, FluidStack metal) {
        return new SmelteryCastEvent.CastingTable(recipe, metal);
    }

    @Override
    public SmelteryCastedEvent getCastedEvent(CastingRecipe recipe, ItemStack result) {
        return new SmelteryCastedEvent.CastingTable(recipe, result);
    }
}