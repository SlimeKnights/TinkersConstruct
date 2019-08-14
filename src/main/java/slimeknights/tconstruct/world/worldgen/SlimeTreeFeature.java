package slimeknights.tconstruct.world.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class SlimeTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {

  private final int minTreeHeight;
  private final int treeHeightRange;
  private final BlockState trunk;
  private final BlockState leaf;
  private final BlockState vine;
  private final boolean seekHeight;

  public SlimeTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn, boolean doBlockNotifyOnPlace, int minTreeHeightIn, int treeHeightRangeIn, BlockState trunkState, BlockState leafState, BlockState vineState, Block sapling, boolean seekHeightIn) {
    super(configFactoryIn, doBlockNotifyOnPlace);

    this.minTreeHeight = minTreeHeightIn;
    this.treeHeightRange = treeHeightRangeIn;
    this.trunk = trunkState;
    this.leaf = leafState;
    this.vine = vineState;
    this.seekHeight = seekHeightIn;
    this.setSapling((net.minecraftforge.common.IPlantable) sapling);
  }

  @Override
  protected boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox boundingBox) {
    int height = rand.nextInt(this.treeHeightRange) + this.minTreeHeight;

    if (this.seekHeight) {
      if (!(worldIn instanceof IWorld)) {
        return false;
      }

      position = this.findGround((IWorld) worldIn, position);
      if (position.getY() < 0) {
        return false;
      }
    }

    if (position.getY() >= 1 && position.getY() + height + 1 <= worldIn.getMaxHeight()) {
      if (isSoil(worldIn, position.down(), this.getSapling())) {
        this.setSlimeDirtAt(worldIn, position.down(), position);
        this.placeTrunk(changedBlocks, worldIn, position, height, boundingBox);
        this.placeCanopy(changedBlocks, worldIn, rand, position, height, boundingBox);
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }

  private void placeCanopy(Set<BlockPos> changedBlocks, IWorldGenerationReader world, Random random, BlockPos position, int height, MutableBoundingBox boundingBox) {
    position = position.up(height);
    for (int i = 0; i < 4; i++) {
      this.placeDiamondLayer(changedBlocks, world, position.down(i), i + 1, boundingBox);
    }
    BlockState air = Blocks.AIR.getDefaultState();

    position = position.down(3);
    this.placeAtPosition(changedBlocks, world, position.add(+4, 0, 0), air, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(-4, 0, 0), air, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(0, 0, +4), air, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(0, 0, -4), air, boundingBox);
    if (this.vine != null) {
      this.placeAtPosition(changedBlocks, world, position.add(+1, 0, +1), air, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(+1, 0, -1), air, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(-1, 0, +1), air, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(-1, 0, -1), air, boundingBox);
    }

    //Drippers
    // stuck with only one block down because of leaf decay distance
    position = position.down();
    this.placeAtPosition(changedBlocks, world, position.add(+3, 0, 0), this.leaf, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(-3, 0, 0), this.leaf, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(0, 0, -3), this.leaf, boundingBox);
    this.placeAtPosition(changedBlocks, world, position.add(0, 0, +3), this.leaf, boundingBox);
    if (this.vine == null) {
      this.placeAtPosition(changedBlocks, world, position.add(+1, 0, +1), this.leaf, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(+1, 0, -1), this.leaf, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(-1, 0, +1), this.leaf, boundingBox);
      this.placeAtPosition(changedBlocks, world, position.add(-1, 0, -1), this.leaf, boundingBox);
    }

    if (this.vine != null) {
      position = position.down();

      this.placeVineAtPosition(world, position.add(+3, 0, 0), this.getRandomizedVine(random));
      this.placeVineAtPosition(world, position.add(-3, 0, 0), this.getRandomizedVine(random));
      this.placeVineAtPosition(world, position.add(0, 0, -3), this.getRandomizedVine(random));
      this.placeVineAtPosition(world, position.add(0, 0, +3), this.getRandomizedVine(random));
      BlockState randomVine = this.getRandomizedVine(random);
      this.placeVineAtPosition(world, position.add(+2, 1, +2), randomVine);
      this.placeVineAtPosition(world, position.add(+2, 0, +2), randomVine);
      randomVine = this.getRandomizedVine(random);
      this.placeVineAtPosition(world, position.add(+2, 1, -2), randomVine);
      this.placeVineAtPosition(world, position.add(+2, 0, -2), randomVine);
      randomVine = this.getRandomizedVine(random);
      this.placeVineAtPosition(world, position.add(-2, 1, +2), randomVine);
      this.placeVineAtPosition(world, position.add(-2, 0, +2), randomVine);
      randomVine = this.getRandomizedVine(random);
      this.placeVineAtPosition(world, position.add(-2, 1, -2), randomVine);
      this.placeVineAtPosition(world, position.add(-2, 0, -2), randomVine);
    }
  }

  private BlockState getRandomizedVine(Random random) {
    BlockState state = this.vine;

    BooleanProperty[] sides = new BooleanProperty[] { VineBlock.NORTH, VineBlock.EAST, VineBlock.SOUTH, VineBlock.WEST };

    for (BooleanProperty side : sides) {
      state = state.with(side, false);
    }

    for (int i = random.nextInt(3) + 1; i > 0; i--) {
      state = state.with(sides[random.nextInt(sides.length)], true);
    }

    return state;
  }

  private void placeDiamondLayer(Set<BlockPos> changedBlocks, IWorldGenerationReader world, BlockPos pos, int range, MutableBoundingBox boundingBox) {
    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        if (Math.abs(x) + Math.abs(z) <= range) {
          BlockPos blockpos = pos.add(x, 0, z);
          if (isAirOrLeaves(world, blockpos)) {
            this.setSlimyLogState(changedBlocks, world, blockpos, this.leaf, boundingBox);
          }
        }
      }
    }
  }

  private void placeTrunk(Set<BlockPos> changedBlocks, IWorldGenerationReader world, BlockPos pos, int height, MutableBoundingBox boundingBox) {
    while (height > 0) {
      if (isAirOrLeaves(world, pos)) {
        this.setSlimyLogState(changedBlocks, world, pos, this.trunk, boundingBox);
      }

      pos = pos.up();
      height--;
    }
  }

  private void placeVineAtPosition(IWorldGenerationReader world, BlockPos pos, BlockState state) {
    if (isAirOrLeaves(world, pos)) {
      this.setBlockState(world, pos, state);
    }
  }

  private void placeAtPosition(Set<BlockPos> changedBlocks, IWorldGenerationReader world, BlockPos pos, BlockState state, MutableBoundingBox boundingBox) {
    if (isAirOrLeaves(world, pos)) {
      this.setSlimyLogState(changedBlocks, world, pos, state, boundingBox);
    }
  }

  protected static boolean isAirOrLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    if (!(worldIn instanceof net.minecraft.world.IWorldReader)) { // FORGE: Redirect to state method when possible
      return worldIn.hasBlockState(pos, (state) -> state.isAir() || state.isIn(BlockTags.LEAVES) || state.isIn(TinkerWorld.SLIMY_LEAVES));
    }
    else {
      return worldIn.hasBlockState(pos, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader) worldIn, pos));
    }
  }

  private BlockPos findGround(IWorld reader, BlockPos position) {
    do {
      BlockState state = reader.getBlockState(position);
      Block block = state.getBlock();
      BlockState upState = reader.getBlockState(position.up());
      if ((block == TinkerWorld.green_slime_dirt || block == TinkerWorld.blue_slime_dirt || block == TinkerWorld.purple_slime_dirt || block == TinkerWorld.magma_slime_dirt || block == TinkerWorld.blue_vanilla_slime_grass || block == TinkerWorld.purple_vanilla_slime_grass || block == TinkerWorld.orange_vanilla_slime_grass || block == TinkerWorld.blue_green_slime_grass || block == TinkerWorld.purple_green_slime_grass || block == TinkerWorld.orange_green_slime_grass || block == TinkerWorld.blue_blue_slime_grass || block == TinkerWorld.purple_blue_slime_grass || block == TinkerWorld.orange_blue_slime_grass || block == TinkerWorld.blue_purple_slime_grass || block == TinkerWorld.purple_purple_slime_grass || block == TinkerWorld.orange_purple_slime_grass
              || block == TinkerWorld.blue_magma_slime_grass || block == TinkerWorld.purple_magma_slime_grass || block == TinkerWorld.orange_magma_slime_grass) && !upState.getBlock().isOpaqueCube(upState, reader, position)) {
        return position.up();
      }
      position = position.down();
    }
    while (position.getY() > 0);

    return position;
  }

  @Deprecated
  protected static boolean isSlimyDirtOrGrass(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    return worldIn.hasBlockState(pos, (p_214582_0_) -> {
      Block block = p_214582_0_.getBlock();
      return block == TinkerWorld.green_slime_dirt || block == TinkerWorld.blue_slime_dirt || block == TinkerWorld.purple_slime_dirt || block == TinkerWorld.magma_slime_dirt || block == TinkerWorld.blue_vanilla_slime_grass || block == TinkerWorld.purple_vanilla_slime_grass || block == TinkerWorld.orange_vanilla_slime_grass || block == TinkerWorld.blue_green_slime_grass || block == TinkerWorld.purple_green_slime_grass || block == TinkerWorld.orange_green_slime_grass || block == TinkerWorld.blue_blue_slime_grass || block == TinkerWorld.purple_blue_slime_grass || block == TinkerWorld.orange_blue_slime_grass || block == TinkerWorld.blue_purple_slime_grass || block == TinkerWorld.purple_purple_slime_grass || block == TinkerWorld.orange_purple_slime_grass
              || block == TinkerWorld.blue_magma_slime_grass || block == TinkerWorld.purple_magma_slime_grass || block == TinkerWorld.orange_magma_slime_grass;
    });
  }

  protected static boolean isSoil(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
    if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null) {
      return isSlimyDirtOrGrass(reader, pos);
    }
    return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader) reader, pos, Direction.UP, sapling));
  }

  private void setSlimeDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
    if (!(reader instanceof IWorld)) {
      return;
    }
    ((IWorld) reader).getBlockState(pos).onPlantGrow((IWorld) reader, pos, origin);
  }

  private void setSlimyLogState(Set<BlockPos> changedBlocks, IWorldWriter worldIn, BlockPos pos, BlockState state, MutableBoundingBox boundingBox) {
    if (this.doBlockNotify) {
      worldIn.setBlockState(pos, state, 19);
    }
    else {
      worldIn.setBlockState(pos, state, 18);
    }

    boundingBox.expandTo(new MutableBoundingBox(pos, pos));

    if (TinkerWorld.SLIMY_LOGS.contains(state.getBlock())) {
      changedBlocks.add(pos.toImmutable());
    }
  }
}
