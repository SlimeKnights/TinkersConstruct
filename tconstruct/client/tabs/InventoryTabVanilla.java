package tconstruct.client.tabs;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class InventoryTabVanilla extends AbstractTab
{
    public InventoryTabVanilla()
    {
        super(0, 0, 0, new ItemStack(Block.workbench));
    }

    @Override
    public void onTabClicked()
    {
        TabRegistry.openInventoryGui();
    }

    @Override
    public boolean shouldAddToList()
    {
        return true;
    }
}
