package slimeknights.tconstruct.common.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import java.util.Locale;
import java.util.function.Consumer;

public class TConstructBlockTagsProvider extends BlockTagsProvider {

  public TConstructBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.modID, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    this.addCommon();
    this.addGadgets();
    this.addWorld();
    this.addSmeltery();
  }

  private void addCommon() {
    this.getOrCreateBuilder(Tags.Blocks.STORAGE_BLOCKS)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_COPPER)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD);
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT).add(TinkerMaterials.cobaltBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE).add(TinkerMaterials.arditeBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN).add(TinkerMaterials.manyullynBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME).add(TinkerMaterials.knightSlimeBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON).add(TinkerMaterials.pigironBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_COPPER).add(TinkerMaterials.copperBlock.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD).add(TinkerMaterials.roseGoldBlock.get());
    this.getOrCreateBuilder(BlockTags.BEACON_BASE_BLOCKS)
        .add(TinkerModifiers.silkyJewelBlock.get())
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_COBALT)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_ARDITE)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_MANYULLYN)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_PIG_IRON)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_COPPER)
        .addTag(TinkerTags.Blocks.STORAGE_BLOCKS_ROSE_GOLD);

    this.getOrCreateBuilder(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.getOrCreateBuilder(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addColored(getOrCreateBuilder(Tags.Blocks.STAINED_GLASS)::add, Tags.Blocks.GLASS, "{color}_clear_stained_glass");
    addColored(getOrCreateBuilder(Tags.Blocks.STAINED_GLASS_PANES)::add, Tags.Blocks.GLASS_PANES, "{color}_clear_stained_glass_pane");
  }

  private void addWorld() {
    TagsProvider.Builder<Block> congealedBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.CONGEALED_SLIME);
    for (SlimeType type : SlimeType.values()) {
      congealedBuilder.add(TinkerWorld.congealedSlime.get(type));
    }
    TagsProvider.Builder<Block> logBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_LOGS);
    for (SlimeType type : SlimeType.values()) {
      logBuilder.add(TinkerWorld.congealedSlime.get(type));
    }
    TagsProvider.Builder<Block> leavesBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_LEAVES);
    TagsProvider.Builder<Block> saplingBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_SAPLINGS);
    for (FoliageType type : FoliageType.values()) {
      leavesBuilder.add(TinkerWorld.slimeLeaves.get(type));
      saplingBuilder.add(TinkerWorld.slimeSapling.get(type));
    }

    this.getOrCreateBuilder(Tags.Blocks.ORES)
        .addTag(TinkerTags.Blocks.ORES_COBALT)
        .addTag(TinkerTags.Blocks.ORES_ARDITE)
        .addTag(TinkerTags.Blocks.ORES_COPPER);
    this.getOrCreateBuilder(TinkerTags.Blocks.ORES_COBALT).add(TinkerWorld.cobaltOre.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.ORES_ARDITE).add(TinkerWorld.arditeOre.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.ORES_COPPER).add(TinkerWorld.copperOre.get());
  }

  private void addGadgets() {
    this.getOrCreateBuilder(BlockTags.RAILS).add(TinkerGadgets.woodenRail.get(), TinkerGadgets.woodenDropperRail.get());
  }

  private void addSmeltery() {
    this.getOrCreateBuilder(TinkerTags.Blocks.SEARED_BRICKS).add(
      TinkerSmeltery.searedBricks.get(),
      TinkerSmeltery.searedFancyBricks.get(),
      TinkerSmeltery.searedTriangleBricks.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedStone.get(), TinkerSmeltery.searedCrackedBricks.get(), TinkerSmeltery.searedCobble.get(), TinkerSmeltery.searedPaver.get())
        .addTag(TinkerTags.Blocks.SEARED_BRICKS);
    this.getOrCreateBuilder(BlockTags.WALLS).add(TinkerSmeltery.searedBricks.getWall(), TinkerSmeltery.searedCobble.getWall());

    // structure tags
    // floor allows any basic seared blocks
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_FLOOR).addTag(TinkerTags.Blocks.SEARED_BLOCKS);
    // melter supports the heater as a tank
    Builder<Block> melterBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.MELTER_TANKS).add(TinkerSmeltery.searedHeater.get());
    Builder<Block> smelteryBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_TANKS);
    TinkerSmeltery.searedTank.forEach(tank -> {
      melterBuilder.add(tank);
      smelteryBuilder.add(tank);
    });
    // wall allows anything in the floor, tanks, and glass
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS)
        .add(TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedLadder.get(),
             TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());

    // climb seared ladder
    this.getOrCreateBuilder(BlockTags.CLIMBABLE).add(TinkerSmeltery.searedLadder.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /*
  * Credit to forge for this code to generate the tags.
   */
  private void addColored(Consumer<Block> consumer, INamedTag<Block> group, String pattern) {
    String prefix = group.getName().getPath().toUpperCase(Locale.ENGLISH) + '_';
    for (DyeColor color : DyeColor.values()) {
      ResourceLocation key = new ResourceLocation("tconstruct", pattern.replace("{color}", color.getTranslationKey()));
      INamedTag<Block> tag = getForgeTag(prefix + color.getTranslationKey());
      Block block = ForgeRegistries.BLOCKS.getValue(key);
      if (block == null || block == Blocks.AIR)
        throw new IllegalStateException("Unknown tconstruct block: " + key.toString());
      getOrCreateBuilder(tag).add(block);
      consumer.accept(block);
    }
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private INamedTag<Block> getForgeTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (INamedTag<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

}
