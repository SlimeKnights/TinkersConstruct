package tconstruct.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPartBuilder extends Slot
{

    public SlotPartBuilder(IInventory inv, int index, int posX, int posY)
    {
        super(inv, index, posX, posY);
    }

    protected void onCrafting (ItemStack par1ItemStack, int par2)
    {

    }
}
