package slimeknights.tconstruct.common.data.tags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import net.fabricmc.fabric.api.tag.TagRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;
import slimeknights.tconstruct.tables.TinkerTables;
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
  protected void configure() {
    this.addCommon();
    this.addGadgets();
    this.addWorld();
    this.addSmeltery();
  }

  private void addCommon() {
    // ores
    addMetalTags(TinkerMaterials.copper);
    addMetalTags(TinkerMaterials.cobalt);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel);
    addMetalTags(TinkerMaterials.tinkersBronze);
    addMetalTags(TinkerMaterials.roseGold);
    addMetalTags(TinkerMaterials.pigIron);
    // tier 4
    addMetalTags(TinkerMaterials.queensSlime);
    addMetalTags(TinkerMaterials.manyullyn);
    addMetalTags(TinkerMaterials.hepatizon);
    addMetalTags(TinkerMaterials.soulsteel);
    // tier 5
    addMetalTags(TinkerMaterials.knightslime);
    this.getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS).add(TinkerModifiers.silkyJewelBlock.get());

    // glass
    this.getOrCreateTagBuilder(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.getOrCreateTagBuilder(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addColored(getOrCreateTagBuilder(Tags.Blocks.STAINED_GLASS)::add, Tags.Blocks.GLASS, "{color}_clear_stained_glass");
    addColored(getOrCreateTagBuilder(Tags.Blocks.STAINED_GLASS_PANES)::add, Tags.Blocks.GLASS_PANES, "{color}_clear_stained_glass_pane");

    // vanilla is not tagged, so tag it
    this.getOrCreateTagBuilder(TinkerTags.Blocks.WORKBENCHES)
        .add(Blocks.CRAFTING_TABLE, TinkerTables.craftingStation.get())
        .addTag((Identified<Block>) TagRegistry.block(new Identifier("c:workbench"))); // some mods use a non-standard name here, so support it I guess
    this.getOrCreateTagBuilder(TinkerTags.Blocks.TABLES)
        .add(TinkerTables.craftingStation.get(), TinkerTables.partBuilder.get(), TinkerTables.tinkerStation.get());

    ObjectBuilder<Block> builder = this.getOrCreateTagBuilder(TinkerTags.Blocks.ANVIL_METAL)
        // tier 3
        .addTag(TinkerMaterials.slimesteel.getBlockTag())
        .addTag(TinkerMaterials.tinkersBronze.getBlockTag())
        .addTag(TinkerMaterials.roseGold.getBlockTag())
        .addTag(TinkerMaterials.pigIron.getBlockTag())
        // tier 4
        .addTag(TinkerMaterials.queensSlime.getBlockTag())
        .addTag(TinkerMaterials.manyullyn.getBlockTag())
        .addTag(TinkerMaterials.hepatizon.getBlockTag())
        .addTag(Tags.Blocks.STORAGE_BLOCKS_NETHERITE);
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      if (!compat.isOre()) {
        builder.addOptionalTag(new Identifier("forge", "storage_blocks/" + compat.getName()));
      }
    }
  }

  private void addWorld() {
    ObjectBuilder<Block> congealedBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.CONGEALED_SLIME);
    for (SlimeType type : SlimeType.values()) {
      congealedBuilder.add(TinkerWorld.congealedSlime.get(type));
    }
    ObjectBuilder<Block> logBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.SLIMY_LOGS);
    for (SlimeType type : SlimeType.values()) {
      logBuilder.add(TinkerWorld.congealedSlime.get(type));
    }
    ObjectBuilder<Block> leavesBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.SLIMY_LEAVES);
    ObjectBuilder<Block> saplingBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.SLIMY_SAPLINGS);
    for (FoliageType type : FoliageType.values()) {
      leavesBuilder.add(TinkerWorld.slimeLeaves.get(type));
      saplingBuilder.add(TinkerWorld.slimeSapling.get(type));
    }

    this.getOrCreateTagBuilder(Tags.Blocks.ORES)
        .addTag(TinkerTags.Blocks.ORES_COBALT)
        .addTag(TinkerTags.Blocks.ORES_COPPER);
    this.getOrCreateTagBuilder(TinkerTags.Blocks.ORES_COBALT).add(TinkerWorld.cobaltOre.get());
    this.getOrCreateTagBuilder(TinkerTags.Blocks.ORES_COPPER).add(TinkerWorld.copperOre.get());
  }

  private void addGadgets() {
    this.getOrCreateTagBuilder(BlockTags.RAILS).add(TinkerGadgets.woodenRail.get(), TinkerGadgets.woodenDropperRail.get());
  }

  private void addSmeltery() {
    this.getOrCreateTagBuilder(TinkerTags.Blocks.SEARED_BRICKS).add(
      TinkerSmeltery.searedBricks.get(),
      TinkerSmeltery.searedFancyBricks.get(),
      TinkerSmeltery.searedTriangleBricks.get());
    this.getOrCreateTagBuilder(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedStone.get(), TinkerSmeltery.searedCrackedBricks.get(), TinkerSmeltery.searedCobble.get(), TinkerSmeltery.searedPaver.get())
        .addTag(TinkerTags.Blocks.SEARED_BRICKS);
    this.getOrCreateTagBuilder(BlockTags.WALLS).add(TinkerSmeltery.searedBricks.getWall(), TinkerSmeltery.searedCobble.getWall());

    // structure tags
    // floor allows any basic seared blocks
    this.getOrCreateTagBuilder(TinkerTags.Blocks.SMELTERY_FLOOR).addTag(TinkerTags.Blocks.SEARED_BLOCKS);
    // melter supports the heater as a tank
    ObjectBuilder<Block> melterBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.MELTER_TANKS).add(TinkerSmeltery.searedHeater.get());
    ObjectBuilder<Block> smelteryBuilder = this.getOrCreateTagBuilder(TinkerTags.Blocks.SMELTERY_TANKS);
    TinkerSmeltery.searedTank.forEach(tank -> {
      melterBuilder.add(tank);
      smelteryBuilder.add(tank);
    });
    // wall allows anything in the floor, tanks, and glass
    this.getOrCreateTagBuilder(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS)
        .add(TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedLadder.get(),
             TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());

    // climb seared ladder
    this.getOrCreateTagBuilder(BlockTags.CLIMBABLE).add(TinkerSmeltery.searedLadder.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /*
  * Credit to forge for this code to generate the tags.
   */
  private void addColored(Consumer<Block> consumer, Identified<Block> group, String pattern) {
    String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
    for (DyeColor color : DyeColor.values()) {
      Identifier key = new Identifier("tconstruct", pattern.replace("{color}", color.getName()));
      Identified<Block> tag = getForgeTag(prefix + color.getName());
      Block block = ForgeRegistries.BLOCKS.getValue(key);
      if (block == null || block == Blocks.AIR)
        throw new IllegalStateException("Unknown tconstruct block: " + key.toString());
      getOrCreateTagBuilder(tag).add(block);
      consumer.accept(block);
    }
  }

  /**
   * Adds relevant tags for a metal object
   * @param metal  Metal object
   */
  private void addMetalTags(MetalItemObject metal) {
    this.getOrCreateTagBuilder(metal.getBlockTag()).add(metal.get());
    this.getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS).addTag(metal.getBlockTag());
    this.getOrCreateTagBuilder(Tags.Blocks.STORAGE_BLOCKS).addTag(metal.getBlockTag());
  }

  /*
   * Credit to forge for this code to generate the tags.
   */
  @SuppressWarnings("unchecked")
  private Identified<Block> getForgeTag(String name) {
    try {
      name = name.toUpperCase(Locale.ENGLISH);
      return (Identified<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
    }
  }

}
