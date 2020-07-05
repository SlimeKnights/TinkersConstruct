package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.world.TinkerWorld;
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
    this.addSmeltery();
  }

  private void addCommon() {
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS).add(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT, TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE, TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN, TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON, TinkerTags.Blocks.STORAGE_BLOCKS_COPPER, TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD);
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT).add(TinkerMaterials.cobaltBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE).add(TinkerMaterials.arditeBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN).add(TinkerMaterials.manyullynBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME).add(TinkerMaterials.knightSlimeBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON).add(TinkerMaterials.pigironBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_COPPER).add(TinkerMaterials.copperBlock.get());
    this.getBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD).add(TinkerMaterials.roseGoldBlock.get());

    this.getBuilder(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.getBuilder(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addColored(getBuilder(Tags.Blocks.STAINED_GLASS)::add, Tags.Blocks.GLASS, "{color}_clear_stained_glass");
    addColored(getBuilder(Tags.Blocks.STAINED_GLASS_PANES)::add, Tags.Blocks.GLASS_PANES, "{color}_clear_stained_glass_pane");
  }

  private void addWorld() {
    this.getBuilder(TinkerTags.Blocks.SLIMY_LOGS).add(TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.GREEN), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.BLUE), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.PURPLE), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.BLOOD), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.MAGMA));
    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      this.getBuilder(TinkerTags.Blocks.SLIMY_LEAVES).add(TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLUE), TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.PURPLE), TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ORANGE));
      this.getBuilder(TinkerTags.Blocks.SLIMY_SAPLINGS).add(TinkerWorld.slimeSapling.get(type));
    }

    this.getBuilder(Tags.Blocks.ORES).add(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Blocks.ORES_ARDITE, TinkerTags.Blocks.ORES_COPPER);
    this.getBuilder(TinkerTags.Blocks.ORES_COBALT).add(TinkerWorld.cobaltOre.get());
    this.getBuilder(TinkerTags.Blocks.ORES_ARDITE).add(TinkerWorld.arditeOre.get());
    this.getBuilder(TinkerTags.Blocks.ORES_COPPER).add(TinkerWorld.copperOre.get());
  }

  private void addGadgets() {
    this.getBuilder(BlockTags.RAILS).add(TinkerGadgets.woodenRail.get(), TinkerGadgets.woodenDropperRail.get());
  }

  private void addSmeltery() {
    this.getBuilder(TinkerTags.Blocks.SEARED_BRICKS).add(
      TinkerSmeltery.searedBricks.get(),
      TinkerSmeltery.searedFancyBricks.get(),
      TinkerSmeltery.searedSquareBricks.get(),
      TinkerSmeltery.searedSmallBricks.get(),
      TinkerSmeltery.searedTriangleBricks.get(),
      TinkerSmeltery.searedRoad.get());
    this.getBuilder(TinkerTags.Blocks.SMOOTH_SEARED_BLOCKS).add(
      TinkerSmeltery.searedPaver.get(),
      TinkerSmeltery.searedCreeper.get(),
      TinkerSmeltery.searedTile.get());
    this.getBuilder(TinkerTags.Blocks.SEARED_BLOCKS).add(
      TinkerSmeltery.searedStone.get(),
      TinkerSmeltery.searedCrackedBricks.get(),
      TinkerSmeltery.searedCobble.get()).add(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Blocks.SMOOTH_SEARED_BLOCKS);
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
        throw new IllegalStateException("Unknown tconstruct block: " + key.toString());
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
      return (Tag<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

}
