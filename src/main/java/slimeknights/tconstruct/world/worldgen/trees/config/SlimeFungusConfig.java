package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.world.gen.feature.HugeFungusConfig;

/**
 * Extension of huge fungus config that replaces the ground state with a ground tag
 */
public class SlimeFungusConfig extends HugeFungusConfig {
  public static final Codec<HugeFungusConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ITag.getTagCodec(() -> TagCollectionManager.getManager().getBlockTags()).fieldOf("valid_base").forGetter(
      config -> config instanceof SlimeFungusConfig ? ((SlimeFungusConfig)config).getGroundTag() : BlockTags.NYLIUM),
    BlockState.CODEC.fieldOf("stem_state").forGetter(config -> config.stemState),
    BlockState.CODEC.fieldOf("hat_state").forGetter(config -> config.hatState),
    BlockState.CODEC.fieldOf("decor_state").forGetter(config -> config.decorState),
    Codec.BOOL.fieldOf("planted").orElse(false).forGetter(config -> config.planted)
  ).apply(instance, SlimeFungusConfig::new));
  @Getter
  private final ITag<Block> groundTag;
  public SlimeFungusConfig(ITag<Block> groundTag, BlockState stem, BlockState hat, BlockState decor, boolean planted) {
    super(Blocks.AIR.getDefaultState(), stem, hat, decor, planted);
    this.groundTag = groundTag;
  }
}
