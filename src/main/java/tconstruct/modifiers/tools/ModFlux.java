package tconstruct.modifiers.tools;

import cofh.api.energy.IEnergyContainerItem;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;

/* TE3 support */

public class ModFlux extends ModBoolean
{
    public ArrayList<ItemStack> batteries = new ArrayList<ItemStack>();
    public int modifiersRequired = 1; // LALALALA totally not hidden IguanaTweaks Support LALALALA

    public ModFlux()
    {
        super(new ItemStack[0], 9, "Flux", "\u00a7e", "");
    }

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        ItemStack foundBattery = null;
        // try to find the battery in the input
        for (ItemStack stack : input)
            for (ItemStack battery : batteries)
            {
                if (stack == null)
                    continue;
                if (stack.getItem() != battery.getItem())
                    continue;
                if (!(stack.getItem() instanceof IEnergyContainerItem))
                    continue;
                // we don't allow multiple batteries to be added
                if (foundBattery != null)
                    return false;

                // battery found, gogogo
                foundBattery = stack;
            }

        // no battery present
        if (foundBattery == null)
            return false;

        // check if we already have a flux modifier
        if (tags.getBoolean(key))
        {
            // only allow if it's an upgrade
            // remark: we use the ToolCores function here instead of accessing the tag directly, to achieve backwards compatibility with tools without tags.
            int a = ((IEnergyContainerItem) foundBattery.getItem()).getMaxEnergyStored(foundBattery);
            int b = ((ToolCore) tool.getItem()).getMaxEnergyStored(tool);
            return ((IEnergyContainerItem) foundBattery.getItem()).getMaxEnergyStored(foundBattery) > ((ToolCore) tool.getItem()).getMaxEnergyStored(tool);
        }
        // otherwise check if we have enough modfiers
        else if (tags.getInteger("Modifiers") < modifiersRequired)
            return false;

        // all requirements satisfied!
        return true;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound();

        // update modifiers (only applies if it's not an upgrade)
        if (!tags.hasKey(key))
        {
            int modifiers = tags.getCompoundTag("InfiTool").getInteger("Modifiers");
            modifiers -= modifiersRequired;
            tags.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);
            addModifierTip(tool, "\u00a7eFlux");
        }

        tags.getCompoundTag("InfiTool").setBoolean(key, true);

        // find the battery in the input
        ItemStack inputBattery = null;
        for (ItemStack stack : input)
            for (ItemStack battery : batteries)
            {
                if (stack == null)
                    continue;
                if (stack.getItem() != battery.getItem())
                    continue;
                if (!(stack.getItem() instanceof IEnergyContainerItem))
                    continue;

                // we're guaranteed to only find one battery because more are prevented above
                inputBattery = stack;
            }

        // get the energy interface
        IEnergyContainerItem energyContainer = (IEnergyContainerItem) inputBattery.getItem();

        // set the charge values
        int charge = energyContainer.getEnergyStored(inputBattery);

        // add already present charge in the tool
        if (tags.hasKey("Energy"))
            charge += tags.getInteger("Energy");
        int maxCharge = energyContainer.getMaxEnergyStored(inputBattery);

        ItemStack subject42 = inputBattery.copy();

        int progress = 0, change = 1; // prevent endless loops with creative battery, blah
        // fill the battery full
        while (progress < maxCharge && change > 0)
        {
            change = energyContainer.receiveEnergy(subject42, 100000, false);
            progress += change;
        }
        // get the maximum extraction rate
        int maxExtract = energyContainer.extractEnergy(subject42, Integer.MAX_VALUE, true);

        subject42 = inputBattery.copy();

        // completely empty the battery
        progress = 0;
        change = 1;
        while (progress < maxCharge && change > 0)
        {
            change = energyContainer.extractEnergy(subject42, 100000, false);
            progress += change;
        }
        int maxReceive = energyContainer.receiveEnergy(subject42, Integer.MAX_VALUE, true);

        // make sure we don't overcharge
        charge = Math.min(charge, maxCharge);

        tags.setInteger("Energy", charge);
        tags.setInteger("EnergyMax", maxCharge);
        tags.setInteger("EnergyExtractionRate", maxExtract);
        tags.setInteger("EnergyReceiveRate", maxReceive);

        tags.setInteger(key, 1);
        ToolCore toolcore = (ToolCore) tool.getItem();
        tool.setItemDamage(1 + (toolcore.getMaxEnergyStored(tool) - charge) * (tool.getMaxDamage() - 1) / toolcore.getMaxEnergyStored(tool));
    }
}
