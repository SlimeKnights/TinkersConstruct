package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
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

  public SlimeIslandPiece(TemplateManager templateManager, IIslandVariant variant, String templateName, BlockPos templatePosition, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation) {
    this(templateManager, variant, templateName, templatePosition, tree, rotation, Mirror.NONE);
  }

  public SlimeIslandPiece(TemplateManager templateManager, IIslandVariant variant, String templateName, BlockPos templatePosition, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.slimeIslandPiece, 0);
    this.templateName = templateName;
    this.variant = variant;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.tree = tree;
    this.loadTemplate(templateManager);
  }

  public SlimeIslandPiece(TemplateManager templateManager, CompoundNBT nbt) {
    super(TinkerStructures.slimeIslandPiece, nbt);
    this.templateName = nbt.getString("Template");
    this.variant = IslandVariants.getVariantFromIndex(nbt.getInt("Variant"));
    this.rotation = Rotation.valueOf(nbt.getString("Rot"));
    this.mirror = Mirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    ResourceLocation tree = ResourceLocation.tryCreate(nbt.getString("Tree"));
    this.tree = Optional.of(nbt.getString("Tree"))
                        .filter(s -> !s.isEmpty())
                        .map(ResourceLocation::tryCreate)
                        .flatMap(WorldGenRegistries.CONFIGURED_FEATURE::getOptional)
                        .orElse(null);
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(TemplateManager templateManager) {
    Template template = templateManager.getTemplateDefaulted(this.variant.getStructureName(this.templateName));
    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(this.variant.getStructureProcessor());
    this.setup(template, this.templatePosition, placementsettings);
  }

  @Override
  protected void readAdditional(CompoundNBT tagCompound) {
    super.readAdditional(tagCompound);
    tagCompound.putString("Template", this.templateName);
    tagCompound.putInt("Variant", this.variant.getIndex());
    tagCompound.putString("Rot", this.placeSettings.getRotation().name());
    tagCompound.putString("Mi", this.placeSettings.getMirror().name());
    tagCompound.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
    if (tree != null) {
      ResourceLocation key = WorldGenRegistries.CONFIGURED_FEATURE.getKey(tree);
      if (key != null) {
        tagCompound.putString("Tree", key.toString());
      }
    }
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
    switch (function) {
      case "tconstruct:lake_bottom":
        worldIn.setBlockState(pos, this.variant.getLakeBottom(), 2);
        break;
      case "tconstruct:slime_fluid":
        worldIn.setBlockState(pos, this.variant.getLakeFluid(), 2);
        break;
      case "tconstruct:congealed_slime":
        worldIn.setBlockState(pos, this.variant.getCongealedSlime(rand), 2);
        break;
      case "tconstruct:slime_vine": {
        BlockState vines = this.variant.getVines();
        if (vines != null) {
          if (rand.nextBoolean()) {
            this.placeVine(worldIn, pos, rand, vines);
          }
        }
        break;
      }
      case "tconstruct:slime_tree":
        if (tree != null && this.numberOfTreesPlaced < 3 && rand.nextBoolean()) {
          if (worldIn instanceof ISeedReader) {
            ISeedReader seedReader = (ISeedReader) worldIn;
            if (tree.generate(seedReader, this.chunkGenerator, rand, pos)) {
              this.numberOfTreesPlaced++;
            }
          }
        }

        break;
      case "tconstruct:slime_tall_grass":
        if (rand.nextBoolean()) {
          BlockState state = this.variant.getPlant(rand);
          if (state != null && state.getBlock() instanceof BushBlock && ((BushBlock) state.getBlock()).isValidPosition(state, worldIn, pos)) {
            worldIn.setBlockState(pos, state, 2);
          }
        }
        break;
    }
  }

  private void placeVine(IWorld worldIn, BlockPos pos, Random random, BlockState vineToPlace) {
    for (Direction direction : Direction.values()) {
      if (direction != Direction.DOWN && SlimeVineBlock.canAttachTo(worldIn, pos.offset(direction), direction)) {
        worldIn.setBlockState(pos, vineToPlace.with(SlimeVineBlock.getPropertyFor(direction), Boolean.TRUE), 2);
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
  public boolean func_230383_a_(ISeedReader world, StructureManager manager, ChunkGenerator generator, Random rand, MutableBoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    if (this.variant.isPositionValid(world, this.templatePosition, generator)) {
      return super.func_230383_a_(world, manager, generator, rand, bounds, chunk, pos);
    }
    return false;
  }
}
