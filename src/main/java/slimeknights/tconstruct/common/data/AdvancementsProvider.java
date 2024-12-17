package slimeknights.tconstruct.common.data;

import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.data.GenericDataProvider;
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
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancementsProvider extends GenericDataProvider {

  /** Advancment consumer instance */
  protected Consumer<Advancement> advancementConsumer;
  /** Advancment consumer instance */
  protected BiConsumer<ResourceLocation, ConditionalAdvancement.Builder> conditionalConsumer;

  public AdvancementsProvider(DataGenerator generatorIn) {
    super(generatorIn, PackType.SERVER_DATA, "advancements");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Advancements";
  }

  /** Generates the advancements */
  protected void generate() {
    // tinkering path
    Advancement materialsAndYou = builder(TinkerCommons.materialsAndYou, resource("tools/materials_and_you"), resource("textures/gui/advancement_background.png"), FrameType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.materialsAndYou)));
    Advancement partBuilder = builder(TinkerTables.partBuilder, resource("tools/part_builder"), materialsAndYou, FrameType.TASK, builder ->
      builder.addCriterion("crafted_block", hasItem(TinkerTables.partBuilder)));
    builder(TinkerToolParts.pickHead.get().withMaterialForDisplay(MaterialIds.wood), resource("tools/make_part"), partBuilder, FrameType.TASK, builder ->
      builder.addCriterion("crafted_part", hasTag(TinkerTags.Items.TOOL_PARTS)));
    Advancement tinkerStation = builder(TinkerTables.tinkerStation, resource("tools/tinker_station"), partBuilder, FrameType.TASK, builder ->
      builder.addCriterion("crafted_block", hasItem(TinkerTables.tinkerStation)));
    Advancement tinkerTool = builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tinker_tool"), tinkerStation, FrameType.TASK, builder ->
      builder.addCriterion("crafted_tool", hasTag(TinkerTags.Items.MULTIPART_TOOL)));
    Advancement harvestLevel = builder(Items.NETHERITE_INGOT, resource("tools/netherite_tier"), tinkerTool, FrameType.GOAL, builder ->
      builder.addCriterion("harvest_level", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(new StatInSetPredicate<>(ToolStats.HARVEST_TIER, Tiers.NETHERITE)))));
    builder(Items.TARGET, resource("tools/perfect_aim"), tinkerTool, FrameType.GOAL, builder ->
      builder.addCriterion("accuracy", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(StatInRangePredicate.match(ToolStats.ACCURACY, 1)))));
    // note that attack damage gets +1 from player attributes, so 20 is actually 21 damage with the tool
    builder(Items.ZOMBIE_HEAD, resource("tools/one_shot"), tinkerTool, FrameType.GOAL, builder ->
      builder.addCriterion("damage", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(StatInRangePredicate.min(ToolStats.ATTACK_DAMAGE, 20)))));
    builder(TinkerMaterials.manyullyn.getIngot(), resource("tools/material_master"), harvestLevel, FrameType.CHALLENGE, builder -> {
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
      with.accept(MaterialIds.skyslimeVine);
      with.accept(MaterialIds.whitestone);
      // tier 3
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.slimesteel);
      with.accept(MaterialIds.nahuatl);
      with.accept(MaterialIds.amethystBronze);
      with.accept(MaterialIds.pigIron);
      with.accept(MaterialIds.cobalt);
      with.accept(MaterialIds.darkthread);
      // tier 4
      with.accept(MaterialIds.manyullyn);
      with.accept(MaterialIds.hepatizon);
      with.accept(MaterialIds.queensSlime);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.blazewood);
      with.accept(MaterialIds.ancientHide);
      with.accept(MaterialIds.enderslimeVine);
    });
    builder(TinkerTools.travelersGear.get(ArmorSlotType.HELMET).getRenderTool(), resource("tools/travelers_gear"), tinkerStation, FrameType.TASK, builder ->
      TinkerTools.travelersGear.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getSerializedName(), hasItem(armor))));
    builder(TinkerTools.pickaxe.get().getRenderTool(), resource("tools/tool_smith"), tinkerTool, FrameType.CHALLENGE, builder -> {
      Consumer<Item> with = item -> builder.addCriterion(Registry.ITEM.getKey(item).getPath(), hasItem(item));
      with.accept(TinkerTools.pickaxe.get());
      with.accept(TinkerTools.mattock.get());
      with.accept(TinkerTools.pickadze.get());
      with.accept(TinkerTools.handAxe.get());
      with.accept(TinkerTools.kama.get());
      with.accept(TinkerTools.dagger.get());
      with.accept(TinkerTools.sword.get());
    });
    Advancement modified = builder(Items.REDSTONE, resource("tools/modified"), tinkerTool, FrameType.TASK, builder ->
      builder.addCriterion("crafted_tool", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(ToolContextPredicate.HAS_UPGRADES))));
    //    builder(TinkerTools.cleaver.get().buildToolForRendering(), location("tools/glass_cannon"), modified, FrameType.CHALLENGE, builder ->
    //      builder.addCriterion()("crafted_tool", InventoryChangeTrigger.TriggerInstance.hasItems(ToolPredicate.builder()
    //                                                                                                  .withStat(StatPredicate.max(ToolStats.DURABILITY, 100))
    //                                                                                                  .withStat(StatPredicate.min(ToolStats.ATTACK_DAMAGE, 20))
    //                                                                                                  .build())));
    builder(Items.WRITABLE_BOOK, resource("tools/upgrade_slots"), modified, FrameType.CHALLENGE, builder ->
      builder.addCriterion("has_modified", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(
        ToolContextPredicate.and(
          HasModifierPredicate.hasUpgrade(ModifierIds.writable, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.recapitated, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.harmonious, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.resurrected, 1),
          HasModifierPredicate.hasUpgrade(ModifierIds.gilded, 1)))))
    );

    // smeltery path
    Advancement punySmelting = builder(TinkerCommons.punySmelting, resource("smeltery/puny_smelting"), materialsAndYou, FrameType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.punySmelting)));
    Advancement melter = builder(TinkerSmeltery.searedMelter, resource("smeltery/melter"), punySmelting, FrameType.TASK, builder -> {
      Consumer<Block> with = block -> builder.addCriterion(Registry.BLOCK.getKey(block).getPath(), PlacedBlockTrigger.TriggerInstance.placedBlock(block));
      with.accept(TinkerSmeltery.searedMelter.get());
      with.accept(TinkerSmeltery.searedTable.get());
      with.accept(TinkerSmeltery.searedBasin.get());
      with.accept(TinkerSmeltery.searedFaucet.get());
      with.accept(TinkerSmeltery.searedHeater.get());
      TinkerSmeltery.searedTank.forEach(with);
      // first 4 are required, and then any of the last 5
      builder.requirements(new CountRequirementsStrategy(1, 1, 1, 1, 1 + TankType.values().length));
    });
    builder(TinkerSmeltery.toolHandleCast.getSand(), resource("smeltery/sand_casting"), melter, FrameType.TASK, builder ->
      builder.addCriterion("crafted_cast", hasTag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS)));
    Advancement goldCasting = builder(TinkerSmeltery.pickHeadCast, resource("smeltery/gold_casting"), melter, FrameType.TASK, builder ->
      builder.addCriterion("crafted_cast", hasTag(TinkerTags.Items.GOLD_CASTS)));
    builder(TinkerSmeltery.hammerHeadCast, resource("smeltery/cast_collector"), goldCasting, FrameType.GOAL, builder -> {
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
      with.accept(TinkerSmeltery.hammerHeadCast);
      with.accept(TinkerSmeltery.broadBladeCast);
      with.accept(TinkerSmeltery.broadAxeHeadCast);
      with.accept(TinkerSmeltery.toolBindingCast);
      with.accept(TinkerSmeltery.roundPlateCast);
      with.accept(TinkerSmeltery.largePlateCast);
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
    Advancement mightySmelting = builder(TinkerCommons.mightySmelting, resource("smeltery/mighty_smelting"), melter, FrameType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.mightySmelting)));
    Advancement smeltery = builder(TinkerSmeltery.smelteryController, resource("smeltery/structure"), mightySmelting, FrameType.TASK, builder ->
      builder.addCriterion("open_smeltery", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.smeltery.get())));
    Advancement anvil = builder(TinkerTables.tinkersAnvil, resource("smeltery/tinkers_anvil"), smeltery, FrameType.GOAL, builder -> {
      builder.addCriterion("crafted_overworld", hasItem(TinkerTables.tinkersAnvil));
      builder.addCriterion("crafted_nether", hasItem(TinkerTables.scorchedAnvil));
      builder.requirements(RequirementsStrategy.OR);
    });
    builder(TinkerTools.veinHammer.get().getRenderTool(), resource("smeltery/tool_forge"), anvil, FrameType.CHALLENGE, builder -> {
      Consumer<Item> with = item -> builder.addCriterion(Registry.ITEM.getKey(item).getPath(), hasItem(item));
      with.accept(TinkerTools.sledgeHammer.get());
      with.accept(TinkerTools.veinHammer.get());
      with.accept(TinkerTools.excavator.get());
      with.accept(TinkerTools.broadAxe.get());
      with.accept(TinkerTools.scythe.get());
      with.accept(TinkerTools.cleaver.get());
      with.accept(TinkerTools.longbow.get());
    });
    builder(TinkerModifiers.silkyCloth, resource("smeltery/abilities"), anvil, FrameType.CHALLENGE, builder -> {
      Consumer<ModifierId> with = modifier -> builder.addCriterion(modifier.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(HasModifierPredicate.hasUpgrade(modifier, 1))));
      Consumer<LazyModifier> withL = modifier -> with.accept(modifier.getId());

      // general
      with.accept(ModifierIds.gilded);
      with.accept(ModifierIds.luck);
      withL.accept(TinkerModifiers.unbreakable);
      // armor
      with.accept(ModifierIds.protection);
      // helmet
      with.accept(ModifierIds.aquaAffinity);
      withL.accept(TinkerModifiers.slurping);
      withL.accept(TinkerModifiers.zoom);
      // chestplate
      withL.accept(TinkerModifiers.ambidextrous);
      with.accept(ModifierIds.reach);
      with.accept(ModifierIds.strength);
      // leggings
      with.accept(ModifierIds.pockets);
      with.accept(ModifierIds.toolBelt);
      withL.accept(TinkerModifiers.wetting);
      // boots
      withL.accept(TinkerModifiers.bouncy);
      withL.accept(TinkerModifiers.doubleJump);
      withL.accept(TinkerModifiers.flamewake);
      with.accept(ModifierIds.frostWalker);
      with.accept(ModifierIds.longFall);
      with.accept(ModifierIds.pathMaker);
      with.accept(ModifierIds.plowing);
      with.accept(ModifierIds.snowdrift);
      // shield
      with.accept(ModifierIds.boundless);
      withL.accept(TinkerModifiers.reflecting);
      // harvest
      withL.accept(TinkerModifiers.autosmelt);
      withL.accept(TinkerModifiers.exchanging);
      withL.accept(TinkerModifiers.expanded);
      withL.accept(TinkerModifiers.silky);
      // interact
      withL.accept(TinkerModifiers.bucketing);
      withL.accept(TinkerModifiers.firestarter);
      withL.accept(TinkerModifiers.glowing);
      with.accept(ModifierIds.pathing);
      with.accept(ModifierIds.stripping);
      with.accept(ModifierIds.tilling);
      // staff
      withL.accept(TinkerModifiers.bonking);
      withL.accept(TinkerModifiers.flinging);
      withL.accept(TinkerModifiers.springing);
      withL.accept(TinkerModifiers.warping);
      // weapon
      withL.accept(TinkerModifiers.dualWielding);
      withL.accept(TinkerModifiers.melting);
      withL.accept(TinkerModifiers.blocking);
      withL.accept(TinkerModifiers.parrying);
      // ranged
      withL.accept(TinkerModifiers.crystalshot);
      withL.accept(TinkerModifiers.multishot);
      with.accept(ModifierIds.bulkQuiver);
      with.accept(ModifierIds.trickQuiver);
      // fluid
      withL.accept(TinkerModifiers.spitting);
      withL.accept(TinkerModifiers.spilling);
      withL.accept(TinkerModifiers.splashing);
      withL.accept(TinkerModifiers.bursting);
    });

    // foundry path
    Advancement fantasticFoundry = builder(TinkerCommons.fantasticFoundry, resource("foundry/fantastic_foundry"), materialsAndYou, FrameType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.fantasticFoundry)));
    builder(TinkerCommons.encyclopedia, resource("foundry/encyclopedia"), fantasticFoundry, FrameType.GOAL, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.encyclopedia)));
    Advancement alloyer = builder(TinkerSmeltery.scorchedAlloyer, resource("foundry/alloyer"), fantasticFoundry, FrameType.TASK, builder -> {
      Consumer<Block> with = block -> builder.addCriterion(Registry.BLOCK.getKey(block).getPath(), PlacedBlockTrigger.TriggerInstance.placedBlock(block));
      with.accept(TinkerSmeltery.scorchedAlloyer.get());
      with.accept(TinkerSmeltery.scorchedFaucet.get());
      with.accept(TinkerSmeltery.scorchedTable.get());
      with.accept(TinkerSmeltery.scorchedBasin.get());
      for (TankType type : TankType.values()) {
        with.accept(TinkerSmeltery.scorchedTank.get(type));
      }
      builder.requirements(new CountRequirementsStrategy(1, 1, 1, 1, 2, 2));
    });
    Advancement foundry = builder(TinkerSmeltery.foundryController, resource("foundry/structure"), alloyer, FrameType.TASK, builder ->
      builder.addCriterion("open_foundry", BlockContainerOpenedTrigger.Instance.container(TinkerSmeltery.foundry.get())));
    Advancement blazingBlood = builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)), getTankWith(TinkerFluids.blazingBlood.get(), TankType.FUEL_GAUGE.getCapacity())),
            resource("foundry/blaze"), foundry, FrameType.GOAL, builder -> {
      Consumer<SearedTankBlock> with = block -> {
        CompoundTag nbt = new CompoundTag();
        nbt.put(NBTTags.TANK, getTankWith(TinkerFluids.blazingBlood.get(), block.getCapacity()).writeToNBT(new CompoundTag()));
        builder.addCriterion(Registry.BLOCK.getKey(block).getPath(),
                              InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(block).hasNbt(nbt).build()));
        builder.requirements(RequirementsStrategy.OR);
      };
      TinkerSmeltery.searedTank.forEach(with);
      TinkerSmeltery.scorchedTank.forEach(with);
    });
    builder(TinkerTools.plateArmor.get(ArmorSlotType.CHESTPLATE).getRenderTool(), resource("foundry/plate_armor"), blazingBlood, FrameType.GOAL, builder ->
      TinkerTools.plateArmor.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getSerializedName(), hasItem(armor))));
    builder(TankItem.setTank(new ItemStack(TinkerSmeltery.scorchedLantern), getTankWith(TinkerFluids.moltenManyullyn.get(), TinkerSmeltery.scorchedLantern.get().getCapacity())),
            resource("foundry/manyullyn_lanterns"), foundry, FrameType.CHALLENGE, builder -> {
      Consumer<SearedLanternBlock> with = block -> {
        CompoundTag nbt = new CompoundTag();
        nbt.put(NBTTags.TANK, getTankWith(TinkerFluids.moltenManyullyn.get(), block.getCapacity()).writeToNBT(new CompoundTag()));
        builder.addCriterion(Registry.BLOCK.getKey(block).getPath(),
                              InventoryChangeTrigger.TriggerInstance.hasItems(new ItemPredicate(null, Collections.singleton(block.asItem()), MinMaxBounds.Ints.atLeast(64), MinMaxBounds.Ints.ANY,
                                                                                                EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, new NbtPredicate(nbt))));
        builder.requirements(RequirementsStrategy.OR);
      };
      with.accept(TinkerSmeltery.searedLantern.get());
      with.accept(TinkerSmeltery.scorchedLantern.get());
    });

    // exploration path
    Advancement tinkersGadgetry = builder(TinkerCommons.tinkersGadgetry, resource("world/tinkers_gadgetry"), materialsAndYou, FrameType.TASK, builder ->
      builder.addCriterion("crafted_book", hasItem(TinkerCommons.tinkersGadgetry)));
    builder(TinkerWorld.slimeSapling.get(FoliageType.EARTH), resource("world/earth_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(Objects.requireNonNull(TinkerStructures.earthSlimeIsland)))));
    builder(TinkerWorld.slimeSapling.get(FoliageType.SKY), resource("world/sky_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(Objects.requireNonNull(TinkerStructures.skySlimeIsland)))));
    builder(TinkerWorld.slimeSapling.get(FoliageType.BLOOD), resource("world/blood_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(Objects.requireNonNull(TinkerStructures.bloodIsland)))));
    Advancement enderslimeIsland = builder(TinkerWorld.slimeSapling.get(FoliageType.ENDER), resource("world/ender_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(Objects.requireNonNull(TinkerStructures.endSlimeIsland)))));
    builder(Items.CLAY_BALL, resource("world/clay_island"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("found_island", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(Objects.requireNonNull(TinkerStructures.clayIsland)))));
    Advancement slimes = builder(TinkerCommons.slimeball.get(SlimeType.ICHOR), resource("world/slime_collector"), tinkersGadgetry, FrameType.TASK, builder -> {
      for (SlimeType type : SlimeType.values()) {
        builder.addCriterion(type.getSerializedName(), hasTag(type.getSlimeballTag()));
      }
      builder.addCriterion("magma_cream", hasItem(Items.MAGMA_CREAM));
    });
    builder(TinkerGadgets.piggyBackpack, resource("world/piggybackpack"), tinkersGadgetry, FrameType.GOAL, builder ->
      builder.addCriterion("used_pack", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().of(TinkerGadgets.piggyBackpack), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.PIG).build()))));
    Advancement slimesuit = builder(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE).getRenderTool(), resource("world/slimesuit"), enderslimeIsland, FrameType.GOAL, builder ->
      TinkerTools.slimesuit.forEach((type, armor) -> builder.addCriterion("crafted_" + type.getSerializedName(), hasItem(armor))));
    builder(new MaterialIdNBT(Collections.singletonList(MaterialIds.glass)).updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorSlotType.HELMET))),
            resource("world/slimeskull"), slimesuit, FrameType.CHALLENGE, builder -> {
      Item helmet = TinkerTools.slimesuit.get(ArmorSlotType.HELMET);
      Consumer<MaterialId> with = mat -> builder.addCriterion(mat.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofContext(
        ToolContextPredicate.and(ToolContextPredicate.set(helmet), new HasMaterialPredicate(mat, 0)))));
      with.accept(MaterialIds.glass);
      with.accept(MaterialIds.bone);
      with.accept(MaterialIds.necroticBone);
      with.accept(MaterialIds.rottenFlesh);
      with.accept(MaterialIds.enderPearl);
      with.accept(MaterialIds.venombone);
      with.accept(MaterialIds.string);
      with.accept(MaterialIds.darkthread);
      with.accept(MaterialIds.iron);
      with.accept(MaterialIds.copper);
      with.accept(MaterialIds.blazingBone);
      with.accept(MaterialIds.gold);
      with.accept(MaterialIds.roseGold);
      with.accept(MaterialIds.pigIron);
    });

    // internal advancements
    hiddenBuilder(resource("internal/starting_book"), ConfigEnabledCondition.SPAWN_WITH_BOOK, builder -> {
      builder.addCriterion("tick", new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.getId(), EntityPredicate.Composite.ANY));
      builder.rewards(AdvancementRewards.Builder.loot(TConstruct.getResource("gameplay/starting_book")));
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
  private CriterionTriggerInstance hasTag(TagKey<Item> tag) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
  }

  /**
   * Creates an item predicate for an item
   */
  private CriterionTriggerInstance hasItem(ItemLike item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
  }

  @Override
  public void run(CachedOutput cache) {
    Set<ResourceLocation> set = Sets.newHashSet();
    this.advancementConsumer = advancement -> {
      if (!set.add(advancement.getId())) {
        throw new IllegalStateException("Duplicate advancement " + advancement.getId());
      } else {
        saveJson(cache, advancement.getId(), advancement.deconstruct().serializeToJson());
      }
    };
    this.conditionalConsumer = (id, advancement) -> {
      if (!set.add(id)) {
        throw new IllegalStateException("Duplicate advancement " + id);
      } else {
        saveJson(cache, id, advancement.write());
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
  protected Advancement builder(ItemLike display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
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
      builder.parent(parent);
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
  protected Advancement builder(ItemLike display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
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
      .advancement().display(display,
                             Component.translatable(makeTranslationKey(name) + ".title"),
                             Component.translatable(makeTranslationKey(name) + ".description"),
                             background, frame, true, frame != FrameType.TASK, false);
    consumer.accept(builder);
    return builder.save(advancementConsumer, name.toString());
  }

  /**
   * Helper for making an advancement builder
   * @param name         Advancement name
   */
  protected void hiddenBuilder(ResourceLocation name, ICondition condition, Consumer<Advancement.Builder> consumer) {
    Advancement.Builder builder = Advancement.Builder.advancement();
    consumer.accept(builder);
    ConditionalAdvancement.Builder conditionalBuilder = new ConditionalAdvancement.Builder();
    conditionalBuilder.addCondition(condition);
    conditionalBuilder.addAdvancement(builder);
    conditionalBuilder.write();
    conditionalConsumer.accept(name, conditionalBuilder);
  }
}
