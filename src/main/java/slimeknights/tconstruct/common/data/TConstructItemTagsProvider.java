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
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.shared.block.SlimeBlock;

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
  }

  private void addCommon() {

    this.getBuilder(net.minecraftforge.common.Tags.Items.SLIMEBALLS).add(Tags.Items.BLUE_SLIMEBALL, Tags.Items.PURPLE_SLIMEBALL, Tags.Items.BLOOD_SLIMEBALL, Tags.Items.MAGMA_SLIMEBALL, Tags.Items.PINK_SLIMEBALL);
    this.getBuilder(Tags.Items.GREEN_SLIMEBALL).add(Items.SLIME_BALL);
    this.getBuilder(Tags.Items.BLUE_SLIMEBALL).add(FoodItems.slime_ball.get(SlimeBlock.SlimeType.BLUE));
    this.getBuilder(Tags.Items.PURPLE_SLIMEBALL).add(FoodItems.slime_ball.get(SlimeBlock.SlimeType.PURPLE));
    this.getBuilder(Tags.Items.BLOOD_SLIMEBALL).add(FoodItems.slime_ball.get(SlimeBlock.SlimeType.BLOOD));
    this.getBuilder(Tags.Items.MAGMA_SLIMEBALL).add(FoodItems.slime_ball.get(SlimeBlock.SlimeType.MAGMA));
    this.getBuilder(Tags.Items.PINK_SLIMEBALL).add(FoodItems.slime_ball.get(SlimeBlock.SlimeType.PINK));

    this.getBuilder(net.minecraftforge.common.Tags.Items.INGOTS).add(Tags.Items.INGOTS_COBALT, Tags.Items.INGOTS_ARDITE, Tags.Items.INGOTS_MANYULLYN, Tags.Items.INGOTS_KNIGHTSLIME, Tags.Items.INGOTS_PIGIRON, Tags.Items.INGOTS_ALUBRASS);
    this.getBuilder(Tags.Items.INGOTS_COBALT).add(CommonItems.cobalt_ingot.get());
    this.getBuilder(Tags.Items.INGOTS_ARDITE).add(CommonItems.ardite_ingot.get());
    this.getBuilder(Tags.Items.INGOTS_MANYULLYN).add(CommonItems.manyullyn_ingot.get());
    this.getBuilder(Tags.Items.INGOTS_KNIGHTSLIME).add(CommonItems.knightslime_ingot.get());
    this.getBuilder(Tags.Items.INGOTS_PIGIRON).add(CommonItems.pigiron_ingot.get());
    this.getBuilder(Tags.Items.INGOTS_ALUBRASS).add(CommonItems.alubrass_ingot.get());

    this.getBuilder(net.minecraftforge.common.Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_COBALT, Tags.Items.NUGGETS_ARDITE, Tags.Items.NUGGETS_MANYULLYN, Tags.Items.NUGGETS_KNIGHTSLIME, Tags.Items.NUGGETS_PIGIRON, Tags.Items.NUGGETS_ALUBRASS);
    this.getBuilder(Tags.Items.NUGGETS_COBALT).add(CommonItems.cobalt_nugget.get());
    this.getBuilder(Tags.Items.NUGGETS_ARDITE).add(CommonItems.ardite_nugget.get());
    this.getBuilder(Tags.Items.NUGGETS_MANYULLYN).add(CommonItems.manyullyn_nugget.get());
    this.getBuilder(Tags.Items.NUGGETS_KNIGHTSLIME).add(CommonItems.knightslime_nugget.get());
    this.getBuilder(Tags.Items.NUGGETS_PIGIRON).add(CommonItems.pigiron_nugget.get());
    this.getBuilder(Tags.Items.NUGGETS_ALUBRASS).add(CommonItems.alubrass_nugget.get());

    this.copy(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS, net.minecraftforge.common.Tags.Items.STORAGE_BLOCKS);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_COBALT, Tags.Items.STORAGE_BLOCKS_COBALT);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_ARDITE, Tags.Items.STORAGE_BLOCKS_ARDITE);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_MANYULLYN, Tags.Items.STORAGE_BLOCKS_MANYULLYN);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, Tags.Items.STORAGE_BLOCKS_KNIGHTSLIME);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_PIGIRON, Tags.Items.STORAGE_BLOCKS_PIGIRON);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_ALUBRASS, Tags.Items.STORAGE_BLOCKS_ALUBRASS);

    copyColored(net.minecraftforge.common.Tags.Blocks.GLASS, net.minecraftforge.common.Tags.Items.GLASS);
    copy(net.minecraftforge.common.Tags.Blocks.STAINED_GLASS, net.minecraftforge.common.Tags.Items.STAINED_GLASS);
  }

  private void addWorld() {
    this.copy(Tags.Blocks.SLIMY_LOGS, Tags.Items.SLIMY_LOGS);
    this.copy(Tags.Blocks.SLIMY_LEAVES, Tags.Items.SLIMY_LEAVES);
    this.copy(Tags.Blocks.SLIMY_SAPLINGS, Tags.Items.SLIMY_SAPLINGS);

    this.copy(net.minecraftforge.common.Tags.Blocks.ORES, net.minecraftforge.common.Tags.Items.ORES);
    this.copy(Tags.Blocks.ORES_COBALT, Tags.Items.ORES_COBALT);
    this.copy(Tags.Blocks.ORES_ARDITE, Tags.Items.ORES_ARDITE);
  }

  private void addGadgets() {
    this.copy(BlockTags.RAILS, ItemTags.RAILS);
    this.getBuilder(net.minecraftforge.common.Tags.Items.RODS).add(Tags.Items.RODS_STONE);
    this.getBuilder(Tags.Items.RODS_STONE).add(GadgetItems.stone_stick.get());
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
      return (Tag<Block>) net.minecraftforge.common.Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(net.minecraftforge.common.Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private Tag<Item> getForgeItemTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (Tag<Item>) net.minecraftforge.common.Tags.Items.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(net.minecraftforge.common.Tags.Items.class.getName() + " is missing tag name: " + name);
    }
  }

}
