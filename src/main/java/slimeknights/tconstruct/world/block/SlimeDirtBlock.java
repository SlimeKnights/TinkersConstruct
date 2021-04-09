package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import slimeknights.tconstruct.world.TinkerWorld;

public class SlimeDirtBlock extends Block {

  public SlimeDirtBlock(Settings properties) {
    super(properties);
  }

//  @Override
//  public boolean canSustainPlant(BlockState state, BlockView world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
//     can sustain both slimeplants and normal plants
//    return plantable.getPlantType(world, pos) == TinkerWorld.SLIME_PLANT_TYPE || plantable.getPlantType(world, pos) == PlantType.PLAINS;
//  }
}
