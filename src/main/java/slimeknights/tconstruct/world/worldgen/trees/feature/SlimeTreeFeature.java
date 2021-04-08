package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
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
  public final boolean generate(StructureWorldAccess seedReader, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BaseSlimeTreeFeatureConfig config) {
    Set<BlockPos> set = Sets.newHashSet();
    Set<BlockPos> set1 = Sets.newHashSet();
    Set<BlockPos> set2 = Sets.newHashSet();
    BlockBox mutableboundingbox = BlockBox.empty();
    boolean flag = this.place(seedReader, random, blockPos, set, set1, mutableboundingbox, config);

    if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !set.isEmpty()) {
      VoxelSet voxelshapepart = this.func_236403_a_(seedReader, mutableboundingbox, set, set2);
      Structure.updateCorner(seedReader, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
      return true;
    }
    else {
      return false;
    }
  }

  private boolean place(ModifiableTestableWorld generationReader, Random rand, BlockPos positionIn, Set<BlockPos> trunkBlockPosSet, Set<BlockPos> p_225557_5_, BlockBox boundingBoxIn, BaseSlimeTreeFeatureConfig configIn) {
    int height = rand.nextInt(configIn.randomHeight) + configIn.baseHeight;

    if (!(generationReader instanceof WorldAccess)) {
      return false;
    }

    BlockPos blockpos;
    if (!configIn.forcePlacement) {
      int oceanFloorHeight = generationReader.getTopPosition(Heightmap.Type.OCEAN_FLOOR, positionIn).getY();

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

  protected void setDirtAt(ModifiableTestableWorld reader, BlockPos pos, BlockPos origin) {
    if (!(reader instanceof WorldAccess)) {
      return;
    }

    ((WorldAccess) reader).getBlockState(pos).getBlock().onPlantGrow(((WorldAccess) reader).getBlockState(pos), (WorldAccess) reader, pos, origin);
  }

  protected void placeTrunk(ModifiableTestableWorld worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    while (treeHeight > 0) {
      this.setLog(worldIn, randomIn, blockPos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);

      blockPos = blockPos.up();
      treeHeight--;
    }
  }

  protected void placeCanopy(ModifiableTestableWorld worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
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

  private void placeDiamondLayer(ModifiableTestableWorld worldIn, Random randomIn, int range, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        if (Math.abs(x) + Math.abs(z) <= range) {
          BlockPos blockpos = blockPos.add(x, 0, z);
          this.setLeaf(worldIn, randomIn, blockpos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
        }
      }
    }
  }

  protected boolean setLog(ModifiableTestableWorld worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, treeFeatureConfigIn.trunkProvider.getBlockState(randomIn, blockPos));
      mutableBoundingBoxIn.encompass(new BlockBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeAir(ModifiableTestableWorld worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, Blocks.AIR.getDefaultState());
      mutableBoundingBoxIn.encompass(new BlockBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean setLeaf(ModifiableTestableWorld worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BaseSlimeTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, treeFeatureConfigIn.leavesProvider.getBlockState(random, blockPos));
      mutableBoundingBoxIn.encompass(new BlockBox(blockPos, blockPos));
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeVine(ModifiableTestableWorld worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, BlockBox mutableBoundingBoxIn, BlockState vineState) {
    if (!isAirOrLeavesAt(worldIn, blockPos)) {
      return false;
    }
    else {
      this.setBlockState(worldIn, blockPos, vineState);
      mutableBoundingBoxIn.encompass(new BlockBox(blockPos, blockPos));
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

  public static boolean isEmptyOrLogAt(TestableWorld reader, BlockPos blockPos) {
    return isReplaceableAt(reader, blockPos) || reader.testBlockState(blockPos, (p_236417_0_) -> p_236417_0_.isIn(BlockTags.LOGS));
  }

  private static boolean isVineAt(TestableWorld reader, BlockPos blockPos) {
    return reader.testBlockState(blockPos, (p_236415_0_) -> p_236415_0_.isOf(Blocks.VINE));
  }

  private static boolean isWaterAt(TestableWorld reader, BlockPos blockPos) {
    return reader.testBlockState(blockPos, (p_236413_0_) -> p_236413_0_.isOf(Blocks.WATER));
  }

  public static boolean isAirOrLeavesAt(TestableWorld reader, BlockPos blockPos) {
    return reader.testBlockState(blockPos, (p_236411_0_) -> p_236411_0_.isAir() || p_236411_0_.isIn(BlockTags.LEAVES));
  }

  private static boolean isDirtOrFarmlandAt(TestableWorld p_236418_0_, BlockPos blockPos) {
    return p_236418_0_.testBlockState(blockPos, (p_236409_0_) -> {
      Block block = p_236409_0_.getBlock();
      return (TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block));
    });
  }

  private static boolean isTallPlantAt(TestableWorld reader, BlockPos blockPos) {
    return reader.testBlockState(blockPos, (p_236406_0_) -> {
      Material material = p_236406_0_.getMaterial();
      return material == Material.REPLACEABLE_PLANT;
    });
  }

  public static boolean isReplaceableAt(TestableWorld reader, BlockPos blockPos) {
    return isAirOrLeavesAt(reader, blockPos) || isTallPlantAt(reader, blockPos) || isWaterAt(reader, blockPos);
  }

  public static void setBlockStateAt(ModifiableWorld writer, BlockPos blockPos, BlockState state) {
    writer.setBlockState(blockPos, state, 19);
  }

  private VoxelSet func_236403_a_(WorldAccess world, BlockBox boundingBox, Set<BlockPos> logs, Set<BlockPos> leaves) {
    List<Set<BlockPos>> list = Lists.newArrayList();
    VoxelSet voxelshapepart = new BitSetVoxelSet(boundingBox.getBlockCountX(), boundingBox.getBlockCountY(), boundingBox.getBlockCountZ());
    int i = 6;

    for (int j = 0; j < 6; ++j) {
      list.add(Sets.newHashSet());
    }

    BlockPos.Mutable mutable = new BlockPos.Mutable();

    for (BlockPos blockpos : Lists.newArrayList(leaves)) {
      if (boundingBox.contains(blockpos)) {
        voxelshapepart.set(blockpos.getX() - boundingBox.minX, blockpos.getY() - boundingBox.minY, blockpos.getZ() - boundingBox.minZ, true, true);
      }
    }

    for (BlockPos blockpos1 : Lists.newArrayList(logs)) {
      if (boundingBox.contains(blockpos1)) {
        voxelshapepart.set(blockpos1.getX() - boundingBox.minX, blockpos1.getY() - boundingBox.minY, blockpos1.getZ() - boundingBox.minZ, true, true);
      }

      for (Direction direction : Direction.values()) {
        mutable.set(blockpos1, direction);
        if (!logs.contains(mutable)) {
          BlockState blockstate = world.getBlockState(mutable);
          if (blockstate.contains(Properties.DISTANCE_1_7)) {
            list.get(0).add(mutable.toImmutable());
            setBlockStateAt(world, mutable, blockstate.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
            if (boundingBox.contains(mutable)) {
              voxelshapepart.set(mutable.getX() - boundingBox.minX, mutable.getY() - boundingBox.minY, mutable.getZ() - boundingBox.minZ, true, true);
            }
          }
        }
      }
    }

    for (int l = 1; l < 6; ++l) {
      Set<BlockPos> set = list.get(l - 1);
      Set<BlockPos> set1 = list.get(l);

      for (BlockPos blockpos2 : set) {
        if (boundingBox.contains(blockpos2)) {
          voxelshapepart.set(blockpos2.getX() - boundingBox.minX, blockpos2.getY() - boundingBox.minY, blockpos2.getZ() - boundingBox.minZ, true, true);
        }

        for (Direction direction1 : Direction.values()) {
          mutable.set(blockpos2, direction1);
          if (!set.contains(mutable) && !set1.contains(mutable)) {
            BlockState blockstate1 = world.getBlockState(mutable);
            if (blockstate1.contains(Properties.DISTANCE_1_7)) {
              int k = blockstate1.get(Properties.DISTANCE_1_7);
              if (k > l + 1) {
                BlockState blockstate2 = blockstate1.with(Properties.DISTANCE_1_7, Integer.valueOf(l + 1));
                setBlockStateAt(world, mutable, blockstate2);
                if (boundingBox.contains(mutable)) {
                  voxelshapepart.set(mutable.getX() - boundingBox.minX, mutable.getY() - boundingBox.minY, mutable.getZ() - boundingBox.minZ, true, true);
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
