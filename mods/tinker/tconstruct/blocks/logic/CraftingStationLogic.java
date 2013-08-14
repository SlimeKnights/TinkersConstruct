package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.inventory.CraftingStationContainer;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CraftingStationLogic extends InventoryLogic
{
    public CraftingStationLogic()
    {
        super(11); //9 for crafting, 1 for output, 1 for plans
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new CraftingStationContainer(inventoryplayer, this, x, y, z);
    }

    @Override
    protected String getDefaultName ()
    {
        return "crafters.craftingstation";
    }

    public boolean canDropInventorySlot (int slot)
    {
        if (slot == 9)
            return false;
        return true;
    }
}
