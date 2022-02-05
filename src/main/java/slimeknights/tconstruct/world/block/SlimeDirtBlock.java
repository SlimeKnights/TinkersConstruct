package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.world.TinkerWorld;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SlimeDirtBlock extends Block {

  public SlimeDirtBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
    // can sustain both slimeplants and normal plants
    return plantable.getPlantType(world, pos) == TinkerWorld.SLIME_PLANT_TYPE || plantable.getPlantType(world, pos) == PlantType.PLAINS;
  }
}
