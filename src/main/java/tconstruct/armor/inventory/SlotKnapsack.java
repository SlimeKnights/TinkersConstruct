package tconstruct.armor.inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.armor.items.Knapsack;

public class SlotKnapsack extends Slot
{
    public SlotKnapsack(IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
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
        return item != null && (item instanceof Knapsack);
    }
    
    @Override
    public void onSlotChanged ()
    {
        super.onSlotChanged();
        
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            Minecraft mc = Minecraft.getMinecraft();
            
            if (mc.currentScreen instanceof GuiScreen) {
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                int i1 = scaledresolution.getScaledWidth();
                int j1 = scaledresolution.getScaledHeight();
                mc.currentScreen.setWorldAndResolution(mc, i1, j1);
            }
        }
    }
}