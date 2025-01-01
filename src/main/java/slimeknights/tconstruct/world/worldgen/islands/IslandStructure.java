package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.util.IdExtender.LocationExtender;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.Optional;

/** Base logic for all island variants */
public class IslandStructure extends Structure {
  public static final Codec<IslandStructure> CODEC = RecordCodecBuilder.create(inst ->
    inst.group(settingsCodec(inst)).and(inst.group(
          IslandPlacement.CODEC.fieldOf("placement").forGetter(s -> s.placement),
          SimpleWeightedRandomList.wrappedCodec(ResourceLocation.CODEC).fieldOf("templates").forGetter(s -> s.templates),
          SimpleWeightedRandomList.wrappedCodec(ConfiguredFeature.CODEC).fieldOf("trees").forGetter(s -> s.trees),
          BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("vines").forGetter(s -> s.vines),
          SimpleWeightedRandomList.wrappedCodec(BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("grasses").forGetter(s -> s.grasses)))
        .apply(inst, IslandStructure::new));

  @Getter
  private final IslandPlacement placement;
  private final SimpleWeightedRandomList<ResourceLocation> templates;
  private final SimpleWeightedRandomList<Holder<ConfiguredFeature<?,?>>> trees;
  private final Optional<Block> vines;
  @Getter
  private final SimpleWeightedRandomList<Block> grasses;

  public IslandStructure(StructureSettings settings, IslandPlacement placement, SimpleWeightedRandomList<ResourceLocation> templates, SimpleWeightedRandomList<Holder<ConfiguredFeature<?,?>>> trees, Optional<Block> vines, SimpleWeightedRandomList<Block> grasses) {
    super(settings);
    this.placement = placement;
    this.templates = templates;
    this.trees = trees;
    this.vines = vines;
    this.grasses = grasses;
  }

  @Override
  public StructureType<?> type() {
    return TinkerStructures.island.get();
  }

  /** Gets the vines for this island */
  @Nullable
  public Block getVines() {
    return vines.orElse(null);
  }

  @Override
  public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
    // get height
    // biome check
//    BlockPos targetPos = context.chunkPos().getMiddleBlockPosition(height);
//    if (!context.validBiome().test(generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(targetPos.getX()), QuartPos.fromBlock(targetPos.getY()), QuartPos.fromBlock(targetPos.getZ()), context.randomState().sampler()))) {
//      return Optional.empty();
//    }

    // find variant
    return onTopOfChunkCenter(context, Types.WORLD_SURFACE, builder -> {
      this.generatePieces(builder, context);
     });
  }

  private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
    RandomSource random = context.random();
    Optional<ResourceLocation> template = templates.getRandomValue(random);
    if (template.isPresent()) {
      Rotation rotation = Rotation.getRandom(random);
      int height = placement.getHeight(context.chunkPos(), context.chunkGenerator(), context.heightAccessor(), rotation, random, context.randomState());
      BlockPos targetPos = context.chunkPos().getMiddleBlockPosition(height);
      Mirror mirror = Util.getRandom(Mirror.values(), random);
      builder.addPiece(new IslandPiece(context.structureTemplateManager(), this, template.get(), targetPos, trees.getRandomValue(random).map(Holder::value).orElse(null), rotation, mirror));
    }
  }


  /* Builder */

  /** Creates a builder for a sea based island */
  public static Builder seaBuilder() {
    return new Builder(IslandPlacement.SEA);
  }

  /** Creates a builder for a sky based island */
  public static Builder skyBuilder() {
    return new Builder(IslandPlacement.SKY);
  }

  @SuppressWarnings("UnusedReturnValue")  // its a builder my dude
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };

    private final IslandPlacement placement;
    private final SimpleWeightedRandomList.Builder<ResourceLocation> templates = SimpleWeightedRandomList.builder();
    private final SimpleWeightedRandomList.Builder<Holder<ConfiguredFeature<?,?>>> trees = SimpleWeightedRandomList.builder();
    private final SimpleWeightedRandomList.Builder<Block> grasses = SimpleWeightedRandomList.builder();
    @Nullable
    @Accessors(fluent = true)
    private Block vines;

    /** Adds the given template to the builder */
    public Builder addTemplate(ResourceLocation template, int weight) {
      this.templates.add(template, weight);
      return this;
    }

    /** Adds the default 5 templates around the given prefix to the builder */
    public Builder addDefaultTemplates(ResourceLocation prefix) {
      for (String size : SIZES) {
        addTemplate(LocationExtender.INSTANCE.suffix(prefix, size), 1);
      }
      return this;
    }

    /** Adds a new tree to the builder with the given weight */
    public Builder addTree(Holder<ConfiguredFeature<?,?>> tree, int weight) {
      trees.add(tree, weight);
      return this;
    }

    /** Adds a new grass type to the builder with the given weight */
    public Builder vines(Block block) {
      this.vines = block;
      return this;
    }

    /** Adds a new grass type to the builder with the given weight */
    public Builder vines(RegistryObject<? extends Block> block) {
      return vines(block.get());
    }

    /** Adds a new grass type to the builder with the given weight */
    public Builder addGrass(Block block, int weight) {
      this.grasses.add(block, weight);
      return this;
    }

    /** Adds a new grass type to the builder with the given weight */
    public Builder addGrass(RegistryObject<? extends Block> block, int weight) {
      return addGrass(block.get(), weight);
    }

    /** Adds slimy grass of the given type to the builder */
    public Builder addSlimyGrass(FoliageType foliage) {
      addGrass(TinkerWorld.slimeTallGrass.get(foliage), 7);
      addGrass(TinkerWorld.slimeFern.get(foliage), 1);
      return this;
    }

    /** Builds the final config */
    public IslandStructure build(StructureSettings settings) {
      return new IslandStructure(settings, placement, templates.build(), trees.build(), Optional.ofNullable(vines), grasses.build());
    }
  }
}
