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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class SlimeIslandPiece extends TemplateStructurePiece {
  private static final Random TREE_RANDOM = new Random();

  @Nullable
  private final ConfiguredFeature<?,?> tree;
  private final String templateName;
  private final IIslandVariant variant;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

  public SlimeIslandPiece(StructureManager templateManager, IIslandVariant variant, String templateName, BlockPos templatePosition, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation) {
    this(templateManager, variant, templateName, templatePosition, tree, rotation, Mirror.NONE);
  }

  public SlimeIslandPiece(StructureManager templateManager, IIslandVariant variant, String templateName, BlockPos templatePosition, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.slimeIslandPiece, 0, templateManager, null, templateName, null, templatePosition); // TODO
    this.templateName = templateName;
    this.variant = variant;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.tree = tree;
    this.loadTemplate(templateManager);
  }

  public SlimeIslandPiece(StructureManager templateManager, CompoundTag nbt) {
    super(TinkerStructures.slimeIslandPiece, nbt, templateManager, null); // TODO
    this.templateName = nbt.getString("Template");
    this.variant = IslandVariants.getVariantFromIndex(nbt.getInt("Variant"));
    this.rotation = Rotation.valueOf(nbt.getString("Rot"));
    this.mirror = Mirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    ResourceLocation tree = ResourceLocation.tryParse(nbt.getString("Tree"));
    this.tree = Optional.of(nbt.getString("Tree"))
                        .filter(s -> !s.isEmpty())
                        .map(ResourceLocation::tryParse)
                        .flatMap(BuiltinRegistries.CONFIGURED_FEATURE::getOptional)
                        .orElse(null);
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(StructureManager templateManager) {
    StructureTemplate template = templateManager.getOrCreate(this.variant.getStructureName(this.templateName));
    StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(this.variant.getStructureProcessor());
    //TODO this.setup(template, this.templatePosition, placementsettings);
  }

  //TODO @Override
  protected void addAdditionalSaveData(CompoundTag tagCompound) {
    //super.addAdditionalSaveData(tagCompound);
    tagCompound.putString("Template", this.templateName);
    tagCompound.putInt("Variant", this.variant.getIndex());
    tagCompound.putString("Rot", this.placeSettings.getRotation().name());
    tagCompound.putString("Mi", this.placeSettings.getMirror().name());
    tagCompound.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
    if (tree != null) {
      ResourceLocation key = BuiltinRegistries.CONFIGURED_FEATURE.getKey(tree);
      if (key != null) {
        tagCompound.putString("Tree", key.toString());
      }
    }
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {
    switch (function) {
      case "tconstruct:lake_bottom" -> worldIn.setBlock(pos, this.variant.getLakeBottom(), 2);
      case "tconstruct:slime_fluid" -> worldIn.setBlock(pos, this.variant.getLakeFluid(), 2);
      case "tconstruct:congealed_slime" -> worldIn.setBlock(pos, this.variant.getCongealedSlime(rand), 2);
      case "tconstruct:slime_vine" -> {
        BlockState vines = this.variant.getVines();
        if (vines != null) {
          if (rand.nextBoolean()) {
            this.placeVine(worldIn, pos, rand, vines);
          }
        }
      }
      case "tconstruct:slime_tree" -> {
        if (tree != null && this.numberOfTreesPlaced < 3 && rand.nextBoolean()) {
          if (worldIn instanceof WorldGenLevel seedReader) {
            if (tree.place(seedReader, this.chunkGenerator, rand, pos)) {
              this.numberOfTreesPlaced++;
            }
          }
        }
      }
      case "tconstruct:slime_tall_grass" -> {
        if (rand.nextBoolean()) {
          BlockState state = this.variant.getPlant(rand);
          if (state != null && state.getBlock() instanceof BushBlock && ((BushBlock)state.getBlock()).canSurvive(state, worldIn, pos)) {
            worldIn.setBlock(pos, state, 2);
          }
        }
      }
    }
  }

  private void placeVine(LevelAccessor worldIn, BlockPos pos, Random random, BlockState vineToPlace) {
    for (Direction direction : Direction.values()) {
      if (direction != Direction.DOWN && SlimeVineBlock.isAcceptableNeighbour(worldIn, pos.relative(direction), direction)) {
        worldIn.setBlock(pos, vineToPlace.setValue(SlimeVineBlock.getPropertyForFace(direction), Boolean.TRUE), 2);
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
      vinePos = vinePos.below();
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
