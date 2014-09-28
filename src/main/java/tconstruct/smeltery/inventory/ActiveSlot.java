package tconstruct.smeltery.inventory;

import net.minecraft.inventory.*;

public class ActiveSlot extends Slot
{
    protected boolean active;
    public int activeSlotNumber;

    public ActiveSlot(IInventory iinventory, int par2, int par3, int par4, boolean flag)
    {
        super(iinventory, par2, par3, par4);
        active = flag;
    }

    public void setActive (boolean flag)
    {
        active = flag;
    }

    public boolean getActive ()
    {
        return active;
    }
}
