package tconstruct.smeltery.inventory;

import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

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