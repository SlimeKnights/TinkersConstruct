package slimeknights.tconstruct.world.worldgen.trees;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/** Extension of {@link MangroveRootPlacer} to allow more root variants */
public class ExtraRootVariantPlacer extends MangroveRootPlacer {
  public static final Codec<ExtraRootVariantPlacer> CODEC = RecordCodecBuilder.create(inst -> rootPlacerParts(inst).and(inst.group(
    MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter(p -> p.mangroveRootPlacement),
    RootVariant.CODEC.listOf().fieldOf("root_variants").forGetter(p -> p.rootVariants))).apply(inst, ExtraRootVariantPlacer::new));

  private final List<RootVariant> rootVariants;
  public ExtraRootVariantPlacer(IntProvider pTrunkOffset, BlockStateProvider pRootProvider, Optional<AboveRootPlacement> pAboveRootPlacement, MangroveRootPlacement mangrovePlacement, List<RootVariant> rootVariants) {
    super(pTrunkOffset, pRootProvider, pAboveRootPlacement, mangrovePlacement);
    this.rootVariants = rootVariants;
  }

  @Override
  protected RootPlacerType<?> type() {
    return TinkerStructures.extraRootVariantPlacer.get();
  }

  @Override
  protected void placeRoot(LevelSimulatedReader level, BiConsumer<BlockPos,BlockState> placer, RandomSource pRandom, BlockPos pos, TreeConfiguration pTreeConfig) {
    for (RootVariant variant : rootVariants) {
      if (level.isStateAtPosition(pos, variant)) {
        placer.accept(pos, this.getPotentiallyWaterloggedState(level, pos, variant.state.getState(pRandom, pos)));
        return;
      }
    }
    super.placeRoot(level, placer, pRandom, pos, pTreeConfig);
  }

  /** Variant of roots to replace in the tree */
  public record RootVariant(HolderSet<Block> holder, BlockStateProvider state) implements Predicate<BlockState> {
    public static final Codec<RootVariant> CODEC = RecordCodecBuilder.create(inst -> inst.group(
      RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("matches").forGetter(RootVariant::holder),
      BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(RootVariant::state)).apply(inst, RootVariant::new));

    @Override
    public boolean test(BlockState state) {
      return state.is(holder);
    }
  }

  /** Creates a new builder for a placer */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for mangrove style roots */
  @Setter
  @Accessors(fluent = true)
  public static class Builder {
    private IntProvider trunkOffset = null;
    @Setter
    private BlockStateProvider roots = null;
    @Nullable
    private AboveRootPlacement aboveRootPlacement = null;
    private HolderSet<Block> canGrowThrough = null;
    private int maxRootWidth = 8;
    private int maxRootLength = 15;
    private float randomSkewChance = 0.2f;
    private final ImmutableList.Builder<RootVariant> rootVariants = ImmutableList.builder();

    private Builder() {}

    /** Sets the root to a block */
    @CanIgnoreReturnValue
    public Builder rootBlock(Block block) {
      return roots(BlockStateProvider.simple(block));
    }

    /**
     * Adds a new root variant
     * @param holder  Replacement condition
     * @param state   Replacement state
     * @return  Builder instance
     */
    @CanIgnoreReturnValue
    public Builder rootVariant(HolderSet<Block> holder, BlockStateProvider state) {
      rootVariants.add(new RootVariant(holder, state));
      return this;
    }

    /**
     * Creates a root variant with most common configuration
     * @param rootVariant  Root variant
     * @param replace      Block to replace
     * @return  Builder instance
     */
    @SuppressWarnings("deprecation")  // no idea why mojang deprecates the one API for that thing
    @CanIgnoreReturnValue
    public Builder rootVariant(Block rootVariant, Block... replace) {
      return rootVariant(HolderSet.direct(Block::builtInRegistryHolder, replace), BlockStateProvider.simple(rootVariant));
    }

    /**
     * Adds root variants for slime types
     * @param slimyRoots  Slimy roots to add
     * @return  Builder instance
     */
    @CanIgnoreReturnValue
    public Builder slimyRoots(EnumObject<SlimeType,Block> slimyRoots) {
      slimyRoots.forEach((type, block) -> rootVariant(block, block, TinkerWorld.congealedSlime.get(type)));
      return this;
    }

    /** Sets the blocks that the roots can grow though */
    @SuppressWarnings("deprecation")
    @CanIgnoreReturnValue
    public Builder canGrowThroughTag(TagKey<Block> tag) {
      return canGrowThrough(BuiltInRegistries.BLOCK.getOrCreateTag(tag));
    }

    /** Builds the final placer */
    public MangroveRootPlacer build() {
      if (trunkOffset == null) throw new IllegalStateException("Must set trunk offset");
      if (roots == null) throw new IllegalStateException("Must set roots");
      if (canGrowThrough == null) throw new IllegalStateException("Must set can grow through");
      List<RootVariant> rootVariants = this.rootVariants.build();
      if (rootVariants.isEmpty()) throw new IllegalStateException("Must have at least one root variant");
      RootVariant first = rootVariants.get(0);
      MangroveRootPlacement mangrovePlacement = new MangroveRootPlacement(canGrowThrough, first.holder, first.state, maxRootWidth, maxRootLength, randomSkewChance);
      if (rootVariants.size() == 1) {
        return new MangroveRootPlacer(trunkOffset, roots, Optional.ofNullable(aboveRootPlacement), mangrovePlacement);
      }
      return new ExtraRootVariantPlacer(trunkOffset, roots, Optional.ofNullable(aboveRootPlacement), mangrovePlacement, rootVariants.stream().skip(1).toList());
    }

    /** Builds the placer into an optional */
    public Optional<RootPlacer> buildOptional() {
      return Optional.of(build());
    }
  }
}
