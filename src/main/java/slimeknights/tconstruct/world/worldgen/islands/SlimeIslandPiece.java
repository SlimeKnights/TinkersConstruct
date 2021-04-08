package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import java.util.Random;

public class SlimeIslandPiece extends SimpleStructurePiece {

  private final String templateName;
  private final SlimeIslandVariant variant;
  private final BlockRotation rotation;
  private final BlockMirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

  public SlimeIslandPiece(StructureManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, BlockRotation rotation) {
    this(templateManager, variant, templateName, templatePosition, rotation, BlockMirror.NONE);
  }

  public SlimeIslandPiece(StructureManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, BlockRotation rotation, BlockMirror mirror) {
    super(TinkerStructures.slimeIslandPiece, 0);
    this.templateName = templateName;
    this.variant = variant;
    this.pos = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.loadTemplate(templateManager);
  }

  public SlimeIslandPiece(StructureManager templateManager, CompoundTag nbt) {
    super(TinkerStructures.slimeIslandPiece, nbt);
    this.templateName = nbt.getString("Template");
    this.variant = SlimeIslandVariant.getVariantFromIndex(nbt.getInt("Variant"));
    this.rotation = BlockRotation.valueOf(nbt.getString("Rot"));
    this.mirror = BlockMirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(StructureManager templateManager) {
    Structure template = templateManager.getStructureOrBlank(new Identifier("tconstruct:slime_islands/" + this.variant.asString() + "/" + this.templateName));
    StructurePlacementData placementsettings = (new StructurePlacementData()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(this.variant.getStructureProcessor());
    this.setStructureData(template, this.pos, placementsettings);
  }

  @Override
  protected void toNbt(CompoundTag tagCompound) {
    super.toNbt(tagCompound);
    tagCompound.putString("Template", this.templateName);
    tagCompound.putInt("Variant", this.variant.getIndex());
    tagCompound.putString("Rot", this.placementData.getRotation().name());
    tagCompound.putString("Mi", this.placementData.getMirror().name());
    tagCompound.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
  }

  @Override
  protected void handleMetadata(String function, BlockPos pos, ServerWorldAccess worldIn, Random rand, BlockBox sbb) {
    switch (function) {
      case "tconstruct:lake_bottom":
        worldIn.setBlockState(pos, this.variant.getLakeBottom(), 2);
        break;
      case "tconstruct:slime_fluid":
        worldIn.setBlockState(pos, this.variant.getLakeFluid(), 2);
        break;
      case "tconstruct:congealed_slime":
        int congealed_slime_random = rand.nextInt(this.variant.getCongealedSlime().length);
        worldIn.setBlockState(pos, this.variant.getCongealedSlime()[congealed_slime_random], 2);
        break;
      case "tconstruct:slime_vine":
        if (this.variant.getVine() != null) {
          if (rand.nextBoolean()) {
            this.placeVine(worldIn, pos, rand, this.variant.getVine());
          }
          else {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
          }
        }
        else {
          worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        }
        break;
      case "tconstruct:slime_tree":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

        if (rand.nextBoolean() && this.numberOfTreesPlaced < 3) {
          ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> treeFeature = this.variant.getConfiguredTreeFeature();

          if (treeFeature != null && worldIn instanceof StructureWorldAccess) {
            StructureWorldAccess seedReader = (StructureWorldAccess) worldIn;
            treeFeature.generate(seedReader, this.chunkGenerator, rand, pos);
          }
        }

        this.numberOfTreesPlaced++;
        break;
      case "tconstruct:slime_tall_grass":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

        if (rand.nextBoolean()) {
          int slime_grass_random = rand.nextInt(this.variant.getTallGrass().length);
          BlockState state = this.variant.getTallGrass()[slime_grass_random];

          if (state.getBlock() instanceof SlimeTallGrassBlock) {
            if (((SlimeTallGrassBlock) state.getBlock()).canPlaceAt(state, worldIn, pos)) {
              worldIn.setBlockState(pos, state, 2);
            }
          }
        }
        break;
    }
  }

  private void placeVine(WorldAccess worldIn, BlockPos pos, Random random, BlockState vineToPlace) {
    for (Direction direction : Direction.values()) {
      if (direction != Direction.DOWN && SlimeVineBlock.shouldConnectTo(worldIn, pos.offset(direction), direction)) {
        worldIn.setBlockState(pos, vineToPlace.with(SlimeVineBlock.getFacingProperty(direction), Boolean.TRUE), 2);
      }
    }

    // grow the vine a few times to start
    BlockPos vinePos = pos;
    for (int size = random.nextInt(8); size >= 0; size--) {
      BlockState state = worldIn.getBlockState(vinePos);
      if (!(state.getBlock() instanceof SlimeVineBlock)) {
        break;
      }
      ((SlimeVineBlock) state.getBlock()).grow(worldIn, random, vinePos, state);
      vinePos = vinePos.down();
    }
  }

  @Override
  public boolean generate(StructureWorldAccess world, StructureAccessor manager, ChunkGenerator generator, Random rand, BlockBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    if (this.variant == SlimeIslandVariant.BLOOD) {
      BlockPos up = this.pos.up();

      if (this.isLava(world, up)) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
          if (!this.isLava(world, up.offset(dir))) {
            return false;
          }
        }

        return super.generate(world, manager, generator, rand, bounds, chunk, pos);
      }

      return false;
    }
    else {
      return super.generate(world, manager, generator, rand, bounds, chunk, pos);
    }
  }

  private boolean isLava(WorldAccess world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }
}
