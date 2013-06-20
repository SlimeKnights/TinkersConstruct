package mods.tinker.tconstruct.blocks.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;

public abstract class AggregatorLogic extends InventoryLogic
{
    public AggregatorLogic(int invSize)
    {
        super(invSize);
    }
}
