package slimeknights.tconstruct.library.materials;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TestMaterial extends Material {

  public TestMaterial(ResourceLocation identifier, Fluid fluid, boolean craftable, ItemStack shardItem) {
    super(identifier, fluid, craftable, shardItem);
  }
}
