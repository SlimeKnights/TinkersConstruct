package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class SlimeIslandPiece extends TemplateStructurePiece {
  @Nullable
  private final ConfiguredFeature<?,?> tree;
  private final IIslandVariant variant;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;


  private SlimeIslandPiece(StructureManager manager, IIslandVariant variant, ResourceLocation templateName, BlockPos templatePos, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.slimeIslandPiece.get(), 0, manager, templateName, templateName.toString(), makeSettings(rotation, mirror), templatePos);
    this.variant = variant;
    this.numberOfTreesPlaced = 0;
    this.tree = tree;
  }

  public SlimeIslandPiece(StructureManager manager, IIslandVariant variant, String templateName, BlockPos templatePos, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation, Mirror mirror) {
    this(manager, variant, variant.getStructureName(templateName), templatePos, tree, rotation, mirror);
  }

  public SlimeIslandPiece(StructureManager templateManager, CompoundTag nbt) {
    super(TinkerStructures.slimeIslandPiece.get(), nbt, templateManager, context -> makeSettings(Rotation.valueOf(nbt.getString("Rot")), Mirror.valueOf(nbt.getString("Mi"))));
    this.variant = IslandVariants.getVariantFromIndex(nbt.getInt("Variant"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    this.tree = Optional.of(nbt.getString("Tree"))
                        .filter(s -> !s.isEmpty())
                        .map(ResourceLocation::tryParse)
                        .flatMap(BuiltinRegistries.CONFIGURED_FEATURE::getOptional)
                        .orElse(null);
  }

  private static StructurePlaceSettings makeSettings(Rotation rotation, Mirror mirror) {
    return new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setRotation(rotation).setMirror(mirror);
  }

  @Override
  protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag tag) {
    super.addAdditionalSaveData(pContext, tag);
    tag.putInt("Variant", this.variant.getIndex());
    tag.putString("Rot", this.placeSettings.getRotation().name());
    tag.putString("Mi", this.placeSettings.getMirror().name());
    tag.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
    if (tree != null) {
      ResourceLocation key = BuiltinRegistries.CONFIGURED_FEATURE.getKey(tree);
      if (key != null) {
        tag.putString("Tree", key.toString());
      }
    }
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, Random rand, BoundingBox sbb) {
    switch (function) {
      case "tconstruct:lake_bottom" -> level.setBlock(pos, this.variant.getLakeBottom(), 2);
      case "tconstruct:slime_fluid" -> level.setBlock(pos, this.variant.getLakeFluid(), 2);
      case "tconstruct:congealed_slime" -> level.setBlock(pos, this.variant.getCongealedSlime(rand), 2);
      case "tconstruct:slime_vine" -> {
        BlockState vines = this.variant.getVines();
        if (vines != null && rand.nextBoolean()) {
          placeVine(level, pos, rand, vines);
        }
      }
      case "tconstruct:slime_tree" -> {
        if (tree != null && this.numberOfTreesPlaced < 3 && rand.nextBoolean() && level instanceof WorldGenLevel worldgenLevel) {
          if (tree.place(worldgenLevel, this.chunkGenerator, rand, pos)) {
            this.numberOfTreesPlaced++;
          }
        }
      }
      case "tconstruct:slime_tall_grass" -> {
        if (rand.nextBoolean()) {
          BlockState state = this.variant.getPlant(rand);
          if (state != null && state.getBlock() instanceof BushBlock bush && bush.canSurvive(state, level, pos)) {
            level.setBlock(pos, state, 2);
          }
        }
      }
    }
  }

  private static void placeVine(LevelAccessor worldIn, BlockPos pos, Random random, BlockState vineToPlace) {
    for (Direction direction : Direction.values()) {
      if (direction != Direction.DOWN && SlimeVineBlock.isAcceptableNeighbour(worldIn, pos.relative(direction), direction)) {
        worldIn.setBlock(pos, vineToPlace.setValue(SlimeVineBlock.getPropertyForFace(direction), Boolean.TRUE), 2);
      }
    }

    // grow the vine a few times to start
    BlockPos vinePos = pos;
    for (int size = random.nextInt(8); size >= 0; size--) {
      BlockState state = worldIn.getBlockState(vinePos);
      if (state.getBlock() instanceof SlimeVineBlock vine) {
        vine.grow(worldIn, random, vinePos, state);
        vinePos = vinePos.below();
      } else {
        break;
      }
    }
  }

  @Override
  public void postProcess(WorldGenLevel world, StructureFeatureManager manager, ChunkGenerator generator, Random rand, BoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    if (this.variant.isPositionValid(world, this.templatePosition, generator)) {
      super.postProcess(world, manager, generator, rand, bounds, chunk, pos);
    }
  }
}
