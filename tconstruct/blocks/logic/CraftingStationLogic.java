package tconstruct.blocks.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import tconstruct.inventory.CraftingStationContainer;
import tconstruct.library.blocks.InventoryLogic;

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
