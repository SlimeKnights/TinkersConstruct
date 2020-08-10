package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerMaterials;

import java.util.Locale;
import java.util.Set;

public class TConstructItemTagsProvider extends ItemTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider) {
    super(generatorIn, blockTagProvider);
  }

  @Override
  protected void registerTags() {
    this.addCommon();
    this.addGadgets();
    this.addWorld();
    this.addSmeltery();
  }

  private void addCommon() {
    this.getOrCreateBuilder(Tags.Items.SLIMEBALLS)
        .addTag(TinkerTags.Items.BLUE_SLIMEBALL)
        .addTag(TinkerTags.Items.PURPLE_SLIMEBALL)
        .addTag(TinkerTags.Items.BLOOD_SLIMEBALL)
        .addTag(TinkerTags.Items.MAGMA_SLIMEBALL);
    this.getOrCreateBuilder(TinkerTags.Items.GREEN_SLIMEBALL).add(Items.SLIME_BALL);
    this.getOrCreateBuilder(TinkerTags.Items.BLUE_SLIMEBALL).add(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.BLUE));
    this.getOrCreateBuilder(TinkerTags.Items.PURPLE_SLIMEBALL).add(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.PURPLE));
    this.getOrCreateBuilder(TinkerTags.Items.BLOOD_SLIMEBALL).add(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.BLOOD));
    this.getOrCreateBuilder(TinkerTags.Items.MAGMA_SLIMEBALL).add(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.MAGMA));

    this.getOrCreateBuilder(Tags.Items.INGOTS)
        .addTag(TinkerTags.Items.INGOTS_COBALT)
        .addTag(TinkerTags.Items.INGOTS_ARDITE)
        .addTag(TinkerTags.Items.INGOTS_MANYULLYN)
        .addTag(TinkerTags.Items.INGOTS_KNIGHTSLIME)
        .addTag(TinkerTags.Items.INGOTS_PIG_IRON)
        .addTag(TinkerTags.Items.INGOTS_COPPER)
        .addTag(TinkerTags.Items.INGOTS_ROSE_GOLD);
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_COBALT).add(TinkerMaterials.cobaltIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_ARDITE).add(TinkerMaterials.arditeIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_MANYULLYN).add(TinkerMaterials.manyullynIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_KNIGHTSLIME).add(TinkerMaterials.knightslimeIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_PIG_IRON).add(TinkerMaterials.pigironIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_COPPER).add(TinkerMaterials.copperIngot.get());
    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_ROSE_GOLD).add(TinkerMaterials.roseGoldIngot.get());

    this.getOrCreateBuilder(Tags.Items.NUGGETS)
        .addTag(TinkerTags.Items.NUGGETS_COBALT)
        .addTag(TinkerTags.Items.NUGGETS_ARDITE)
        .addTag(TinkerTags.Items.NUGGETS_MANYULLYN)
        .addTag(TinkerTags.Items.NUGGETS_KNIGHTSLIME)
        .addTag(TinkerTags.Items.NUGGETS_PIG_IRON)
        .addTag(TinkerTags.Items.NUGGETS_COPPER)
        .addTag(TinkerTags.Items.NUGGETS_ROSE_GOLD);
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_COBALT).add(TinkerMaterials.cobaltNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_ARDITE).add(TinkerMaterials.arditeNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_MANYULLYN).add(TinkerMaterials.manyullynNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_KNIGHTSLIME).add(TinkerMaterials.knightslimeNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_PIG_IRON).add(TinkerMaterials.pigironNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_COPPER).add(TinkerMaterials.copperNugget.get());
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_ROSE_GOLD).add(TinkerMaterials.roseGoldNugget.get());

    this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT, TinkerTags.Items.STORAGE_BLOCKS_COBALT);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE, TinkerTags.Items.STORAGE_BLOCKS_ARDITE);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN, TinkerTags.Items.STORAGE_BLOCKS_MANYULLYN);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, TinkerTags.Items.STORAGE_BLOCKS_KNIGHTSLIME);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON, TinkerTags.Items.STORAGE_BLOCKS_PIG_IRON);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_COPPER, TinkerTags.Items.STORAGE_BLOCKS_COPPER);
    this.copy(TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD, TinkerTags.Items.STORAGE_BLOCKS_ROSE_GOLD);

    copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
    copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
    copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
    copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
  }

  private void addWorld() {
    this.copy(TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Items.CONGEALED_SLIME);
    this.copy(TinkerTags.Blocks.SLIMY_LOGS, TinkerTags.Items.SLIMY_LOGS);
    this.copy(TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Items.SLIMY_LEAVES);
    this.copy(TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Items.SLIMY_SAPLINGS);

    this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    this.copy(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Items.ORES_COBALT);
    this.copy(TinkerTags.Blocks.ORES_ARDITE, TinkerTags.Items.ORES_ARDITE);
    this.copy(TinkerTags.Blocks.ORES_COPPER, TinkerTags.Items.ORES_COPPER);
  }

  private void addGadgets() {
    this.copy(BlockTags.RAILS, ItemTags.RAILS);
    this.getOrCreateBuilder(Tags.Items.RODS).addTag(TinkerTags.Items.RODS_STONE);
    this.getOrCreateBuilder(TinkerTags.Items.RODS_STONE).add(TinkerGadgets.stoneStick.get());
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SMOOTH_SEARED_BLOCKS, TinkerTags.Items.SMOOTH_SEARED_BLOCKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);
    this.getOrCreateBuilder(TinkerTags.Items.CASTS).add(
      TinkerSmeltery.blankCast.get(),
      TinkerSmeltery.ingotCast.get(),
      TinkerSmeltery.nuggetCast.get(),
      TinkerSmeltery.gemCast.get(),
      TinkerSmeltery.pickaxeHeadCast.get(),
      TinkerSmeltery.smallBindingCast.get(),
      TinkerSmeltery.toolRodCast.get(),
      TinkerSmeltery.toughToolRodCast.get(),
      TinkerSmeltery.largePlateCast.get(),
      TinkerSmeltery.swordBladeCast.get(),
      TinkerSmeltery.hammerHeadCast.get(),
      TinkerSmeltery.wideGuardCast.get(),
      TinkerSmeltery.shovelHeadCast.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Item Tags";
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  private void copyColored(INamedTag<Block> blockGroup, INamedTag<Item> itemGroup) {
    String blockPre = blockGroup.getName().getPath().toUpperCase(Locale.ENGLISH) + '_';
    String itemPre = itemGroup.getName().getPath().toUpperCase(Locale.ENGLISH) + '_';
    for (DyeColor color : DyeColor.values()) {
      INamedTag<Block> from = getForgeBlockTag(blockPre + color.getTranslationKey());
      INamedTag<Item> to = getForgeItemTag(itemPre + color.getTranslationKey());
      copy(from, to);
    }
    copy(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private INamedTag<Block> getForgeBlockTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (INamedTag<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private INamedTag<Item> getForgeItemTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (INamedTag<Item>) Tags.Items.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
    }
  }

}
