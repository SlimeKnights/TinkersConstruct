package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

import static net.minecraft.tags.ItemTags.CLUSTER_MAX_HARVESTABLES;
import static slimeknights.tconstruct.common.TinkerTags.Items.ANCIENT_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.AOE;
import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BASIC_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BONUS_SLOTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOOK_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOOTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BROAD_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.CHESTPLATES;
import static slimeknights.tconstruct.common.TinkerTags.Items.CROSSBOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.DYEABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.EMBELLISHMENT_SLIME;
import static slimeknights.tconstruct.common.TinkerTags.Items.EMBELLISHMENT_WOOD;
import static slimeknights.tconstruct.common.TinkerTags.Items.FANTASTIC_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.GADGETRY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.GOLDEN_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELD;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELD_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELMETS;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_DUAL;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_LEFT;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_RIGHT;
import static slimeknights.tconstruct.common.TinkerTags.Items.LEGGINGS;
import static slimeknights.tconstruct.common.TinkerTags.Items.LONGBOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.LOOT_CAPABLE_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_WEAPON;
import static slimeknights.tconstruct.common.TinkerTags.Items.MIGHTY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MULTIPART_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.PARRY;
import static slimeknights.tconstruct.common.TinkerTags.Items.PUNY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.RANGED;
import static slimeknights.tconstruct.common.TinkerTags.Items.SHIELDS;
import static slimeknights.tconstruct.common.TinkerTags.Items.SMALL_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.SPECIAL_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.STAFFS;
import static slimeknights.tconstruct.common.TinkerTags.Items.STONE_HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.SWORD;
import static slimeknights.tconstruct.common.TinkerTags.Items.TRADER_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNARMED;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNSALVAGABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.WORN_ARMOR;

@SuppressWarnings("unchecked")
public class ItemTagProvider extends ItemTagsProvider {

  public ItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
    super(generatorIn, blockTagProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    this.addCommon();
    this.addWorld();
    this.addSmeltery();
    this.addTools();
  }

  @SuppressWarnings("unchecked")
  private void addCommon() {
    this.tag(TinkerTags.Items.TINKERS_GUIDES)
        .add(TinkerCommons.materialsAndYou.get(), TinkerCommons.tinkersGadgetry.get(),
             TinkerCommons.punySmelting.get(), TinkerCommons.mightySmelting.get(),
             TinkerCommons.fantasticFoundry.get(), TinkerCommons.encyclopedia.get());
    this.tag(ItemTags.LECTERN_BOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(TinkerTags.Items.GUIDEBOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(TinkerTags.Items.BOOKS).addTag(TinkerTags.Items.GUIDEBOOKS);

    TagAppender<Item> slimeballs = this.tag(Tags.Items.SLIMEBALLS);
    for (SlimeType type : SlimeType.values()) {
      slimeballs.addTag(type.getSlimeballTag());
    }
    TinkerCommons.slimeball.forEach((type, ball) -> this.tag(type.getSlimeballTag()).add(ball));

    this.tag(Tags.Items.INGOTS).add(TinkerSmeltery.searedBrick.get(), TinkerSmeltery.scorchedBrick.get()).addTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP);
    this.tag(Tags.Items.NUGGETS).addTags(TinkerTags.Items.NUGGETS_COPPER, TinkerTags.Items.NUGGETS_NETHERITE, TinkerTags.Items.NUGGETS_NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.WITHER_BONES).add(TinkerMaterials.necroticBone.get());

    this.tag(TinkerTags.Items.NUGGETS_COPPER).add(TinkerMaterials.copperNugget.get());
    this.tag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP).add(Items.NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE).add(TinkerMaterials.netheriteNugget.get());
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP).add(TinkerMaterials.debrisNugget.get());

