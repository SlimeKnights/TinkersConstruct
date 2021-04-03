package slimeknights.tconstruct.common.data.tags;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Locale;
import java.util.function.Consumer;

public class TConstructItemTagsProvider extends ItemTagsProvider {

  public TConstructItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
    super(generatorIn, blockTagProvider, TConstruct.modID, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    this.addCommon();
    this.addGadgets();
    this.addWorld();
    this.addSmeltery();
    this.addTools();
  }

  private void addCommon() {
    this.getOrCreateBuilder(Tags.Items.SLIMEBALLS)
        .addTag(TinkerTags.Items.SKY_SLIMEBALL)
        .addTag(TinkerTags.Items.ENDER_SLIMEBALL)
        .addTag(TinkerTags.Items.BLOOD_SLIMEBALL)
        .addTag(TinkerTags.Items.ICHOR_SLIMEBALL);
    this.getOrCreateBuilder(TinkerTags.Items.EARTH_SLIMEBALL).add(Items.SLIME_BALL);
    this.getOrCreateBuilder(TinkerTags.Items.SKY_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeType.SKY));
    this.getOrCreateBuilder(TinkerTags.Items.ENDER_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeType.ENDER));
    this.getOrCreateBuilder(TinkerTags.Items.BLOOD_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeType.BLOOD));
    this.getOrCreateBuilder(TinkerTags.Items.ICHOR_SLIMEBALL).add(TinkerCommons.slimeball.get(SlimeType.ICHOR));

    this.getOrCreateBuilder(Tags.Items.INGOTS).add(TinkerCommons.driedBrick.get(), TinkerSmeltery.searedBrick.get());
    this.getOrCreateBuilder(TinkerTags.Items.WITHER_BONES).add(TinkerModifiers.necroticBone.get());

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
    this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

    this.getOrCreateBuilder(TinkerTags.Items.INGOTS_NETHERITE_SCRAP).add(Items.NETHERITE_SCRAP);
    this.getOrCreateBuilder(TinkerTags.Items.NUGGETS_NETHERITE).add(TinkerMaterials.netheriteNugget.get());

    copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
    copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
    copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
    copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
    copy(TinkerTags.Blocks.WORKBENCHES, TinkerTags.Items.WORKBENCHES);
    copy(TinkerTags.Blocks.TABLES, TinkerTags.Items.TABLES);
    copy(TinkerTags.Blocks.ANVIL_METAL, TinkerTags.Items.ANVIL_METAL);
  }

  private void addWorld() {
    this.copy(TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Items.CONGEALED_SLIME);
    this.copy(TinkerTags.Blocks.SLIMY_LOGS, TinkerTags.Items.SLIMY_LOGS);
    this.copy(TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Items.SLIMY_LEAVES);
    this.copy(TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Items.SLIMY_SAPLINGS);

    this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    this.copy(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Items.ORES_COBALT);
    this.copy(TinkerTags.Blocks.ORES_COPPER, TinkerTags.Items.ORES_COPPER);
  }

  private void addGadgets() {
    this.copy(BlockTags.RAILS, ItemTags.RAILS);
    this.getOrCreateBuilder(Tags.Items.RODS).addTag(TinkerTags.Items.RODS_STONE);
    this.getOrCreateBuilder(TinkerTags.Items.RODS_STONE).add(TinkerGadgets.stoneStick.get());
  }


  private void addTools() {
    // multipart is basically all our tools right now, but future armor/shields won't be
    // required for part swapping and a few other things
    this.getOrCreateBuilder(TinkerTags.Items.MULTIPART_TOOL)
        .add(TinkerTools.pickaxe.get(), TinkerTools.sledgeHammer.get(),
						 TinkerTools.mattock.get(), TinkerTools.excavator.get(),
						 TinkerTools.axe.get(), TinkerTools.kama.get(), TinkerTools.broadSword.get());
    // mine blocks
    this.getOrCreateBuilder(TinkerTags.Items.HARVEST)
        .add(TinkerTools.pickaxe.get(), TinkerTools.sledgeHammer.get(),
						 TinkerTools.mattock.get(), TinkerTools.excavator.get(),
						 TinkerTools.axe.get(), TinkerTools.kama.get());
    // support expanders
    this.getOrCreateBuilder(TinkerTags.Items.AOE)
        .add(TinkerTools.pickaxe.get(), TinkerTools.sledgeHammer.get(),
						 TinkerTools.mattock.get(), TinkerTools.excavator.get(),
						 TinkerTools.axe.get(), TinkerTools.kama.get());
    // support all weapon modifiers
    this.getOrCreateBuilder(TinkerTags.Items.COMBAT)
        .add(TinkerTools.sledgeHammer.get(), TinkerTools.axe.get(), TinkerTools.broadSword.get());
    // can receive damage boosts
    this.getOrCreateBuilder(TinkerTags.Items.MELEE)
        .addTag(TinkerTags.Items.COMBAT)
        .add(TinkerTools.pickaxe.get(), TinkerTools.kama.get(),
						 TinkerTools.mattock.get(), TinkerTools.excavator.get());
    // supports modifiers
    this.getOrCreateBuilder(TinkerTags.Items.MELEE_OR_HARVEST)
        .addTag(TinkerTags.Items.MELEE)
        .addTag(TinkerTags.Items.HARVEST);
    this.getOrCreateBuilder(TinkerTags.Items.MODIFIABLE)
        .addTag(TinkerTags.Items.MULTIPART_TOOL)
        .addTag(TinkerTags.Items.MELEE_OR_HARVEST)
        .addTag(TinkerTags.Items.AOE);

    // kamas are a shear type, when broken we don't pass it to loot tables
    this.getOrCreateBuilder(Tags.Items.SHEARS).add(TinkerTools.kama.get());
    // nothing to blacklist, just want the empty tag so it appears in datapacks
    this.getOrCreateBuilder(TinkerTags.Items.AUTOSMELT_BLACKLIST);
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);

    // tag each type of cast
    TagsProvider.Builder<Item> goldCasts = this.getOrCreateBuilder(TinkerTags.Items.GOLD_CASTS);
    TagsProvider.Builder<Item> sandCasts = this.getOrCreateBuilder(TinkerTags.Items.SAND_CASTS);
    TagsProvider.Builder<Item> redSandCasts = this.getOrCreateBuilder(TinkerTags.Items.RED_SAND_CASTS);
    Consumer<CastItemObject> addCast = cast -> {
      goldCasts.add(cast.get());
      sandCasts.add(cast.getSand());
      redSandCasts.add(cast.getRedSand());
      this.getOrCreateBuilder(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
    };
    // basic
    addCast.accept(TinkerSmeltery.blankCast);
    addCast.accept(TinkerSmeltery.ingotCast);
    addCast.accept(TinkerSmeltery.nuggetCast);
    addCast.accept(TinkerSmeltery.gemCast);
    // small heads
    addCast.accept(TinkerSmeltery.pickaxeHeadCast);
    addCast.accept(TinkerSmeltery.axeHeadCast);
    addCast.accept(TinkerSmeltery.kamaHeadCast);
    addCast.accept(TinkerSmeltery.swordBladeCast);
    // large heads
    addCast.accept(TinkerSmeltery.hammerHeadCast);
    // bindings
    addCast.accept(TinkerSmeltery.toolBindingCast);
    addCast.accept(TinkerSmeltery.largePlateCast);
    // tool rods
    addCast.accept(TinkerSmeltery.toolRodCast);
    addCast.accept(TinkerSmeltery.toughToolRodCast);

    // add all casts to a common tag
    this.getOrCreateBuilder(TinkerTags.Items.CASTS)
        .addTag(TinkerTags.Items.GOLD_CASTS)
        .addTag(TinkerTags.Items.SAND_CASTS)
        .addTag(TinkerTags.Items.RED_SAND_CASTS);

    this.getOrCreateBuilder(TinkerTags.Items.DUCT_CONTAINERS).add(Items.BUCKET, TinkerSmeltery.copperCan.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Item Tags";
  }



  /**
   * Adds relevant tags for a metal object
   * @param metal  Metal object
   */
  private void addMetalTags(MetalItemObject metal) {
    this.getOrCreateBuilder(metal.getIngotTag()).add(metal.getIngot());
    this.getOrCreateBuilder(Tags.Items.INGOTS).addTag(metal.getIngotTag());
    this.getOrCreateBuilder(metal.getNuggetTag()).add(metal.getNugget());
    this.getOrCreateBuilder(Tags.Items.NUGGETS).addTag(metal.getNuggetTag());
    this.copy(metal.getBlockTag(), metal.getBlockItemTag());
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
