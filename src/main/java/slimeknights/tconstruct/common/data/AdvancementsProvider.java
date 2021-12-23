package slimeknights.tconstruct.common.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds.IntBound;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.PlayerEntityInteractionTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.tools.ToolPredicate;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.shared.inventory.BlockContainerOpenedTrigger;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.SearedLanternBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancementsProvider extends GenericDataProvider {

  /** Advancment consumer instance */
  protected Consumer<Advancement> advancementConsumer;

  public AdvancementsProvider(DataGenerator generatorIn) {
    super(generatorIn, "advancements");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Advancements";
  }

  /** Generates the advancements */
  protected void generate() {
    // tinkering path
    Advancement materialsAndYou = builder(TinkerCommons.materialsAndYou, resource("tools/materials_and_you"), resource("textures/gui/advancement_background.png"), FrameType.TASK, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.materialsAndYou)));
    Advancement partBuilder = builder(TinkerTables.partBuilder, resource("tools/part_builder"), materialsAndYou, FrameType.TASK, builder ->
      builder.withCriterion("crafted_block", hasItem(TinkerTables.partBuilder)));
    builder(TinkerToolParts.pickaxeHead.get().withMaterialForDisplay(MaterialIds.wood), resource("tools/make_part"), partBuilder, FrameType.TASK, builder ->
      builder.withCriterion("crafted_part", hasTag(TinkerTags.Items.TOOL_PARTS)));
    Advancement tinkerStation = builder(TinkerTables.tinkerStation, resource("tools/tinker_station"), partBuilder, FrameType.TASK, builder ->
      builder.withCriterion("crafted_block", hasItem(TinkerTables.tinkerStation)));
    Advancement tinkerTool = builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tinker_tool"), tinkerStation, FrameType.TASK, builder ->
      builder.withCriterion("crafted_tool", hasTag(TinkerTags.Items.MULTIPART_TOOL)));
    builder(TinkerMaterials.manyullyn.getIngot(), resource("tools/material_master"), tinkerTool, FrameType.CHALLENGE, builder -> {
      Consumer<MaterialId> with = id -> builder.withCriterion(id.getPath(), InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder().withMaterial(id).build()));
      // tier 1
      with.accept(MaterialIds.wood);
      with.accept(MaterialIds.flint);
      with.accept(MaterialIds.stone);
      with.accept(MaterialIds.bone);
      with.accept(MaterialIds.necroticBone);
      with.accept(MaterialIds.leather);
      with.accept(MaterialIds.string);
      with.accept(MaterialIds.vine);
      // tier 2
      with.accept(MaterialIds.iron);
      with.accept(MaterialIds.searedStone);
      with.accept(MaterialIds.scorchedStone);
      with.accept(MaterialIds.copper);
      with.accept(MaterialIds.slimewood);
      with.accept(MaterialIds.chain);
      with.accept(MaterialIds.skyslimeVine);
      // tier 3
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.slimesteel);
      with.accept(MaterialIds.nahuatl);
      with.accept(MaterialIds.tinkersBronze);
      with.accept(MaterialIds.pigIron);
      with.accept(MaterialIds.cobalt);
      // tier 4
      with.accept(MaterialIds.manyullyn);
      with.accept(MaterialIds.hepatizon);
      with.accept(MaterialIds.queensSlime);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.enderslimeVine);
    });
    builder(TinkerTools.travelersGear.get(ArmorSlotType.HELMET).getRenderTool(), resource("tools/travelers_gear"), tinkerStation, FrameType.TASK, builder ->
      TinkerTools.travelersGear.forEach((type, armor) -> builder.withCriterion("crafted_" + type.getString(), hasItem(armor))));
    builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tool_smith"), tinkerTool, FrameType.CHALLENGE, builder -> {
      Consumer<Item> with = item -> builder.withCriterion(Objects.requireNonNull(item.getRegistryName()).getPath(), hasItem(item));
      with.accept(TinkerTools.pickaxe.get());
      with.accept(TinkerTools.mattock.get());
      with.accept(TinkerTools.handAxe.get());
      with.accept(TinkerTools.kama.get());
      with.accept(TinkerTools.dagger.get());
      with.accept(TinkerTools.sword.get());
    });
    Advancement modified = builder(Items.REDSTONE, resource("tools/modified"), tinkerTool, FrameType.TASK, builder ->
      builder.withCriterion("crafted_tool", InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder().hasUpgrades(true).build())));
    //    builder(TinkerTools.cleaver.get().buildToolForRendering(), location("tools/glass_cannon"), modified, FrameType.CHALLENGE, builder ->
    //      builder.withCriterion("crafted_tool", InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder()
    //                                                                                                  .withStat(StatPredicate.max(ToolStats.DURABILITY, 100))
    //                                                                                                  .withStat(StatPredicate.min(ToolStats.ATTACK_DAMAGE, 20))
    //                                                                                                  .build())));
    builder(Items.WRITABLE_BOOK, resource("tools/upgrade_slots"), modified, FrameType.CHALLENGE, builder ->
      builder.withCriterion("has_modified", InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder().upgrades(
        ModifierMatch.list(5, ModifierMatch.entry(TinkerModifiers.writable.get()),
                           ModifierMatch.entry(TinkerModifiers.recapitated.get()),
                           ModifierMatch.entry(TinkerModifiers.harmonious.get()),
                           ModifierMatch.entry(TinkerModifiers.resurrected.get()),
                           ModifierMatch.entry(TinkerModifiers.gilded.get()))).build()))
    );

    // smeltery path
    Advancement punySmelting = builder(TinkerCommons.punySmelting, resource("smeltery/puny_smelting"), materialsAndYou, FrameType.TASK, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.punySmelting)));
    Advancement melter = builder(TinkerSmeltery.searedMelter, resource("smeltery/melter"), punySmelting, FrameType.TASK, builder -> {
      Consumer<Block> with = block -> builder.withCriterion(Objects.requireNonNull(block.getRegistryName()).getPath(), PlacedBlockTrigger.Instance.placedBlock(block));
      with.accept(TinkerSmeltery.searedMelter.get());
      with.accept(TinkerSmeltery.searedTable.get());
      with.accept(TinkerSmeltery.searedBasin.get());
      with.accept(TinkerSmeltery.searedFaucet.get());
      with.accept(TinkerSmeltery.searedHeater.get());
      TinkerSmeltery.searedTank.forEach(with);
      // first 4 are required, and then any of the last 5
      builder.withRequirementsStrategy(new CountRequirementsStrategy(1, 1, 1, 1, 1 + TankType.values().length));
    });
    builder(TinkerSmeltery.toolHandleCast.getSand(), resource("smeltery/sand_casting"), melter, FrameType.TASK, builder ->
      builder.withCriterion("crafted_cast", hasTag(TinkerSmeltery.blankCast.getSingleUseTag())));
    Advancement goldCasting = builder(TinkerSmeltery.pickaxeHeadCast, resource("smeltery/gold_casting"), melter, FrameType.TASK, builder ->
      builder.withCriterion("crafted_cast", hasTag(TinkerTags.Items.GOLD_CASTS)));
    builder(TinkerSmeltery.hammerHeadCast, resource("smeltery/cast_collector"), goldCasting, FrameType.GOAL, builder -> {
      Consumer<CastItemObject> with = cast -> builder.withCriterion(cast.getName().getPath(), hasItem(cast.get()));
      with.accept(TinkerSmeltery.blankCast);
      with.accept(TinkerSmeltery.ingotCast);
      with.accept(TinkerSmeltery.nuggetCast);
      with.accept(TinkerSmeltery.gemCast);
      with.accept(TinkerSmeltery.rodCast);
      with.accept(TinkerSmeltery.repairKitCast);
      // parts
      with.accept(TinkerSmeltery.pickaxeHeadCast);
      with.accept(TinkerSmeltery.smallAxeHeadCast);
      with.accept(TinkerSmeltery.smallBladeCast);
      with.accept(TinkerSmeltery.hammerHeadCast);
      with.accept(TinkerSmeltery.broadBladeCast);
      with.accept(TinkerSmeltery.broadAxeHeadCast);
      with.accept(TinkerSmeltery.toolBindingCast);
      with.accept(TinkerSmeltery.largePlateCast);
      with.accept(TinkerSmeltery.toolHandleCast);
      with.accept(TinkerSmeltery.toughHandleCast);
    });
    Advancement mightySmelting = builder(TinkerCommons.mightySmelting, resource("smeltery/mighty_smelting"), melter, FrameType.TASK, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.mightySmelting)));
    Advancement smeltery = builder(TinkerSmeltery.smelteryController, resource("smeltery/structure"), mightySmelting, FrameType.TASK, builder ->
      builder.withCriterion("open_smeltery", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.smeltery.get())));
    Advancement anvil = builder(TinkerTables.tinkersAnvil, resource("smeltery/tinkers_anvil"), smeltery, FrameType.GOAL, builder -> {
      builder.withCriterion("crafted_overworld", hasItem(TinkerTables.tinkersAnvil));
      builder.withCriterion("crafted_nether", hasItem(TinkerTables.scorchedAnvil));
      builder.withRequirementsStrategy(IRequirementsStrategy.OR);
    });
    builder(TinkerTools.veinHammer.get().getRenderTool(), resource("smeltery/tool_forge"), anvil, FrameType.CHALLENGE, builder -> {
      Consumer<Item> with = item -> builder.withCriterion(Objects.requireNonNull(item.getRegistryName()).getPath(), hasItem(item));
      with.accept(TinkerTools.sledgeHammer.get());
      with.accept(TinkerTools.veinHammer.get());
      with.accept(TinkerTools.excavator.get());
      with.accept(TinkerTools.broadAxe.get());
      with.accept(TinkerTools.scythe.get());
      with.accept(TinkerTools.cleaver.get());
    });
    builder(TinkerModifiers.silkyCloth, resource("smeltery/abilities"), anvil, FrameType.CHALLENGE, builder -> {
      Consumer<Supplier<? extends Modifier>> with = modifier -> builder.withCriterion(modifier.get().getId().getPath(), InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder().modifiers(ModifierMatch.entry(modifier.get())).build()));
      // general
      with.accept(TinkerModifiers.gilded);
      with.accept(TinkerModifiers.luck);
      with.accept(TinkerModifiers.reach);
      with.accept(TinkerModifiers.unbreakable);
      // armor
      with.accept(TinkerModifiers.aquaAffinity);
      with.accept(TinkerModifiers.bouncy);
      with.accept(TinkerModifiers.doubleJump);
      with.accept(TinkerModifiers.flamewake);
      with.accept(TinkerModifiers.frostWalker);
      with.accept(TinkerModifiers.looting);
      with.accept(TinkerModifiers.pathMaker);
      with.accept(TinkerModifiers.plowing);
      with.accept(TinkerModifiers.pockets);
      with.accept(TinkerModifiers.shieldStrap);
      with.accept(TinkerModifiers.slurping);
      with.accept(TinkerModifiers.snowdrift);
      with.accept(TinkerModifiers.strength);
      with.accept(TinkerModifiers.toolBelt);
      with.accept(TinkerModifiers.unarmed);
      with.accept(TinkerModifiers.zoom);
      // harvest
      with.accept(TinkerModifiers.autosmelt);
      with.accept(TinkerModifiers.exchanging);
      with.accept(TinkerModifiers.expanded);
      with.accept(TinkerModifiers.silky);
      // interact
      with.accept(TinkerModifiers.bucketing);
      with.accept(TinkerModifiers.firestarter);
      with.accept(TinkerModifiers.glowing);
      with.accept(TinkerModifiers.pathing);
      with.accept(TinkerModifiers.stripping);
      with.accept(TinkerModifiers.tilling);
      // weapon
      with.accept(TinkerModifiers.dualWielding);
      with.accept(TinkerModifiers.melting);
      with.accept(TinkerModifiers.spilling);
    });

    // foundry path
    Advancement fantasticFoundry = builder(TinkerCommons.fantasticFoundry, resource("foundry/fantastic_foundry"), materialsAndYou, FrameType.TASK, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.fantasticFoundry)));
    builder(TinkerCommons.encyclopedia, resource("foundry/encyclopedia"), fantasticFoundry, FrameType.GOAL, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.encyclopedia)));
    Advancement alloyer = builder(TinkerSmeltery.scorchedAlloyer, resource("foundry/alloyer"), fantasticFoundry, FrameType.TASK, builder -> {
      Consumer<Block> with = block -> builder.withCriterion(Objects.requireNonNull(block.getRegistryName()).getPath(), PlacedBlockTrigger.Instance.placedBlock(block));
      with.accept(TinkerSmeltery.scorchedAlloyer.get());
      with.accept(TinkerSmeltery.scorchedFaucet.get());
      with.accept(TinkerSmeltery.scorchedTable.get());
      with.accept(TinkerSmeltery.scorchedBasin.get());
      for (TankType type : TankType.values()) {
        with.accept(TinkerSmeltery.scorchedTank.get(type));
      }
      builder.withRequirementsStrategy(new CountRequirementsStrategy(1, 1, 1, 1, 2, 2));
    });
    Advancement foundry = builder(TinkerSmeltery.foundryController, resource("foundry/structure"), alloyer, FrameType.TASK, builder ->
      builder.withCriterion("open_foundry", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.foundry.get())));
    Advancement blazingBlood = builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)), getTankWith(TinkerFluids.blazingBlood.get(), TankType.FUEL_GAUGE.getCapacity())),
            resource("foundry/blaze"), foundry, FrameType.GOAL, builder -> {
      Consumer<SearedTankBlock> with = block -> {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(NBTTags.TANK, getTankWith(TinkerFluids.blazingBlood.get(), block.getCapacity()).writeToNBT(new CompoundNBT()));
        builder.withCriterion(Objects.requireNonNull(block.getRegistryName()).getPath(),
                              InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(block).nbt(nbt).build()));
        builder.withRequirementsStrategy(IRequirementsStrategy.OR);
      };
      TinkerSmeltery.searedTank.forEach(with);
      TinkerSmeltery.scorchedTank.forEach(with);
    });
    builder(TinkerTools.plateArmor.get(ArmorSlotType.CHESTPLATE).getRenderTool(), resource("foundry/plate_armor"), blazingBlood, FrameType.GOAL, builder ->
      TinkerTools.plateArmor.forEach((type, armor) -> builder.withCriterion("crafted_" + type.getString(), hasItem(armor))));
    builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedLantern), getTankWith(TinkerFluids.moltenManyullyn.get(), TinkerSmeltery.scorchedLantern.get().getCapacity())),
            resource("foundry/manyullyn_lanterns"), foundry, FrameType.CHALLENGE, builder -> {
      Consumer<SearedLanternBlock> with = block -> {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(NBTTags.TANK, getTankWith(TinkerFluids.moltenManyullyn.get(), block.getCapacity()).writeToNBT(new CompoundNBT()));
        builder.withCriterion(Objects.requireNonNull(block.getRegistryName()).getPath(),
                              InventoryChangeTrigger.Instance.forItems(new ItemPredicate(null, block.asItem(), IntBound.atLeast(64), IntBound.UNBOUNDED,
                                                                                         EnchantmentPredicate.enchantments, EnchantmentPredicate.enchantments, null, new NBTPredicate(nbt))));
        builder.withRequirementsStrategy(IRequirementsStrategy.OR);
      };
      with.accept(TinkerSmeltery.searedLantern.get());
      with.accept(TinkerSmeltery.scorchedLantern.get());
    });

    // exploration path
    Advancement tinkersGadgetry = builder(TinkerCommons.tinkersGadgetry, resource("world/tinkers_gadgetry"), materialsAndYou, FrameType.TASK, builder ->
      builder.withCriterion("crafted_book", hasItem(TinkerCommons.tinkersGadgetry)));
    builder(TinkerWorld.slimeSapling.get(SlimeType.EARTH), resource("world/earth_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("found_island", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(TinkerStructures.earthSlimeIsland.get()))));
    builder(TinkerWorld.slimeSapling.get(SlimeType.SKY), resource("world/sky_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("found_island", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(TinkerStructures.skySlimeIsland.get()))));
    builder(TinkerWorld.slimeSapling.get(SlimeType.BLOOD), resource("world/blood_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("found_island", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(TinkerStructures.bloodSlimeIsland.get()))));
    Advancement enderslimeIsland = builder(TinkerWorld.slimeSapling.get(SlimeType.ENDER), resource("world/ender_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("found_island", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(TinkerStructures.endSlimeIsland.get()))));
    builder(Items.CLAY_BALL, resource("world/clay_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("found_island", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(TinkerStructures.clayIsland.get()))));
    Advancement slimes = builder(TinkerCommons.slimeball.get(SlimeType.ICHOR), resource("world/slime_collector"), tinkersGadgetry, FrameType.TASK, builder -> {
      for (SlimeType type : SlimeType.values()) {
        builder.withCriterion(type.getString(), hasTag(type.getSlimeballTag()));
      }
      builder.withCriterion("magma_cream", hasItem(Items.MAGMA_CREAM));
    });
    builder(TinkerGadgets.slimeSling.get(SlimeType.ENDER), resource("world/slime_sling"), slimes, FrameType.CHALLENGE, builder -> {
      JsonObject boundJSON = new JsonObject();
      boundJSON.addProperty("max", 150);
      IntBound mojangDeletedTheMaxMethods = IntBound.fromJson(boundJSON);
      TinkerGadgets.slimeSling.forEach((type, sling) -> builder.withCriterion(type.getString(), ItemDurabilityTrigger.Instance.create(AndPredicate.ANY_AND, ItemPredicate.Builder.create().item(sling).build(), mojangDeletedTheMaxMethods)));
    });
    builder(TinkerGadgets.piggyBackpack, resource("world/piggybackpack"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.withCriterion("used_pack", PlayerEntityInteractionTrigger.Instance.create(AndPredicate.ANY_AND, ItemPredicate.Builder.create().item(TinkerGadgets.piggyBackpack), EntityPredicate.AndPredicate.createAndFromEntityCondition(EntityPredicate.Builder.create().type(EntityType.PIG).build()))));
    Advancement slimesuit = builder(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE).getRenderTool(), resource("world/slimesuit"), enderslimeIsland, FrameType.GOAL, builder ->
      TinkerTools.slimesuit.forEach((type, armor) -> builder.withCriterion("crafted_" + type.getString(), hasItem(armor))));
    builder(new MaterialIdNBT(Collections.singletonList(MaterialIds.gunpowder)).updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorSlotType.HELMET))),
            resource("world/slimeskull"), slimesuit, FrameType.CHALLENGE, builder -> {
      Item helmet = TinkerTools.slimesuit.get(ArmorSlotType.HELMET);
      Consumer<MaterialId> with = mat -> builder.withCriterion(mat.getPath(), InventoryChangeTrigger.Instance.forItems(ToolPredicate.builder(helmet).withMaterial(mat).build()));
      with.accept(MaterialIds.gunpowder);
      with.accept(MaterialIds.bone);
      with.accept(MaterialIds.necroticBone);
      with.accept(MaterialIds.rottenFlesh);
      with.accept(MaterialIds.enderPearl);
      with.accept(MaterialIds.bloodbone);
      with.accept(MaterialIds.spider);
      with.accept(MaterialIds.venom);
      with.accept(MaterialIds.iron);
      with.accept(MaterialIds.copper);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.gold);
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.pigIron);
    });
  }

  /** Gets a tank filled with the given fluid */
  private static FluidTank getTankWith(Fluid fluid, int capacity) {
    FluidTank tank = new FluidTank(capacity);
    tank.fill(new FluidStack(fluid, capacity), FluidAction.EXECUTE);
    return tank;
  }

  /**
   * Creates an item predicate for a tag
   */
  private ICriterionInstance hasTag(ITag<Item> tag) {
    return InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(tag).build());
  }

  /**
   * Creates an item predicate for an item
   */
  private ICriterionInstance hasItem(IItemProvider item) {
    return InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(item).build());
  }

  @Override
  public void act(DirectoryCache cache) {
    Path path = this.generator.getOutputFolder();
    Set<ResourceLocation> set = Sets.newHashSet();
    this.advancementConsumer = advancement -> {
      if (!set.add(advancement.getId())) {
        throw new IllegalStateException("Duplicate advancement " + advancement.getId());
      } else {
        saveThing(cache, advancement.getId(), advancement.copy().serialize());
      }
    };
    generate();
  }


  /* Helpers */

  /** Gets a tinkers resource location */
  protected ResourceLocation resource(String name) {
    return TConstruct.getResource(name);
  }

  /**
   * Helper for making an advancement builder
   * @param display      Item to display
   * @param name         Advancement name
   * @param parent       Parent advancement
   * @param frame        Frame type
   * @return  Builder
   */
  protected Advancement builder(IItemProvider display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
    return builder(new ItemStack(display), name, parent, frame, consumer);
  }

  /**
   * Helper for making an advancement builder
   * @param display      Stack to display
   * @param name         Advancement name
   * @param parent       Parent advancement
   * @param frame        Frame type
   * @return  Builder
   */
  protected Advancement builder(ItemStack display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
    return builder(display, name, (ResourceLocation)null, frame, builder -> {
      builder.withParent(parent);
      consumer.accept(builder);
    });
  }

  /**
   * Helper for making an advancement builder
   * @param display      Item to display
   * @param name         Advancement name
   * @param background   Background image
   * @param frame        Frame type
   * @return  Builder
   */
  protected Advancement builder(IItemProvider display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
    return builder(new ItemStack(display), name, background, frame, consumer);
  }

  /** Makes an advancement translation key from the given ID */
  private static String makeTranslationKey(ResourceLocation advancement) {
    return "advancements." + advancement.getNamespace() + "." + advancement.getPath().replace('/', '.');
  }

  /**
   * Helper for making an advancement builder
   * @param display      Stack to display
   * @param name         Advancement name
   * @param background   Background image
   * @param frame        Frame type
   * @return  Builder
   */
  protected Advancement builder(ItemStack display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
    Advancement.Builder builder = Advancement.Builder
      .builder().withDisplay(display,
                             new TranslationTextComponent(makeTranslationKey(name) + ".title"),
                             new TranslationTextComponent(makeTranslationKey(name) + ".description"),
                             background, frame, true, frame != FrameType.TASK, false);
    consumer.accept(builder);
    return builder.register(advancementConsumer, name.toString());
  }
}