    // ores
    addMetalTags(TinkerMaterials.cobalt);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel);
    addMetalTags(TinkerMaterials.amethystBronze);
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

    // glass
    copy(Tags.Blocks.GLASS_SILICA, Tags.Items.GLASS_SILICA);
    copy(Tags.Blocks.GLASS_TINTED, Tags.Items.GLASS_TINTED);
    copy(TinkerTags.Blocks.GLASS_PANES_SILICA, TinkerTags.Items.GLASS_PANES_SILICA);
    copy(Tags.Blocks.GLASS_COLORLESS, Tags.Items.GLASS_COLORLESS);
    copy(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS);
    copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
    copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
    for (DyeColor color : DyeColor.values()) {
      ResourceLocation name = new ResourceLocation("forge", "glass/" + color.getSerializedName());
      copy(TagKey.create(Registry.BLOCK_REGISTRY, name), TagKey.create(Registry.ITEM_REGISTRY, name));
      name = new ResourceLocation("forge", "glass_panes/" + color.getSerializedName());
      copy(TagKey.create(Registry.BLOCK_REGISTRY, name), TagKey.create(Registry.ITEM_REGISTRY, name));
    }

    copy(TinkerTags.Blocks.WORKBENCHES, TinkerTags.Items.WORKBENCHES);
    copy(TinkerTags.Blocks.TABLES, TinkerTags.Items.TABLES);
    copy(TinkerTags.Blocks.WORKSTATION_ROCK, TinkerTags.Items.WORKSTATION_ROCK);
    copy(TinkerTags.Blocks.ANVIL_METAL, TinkerTags.Items.ANVIL_METAL);
    copy(TinkerTags.Blocks.PLANKLIKE, TinkerTags.Items.PLANKLIKE);

    // piglins like gold and dislike zombie piglin heads
    this.tag(ItemTags.PIGLIN_LOVED)
        .add(TinkerModifiers.goldReinforcement.get(), TinkerGadgets.itemFrame.get(FrameType.GOLD), TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD), TinkerFluids.moltenGold.asItem(), TinkerCommons.goldBars.asItem(), TinkerCommons.goldPlatform.asItem())
        .addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(ItemTags.PIGLIN_REPELLENTS).add(TinkerWorld.headItems.get(TinkerHeadType.ZOMBIFIED_PIGLIN));

    // beacons are happy to accept any expensive ingots
    this.tag(ItemTags.BEACON_PAYMENT_ITEMS)
        .addTags(TinkerMaterials.cobalt.getIngotTag(), TinkerMaterials.queensSlime.getIngotTag(),
                 TinkerMaterials.manyullyn.getIngotTag(), TinkerMaterials.hepatizon.getIngotTag());

    this.copy(TinkerTags.Blocks.COPPER_PLATFORMS, TinkerTags.Items.COPPER_PLATFORMS);

    this.tag(TinkerTags.Items.SPLASH_BOTTLE).add(TinkerFluids.splashBottle.get());
    this.tag(TinkerTags.Items.LINGERING_BOTTLE).add(TinkerFluids.lingeringBottle.get());
  }

  private void addWorld() {
    TagAppender<Item> heads = this.tag(Tags.Items.HEADS);
    TinkerWorld.heads.forEach(head -> heads.add(head.asItem()));

    this.copy(TinkerTags.Blocks.SLIME_BLOCK, TinkerTags.Items.SLIME_BLOCK);
    this.copy(TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Items.CONGEALED_SLIME);
    this.copy(TinkerTags.Blocks.SLIMY_LOGS, TinkerTags.Items.SLIMY_LOGS);
    this.copy(TinkerTags.Blocks.SLIMY_PLANKS, TinkerTags.Items.SLIMY_PLANKS);
    this.copy(TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Items.SLIMY_LEAVES);
    this.copy(TinkerTags.Blocks.SLIMY_VINES, TinkerTags.Items.SLIMY_VINES);
    this.copy(TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Items.SLIMY_SAPLINGS);
    this.copy(TinkerTags.Blocks.ENDERBARK_ROOTS, TinkerTags.Items.ENDERBARK_ROOTS);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);

    this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
    this.copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
    this.copy(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Items.ORES_COBALT);
    this.copy(TinkerTags.Blocks.RAW_BLOCK_COBALT, TinkerTags.Items.RAW_BLOCK_COBALT);
    this.tag(TinkerTags.Items.RAW_COBALT).add(TinkerWorld.rawCobalt.get());
    this.tag(Tags.Items.RAW_MATERIALS).addTag(TinkerTags.Items.RAW_COBALT);

    // wood
    this.copy(BlockTags.NON_FLAMMABLE_WOOD, ItemTags.NON_FLAMMABLE_WOOD);
    // planks
    this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
    this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
    this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    // logs
    this.copy(TinkerWorld.greenheart.getLogBlockTag(), TinkerWorld.greenheart.getLogItemTag());
    this.copy(TinkerWorld.skyroot.getLogBlockTag(), TinkerWorld.skyroot.getLogItemTag());
    this.copy(TinkerWorld.bloodshroom.getLogBlockTag(), TinkerWorld.bloodshroom.getLogItemTag());
    this.copy(TinkerWorld.enderbark.getLogBlockTag(), TinkerWorld.enderbark.getLogItemTag());
    this.copy(BlockTags.LOGS, ItemTags.LOGS);
    this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
    // doors
    this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
    this.copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
    this.copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
    this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    // redstone
    this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
    this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
    this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
  }


  private void addTools() {
    // stone
    addToolTags(TinkerTools.pickaxe,      MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.sledgeHammer, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, BROAD_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.veinHammer,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, BROAD_TOOLS, BONUS_SLOTS);
    // dirtD
    addToolTags(TinkerTools.mattock,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.pickadze,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS, STONE_HARVEST);
    addToolTags(TinkerTools.excavator, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS);
    // wood
    addToolTags(TinkerTools.handAxe,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.broadAxe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS);
    // plants
    addToolTags(TinkerTools.kama,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.scythe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS);
    // sword
    addToolTags(TinkerTools.dagger,  MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, PARRY, SMALL_TOOLS, BONUS_SLOTS, UNSALVAGABLE);
    addToolTags(TinkerTools.sword,   MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, SMALL_TOOLS, BONUS_SLOTS, AOE);
    addToolTags(TinkerTools.cleaver, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, BROAD_TOOLS, BONUS_SLOTS, AOE);
    // bow
    addToolTags(TinkerTools.crossbow, MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, CROSSBOWS, INTERACTABLE_LEFT, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.longbow,  MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, LONGBOWS,  INTERACTABLE_LEFT, BROAD_TOOLS, BONUS_SLOTS);
    // specialized
    addToolTags(TinkerTools.flintAndBrick, DURABILITY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.skyStaff,      DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.earthStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.ichorStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.enderStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    // ancient
    addToolTags(TinkerTools.meltingPan, MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, HARVEST_PRIMARY, STAFFS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, BONUS_SLOTS);
    addToolTags(TinkerTools.warPick,    MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON, HELD, AOE, CLUSTER_MAX_HARVESTABLES, CROSSBOWS, BONUS_SLOTS);
    addToolTags(TinkerTools.battlesign, MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, MELEE_PRIMARY, SHIELDS, BONUS_SLOTS);

    // armor
    addArmorTags(TinkerTools.travelersGear, DURABILITY, BONUS_SLOTS, GOLDEN_ARMOR, DYEABLE, ItemTags.FREEZE_IMMUNE_WEARABLES);
    addArmorTags(TinkerTools.plateArmor,    DURABILITY, BONUS_SLOTS);
    // want these in top down order as it looks better in the book then
    TagAppender<Item> multipart = tag(MULTIPART_TOOL);
    for (ArmorSlotType slotType : ArmorSlotType.TOP_DOWN) {
      multipart.add(TinkerTools.plateArmor.get(slotType));
    }
    addArmorTags(TinkerTools.slimesuit,     DURABILITY, BONUS_SLOTS, GOLDEN_ARMOR, EMBELLISHMENT_SLIME);
    addToolTags(TinkerTools.slimesuit.get(ArmorSlotType.HELMET), MULTIPART_TOOL);

    // shields
    addToolTags(TinkerTools.travelersShield, DURABILITY, BONUS_SLOTS, SHIELDS, INTERACTABLE_LEFT, EMBELLISHMENT_WOOD, DYEABLE);
    addToolTags(TinkerTools.plateShield,     DURABILITY, BONUS_SLOTS, SHIELDS, INTERACTABLE_LEFT, MULTIPART_TOOL);

    // care about order for armor in the book
    tag(BASIC_ARMOR);
    TagAppender<Item> bookArmor = tag(PUNY_ARMOR);
    for (ArmorSlotType slotType : ArmorSlotType.TOP_DOWN) {
      bookArmor.add(TinkerTools.travelersGear.get(slotType));
    }
    bookArmor.add(TinkerTools.travelersShield.get());
    for (ArmorSlotType slotType : ArmorSlotType.TOP_DOWN) {
      bookArmor.add(TinkerTools.plateArmor.get(slotType));
    }
    bookArmor.add(TinkerTools.plateShield.get());
    tag(MIGHTY_ARMOR);
    tag(FANTASTIC_ARMOR);
    bookArmor = tag(GADGETRY_ARMOR);
    for (ArmorSlotType slotType : ArmorSlotType.TOP_DOWN) {
      bookArmor.add(TinkerTools.slimesuit.get(slotType));
    }
    tag(BOOK_ARMOR).addTags(BASIC_ARMOR, PUNY_ARMOR, MIGHTY_ARMOR, FANTASTIC_ARMOR, GADGETRY_ARMOR);


    // add tags to other tags
    // harvest primary and stone harvest are both automatically harvest
    this.tag(TinkerTags.Items.HARVEST).addTags(HARVEST_PRIMARY, STONE_HARVEST);
    // melee nesting - currently most all sub-tags are held exclusive as they revolve around tool damage or having an item in hand
    this.tag(MELEE_WEAPON).addTags(MELEE_PRIMARY, SWORD, PARRY);
    this.tag(MELEE).addTags(MELEE_WEAPON, UNARMED);
    // modifier helper tags
    this.tag(LOOT_CAPABLE_TOOL).addTag(MELEE).addTag(HARVEST);
    this.tag(UNARMED).addTag(CHESTPLATES);
    // migrating one handed and two handed to interactable right
    this.tag(INTERACTABLE_RIGHT).addTags(INTERACTABLE_DUAL);
    this.tag(INTERACTABLE_LEFT).addTag(INTERACTABLE_DUAL);
    // interactable armor is mostly so some mod could disable all chestplate interactions in one swing
    this.tag(INTERACTABLE_ARMOR).addTag(CHESTPLATES);
    // left and right handed are held, but not armor
    this.tag(HELD).addTags(INTERACTABLE_RIGHT, INTERACTABLE_LEFT, HELD_ARMOR);
    this.tag(INTERACTABLE).addTags(INTERACTABLE_LEFT, INTERACTABLE_RIGHT, INTERACTABLE_ARMOR);
    this.tag(WORN_ARMOR).addTags(BOOTS, LEGGINGS, CHESTPLATES, HELMETS);
    this.tag(HELD_ARMOR).addTag(SHIELDS);
    this.tag(ARMOR).addTags(WORN_ARMOR, HELD_ARMOR);
    this.tag(AOE).addTag(BOOTS); // boot walk modifiers
    this.tag(RANGED).addTags(BOWS, STAFFS);
    this.tag(BOWS).addTags(LONGBOWS, CROSSBOWS);
    this.tag(TRADER_TOOLS).addTag(ANCIENT_TOOLS);

    // general
    this.tag(MODIFIABLE).addTags(MULTIPART_TOOL, DURABILITY, MELEE, HARVEST, AOE, HELD, BONUS_SLOTS);
    // disable parry mod on our items, we have our own modifier for that
    this.tag(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("parry", "excluded_shields"))).addTag(HELD);

    // kamas are a shear type, when broken we don't pass it to loot tables
    this.tag(Tags.Items.SHEARS).add(TinkerTools.kama.get());
    // mark kama and scythe for mods like thermal to use
    this.tag(TinkerTags.Items.SCYTHES).add(TinkerTools.kama.get(), TinkerTools.scythe.get());
    // nothing to blacklist, just want the empty tag so it appears in datapacks
    this.tag(TinkerTags.Items.AUTOSMELT_BLACKLIST);

    // carrots and potatoes are not seeds in vanilla, so make a tag with them
    this.tag(TinkerTags.Items.SEEDS)
        .addTag(Tags.Items.SEEDS)
        .add(Items.CARROT, Items.POTATO, Items.NETHER_WART);

    // tags for modifiers
    copy(TinkerTags.Blocks.CHRYSOPHILITE_ORES, TinkerTags.Items.CHRYSOPHILITE_ORES);

    // tag for tool parts, mostly used by JEI right now
    this.tag(TinkerTags.Items.TOOL_PARTS)
        .add(TinkerToolParts.pickHead.get(), TinkerToolParts.hammerHead.get(),
             TinkerToolParts.smallAxeHead.get(), TinkerToolParts.broadAxeHead.get(),
             TinkerToolParts.smallBlade.get(), TinkerToolParts.broadBlade.get(),
             TinkerToolParts.toolBinding.get(), TinkerToolParts.roundPlate.get(), TinkerToolParts.largePlate.get(),
             TinkerToolParts.toolHandle.get(), TinkerToolParts.toughHandle.get(),
             TinkerToolParts.bowLimb.get(), TinkerToolParts.bowGrip.get(), TinkerToolParts.bowstring.get(),
             TinkerToolParts.maille.get(), TinkerToolParts.shieldCore.get(),
             TinkerToolParts.repairKit.get()) // repair kit is not strictly a tool part, but this list just helps out JEI
        .add(TinkerToolParts.plating.values().toArray(new Item[0]));
    // tag for the part chest items
    this.tag(TinkerTags.Items.CHEST_PARTS).addTag(TinkerTags.Items.TOOL_PARTS).add(TinkerSmeltery.dummyPlating.values().toArray(new Item[0]));

    TagAppender<Item> slimySeeds = this.tag(TinkerTags.Items.SLIMY_SEEDS);
    TinkerWorld.slimeGrassSeeds.values().forEach(slimySeeds::add);

    // contains any ground stones
    this.tag(TinkerTags.Items.STONESHIELDS)
        .addTag(Tags.Items.STONE)
        .addTag(Tags.Items.COBBLESTONE)
        .addTag(Tags.Items.SANDSTONE)
        .addTag(Tags.Items.END_STONES)
        .addTag(Tags.Items.GRAVEL) // for shovels and axes to use
        .add(Items.NETHERRACK, Items.BASALT, Items.POLISHED_BASALT, Items.BLACKSTONE, Items.POLISHED_BLACKSTONE);
    this.tag(TinkerTags.Items.FIREBALLS).add(Items.FIRE_CHARGE);
    this.tag(TinkerTags.Items.TOOL_INVENTORY_BLACKLIST)
        .add(Items.SHULKER_BOX,
             Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
             Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
             Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
             Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);

    this.tag(TinkerTags.Items.VARIANT_PLANKS)
        .add(Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS, Items.DARK_OAK_PLANKS, Items.ACACIA_PLANKS, Items.MANGROVE_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS)
        .addTag(TinkerTags.Items.SLIMY_PLANKS);
    this.tag(TinkerTags.Items.VARIANT_LOGS).addTags(ItemTags.OAK_LOGS, ItemTags.SPRUCE_LOGS, ItemTags.BIRCH_LOGS, ItemTags.JUNGLE_LOGS, ItemTags.DARK_OAK_LOGS, ItemTags.ACACIA_LOGS, ItemTags.MANGROVE_LOGS, ItemTags.CRIMSON_STEMS, ItemTags.WARPED_STEMS, TinkerTags.Items.SLIMY_LOGS);

    // part builder
    this.tag(TinkerTags.Items.DEFAULT_PATTERNS).add(TinkerTables.pattern.get());
    this.tag(TinkerTags.Items.REUSABLE_PATTERNS).addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(TinkerTags.Items.PATTERNS)
        .addTags(TinkerTags.Items.DEFAULT_PATTERNS, TinkerTags.Items.REUSABLE_PATTERNS, TinkerTags.Items.SAND_CASTS, TinkerTags.Items.RED_SAND_CASTS)
        .add(Items.SAND, Items.RED_SAND);

    // stone
    this.copy(TinkerTags.Blocks.STONE,      TinkerTags.Items.STONE);
    this.copy(TinkerTags.Blocks.GRANITE,    TinkerTags.Items.GRANITE);
    this.copy(TinkerTags.Blocks.DIORITE,    TinkerTags.Items.DIORITE);
    this.copy(TinkerTags.Blocks.ANDESITE,   TinkerTags.Items.ANDESITE);
    this.copy(TinkerTags.Blocks.BLACKSTONE, TinkerTags.Items.BLACKSTONE);
    this.copy(TinkerTags.Blocks.DEEPSLATE,  TinkerTags.Items.DEEPSLATE);
    this.copy(TinkerTags.Blocks.BASALT,     TinkerTags.Items.BASALT);
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);
    this.copy(TinkerTags.Blocks.SMELTERY_BRICKS, TinkerTags.Items.SMELTERY_BRICKS);
    this.copy(TinkerTags.Blocks.SCORCHED_BLOCKS, TinkerTags.Items.SCORCHED_BLOCKS);
    this.copy(TinkerTags.Blocks.FOUNDRY_BRICKS, TinkerTags.Items.FOUNDRY_BRICKS);
    this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);

    // smeltery and foundry structure blocks
    this.tag(TinkerTags.Items.SMELTERY)
        .addTag(TinkerTags.Items.SEARED_BLOCKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .add(TinkerSmeltery.smelteryController.asItem(), TinkerSmeltery.searedLadder.asItem(),
             TinkerSmeltery.searedDrain.asItem(), TinkerSmeltery.searedChute.asItem(), TinkerSmeltery.searedDuct.asItem(),
             TinkerSmeltery.searedGlass.asItem(), TinkerSmeltery.searedSoulGlass.asItem(), TinkerSmeltery.searedTintedGlass.asItem());
    this.tag(TinkerTags.Items.FOUNDRY)
        .addTag(TinkerTags.Items.SCORCHED_BLOCKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS)
        .add(TinkerSmeltery.foundryController.asItem(), TinkerSmeltery.scorchedLadder.asItem(),
             TinkerSmeltery.scorchedDrain.asItem(), TinkerSmeltery.scorchedChute.asItem(), TinkerSmeltery.scorchedDuct.asItem(),
             TinkerSmeltery.scorchedGlass.asItem(), TinkerSmeltery.scorchedSoulGlass.asItem(), TinkerSmeltery.scorchedTintedGlass.asItem());
    // structure debug
    this.tag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG);
    this.tag(TinkerTags.Items.SMELTERY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.SMELTERY);
    this.tag(TinkerTags.Items.FOUNDRY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.FOUNDRY);

    // tag each type of cast
    TagAppender<Item> goldCasts = this.tag(TinkerTags.Items.GOLD_CASTS);
    TagAppender<Item> sandCasts = this.tag(TinkerTags.Items.SAND_CASTS);
    TagAppender<Item> redSandCasts = this.tag(TinkerTags.Items.RED_SAND_CASTS);
    TagAppender<Item> singleUseCasts = this.tag(TinkerTags.Items.SINGLE_USE_CASTS);
    TagAppender<Item> multiUseCasts = this.tag(TinkerTags.Items.MULTI_USE_CASTS);
    Consumer<CastItemObject> addCast = cast -> {
      // tag based on material
      goldCasts.add(cast.get());
      sandCasts.add(cast.getSand());
      redSandCasts.add(cast.getRedSand());
      // tag based on usage
      singleUseCasts.addTag(cast.getSingleUseTag());
      this.tag(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
      multiUseCasts.addTag(cast.getMultiUseTag());
      this.tag(cast.getMultiUseTag()).add(cast.get());
    };
    // blank sand casts, no blank gold or this would use the helper
    sandCasts.add(TinkerSmeltery.blankSandCast.get());
    redSandCasts.add(TinkerSmeltery.blankRedSandCast.get());
    singleUseCasts.addTag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS);
    this.tag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS).add(TinkerSmeltery.blankSandCast.get(), TinkerSmeltery.blankRedSandCast.get());
    // basic
    addCast.accept(TinkerSmeltery.ingotCast);
    addCast.accept(TinkerSmeltery.nuggetCast);
    addCast.accept(TinkerSmeltery.gemCast);
    addCast.accept(TinkerSmeltery.rodCast);
    addCast.accept(TinkerSmeltery.repairKitCast);
    // compatibility
    addCast.accept(TinkerSmeltery.plateCast);
    addCast.accept(TinkerSmeltery.gearCast);
    addCast.accept(TinkerSmeltery.coinCast);
    addCast.accept(TinkerSmeltery.wireCast);
    // small heads
    addCast.accept(TinkerSmeltery.pickHeadCast);
    addCast.accept(TinkerSmeltery.smallAxeHeadCast);
    addCast.accept(TinkerSmeltery.smallBladeCast);
    // large heads
    addCast.accept(TinkerSmeltery.hammerHeadCast);
    addCast.accept(TinkerSmeltery.broadAxeHeadCast);
    addCast.accept(TinkerSmeltery.broadBladeCast);
    // bindings
    addCast.accept(TinkerSmeltery.toolBindingCast);
    addCast.accept(TinkerSmeltery.roundPlateCast);
    addCast.accept(TinkerSmeltery.largePlateCast);
    // tool rods
    addCast.accept(TinkerSmeltery.toolHandleCast);
    addCast.accept(TinkerSmeltery.toughHandleCast);
    // bow
    addCast.accept(TinkerSmeltery.bowLimbCast);
    addCast.accept(TinkerSmeltery.bowGripCast);
    // armor
    addCast.accept(TinkerSmeltery.helmetPlatingCast);
    addCast.accept(TinkerSmeltery.chestplatePlatingCast);
    addCast.accept(TinkerSmeltery.leggingsPlatingCast);
    addCast.accept(TinkerSmeltery.bootsPlatingCast);
    addCast.accept(TinkerSmeltery.mailleCast);

    // add all casts to a common tag
    this.tag(TinkerTags.Items.CASTS)
        .addTags(TinkerTags.Items.GOLD_CASTS, TinkerTags.Items.SAND_CASTS, TinkerTags.Items.RED_SAND_CASTS, TinkerTags.Items.TABLE_EMPTY_CASTS, TinkerTags.Items.BASIN_EMPTY_CASTS);
    this.tag(TinkerTags.Items.TABLE_EMPTY_CASTS).add(TinkerCommons.goldBars.asItem());
    this.tag(TinkerTags.Items.BASIN_EMPTY_CASTS).add(TinkerCommons.goldPlatform.asItem());

    this.tag(TinkerTags.Items.DUCT_CONTAINERS).add(Items.BUCKET, TinkerSmeltery.copperCan.get(), TinkerSmeltery.searedLantern.asItem(), TinkerSmeltery.scorchedLantern.asItem());

    // tank tag
    this.copy(TinkerTags.Blocks.SEARED_TANKS, TinkerTags.Items.SEARED_TANKS);
    this.copy(TinkerTags.Blocks.SCORCHED_TANKS, TinkerTags.Items.SCORCHED_TANKS);
    this.tag(TinkerTags.Items.TANKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS);
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
    this.tag(metal.getIngotTag()).add(metal.getIngot());
    this.tag(Tags.Items.INGOTS).addTag(metal.getIngotTag());
    this.tag(metal.getNuggetTag()).add(metal.getNugget());
    this.tag(Tags.Items.NUGGETS).addTag(metal.getNuggetTag());
    this.copy(metal.getBlockTag(), metal.getBlockItemTag());
  }

  @SafeVarargs
  private void addToolTags(ItemLike tool, TagKey<Item>... tags) {
    Item item = tool.asItem();
    for (TagKey<Item> tag : tags) {
      this.tag(tag).add(item);
    }
  }

  private TagKey<Item> getArmorTag(ArmorSlotType slotType) {
    return switch (slotType) {
      case BOOTS -> BOOTS;
      case LEGGINGS -> LEGGINGS;
      case CHESTPLATE -> CHESTPLATES;
      case HELMET -> HELMETS;
    };
  }

  @SafeVarargs
  private void addArmorTags(EnumObject<ArmorSlotType,? extends Item> armor, TagKey<Item>... tags) {
    armor.forEach((type, item) -> {
      for (TagKey<Item> tag : tags) {
        this.tag(tag).add(item);
      }
      this.tag(getArmorTag(type)).add(item);
    });
  }
}
