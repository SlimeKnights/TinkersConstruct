package tconstruct.tools.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class SlotPartBuilder extends Slot
{

    public SlotPartBuilder(IInventory inv, int index, int posX, int posY)
    {
        super(inv, index, posX, posY);
    }

    @Override
    protected void onCrafting (ItemStack par1ItemStack, int par2)
    {

    }
}
