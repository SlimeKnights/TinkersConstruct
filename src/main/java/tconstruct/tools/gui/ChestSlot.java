package tconstruct.tools.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ChestSlot extends Slot {
    /*
     * A Slot, used for adjacent Chest Inventories, that can be disabled.
     */
    public boolean enabled = true;
    
    public ChestSlot(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean func_111238_b/*isEnabled*/() {
        return enabled;
    }
    
    public void disable() {
        enabled = false;
    }
    
    public void enable() {
        enabled = true;
    }

}
