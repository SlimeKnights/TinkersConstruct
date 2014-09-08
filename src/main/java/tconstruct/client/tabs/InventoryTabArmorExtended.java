package tconstruct.client.tabs;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tconstruct.client.ArmorControls;

public class InventoryTabArmorExtended extends AbstractTab
{
    public InventoryTabArmorExtended()
    {
        super(0, 0, 0, new ItemStack(Items.diamond_chestplate));
    }

    @Override
    public void onTabClicked ()
    {
        ArmorControls.openArmorGui();
    }

    @Override
    public boolean shouldAddToList ()
    {
        return true;
    }
}
