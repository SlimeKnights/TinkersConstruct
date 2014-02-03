package tconstruct.client.tabs;

import tconstruct.client.TControls;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryTabArmorExtended extends AbstractTab
{
    public InventoryTabArmorExtended()
    {
        super(0, 0, 0, new ItemStack(Items.diamond_chestplate));
    }

    @Override
    public void onTabClicked ()
    {
        TControls.openArmorGui();
    }

    @Override
    public boolean shouldAddToList ()
    {
        return true;
    }
}
