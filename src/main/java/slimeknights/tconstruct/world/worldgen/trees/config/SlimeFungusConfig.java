package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;

/**
 * Extension of huge fungus config that replaces the ground state with a ground tag
 */
public class SlimeFungusConfig extends HugeFungusConfiguration {
  public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    TagKey.codec(Registries.BLOCK).fieldOf("valid_base").forGetter(
      config -> config instanceof SlimeFungusConfig ? ((SlimeFungusConfig)config).getGroundTag() : BlockTags.NYLIUM),
    BlockState.CODEC.fieldOf("stem_state").forGetter(config -> config.stemState),
    BlockState.CODEC.fieldOf("hat_state").forGetter(config -> config.hatState),
    BlockState.CODEC.fieldOf("decor_state").forGetter(config -> config.decorState),
    BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter(config -> config.replaceableBlocks),
    Codec.BOOL.fieldOf("planted").orElse(false).forGetter(config -> config.planted)
  ).apply(instance, SlimeFungusConfig::new));

  @Getter
  private final TagKey<Block> groundTag;
  public SlimeFungusConfig(TagKey<Block> groundTag, BlockState stem, BlockState hat, BlockState decor, BlockPredicate replaceableBlocks, boolean planted) {
    super(Blocks.AIR.defaultBlockState(), stem, hat, decor, replaceableBlocks, planted);
    this.groundTag = groundTag;
  }
}
