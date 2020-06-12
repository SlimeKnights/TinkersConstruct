package slimeknights.tconstruct.tables.client.model;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ModelProperties {

  //public static final ModelProperty<TableItems> items = new ModelProperty<>();

  public static final ModelProperty<Direction> DIRECTION = new ModelProperty<>();
  public static final ModelProperty<CompoundNBT> TEXTURE = new ModelProperty<>();
  public static final ModelProperty<FluidTank> FLUID_TANK = new ModelProperty<>();
}
