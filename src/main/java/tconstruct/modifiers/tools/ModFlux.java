package tconstruct.modifiers.tools;

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
        super(new ItemStack[0], 9, "Flux", "\u00a7e", "");
    }

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        // check if the regular limitations apply
        if(!canModify(tool, input))
            return false;

        boolean foundBattery = false;
        // try to find the battery in the input
        for(ItemStack stack : input)
            for(ItemStack battery : batteries)
            {
                if(stack == null)
                    continue;
                if(stack.getItem() != battery.getItem())
                    continue;
                if(!(stack.getItem() instanceof  IEnergyContainerItem))
                    continue;
                // we don't allow multiple batteries to be added
                if(foundBattery)
                    return false;

                // battery found, gogogo
                foundBattery = true;
            }

        // no battery found :(
        return foundBattery;
    }

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


            // find the battery in the input
            ItemStack inputBattery = null;
            for(ItemStack stack : input)
                for(ItemStack battery : batteries)
                {
                    if(stack == null)
                        continue;
                    if(stack.getItem() != battery.getItem())
                        continue;
                    if(!(stack.getItem() instanceof  IEnergyContainerItem))
                        continue;

                    // we're guaranteed to only find one battery because more are prevented above
                    inputBattery = stack;
                }

            // get the energy interface
            IEnergyContainerItem energyContainer = (IEnergyContainerItem)inputBattery.getItem();

            // set the charge values
            int charge = energyContainer.getEnergyStored(inputBattery);
            int maxCharge = energyContainer.getMaxEnergyStored(inputBattery);
            // simulate transferring maximum amount of POWER to obtain the maximum receive-limit of the battery ;_;
            int maxExtract = energyContainer.extractEnergy(inputBattery, Integer.MAX_VALUE, true);
            int maxReceive = energyContainer.receiveEnergy(inputBattery, Integer.MAX_VALUE, true);
            tags.setInteger("Energy", charge);
            tags.setInteger("EnergyMax", maxCharge);
            tags.setInteger("EnergyExtractionRate", maxExtract);
            tags.setInteger("EnergyReceiveRate", maxReceive);


            tags.setInteger(key, 1);

            addModifierTip(tool, "\u00a7eFlux");
            ToolCore toolcore = (ToolCore) tool.getItem();
            tool.setItemDamage(1 + (toolcore.getMaxEnergyStored(tool) - charge) * (tool.getMaxDamage() - 1) / toolcore.getMaxEnergyStored(tool));
        }
    }
}
