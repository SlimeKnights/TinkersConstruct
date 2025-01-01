package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeVineBlock;

import javax.annotation.Nullable;
import java.util.Optional;

public class IslandPiece extends TemplateStructurePiece {
  @Nullable
  private final ConfiguredFeature<?,?> tree;
  private final IslandStructure structure;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

  public IslandPiece(StructureTemplateManager manager, IslandStructure structure, ResourceLocation templateName, BlockPos templatePos, @Nullable ConfiguredFeature<?,?> tree, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.islandPiece.get(), 0, manager, templateName, templateName.toString(), makeSettings(rotation, mirror), templatePos);
    this.structure = structure;
    this.numberOfTreesPlaced = 0;
    this.tree = tree;
  }

  public IslandPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
    super(TinkerStructures.islandPiece.get(), nbt, context.structureTemplateManager(), id -> makeSettings(Rotation.valueOf(nbt.getString("Rot")), Mirror.valueOf(nbt.getString("Mi"))));
    RegistryAccess access = context.registryAccess();
    if (find(access.registryOrThrow(Registries.STRUCTURE), nbt.getString("Structure")) instanceof IslandStructure island) {
      this.structure = island;
    } else  {
      this.structure = null;
    }
    this.tree = find(access.registryOrThrow(Registries.CONFIGURED_FEATURE), nbt.getString("Tree"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
  }

  private static StructurePlaceSettings makeSettings(Rotation rotation, Mirror mirror) {
    return new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setRotation(rotation).setMirror(mirror);
  }

  @Override
  protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
    super.addAdditionalSaveData(context, tag);
    RegistryAccess access = context.registryAccess();
    ResourceLocation structure = access.registryOrThrow(Registries.STRUCTURE).getKey(this.structure);
    if (structure != null) {
      tag.putString("Structure", structure.toString());
    }
    tag.putString("Rot", this.placeSettings.getRotation().name());
    tag.putString("Mi", this.placeSettings.getMirror().name());
    tag.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
    if (tree != null) {
      ResourceLocation key = access.registryOrThrow(Registries.CONFIGURED_FEATURE).getKey(tree);
      if (key != null) {
        tag.putString("Tree", key.toString());
      }
    }
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, RandomSource rand, BoundingBox sbb) {
    switch (function) {
      case "tconstruct:slime_vine" -> {
        Block vines = this.structure.getVines();
        if (vines != null && rand.nextBoolean()) {
          placeVine(level, pos, rand, vines.defaultBlockState());
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
          Optional<Block> plant = this.structure.getGrasses().getRandomValue(rand);
          if (plant.isPresent()) {
            Block block = plant.get();
            BlockState state = block.defaultBlockState();
            if (block instanceof BushBlock bush && bush.canSurvive(state, level, pos)) {
              level.setBlock(pos, state, 2);
            }
          }
        }
      }
    }
  }

  private static void placeVine(LevelAccessor worldIn, BlockPos pos, RandomSource random, BlockState vineToPlace) {
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
  public void postProcess(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, RandomSource rand, BoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    // TODO: previously sea islands canceled if not enough water, but that leads to undesirable behavior as the structure "exists" but has no pieces
    // find if there is another place that can handle the check or ditch it
//    if (true || this.structure.getPlacement().isPositionValid(world, this.templatePosition, generator)) {
    super.postProcess(world, manager, generator, rand, bounds, chunk, pos);
//    }
  }


  /* Registry helpers, perhaps put somewhere better? */

  /** Gets a registry, or falls back to builtin */
  private static <T> Registry<T> getRegistry(ResourceKey<? extends Registry<T>> registryKey, Registry<T> builtIn, StructurePieceSerializationContext context) {
    Optional<? extends Registry<T>> registry = context.registryAccess().registry(registryKey);
    if (registry.isPresent()) {
      return registry.get();
    } else {
      return builtIn;
    }
  }

  /** Finds a registry object */
  @Nullable
  private static <T> T find(Registry<T> registry, String key) {
    ResourceLocation id = ResourceLocation.tryParse(key);
    if (id != null) {
      return registry.get(id);
    }
    return null;
  }
}
