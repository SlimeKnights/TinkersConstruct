package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.tables.client.inventory.library.IScalingInventory;

/** Interface for tinker chest TEs */
public interface IChestItemHandler extends IItemHandlerModifiable, INBTSerializable<CompoundNBT>, IScalingInventory {
  /** Sets the parent of this block */
  void setParent(MantleTileEntity parent);
}
