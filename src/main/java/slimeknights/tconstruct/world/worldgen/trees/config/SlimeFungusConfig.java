package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;

/**
 * Extension of huge fungus config that replaces the ground state with a ground tag
 */
public class SlimeFungusConfig extends HugeFungusConfiguration {
  public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Tag.codec(() -> SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY)).fieldOf("valid_base").forGetter(
      config -> config instanceof SlimeFungusConfig ? ((SlimeFungusConfig)config).getGroundTag() : BlockTags.NYLIUM),
    BlockState.CODEC.fieldOf("stem_state").forGetter(config -> config.stemState),
    BlockState.CODEC.fieldOf("hat_state").forGetter(config -> config.hatState),
    BlockState.CODEC.fieldOf("decor_state").forGetter(config -> config.decorState),
    Codec.BOOL.fieldOf("planted").orElse(false).forGetter(config -> config.planted)
  ).apply(instance, SlimeFungusConfig::new));

  @Getter
  private final Tag<Block> groundTag;
  public SlimeFungusConfig(Tag<Block> groundTag, BlockState stem, BlockState hat, BlockState decor, boolean planted) {
    super(Blocks.AIR.defaultBlockState(), stem, hat, decor, planted);
    this.groundTag = groundTag;
  }
}
