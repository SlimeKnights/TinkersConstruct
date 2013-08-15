package tconstruct.client.tabs;

import net.minecraft.item.ItemStack;
import tconstruct.client.TControls;
import tconstruct.client.TProxyClient;
import tconstruct.common.TContent;

public class InventoryTabKnapsack extends AbstractTab
{
    public InventoryTabKnapsack()
    {
        super(0, 0, 0, new ItemStack(TContent.knapsack));
    }

    @Override
    public void onTabClicked()
    {
        TControls.openKnapsackGui();
    }

    @Override
    public boolean shouldAddToList()
    {
        return TProxyClient.armorExtended.inventory[2] != null && TProxyClient.armorExtended.inventory[2].getItem() == TContent.knapsack;
    }
}
