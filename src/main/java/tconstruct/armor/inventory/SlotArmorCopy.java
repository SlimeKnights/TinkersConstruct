package tconstruct.armor.inventory;

import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;

public class SlotArmorCopy extends Slot
{
    /**
     * The armor type that can be placed on that slot, it uses the same values
     * of armorType field on ItemArmor.
     */
    final int armorType;

    /**
     * The parent class of this clot, ContainerPlayer, SlotArmor is a Anon inner
     * class.
     */
    final Container parent;

    public SlotArmorCopy(Container container, IInventory par2IInventory, int par3, int par4, int par5, int par6)
    {
        super(par2IInventory, par3, par4, par5);
        this.parent = container;
        this.armorType = par6;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    @Override
    public int getSlotStackLimit ()
    {
        return 1;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * the armor slots.
     */
    @Override
    public boolean isItemValid (ItemStack par1ItemStack)
    {
        Item item = (par1ItemStack == null ? null : par1ItemStack.getItem());
        boolean isValidArmor = false;
        if (item instanceof ItemArmor)
        {
            isValidArmor = (((ItemArmor) item).armorType == armorType);
        }
        return item != null && (isValidArmor || (item instanceof ItemBlock && armorType == 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public IIcon getBackgroundIconIndex ()
    {
        return ItemArmor.func_94602_b(this.armorType);
    }
}
