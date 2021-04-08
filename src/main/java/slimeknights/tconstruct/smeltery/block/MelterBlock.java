package slimeknights.tconstruct.smeltery.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import org.jetbrains.annotations.Nonnull;
import java.util.Random;

public class MelterBlock extends ControllerBlock {
  public MelterBlock(Settings props) {
    super(props);
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
    if (state.get(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 6F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset);
    }
  }


  /*
   * Fuel tank detection
   */

  /**
   * Checks if the given state is a valid melter fuel source
   * @param state  State instance
   * @return  True if its a valid fuel source
   */
  protected boolean isValidFuelSource(BlockState state) {
    return TinkerTags.Blocks.MELTER_TANKS.contains(state.getBlock());
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    BlockState state = super.getPlacementState(context);
    if (state != null) {
      return state.with(ACTIVE, isValidFuelSource(context.getWorld().getBlockState(context.getBlockPos().down())));
    }
    return null;
  }

  @Deprecated
  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.DOWN) {
      return state.with(ACTIVE, isValidFuelSource(neighbor));
    }
    return state;
  }


  /*
   * Block behavior
   */

  @Deprecated
  @Override
  @Environment(EnvType.CLIENT)
  public float getAmbientOcclusionLightLevel(BlockState state, BlockView worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView reader, BlockPos pos) {
    return true;
  }


  /*
   * Tile Entity interaction
   */

  @Nonnull
  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return new MelterTileEntity();
  }

  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (ITankTileEntity.interactWithTank(world, pos, player, hand, hit)) {
      return ActionResult.SUCCESS;
    }
    return super.onUse(state, world, pos, player, hand, hit);
  }


  /*
   * Comparator
   */

  @Deprecated
  @Override
  public boolean hasComparatorOutput(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }
}
