package tconstruct.client.tabs;

import net.minecraft.item.ItemStack;
import tconstruct.armor.*;
import tconstruct.client.ArmorControls;

public class InventoryTabKnapsack extends AbstractTab
{
    public InventoryTabKnapsack()
    {
        super(0, 0, 0, new ItemStack(TinkerArmor.knapsack));
    }

    @Override
    public void onTabClicked ()
    {
        ArmorControls.openKnapsackGui();
    }

    @Override
    public boolean shouldAddToList ()
    {
        return ArmorProxyClient.armorExtended.inventory[2] != null && ArmorProxyClient.armorExtended.inventory[2].getItem() == TinkerArmor.knapsack;
    }
}
