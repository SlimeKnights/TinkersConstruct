package tconstruct.blocks.logic;

import tconstruct.inventory.PatternChestContainer;
import tconstruct.library.blocks.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class PatternChestLogic extends InventoryLogic
{
    public PatternChestLogic()
    {
        super(30);
    }

    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public String getDefaultName ()
    {
        return "toolstation.patternholder";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new PatternChestContainer(inventoryplayer, this);
    }
}
