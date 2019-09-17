package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PunjiBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  public static final BooleanProperty EAST = BlockStateProperties.EAST;
  public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
  public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");

  public PunjiBlock() {
    super(Block.Properties.create(Material.PLANTS).hardnessAndResistance(3.0F).sound(SoundType.PLANT));
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(NORTHEAST, Boolean.valueOf(false)).with(NORTHWEST, Boolean.valueOf(false)));
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    Direction direction = stateIn.get(FACING);

    int off = -direction.ordinal() % 2;

    Direction face1 = Direction.values()[(direction.ordinal() + 2) % 6];
    Direction face2 = Direction.values()[(direction.ordinal() + 4 + off) % 6];

    // North/East Connector
    BlockState north = worldIn.getBlockState(currentPos.offset(face1));
    BlockState east = worldIn.getBlockState(currentPos.offset(face2));

    if (north.getBlock() == this && north.get(FACING) == direction) {
      stateIn = stateIn.with(NORTH, true);
    }

    if (east.getBlock() == this && east.get(FACING) == direction) {
      stateIn = stateIn.with(EAST, true);
    }

    // Diagonal connections
    BlockState northeast = worldIn.getBlockState(currentPos.offset(face1).offset(face2));
    BlockState northwest = worldIn.getBlockState(currentPos.offset(face1).offset(face2.getOpposite()));

    if (northeast.getBlock() == this && northeast.get(FACING) == direction) {
      stateIn = stateIn.with(NORTHEAST, true);
    }

    if (northwest.getBlock() == this && northwest.get(FACING) == direction) {
      stateIn = stateIn.with(NORTHWEST, true);
    }

    return stateIn;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, NORTH, EAST, NORTHEAST, NORTHWEST);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getNearestLookingDirection().getOpposite();

    BlockState state = this.getDefaultState().with(FACING, direction);

    int off = -direction.ordinal() % 2;

    Direction face1 = Direction.values()[(direction.ordinal() + 2) % 6];
    Direction face2 = Direction.values()[(direction.ordinal() + 4 + off) % 6];

    // North/East Connector
    BlockState north = context.getWorld().getBlockState(context.getPos().offset(face1));
    BlockState east = context.getWorld().getBlockState(context.getPos().offset(face2));

    if (north.getBlock() == this && north.get(FACING) == direction) {
      state = state.with(NORTH, true);
    }

    if (east.getBlock() == this && east.get(FACING) == direction) {
      state = state.with(EAST, true);
    }

    // Diagonal connections
    BlockState northeast = context.getWorld().getBlockState(context.getPos().offset(face1).offset(face2));
    BlockState northwest = context.getWorld().getBlockState(context.getPos().offset(face1).offset(face2.getOpposite()));

    if (northeast.getBlock() == this && northeast.get(FACING) == direction) {
      state = state.with(NORTHEAST, true);
    }

    if (northwest.getBlock() == this && northwest.get(FACING) == direction) {
      state = state.with(NORTHWEST, true);
    }

    return state;
  }

  @Override
  @Deprecated
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    Direction facing = state.get(FACING);

    if (!Block.hasSolidSide(state, worldIn, pos.offset(facing), facing.getOpposite())) {
      spawnDrops(state, worldIn, pos);
      worldIn.removeBlock(pos, false);
    }
  }

  @Override
  @Deprecated
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof LivingEntity) {
      float damage = 3f;
      if (entityIn.fallDistance > 0) {
        damage += entityIn.fallDistance * 1.5f + 2f;
      }
      entityIn.attackEntityFrom(DamageSource.CACTUS, damage);
      ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 1));
    }
  }
}
