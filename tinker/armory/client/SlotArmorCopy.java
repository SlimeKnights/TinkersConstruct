package tinker.armory.client;

import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotArmorCopy extends Slot
{
    /**
     * The armor type that can be placed on that slot, it uses the same values of armorType field on ItemArmor.
     */
    final int armorType;

    /**
     * The parent class of this clot, ContainerPlayer, SlotArmor is a Anon inner class.
     */
    final Container parent;

    public SlotArmorCopy(Container container, IInventory par2IInventory, int par3, int par4, int par5, int par6)
    {
        super(par2IInventory, par3, par4, par5);
        this.parent = container;
        this.armorType = par6;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack == null ? false : (par1ItemStack.getItem() instanceof ItemArmor ? ((ItemArmor)par1ItemStack.getItem()).armorType == this.armorType : (par1ItemStack.getItem().itemID != Block.pumpkin.blockID && par1ItemStack.getItem().itemID != Item.skull.itemID ? false : this.armorType == 0));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public int getBackgroundIconIndex()
    {
        return 15 + this.armorType * 16;
    }
}
