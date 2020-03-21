package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TConstructBlockTagsProvider extends BlockTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructBlockTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  private void addCommon() {
    this.getBuilder(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS).add(Tags.Blocks.STORAGE_BLOCKS_COBALT, Tags.Blocks.STORAGE_BLOCKS_ARDITE, Tags.Blocks.STORAGE_BLOCKS_MANYULLYN, Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, Tags.Blocks.STORAGE_BLOCKS_PIGIRON, Tags.Blocks.STORAGE_BLOCKS_ALUBRASS);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_COBALT).add(CommonBlocks.cobalt_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ARDITE).add(CommonBlocks.ardite_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_MANYULLYN).add(CommonBlocks.manyullyn_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME).add(CommonBlocks.knightslime_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_PIGIRON).add(CommonBlocks.pigiron_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ALUBRASS).add(CommonBlocks.alubrass_block);

    this.getBuilder(net.minecraftforge.common.Tags.Blocks.GLASS_COLORLESS).add(DecorativeBlocks.clear_glass);
    addColored(getBuilder(net.minecraftforge.common.Tags.Blocks.STAINED_GLASS)::add, net.minecraftforge.common.Tags.Blocks.GLASS, "{color}_clear_stained_glass");
  }

  private void addWorld() {
    this.getBuilder(Tags.Blocks.SLIMY_LOGS).add(WorldBlocks.congealed_green_slime, WorldBlocks.congealed_blue_slime, WorldBlocks.congealed_purple_slime, WorldBlocks.congealed_blood_slime, WorldBlocks.congealed_magma_slime);
    this.getBuilder(Tags.Blocks.SLIMY_LEAVES).add(WorldBlocks.blue_slime_leaves, WorldBlocks.purple_slime_leaves, WorldBlocks.orange_slime_leaves);
    this.getBuilder(Tags.Blocks.SLIMY_SAPLINGS).add(WorldBlocks.blue_slime_sapling, WorldBlocks.orange_slime_sapling, WorldBlocks.purple_slime_sapling);

    this.getBuilder(net.minecraftforge.common.Tags.Blocks.ORES).add(Tags.Blocks.ORES_COBALT, Tags.Blocks.ORES_ARDITE);
    this.getBuilder(Tags.Blocks.ORES_COBALT).add(WorldBlocks.cobalt_ore);
    this.getBuilder(Tags.Blocks.ORES_ARDITE).add(WorldBlocks.ardite_ore);
  }

  private void addGadgets() {
  }

  @Override
  public void registerTags() {
    super.registerTags();

    this.filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());

    this.addCommon();
    this.addGadgets();
    this.addWorld();
  }

  @Override
  protected Path makePath(ResourceLocation id) {
    return this.filter != null && this.filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
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
