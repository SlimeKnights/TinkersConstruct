package mods.tinker.tconstruct.inventory;

import java.util.ArrayList;
import java.util.List;

import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ActiveContainer extends Container
{
    public List<ActiveSlot> activeInventorySlots = new ArrayList<ActiveSlot>();

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return false;
    }

    protected ActiveSlot addDualSlotToContainer (ActiveSlot slot)
    {
        slot.activeSlotNumber = this.activeInventorySlots.size();
        this.activeInventorySlots.add(slot);
        this.addSlotToContainer(slot);
        return slot;
    }

}
