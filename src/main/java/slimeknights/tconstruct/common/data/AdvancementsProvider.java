package slimeknights.tconstruct.common.data;

import com.mojang.serialization.JsonOps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import slimeknights.tconstruct.compat.neoforged.neoforge.common.crafting.ConditionalAdvancement;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.json.predicate.tool.HasMaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInRangePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInSetPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
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
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AdvancementsProvider extends GenericDataProvider {

  /** Advancment consumer instance */
  protected Consumer<AdvancementHolder> advancementConsumer;
  /** Advancment consumer instance */
  protected BiConsumer<ResourceLocation, ConditionalAdvancement.Builder> conditionalConsumer;

  public AdvancementsProvider(PackOutput output) {
    super(output, Target.DATA_PACK, "advancements");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Advancements";
  }

  /** Generates the advancements */
  protected void generate() {
    // tinkering path
    AdvancementHolder materialsAndYou = builder(TinkerCommons.materialsAndYou, resource("tools/materials_and_you"), resource("textures/gui/advancement_background.png"), AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.materialsAndYou)));
    AdvancementHolder partBuilder = builder(TinkerTables.partBuilder, resource("tools/part_builder"), materialsAndYou, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_block", hasItem(TinkerTables.partBuilder)));
    builder(TinkerToolParts.pickHead.get().withMaterialForDisplay(MaterialIds.wood), resource("tools/make_part"), partBuilder, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_part", hasTag(TinkerTags.Items.TOOL_PARTS)));
    AdvancementHolder tinkerStation = builder(TinkerTables.tinkerStation, resource("tools/tinker_station"), partBuilder, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_block", hasItem(TinkerTables.tinkerStation)));
    AdvancementHolder tinkerTool = builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tinker_tool"), tinkerStation, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_tool", hasTag(TinkerTags.Items.MULTIPART_TOOL)));
    AdvancementHolder harvestLevel = builder(Items.NETHERITE_INGOT, resource("tools/netherite_tier"), tinkerTool, AdvancementType.GOAL, builder ->
      builder.addCriterion("harvest_level", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(new StatInSetPredicate<>(ToolStats.HARVEST_TIER, Tiers.NETHERITE)))));
    builder(Items.TARGET, resource("tools/perfect_aim"), tinkerTool, AdvancementType.GOAL, builder ->
      builder.addCriterion("accuracy", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(ToolStackPredicate.and(
        ToolStackPredicate.tag(TinkerTags.Items.BOWS),
        StatInRangePredicate.match(ToolStats.ACCURACY, 1)
      )))));
    // note that attack damage gets +1 from player attributes, so 20 is actually 21 damage with the tool
    builder(Items.ZOMBIE_HEAD, resource("tools/one_shot"), tinkerTool, AdvancementType.GOAL, builder ->
      builder.addCriterion("damage", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(StatInRangePredicate.min(ToolStats.ATTACK_DAMAGE, 20)))));
    builder(TinkerMaterials.manyullyn.getIngot(), resource("tools/material_master"), harvestLevel, AdvancementType.CHALLENGE, builder -> {
      Consumer<MaterialId> with = id -> builder.addCriterion(id.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(new HasMaterialPredicate(id))));
      // tier 1
      with.accept(MaterialIds.wood);
      with.accept(MaterialIds.flint);
      with.accept(MaterialIds.rock);
      with.accept(MaterialIds.bone);
      with.accept(MaterialIds.necroticBone);
      with.accept(MaterialIds.leather);
      with.accept(MaterialIds.string);
      with.accept(MaterialIds.vine);
      with.accept(MaterialIds.bamboo);
      with.accept(MaterialIds.chorus);
      // tier 2
      with.accept(MaterialIds.iron);
      with.accept(MaterialIds.searedStone);
      with.accept(MaterialIds.scorchedStone);
      with.accept(MaterialIds.copper);
      with.accept(MaterialIds.slimewood);
      with.accept(MaterialIds.slimeskin);
      with.accept(MaterialIds.skyslimeVine);
      with.accept(MaterialIds.weepingVine);
      with.accept(MaterialIds.twistingVine);
      with.accept(MaterialIds.whitestone);
      // tier 3
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.slimesteel);
      with.accept(MaterialIds.nahuatl);
      with.accept(MaterialIds.amethystBronze);
      with.accept(MaterialIds.pigIron);
      with.accept(MaterialIds.cobalt);
      with.accept(MaterialIds.darkthread);
      with.accept(MaterialIds.ichorskin);
      // tier 4
      with.accept(MaterialIds.manyullyn);
      with.accept(MaterialIds.hepatizon);
      with.accept(MaterialIds.cinderslime);
      with.accept(MaterialIds.queensSlime);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.blazewood);
      with.accept(MaterialIds.jeweledHide);
      with.accept(MaterialIds.knightmetal);
      with.accept(MaterialIds.knightslime);
      with.accept(MaterialIds.enderslimeVine);
    });
    builder(TinkerTools.travelersGear.get(ArmorItem.Type.HELMET).getRenderTool(), resource("tools/travelers_gear"), tinkerStation, AdvancementType.TASK, builder ->
      TinkerTools.travelersGear.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getName(), hasItem(armor))));
    builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tool_smith"), tinkerTool, AdvancementType.CHALLENGE, builder -> {
      Consumer<Item> with = item -> builder.addCriterion(BuiltInRegistries.ITEM.getKey(item).getPath(), hasItem(item));
      with.accept(TinkerTools.pickaxe.get());
      with.accept(TinkerTools.mattock.get());
      with.accept(TinkerTools.pickadze.get());
      with.accept(TinkerTools.handAxe.get());
      with.accept(TinkerTools.kama.get());
      with.accept(TinkerTools.dagger.get());
      with.accept(TinkerTools.sword.get());
    });
    AdvancementHolder modified = builder(Items.REDSTONE, resource("tools/modified"), tinkerTool, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_tool", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(ToolContextPredicate.HAS_UPGRADES))));
    //    builder(TinkerTools.cleaver.get().buildToolForRendering(), location("tools/glass_cannon"), modified, AdvancementType.CHALLENGE, builder ->
    //      builder.addCriterion()("crafted_tool", InventoryChangeTrigger.TriggerInstance.hasItems(ToolPredicate.builder()
    //                                                                                                  .withStat(StatPredicate.max(ToolStats.DURABILITY, 100))
    //                                                                                                  .withStat(StatPredicate.min(ToolStats.ATTACK_DAMAGE, 20))
    //                                                                                                  .build())));
    builder(Items.WRITABLE_BOOK, resource("tools/upgrade_slots"), modified, AdvancementType.CHALLENGE, builder ->
      builder.addCriterion("has_modified", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(
        ToolContextPredicate.and(
          HasModifierPredicate.hasUpgrade(ModifierIds.writable, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.recapitated, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.harmonious, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.forecast, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.gilded, 1)))))
    );

    // smeltery path
    AdvancementHolder punySmelting = builder(TinkerCommons.punySmelting, resource("smeltery/puny_smelting"), materialsAndYou, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.punySmelting)));
    AdvancementHolder melter = builder(TinkerSmeltery.searedMelter, resource("smeltery/melter"), punySmelting, AdvancementType.TASK, builder -> {
      Consumer<Block> with = block -> builder.addCriterion(BuiltInRegistries.BLOCK.getKey(block).getPath(), ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
      with.accept(TinkerSmeltery.searedMelter.get());
      with.accept(TinkerSmeltery.searedTable.get());
      with.accept(TinkerSmeltery.searedBasin.get());
      with.accept(TinkerSmeltery.searedFaucet.get());
      with.accept(TinkerSmeltery.searedHeater.get());
      TinkerSmeltery.searedTank.forEach(with);
      // first 4 are required, and then any of the last 5
      builder.requirements(new CountRequirementsStrategy(1, 1, 1, 1, 1 + TankType.values().length));
    });
    builder(TinkerSmeltery.toolHandleCast.getSand(), resource("smeltery/sand_casting"), melter, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_cast", hasTag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS)));
    AdvancementHolder goldCasting = builder(TinkerSmeltery.pickHeadCast, resource("smeltery/gold_casting"), melter, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_cast", hasTag(TinkerTags.Items.GOLD_CASTS)));
    builder(TinkerSmeltery.hammerHeadCast, resource("smeltery/cast_collector"), goldCasting, AdvancementType.GOAL, builder -> {
      Consumer<CastItemObject> with = cast -> builder.addCriterion(cast.getName().getPath(), hasItem(cast.get()));
      with.accept(TinkerSmeltery.ingotCast);
      with.accept(TinkerSmeltery.nuggetCast);
      with.accept(TinkerSmeltery.gemCast);
      with.accept(TinkerSmeltery.rodCast);
      with.accept(TinkerSmeltery.repairKitCast);
      // parts
      with.accept(TinkerSmeltery.pickHeadCast);
      with.accept(TinkerSmeltery.smallAxeHeadCast);
      with.accept(TinkerSmeltery.smallBladeCast);
      with.accept(TinkerSmeltery.adzeHeadCast);
      with.accept(TinkerSmeltery.hammerHeadCast);
      with.accept(TinkerSmeltery.broadBladeCast);
      with.accept(TinkerSmeltery.broadAxeHeadCast);
      with.accept(TinkerSmeltery.largePlateCast);
      with.accept(TinkerSmeltery.toolBindingCast);
      with.accept(TinkerSmeltery.toughBindingCast);
      with.accept(TinkerSmeltery.toolHandleCast);
      with.accept(TinkerSmeltery.toughHandleCast);
      with.accept(TinkerSmeltery.bowLimbCast);
      with.accept(TinkerSmeltery.bowGripCast);
      with.accept(TinkerSmeltery.helmetPlatingCast);
      with.accept(TinkerSmeltery.chestplatePlatingCast);
      with.accept(TinkerSmeltery.leggingsPlatingCast);
      with.accept(TinkerSmeltery.bootsPlatingCast);
      with.accept(TinkerSmeltery.mailleCast);
    });
    AdvancementHolder mightySmelting = builder(TinkerCommons.mightySmelting, resource("smeltery/mighty_smelting"), melter, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.mightySmelting)));
    AdvancementHolder smeltery = builder(TinkerSmeltery.smelteryController, resource("smeltery/structure"), mightySmelting, AdvancementType.TASK, builder ->
      builder.addCriterion("open_smeltery", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.smeltery.get())));
    AdvancementHolder anvil = builder(TinkerTables.tinkersAnvil, resource("smeltery/tinkers_anvil"), smeltery, AdvancementType.GOAL, builder -> {
      builder.addCriterion("crafted_overworld", hasItem(TinkerTables.tinkersAnvil));
      builder.addCriterion("crafted_nether", hasItem(TinkerTables.scorchedAnvil));
      builder.requirements(Strategy.OR);
    });
    builder(TinkerTools.veinHammer.get().getRenderTool(), resource("smeltery/tool_forge"), anvil, AdvancementType.CHALLENGE, builder -> {
      Consumer<ItemObject<?>> with = item -> builder.addCriterion(item.getId().getPath(), hasItem(item));
      with.accept(TinkerTools.sledgeHammer);
      with.accept(TinkerTools.veinHammer);
      with.accept(TinkerTools.excavator);
      with.accept(TinkerTools.broadAxe);
      with.accept(TinkerTools.scythe);
      with.accept(TinkerTools.cleaver);
      with.accept(TinkerTools.longbow);
      with.accept(TinkerTools.javelin);
    });
    builder(TinkerModifiers.silkyCloth, resource("smeltery/abilities"), anvil, AdvancementType.CHALLENGE, builder -> {
      Consumer<ModifierId> with = modifier -> builder.addCriterion(modifier.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(HasModifierPredicate.hasUpgrade(modifier, 1))));
      Consumer<LazyModifier> withL = modifier -> with.accept(modifier.getId());

      // sorted like the modifier tag provider tags
      // general
      with.accept(ModifierIds.expanded);
      with.accept(ModifierIds.gilded);
      with.accept(ModifierIds.luck);
      with.accept(ModifierIds.unbreakable);
      withL.accept(TinkerModifiers.melting);

      // melee
      with.accept(ModifierIds.blocking);
      withL.accept(TinkerModifiers.parrying);
      withL.accept(TinkerModifiers.dualWielding);
      with.accept(ModifierIds.spilling);

      // harvest
      with.accept(ModifierIds.autosmelt);
      withL.accept(TinkerModifiers.exchanging);
      with.accept(ModifierIds.silky);

      // ranged
      with.accept(ModifierIds.bulkQuiver);
      with.accept(ModifierIds.trickQuiver);
      with.accept(ModifierIds.crystalshot);
      with.accept(ModifierIds.multishot);
      with.accept(ModifierIds.ballista);
      with.accept(ModifierIds.slimeball);
      with.accept(ModifierIds.sliver);
      // fishing
      with.accept(ModifierIds.grapple);
      // throwing
      with.accept(ModifierIds.throwing);
      with.accept(ModifierIds.returning);
      with.accept(ModifierIds.channeling);

      // interaction
      with.accept(ModifierIds.bucketing);
      with.accept(ModifierIds.firestarter);
      with.accept(ModifierIds.glowing);
      with.accept(ModifierIds.pathing);
      with.accept(ModifierIds.stripping);
      with.accept(ModifierIds.tilling);
      with.accept(ModifierIds.brushing);
      // fluid
      with.accept(ModifierIds.spitting);
      with.accept(ModifierIds.splashing);
      with.accept(ModifierIds.slurping);
      // staff
      with.accept(ModifierIds.bonking);
      with.accept(ModifierIds.flinging);
      with.accept(ModifierIds.springing);
      with.accept(ModifierIds.warping);
      with.accept(ModifierIds.drillAttack);

      // armor
      with.accept(ModifierIds.protection);
      withL.accept(TinkerModifiers.bursting);
      withL.accept(TinkerModifiers.wetting);
      // helmet
      with.accept(ModifierIds.aquaAffinity);
      // chestplate
      withL.accept(TinkerModifiers.ambidextrous);
      with.accept(ModifierIds.reach);
      with.accept(ModifierIds.strength);
      with.accept(ModifierIds.wings);
      // leggings
      with.accept(ModifierIds.pockets);
      with.accept(ModifierIds.toolBelt);
      with.accept(ModifierIds.soulBelt);
      with.accept(ModifierIds.craftingTable);
      // boots
      with.accept(ModifierIds.bouncy);
      with.accept(ModifierIds.doubleJump);
      with.accept(ModifierIds.flamewake);
      with.accept(ModifierIds.frostWalker);
      with.accept(ModifierIds.snowdrift);
      // shield
      with.accept(ModifierIds.boundless);
      with.accept(ModifierIds.reflecting);
    });

    // foundry path
    AdvancementHolder fantasticFoundry = builder(TinkerCommons.fantasticFoundry, resource("foundry/fantastic_foundry"), materialsAndYou, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.fantasticFoundry)));
    builder(TinkerCommons.encyclopedia, resource("foundry/encyclopedia"), fantasticFoundry, AdvancementType.GOAL, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.encyclopedia)));
    AdvancementHolder alloyer = builder(TinkerSmeltery.scorchedAlloyer, resource("foundry/alloyer"), fantasticFoundry, AdvancementType.TASK, builder -> {
      Consumer<Block> with = block -> builder.addCriterion(BuiltInRegistries.BLOCK.getKey(block).getPath(), ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
      with.accept(TinkerSmeltery.scorchedAlloyer.get());
      with.accept(TinkerSmeltery.scorchedFaucet.get());
      with.accept(TinkerSmeltery.scorchedTable.get());
      with.accept(TinkerSmeltery.scorchedBasin.get());
      for (TankType type : TankType.values()) {
        with.accept(TinkerSmeltery.scorchedTank.get(type));
      }
      builder.requirements(new CountRequirementsStrategy(1, 1, 1, 1, 2, 2));
    });
    AdvancementHolder foundry = builder(TinkerSmeltery.foundryController, resource("foundry/structure"), alloyer, AdvancementType.TASK, builder ->
      builder.addCriterion("open_foundry", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.foundry.get())));
    AdvancementHolder blazingBlood = builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)), getTankWith(TinkerFluids.blazingBlood.get(), TankType.FUEL_GAUGE.getCapacity())),
            resource("foundry/blaze"), foundry, AdvancementType.GOAL, builder -> {
      Consumer<SearedTankBlock> with = block -> {
        builder.addCriterion(BuiltInRegistries.BLOCK.getKey(block).getPath(), hasItem(block));
        builder.requirements(Strategy.OR);
      };
      TinkerSmeltery.searedTank.forEach(with);
      TinkerSmeltery.scorchedTank.forEach(with);
    });
    builder(TinkerTools.plateArmor.get(ArmorItem.Type.CHESTPLATE).getRenderTool(), resource("foundry/plate_armor"), blazingBlood, AdvancementType.GOAL, builder ->
      TinkerTools.plateArmor.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getName(), hasItem(armor))));
    builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedLantern), getTankWith(TinkerFluids.moltenManyullyn.get(), TinkerSmeltery.scorchedLantern.get().getCapacity())),
            resource("foundry/manyullyn_lanterns"), foundry, AdvancementType.CHALLENGE, builder -> {
      Consumer<SearedLanternBlock> with = block -> {
        builder.addCriterion(BuiltInRegistries.BLOCK.getKey(block).getPath(), hasItem(block));
        builder.requirements(Strategy.OR);
      };
      with.accept(TinkerSmeltery.searedLantern.get());
      with.accept(TinkerSmeltery.scorchedLantern.get());
    });

    // exploration path
    AdvancementHolder tinkersGadgetry = builder(TinkerCommons.tinkersGadgetry, resource("world/tinkers_gadgetry"), materialsAndYou, AdvancementType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.tinkersGadgetry)));
    builder(TinkerWorld.slimeSapling.get(FoliageType.EARTH), resource("world/earth_island"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())));
    AdvancementHolder skyslimeIsland = builder(TinkerWorld.slimeSapling.get(FoliageType.SKY), resource("world/sky_island"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())));
    builder(TinkerWorld.slimeSapling.get(FoliageType.BLOOD), resource("world/blood_island"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())));
    builder(TinkerWorld.slimeSapling.get(FoliageType.ENDER), resource("world/ender_island"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())));
    builder(Items.CLAY_BALL, resource("world/clay_island"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())));
    builder(TinkerCommons.slimeball.get(SlimeType.ICHOR), resource("world/slime_collector"), tinkersGadgetry, AdvancementType.TASK, builder -> {
      for (SlimeType type : SlimeType.values()) {
        builder.addCriterion(type.getSerializedName(), hasTag(type.getSlimeballTag()));
      }
      builder.addCriterion("magma_cream", hasItem(Items.MAGMA_CREAM));
    });
    builder(TinkerGadgets.piggyBackpack, resource("world/piggybackpack"), tinkersGadgetry, AdvancementType.GOAL, builder ->
      builder.addCriterion("used_pack", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(TinkerGadgets.piggyBackpack), EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.PIG).build()))));
    AdvancementHolder slimesuit = builder(new MaterialIdNBT(List.of(MaterialIds.bone, MaterialIds.skyslime)).updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorItem.Type.CHESTPLATE))), resource("world/slimesuit"), skyslimeIsland, AdvancementType.GOAL, builder ->
      TinkerTools.slimesuit.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getName(), hasItem(armor))));
    builder(new MaterialIdNBT(List.of(MaterialIds.glass, MaterialIds.enderslime)).updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET))),
            resource("world/slimeskull"), slimesuit, AdvancementType.CHALLENGE, builder -> {
      Item helmet = TinkerTools.slimesuit.get(ArmorItem.Type.HELMET);
      Consumer<MaterialId> with = mat -> builder.addCriterion(mat.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(
        ToolContextPredicate.and(ToolContextPredicate.set(helmet), new HasMaterialPredicate(mat, 0)))));
      with.accept(MaterialIds.glass);
      with.accept(MaterialIds.blaze);
      // zombie
      with.accept(MaterialIds.leather);
      with.accept(MaterialIds.iron);
      with.accept(MaterialIds.copper);
      // spider
      with.accept(MaterialIds.string);
      with.accept(MaterialIds.darkthread);
      // skeleton
      with.accept(MaterialIds.bone);
      with.accept(MaterialIds.ice);
      with.accept(MaterialIds.necroticBone);
      // piglin
      with.accept(MaterialIds.gold);
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.pigIron);
      // end
      with.accept(MaterialIds.enderPearl);
      with.accept(MaterialIds.dragonScale);
      // crafted
      with.accept(MaterialIds.venombone);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.knightmetal);
    });
    builder(TinkerTools.battlesign.get().getRenderTool(), resource("world/ancient_tools"), tinkersGadgetry, AdvancementType.CHALLENGE, builder -> {
      Consumer<ItemObject<?>> with = item -> builder.addCriterion(item.getId().getPath(), hasItem(item));
      with.accept(TinkerTools.meltingPan);
      with.accept(TinkerTools.warPick);
      with.accept(TinkerTools.battlesign);
      with.accept(TinkerTools.swasher);
    });

    // internal advancements
    hiddenBuilder(resource("internal/starting_book"), ConfigEnabledCondition.SPAWN_WITH_BOOK, builder -> {
      builder.addCriterion("tick", PlayerTrigger.TriggerInstance.tick());
      builder.rewards(AdvancementRewards.Builder.loot(ResourceKey.create(Registries.LOOT_TABLE, TConstruct.getResource("gameplay/starting_book"))));
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
  private Criterion<?> hasTag(TagKey<Item> tag) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
  }

  /**
   * Creates an item predicate for an item
   */
  private Criterion<?> hasItem(ItemLike item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    Set<ResourceLocation> set = Sets.newHashSet();
    record Conditional(ResourceLocation id, ConditionalAdvancement.Builder builder) {}
    List<AdvancementHolder> advancements = new ArrayList<>();
    List<Conditional> conditionals = new ArrayList<>();
    this.advancementConsumer = advancement -> {
      if (!set.add(advancement.id())) {
        throw new IllegalStateException("Duplicate advancement " + advancement.id());
      } else {
        advancements.add(advancement);
      }
    };
    this.conditionalConsumer = (id, advancement) -> {
      if (!set.add(id)) {
        throw new IllegalStateException("Duplicate advancement " + id);
      } else {
        conditionals.add(new Conditional(id, advancement));
      }
    };
    generate();
    return allOf(Stream.concat(
      advancements.stream().map(advancement -> saveJson(cache, advancement.id(), Advancement.CODEC.encodeStart(JsonOps.INSTANCE, advancement.value()).getOrThrow())),
      conditionals.stream().map(conditional -> saveJson(cache, conditional.id, conditional.builder.write(conditional.id)))
    ));
  }


  /* Helpers */

  /** Gets a tinkers resource location */
  protected ResourceLocation resource(String name) {
    return TConstruct.getResource(name);
  }

  /**
   * Helper for making an advancement builder
   * @param display      Item to display
   * @param name         AdvancementHolder name
   * @param parent       Parent advancement
   * @param frame        Frame type
   * @return  Builder
   */
  protected AdvancementHolder builder(ItemLike display, ResourceLocation name, AdvancementHolder parent, AdvancementType frame, Consumer<Advancement.Builder> consumer) {
    return builder(new ItemStack(display), name, parent, frame, consumer);
  }

  /**
   * Helper for making an advancement builder
   * @param display      Stack to display
   * @param name         AdvancementHolder name
   * @param parent       Parent advancement
   * @param frame        Frame type
   * @return  Builder
   */
  protected AdvancementHolder builder(ItemStack display, ResourceLocation name, AdvancementHolder parent, AdvancementType frame, Consumer<Advancement.Builder> consumer) {
    return builder(display, name, (ResourceLocation)null, frame, builder -> {
      builder.parent(parent);
      consumer.accept(builder);
    });
  }

  /**
   * Helper for making an advancement builder
   * @param display      Item to display
   * @param name         AdvancementHolder name
   * @param background   Background image
   * @param frame        Frame type
   * @return  Builder
   */
  protected AdvancementHolder builder(ItemLike display, ResourceLocation name, @Nullable ResourceLocation background, AdvancementType frame, Consumer<Advancement.Builder> consumer) {
    return builder(new ItemStack(display), name, background, frame, consumer);
  }

  /** Makes an advancement translation key from the given ID */
  private static String makeTranslationKey(ResourceLocation advancement) {
    return "advancements." + advancement.getNamespace() + "." + advancement.getPath().replace('/', '.');
  }

  /**
   * Helper for making an advancement builder
   * @param display      Stack to display
   * @param name         AdvancementHolder name
   * @param background   Background image
   * @param frame        Frame type
   * @return  Builder
   */
  protected AdvancementHolder builder(ItemStack display, ResourceLocation name, @Nullable ResourceLocation background, AdvancementType frame, Consumer<Advancement.Builder> consumer) {
    Advancement.Builder builder = Advancement.Builder
      .advancement().display(display,
                             Component.translatable(makeTranslationKey(name) + ".title"),
                             Component.translatable(makeTranslationKey(name) + ".description"),
                             background, frame, true, frame != AdvancementType.TASK, false);
    consumer.accept(builder);
    return builder.save(advancementConsumer, name.toString());
  }

  /**
   * Helper for making an advancement builder
   * @param name         AdvancementHolder name
   */
  @SuppressWarnings("SameParameterValue")
  protected void hiddenBuilder(ResourceLocation name, ICondition condition, Consumer<Advancement.Builder> consumer) {
    Advancement.Builder builder = Advancement.Builder.advancement();
    consumer.accept(builder);
    ConditionalAdvancement.Builder conditionalBuilder = new ConditionalAdvancement.Builder();
    conditionalBuilder.addCondition(condition);
    conditionalBuilder.addAdvancement(builder);
    conditionalConsumer.accept(name, conditionalBuilder);
  }
}
