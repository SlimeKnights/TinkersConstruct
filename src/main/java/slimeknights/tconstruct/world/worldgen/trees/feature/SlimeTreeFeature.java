package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.Template;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class SlimeTreeFeature extends Feature<BaseSlimeTreeFeatureConfig> {

  public SlimeTreeFeature(Codec<BaseSlimeTreeFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public final boolean generate(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BaseSlimeTreeFeatureConfig config) {
    Set<BlockPos> set = Sets.newHashSet();
    Set<BlockPos> set1 = Sets.newHashSet();
    Set<BlockPos> set2 = Sets.newHashSet();
    MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
    boolean flag = this.place(seedReader, random, blockPos, set, set1, mutableboundingbox, config);

    if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !set.isEmpty()) {
      VoxelShapePart voxelshapepart = this.func_236403_a_(seedReader, mutableboundingbox, set, set2);
      Template.func_222857_a(seedReader, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
      return true;
    }
    else {
      return false;
    }
  }

  private boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> trunkBlockPosSet, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, BaseSlimeTreeFeatureConfig configIn) {
    int height = rand.nextInt(configIn.randomHeight) + configIn.baseHeight;

    if (!(generationReader instanceof IWorld)) {
      return false;
    }

    BlockPos blockpos;
    if (!configIn.forcePlacement) {
      int oceanFloorHeight = generationReader.getHeight(Heightmap.Type.OCEAN_FLOOR, positionIn).getY();

      blockpos = new BlockPos(positionIn.getX(), oceanFloorHeight, positionIn.getZ());
    }
    else {
      blockpos = positionIn;
    }

    if (blockpos.getY() >= 1 && blockpos.getY() + height + 1 <= 256) {
      if (!isDirtOrFarmlandAt(generationReader, blockpos.down())) {
        return false;
      }
      else {
        this.setDirtAt(generationReader, blockpos.down(), blockpos);

        this.placeTrunk(generationReader, rand, height, blockpos, trunkBlockPosSet, boundingBoxIn, configIn);

        this.placeCanopy(generationReader, rand, height, blockpos, trunkBlockPosSet, boundingBoxIn, configIn);

        return true;
      }
    }
    else {
      return false;
    }
  }

  protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
    if (!(reader instanceof IWorld)) {
      return;
    }

    ((IWorld) reader).getBlockState(pos).getBlock().onPlantGrow(((IWorld) reader).getBlockState(pos), (IWorld) reader, pos, origin);
  }

  protected void placeTrunk(IWorldGenerationReader worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    while (treeHeight > 0) {
      this.setLog(worldIn, randomIn, blockPos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);

      blockPos = blockPos.up();
      treeHeight--;
    }
  }

  protected void placeCanopy(IWorldGenerationReader worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    blockPos = blockPos.up(treeHeight);
    for (int i = 0; i < 4; i++) {
      this.placeDiamondLayer(worldIn, randomIn, i + 1, blockPos.down(i), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    }

    blockPos = blockPos.down(3);

    this.placeAir(worldIn, randomIn, blockPos.add(+4, 0, 0), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(-4, 0, 0), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(0, 0, +4), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(0, 0, -4), blockPosSet, mutableBoundingBoxIn);

    if (treeFeatureConfigIn.hasVines) {
      this.placeAir(worldIn, randomIn, blockPos.add(+1, 0, +1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(+1, 0, -1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(-1, 0, +1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(-1, 0, -1), blockPosSet, mutableBoundingBoxIn);
    }

    //Drippers
    // stuck with only one block down because of leaf decay distance
    blockPos = blockPos.down();
    this.setLeaf(worldIn, randomIn, blockPos.add(+3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.setLeaf(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.setLeaf(worldIn, randomIn, blockPos.add(0, 0, -3), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.setLeaf(worldIn, randomIn, blockPos.add(0, 0, +3), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);

    if (!treeFeatureConfigIn.hasVines) {
      this.setLeaf(worldIn, randomIn, blockPos.add(+1, 0, +1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.setLeaf(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.setLeaf(worldIn, randomIn, blockPos.add(-1, 0, +1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.setLeaf(worldIn, randomIn, blockPos.add(-1, 0, -1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    }

    if (treeFeatureConfigIn.hasVines) {
      blockPos = blockPos.down();
      this.placeVine(worldIn, randomIn, blockPos.add(+3, 0, 0), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(0, 0, -3), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(0, 0, +3), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      BlockState randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 1, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 0, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 1, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 0, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 1, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 0, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 1, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 0, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine);
    }
  }

  private void placeDiamondLayer(IWorldGenerationReader worldIn, Random randomIn, int range, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        if (Math.abs(x) + Math.abs(z) <= range) {
          BlockPos blockpos = blockPos.add(x, 0, z);
          this.setLeaf(worldIn, randomIn, blockpos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
        }
      }
    }
  }

  protected boolean setLog(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, treeFeatureConfigIn.trunkProvider.getBlockState(randomIn, blockPos));
      mutableBoundingBoxIn.expandTo(new MutableBoundingBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeAir(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, Blocks.AIR.getDefaultState());
      mutableBoundingBoxIn.expandTo(new MutableBoundingBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean setLeaf(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, treeFeatureConfigIn.leavesProvider.getBlockState(random, blockPos));
      mutableBoundingBoxIn.expandTo(new MutableBoundingBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeVine(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BlockState vineState) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, vineState);
      mutableBoundingBoxIn.expandTo(new MutableBoundingBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  private BlockState getRandomizedVine(Random random, BlockPos blockPos, BaseSlimeTreeFeatureConfig config) {
    BlockState state = config.vinesProvider.getBlockState(random, blockPos);

    BooleanProperty[] sides = new BooleanProperty[] { VineBlock.NORTH, VineBlock.EAST, VineBlock.SOUTH, VineBlock.WEST };

    for (BooleanProperty side : sides) {
      state = state.with(side, false);
    }

    for (int i = random.nextInt(3) + 1; i > 0; i--) {
      state = state.with(sides[random.nextInt(sides.length)], true);
    }

    return state;
  }

  public static boolean isEmptyOrLogAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return isReplaceableAt(reader, blockPos) || reader.hasBlockState(blockPos, (p_236417_0_) -> p_236417_0_.isIn(BlockTags.LOGS));
  }

  private static boolean isVineAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return reader.hasBlockState(blockPos, (p_236415_0_) -> p_236415_0_.isIn(Blocks.VINE));
  }

  private static boolean isWaterAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return reader.hasBlockState(blockPos, (p_236413_0_) -> p_236413_0_.isIn(Blocks.WATER));
  }

  public static boolean isAirOrLeavesAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return reader.hasBlockState(blockPos, (p_236411_0_) -> p_236411_0_.isAir() || p_236411_0_.isIn(BlockTags.LEAVES));
  }

  private static boolean isDirtOrFarmlandAt(IWorldGenerationBaseReader p_236418_0_, BlockPos blockPos) {
    return p_236418_0_.hasBlockState(blockPos, (p_236409_0_) -> {
      Block block = p_236409_0_.getBlock();
      return (TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block));
    });
  }

  private static boolean isTallPlantAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return reader.hasBlockState(blockPos, (p_236406_0_) -> {
      Material material = p_236406_0_.getMaterial();
      return material == Material.TALL_PLANTS;
    });
  }

  public static boolean isReplaceableAt(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return isAirOrLeavesAt(reader, blockPos) || isTallPlantAt(reader, blockPos) || isWaterAt(reader, blockPos);
  }

  public static void setBlockStateAt(IWorldWriter writer, BlockPos blockPos, BlockState state) {
    writer.setBlockState(blockPos, state, 19);
  }

  private VoxelShapePart func_236403_a_(IWorld world, MutableBoundingBox boundingBox, Set<BlockPos> logs, Set<BlockPos> leaves) {
    List<Set<BlockPos>> list = Lists.newArrayList();
    VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(boundingBox.getXSize(), boundingBox.getYSize(), boundingBox.getZSize());
    int i = 6;

    for (int j = 0; j < 6; ++j) {
      list.add(Sets.newHashSet());
    }

    BlockPos.Mutable mutable = new BlockPos.Mutable();

    for (BlockPos blockpos : Lists.newArrayList(leaves)) {
      if (boundingBox.isVecInside(blockpos)) {
        voxelshapepart.setFilled(blockpos.getX() - boundingBox.minX, blockpos.getY() - boundingBox.minY, blockpos.getZ() - boundingBox.minZ, true, true);
      }
    }

    for (BlockPos blockpos1 : Lists.newArrayList(logs)) {
      if (boundingBox.isVecInside(blockpos1)) {
        voxelshapepart.setFilled(blockpos1.getX() - boundingBox.minX, blockpos1.getY() - boundingBox.minY, blockpos1.getZ() - boundingBox.minZ, true, true);
      }

      for (Direction direction : Direction.values()) {
        mutable.setAndMove(blockpos1, direction);
        if (!logs.contains(mutable)) {
          BlockState blockstate = world.getBlockState(mutable);
          if (blockstate.hasProperty(BlockStateProperties.DISTANCE_1_7)) {
            list.get(0).add(mutable.toImmutable());
            setBlockStateAt(world, mutable, blockstate.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(1)));
            if (boundingBox.isVecInside(mutable)) {
              voxelshapepart.setFilled(mutable.getX() - boundingBox.minX, mutable.getY() - boundingBox.minY, mutable.getZ() - boundingBox.minZ, true, true);
            }
          }
        }
      }
    }

    for (int l = 1; l < 6; ++l) {
      Set<BlockPos> set = list.get(l - 1);
      Set<BlockPos> set1 = list.get(l);

      for (BlockPos blockpos2 : set) {
        if (boundingBox.isVecInside(blockpos2)) {
          voxelshapepart.setFilled(blockpos2.getX() - boundingBox.minX, blockpos2.getY() - boundingBox.minY, blockpos2.getZ() - boundingBox.minZ, true, true);
        }

        for (Direction direction1 : Direction.values()) {
          mutable.setAndMove(blockpos2, direction1);
          if (!set.contains(mutable) && !set1.contains(mutable)) {
            BlockState blockstate1 = world.getBlockState(mutable);
            if (blockstate1.hasProperty(BlockStateProperties.DISTANCE_1_7)) {
              int k = blockstate1.get(BlockStateProperties.DISTANCE_1_7);
              if (k > l + 1) {
                BlockState blockstate2 = blockstate1.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(l + 1));
                setBlockStateAt(world, mutable, blockstate2);
                if (boundingBox.isVecInside(mutable)) {
                  voxelshapepart.setFilled(mutable.getX() - boundingBox.minX, mutable.getY() - boundingBox.minY, mutable.getZ() - boundingBox.minZ, true, true);
                }

                set1.add(mutable.toImmutable());
              }
            }
          }
        }
      }
    }

    return voxelshapepart;
  }
}
