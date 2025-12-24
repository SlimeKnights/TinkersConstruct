package slimeknights.tconstruct.library.client.model;

import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

/** Model data properties used in Tinker's Construct */
public class ModelProperties {
  /** Property for fluid stack in a fluid model */
  public static final ModelProperty<FluidStack> FLUID_STACK = new ModelProperty<>();
  /** Maximum size for a fluid tank in a tank model */
  public static final ModelProperty<Integer> TANK_CAPACITY = new ModelProperty<>();
  /** Model property for a single material on a tool part. */
  public static final ModelProperty<MaterialVariantId> MATERIAL = new ModelProperty<>();
  /** Model property for the materials list on a tool. */
  public static final ModelProperty<MaterialIdNBT> MATERIALS = new ModelProperty<>();
}
