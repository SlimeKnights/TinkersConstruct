package tconstruct.modifiers;

import cofh.api.energy.IEnergyContainerItem;

import java.util.ArrayList;

import tconstruct.library.tools.ToolCore;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* TE3 support */

public class ModFlux extends ModBoolean
{

    public ArrayList<ItemStack> batteries = new ArrayList<ItemStack>();

    public ModFlux()
    {
        super(new ItemStack[0], 17, "Flux", "\u00a7e", "");
    }

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        boolean battery = false;

        if (input[0] != null)
        {
            for (ItemStack stack : batteries)
            {
                if (stack.itemID == input[0].itemID && input[0].getItem() instanceof IEnergyContainerItem)
                {
                    battery = true;
                }
            }
            return battery && canModify(tool, input);
        }

        if (input[1] != null)
        {

            for (ItemStack stack : batteries)
            {
                if (stack.itemID == input[1].itemID && input[1].getItem() instanceof IEnergyContainerItem)
                {
                    battery = true;
                }
                return battery && canModify(tool, input);
            }
        }

        return false;
    }

    /*@Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
    	NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
    	if (!tags.hasKey(key))
    		return tags.getInteger("Modifiers") > 0;

    	return false;
    }*/

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound();

        if (!tags.hasKey(key))
        {
            tags.setBoolean(key, true);

            int modifiers = tags.getCompoundTag("InfiTool").getInteger("Modifiers");
            modifiers -= 1;
            tags.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);
            int charge = 0;
            if (input[0]!= null && input[0].getItem() != null && input[0].getItem() instanceof IEnergyContainerItem && input[0].hasTagCompound())
                charge = input[0].getTagCompound().getInteger("Energy");
            if (input[1]!= null && input[1].getItem()!= null && input[1].getItem() instanceof IEnergyContainerItem && input[1].hasTagCompound())
                charge = input[1].getTagCompound().getInteger("Energy");
            tags.setInteger("Energy", charge);
            tags.setInteger(key, 1);
            addModifierTip(tool, "\u00a7eFlux");
            ToolCore toolcore = (ToolCore) tool.getItem();
            tool.setItemDamage(1 + (toolcore.getMaxEnergyStored(tool) - charge) * (tool.getMaxDamage() - 1) / toolcore.getMaxEnergyStored(tool));
        }
    }

}
