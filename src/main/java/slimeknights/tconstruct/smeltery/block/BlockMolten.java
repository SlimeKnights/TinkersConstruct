package slimeknights.tconstruct.smeltery.block;


import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockMolten extends BlockFluidClassic {

  public BlockMolten(Fluid fluid) {
    super(fluid, Material.lava);
  }

}
