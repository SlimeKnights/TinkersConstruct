package tconstruct.client.tabs;

import net.minecraft.item.ItemStack;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.armor.TinkerArmor;
import tconstruct.client.TControls;

public class InventoryTabKnapsack extends AbstractTab
{
    public InventoryTabKnapsack()
    {
        super(0, 0, 0, new ItemStack(TinkerArmor.knapsack));
    }

    @Override
    public void onTabClicked ()
    {
        TControls.openKnapsackGui();
    }

    @Override
    public boolean shouldAddToList ()
    {
        return ArmorProxyClient.armorExtended.inventory[2] != null && ArmorProxyClient.armorExtended.inventory[2].getItem() == TinkerArmor.knapsack;
    }
}
