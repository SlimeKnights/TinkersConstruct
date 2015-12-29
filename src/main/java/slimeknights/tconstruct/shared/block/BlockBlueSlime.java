package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockBlueSlime extends BlockFluidClassic {

  public BlockBlueSlime(Fluid fluid, Material material) {
    super(fluid, material);
  }

  @Override
  public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
    if(type == EntityLiving.SpawnPlacementType.IN_WATER) {
      return true;
    }

    return super.canCreatureSpawn(world, pos, type);
  }
}
