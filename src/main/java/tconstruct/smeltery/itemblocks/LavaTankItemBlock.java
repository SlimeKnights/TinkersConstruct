package tconstruct.smeltery.itemblocks;

import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import tconstruct.smeltery.logic.LavaTankLogic;

public class LavaTankItemBlock extends MultiItemBlock implements IFluidContainerItem {
    public static final String blockTypes[] = {"Tank", "Gague", "Window"};

    public LavaTankItemBlock(Block b) {
        super(b, "LavaTank", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (stack.hasTagCompound()) {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null) {
                list.add(StatCollector.translateToLocal("searedtank1.tooltip") + " "
                        + StatCollector.translateToLocal(liquidTag.getString("FluidName")));
                list.add(liquidTag.getInteger("Amount") + " mB");
            }
        } else {
            list.add(StatCollector.translateToLocal("searedtank3.tooltip"));
            list.add(StatCollector.translateToLocal("searedtank2.tooltip"));
        }
    }

    /**
     * @param container
     *         ItemStack which is the fluid container.
     * @return FluidStack representing the fluid in the container, null if the container is empty.
     */
    @Override
    public FluidStack getFluid(ItemStack container) {
        if (container.hasTagCompound()) {
            NBTTagCompound nbt = container.getTagCompound();
            if (nbt.hasKey("Fluid")) {
                return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
            }
        }
        return null;
    }

    /**
     * @param container
     *         ItemStack which is the fluid container.
     * @param resource
     *         FluidStack attempting to set to the container.
     */
    public void setFluid(ItemStack container, FluidStack resource) {
        if (container == null) return;
        if (resource != null && 0 < resource.amount) {
            NBTTagCompound nbt = container.getTagCompound() != null ? container.getTagCompound() : new NBTTagCompound();
            nbt.setTag("Fluid", resource.writeToNBT(new NBTTagCompound()));
            container.setTagCompound(nbt);
            return;
        }
        NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null) return;
        nbt.removeTag("Fluid");
        if (nbt.hasNoTags()) nbt = null;
        container.setTagCompound(nbt);
    }

    /**
     * @param container
     *         ItemStack which is the fluid container.
     * @return Capacity of this fluid container.
     */
    @Override
    public int getCapacity(ItemStack container) {
        return LavaTankLogic.tankCapacity;
    }

    /**
     * @param container
     *         ItemStack which is the fluid container.
     * @param resource
     *         FluidStack attempting to fill the container.
     * @param doFill
     *         If false, the fill will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) filled into the
     * container.
     */
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (container.stackSize != 1) return 0;
        if (resource == null || resource.amount <= 0) return 0;
        FluidStack fluidStack = this.getFluid(container);
        if (fluidStack == null) fluidStack = new FluidStack(resource, 0);
        if (!fluidStack.isFluidEqual(resource)) return 0;
        int amount = Math.min(this.getCapacity(container) - fluidStack.amount, resource.amount);
        if (doFill && amount > 0) {
            fluidStack.amount += amount;
            this.setFluid(container, fluidStack);
        }
        return amount;
    }

    /**
     * @param container
     *         ItemStack which is the fluid container.
     * @param maxDrain
     *         Maximum amount of fluid to be removed from the container.
     * @param doDrain
     *         If false, the drain will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) drained from the
     * container.
     */
    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackSize != 1) return null;
        FluidStack fluidStack = this.getFluid(container);
        if (fluidStack == null || fluidStack.amount <= 0) return null;
        int drain = Math.min(fluidStack.amount, maxDrain);
        if (drain <= 0) return null;
        if (doDrain) {
            fluidStack.amount -= drain;
            this.setFluid(container, fluidStack);
        }
        return new FluidStack(fluidStack, drain);
    }
}
