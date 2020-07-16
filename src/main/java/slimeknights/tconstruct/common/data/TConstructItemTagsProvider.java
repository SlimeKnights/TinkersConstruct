package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerMaterials;

import java.util.Locale;
import java.util.Set;

public class TConstructItemTagsProvider extends ItemTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructItemTagsProvider(DataGenerator generatorIn) {
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

    this.getBuilder(Tags.Items.SLIMEBALLS).add(TinkerTags.Items.BLUE_SLIMEBALL, TinkerTags.Items.PURPLE_SLIMEBALL, TinkerTags.Items.BLOOD_SLIMEBALL, TinkerTags.Items.MAGMA_SLIMEBALL, TinkerTags.Items.PINK_SLIMEBALL);
    this.getBuilder(TinkerTags.Items.GREEN_SLIMEBALL).add(Items.SLIME_BALL);
    this.getBuilder(TinkerTags.Items.BLUE_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.BLUE));
    this.getBuilder(TinkerTags.Items.PURPLE_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.PURPLE));
    this.getBuilder(TinkerTags.Items.BLOOD_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.BLOOD));
    this.getBuilder(TinkerTags.Items.MAGMA_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.MAGMA));
    this.getBuilder(TinkerTags.Items.PINK_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.PINK));

    this.getBuilder(Tags.Items.INGOTS).add(TinkerTags.Items.INGOTS_COBALT, TinkerTags.Items.INGOTS_ARDITE, TinkerTags.Items.INGOTS_MANYULLYN, TinkerTags.Items.INGOTS_KNIGHTSLIME, TinkerTags.Items.INGOTS_PIG_IRON, TinkerTags.Items.INGOTS_COPPER, TinkerTags.Items.INGOTS_ROSE_GOLD);
    this.getBuilder(TinkerTags.Items.INGOTS_COBALT).add(TinkerMaterials.cobaltIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_ARDITE).add(TinkerMaterials.arditeIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_MANYULLYN).add(TinkerMaterials.manyullynIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_KNIGHTSLIME).add(TinkerMaterials.knightslimeIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_PIG_IRON).add(TinkerMaterials.pigironIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_COPPER).add(TinkerMaterials.copperIngot.get());
    this.getBuilder(TinkerTags.Items.INGOTS_ROSE_GOLD).add(TinkerMaterials.roseGoldIngot.get());

    this.getBuilder(Tags.Items.NUGGETS).add(TinkerTags.Items.NUGGETS_COBALT, TinkerTags.Items.NUGGETS_ARDITE, TinkerTags.Items.NUGGETS_MANYULLYN, TinkerTags.Items.NUGGETS_KNIGHTSLIME, TinkerTags.Items.NUGGETS_PIG_IRON, TinkerTags.Items.NUGGETS_COPPER, TinkerTags.Items.NUGGETS_ROSE_GOLD);
    this.getBuilder(TinkerTags.Items.NUGGETS_COBALT).add(TinkerMaterials.cobaltNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_ARDITE).add(TinkerMaterials.arditeNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_MANYULLYN).add(TinkerMaterials.manyullynNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_KNIGHTSLIME).add(TinkerMaterials.knightslimeNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_PIG_IRON).add(TinkerMaterials.pigironNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_COPPER).add(TinkerMaterials.copperNugget.get());
    this.getBuilder(TinkerTags.Items.NUGGETS_ROSE_GOLD).add(TinkerMaterials.roseGoldNugget.get());

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
    this.getBuilder(Tags.Items.RODS).add(TinkerTags.Items.RODS_STONE);
    this.getBuilder(TinkerTags.Items.RODS_STONE).add(TinkerGadgets.stoneStick.get());
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SMOOTH_SEARED_BLOCKS, TinkerTags.Items.SMOOTH_SEARED_BLOCKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);
    this.getBuilder(TinkerTags.Items.CASTS).add(
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
  private void copyColored(Tag<Block> blockGroup, Tag<Item> itemGroup) {
    String blockPre = blockGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
    String itemPre = itemGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
    for (DyeColor color : DyeColor.values()) {
      Tag<Block> from = getForgeBlockTag(blockPre + color.getTranslationKey());
      Tag<Item> to = getForgeItemTag(itemPre + color.getTranslationKey());
      copy(from, to);
    }
    copy(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private Tag<Block> getForgeBlockTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (Tag<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private Tag<Item> getForgeItemTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (Tag<Item>) Tags.Items.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
    }
  }

}
