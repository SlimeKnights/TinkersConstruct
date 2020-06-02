package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Locale;
import java.util.function.Consumer;

public class TConstructBlockTagsProvider extends BlockTagsProvider {

  public TConstructBlockTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerTags() {
    this.addCommon();
    this.addGadgets();
    this.addWorld();
  }

  private void addCommon() {
    this.getBuilder(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS).add(Tags.Blocks.STORAGE_BLOCKS_COBALT, Tags.Blocks.STORAGE_BLOCKS_ARDITE, Tags.Blocks.STORAGE_BLOCKS_MANYULLYN, Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, Tags.Blocks.STORAGE_BLOCKS_PIGIRON, Tags.Blocks.STORAGE_BLOCKS_ALUBRASS);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_COBALT).add(CommonBlocks.cobalt_block.get());
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ARDITE).add(CommonBlocks.ardite_block.get());
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_MANYULLYN).add(CommonBlocks.manyullyn_block.get());
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME).add(CommonBlocks.knightslime_block.get());
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_PIGIRON).add(CommonBlocks.pigiron_block.get());
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ALUBRASS).add(CommonBlocks.alubrass_block.get());

    this.getBuilder(net.minecraftforge.common.Tags.Blocks.GLASS_COLORLESS).add(DecorativeBlocks.clear_glass.get());
    addColored(getBuilder(net.minecraftforge.common.Tags.Blocks.STAINED_GLASS)::add, net.minecraftforge.common.Tags.Blocks.GLASS, "{color}_clear_stained_glass");
  }

  private void addWorld() {
    this.getBuilder(Tags.Blocks.SLIMY_LOGS).add(WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.BLUE), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.PURPLE), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.BLOOD), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.MAGMA));
    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      this.getBuilder(Tags.Blocks.SLIMY_LEAVES).add(WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.BLUE), WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.PURPLE), WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.ORANGE));
      this.getBuilder(Tags.Blocks.SLIMY_SAPLINGS).add(WorldBlocks.slime_sapling.get(type));
    }

    this.getBuilder(net.minecraftforge.common.Tags.Blocks.ORES).add(Tags.Blocks.ORES_COBALT, Tags.Blocks.ORES_ARDITE);
    this.getBuilder(Tags.Blocks.ORES_COBALT).add(WorldBlocks.cobalt_ore.get());
    this.getBuilder(Tags.Blocks.ORES_ARDITE).add(WorldBlocks.ardite_ore.get());
  }

  private void addGadgets() {
    this.getBuilder(BlockTags.RAILS).add(GadgetBlocks.wooden_rail.get(), GadgetBlocks.wooden_dropper_rail.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /*
  * Credit to forge for this code to generate the tags.
   */
  private void addColored(Consumer<Block> consumer, Tag<Block> group, String pattern) {
    String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
    for (DyeColor color : DyeColor.values()) {
      ResourceLocation key = new ResourceLocation("tconstruct", pattern.replace("{color}", color.getTranslationKey()));
      Tag<Block> tag = getForgeTag(prefix + color.getTranslationKey());
      Block block = ForgeRegistries.BLOCKS.getValue(key);
      if (block == null || block == Blocks.AIR)
        throw new IllegalStateException("Unknown vanilla block: " + key.toString());
      getBuilder(tag).add(block);
      consumer.accept(block);
    }
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private Tag<Block> getForgeTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (Tag<Block>) net.minecraftforge.common.Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(net.minecraftforge.common.Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

}
