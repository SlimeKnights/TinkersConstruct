package slimeknights.tconstruct.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.loot.RandomMaterial;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Arrays;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {
  /* Loot injection */

  /**
   * Injects an entry into a loot pool
   * @param event      Loot table event
   * @param poolName   Pool name
   * @param entries    Entry to inject
   */
  private static void injectInto(LootTableLoadEvent event, String poolName, LootPoolEntryContainer... entries) {
    LootPool pool = event.getTable().getPool(poolName);
    //noinspection ConstantConditions method is annotated wrongly
    if (pool != null) {
      int oldLength = pool.entries.length;
      pool.entries = Arrays.copyOf(pool.entries, oldLength + entries.length);
      System.arraycopy(entries, 0, pool.entries, oldLength, entries.length);
    }
  }

  /** Makes a seed injection loot entry */
  private static LootPoolEntryContainer makeSeed(FoliageType type, int weight) {
    return LootItem.lootTableItem(TinkerWorld.slimeGrassSeeds.get(type)).setWeight(weight)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).build();
  }

  /** Makes a sapling injection loot entry */
  private static LootPoolEntryContainer makeSapling(FoliageType type, int weight) {
    return LootItem.lootTableItem(TinkerWorld.slimeSapling.get(type)).setWeight(weight).build();
  }

  @SubscribeEvent
  static void onLootTableLoad(LootTableLoadEvent event) {
    ResourceLocation name = event.getName();
    if ("minecraft".equals(name.getNamespace())) {
      switch (name.getPath()) {
        // sky
        case "chests/simple_dungeon":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "pool1", makeSeed(FoliageType.EARTH, 3), makeSeed(FoliageType.SKY, 7));
            injectInto(event, "main", makeSapling(FoliageType.EARTH, 3), makeSapling(FoliageType.SKY, 7));
          }
          break;
        // ichor
        case "chests/nether_bridge":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "main", makeSeed(FoliageType.BLOOD, 5));
          }
          break;
        case "chests/bastion_bridge":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "pool2", makeSapling(FoliageType.BLOOD, 1));
          }
          break;
        // ender
        case "chests/end_city_treasure":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "main", makeSeed(FoliageType.ENDER, 5), makeSapling(FoliageType.ENDER, 3));
          }
          break;

        // barter for molten blaze lanterns
        case "gameplay/piglin_bartering": {
          int weight = Config.COMMON.barterBlazingBlood.get();
          if (weight > 0) {
            injectInto(event, "main", LootItem.lootTableItem(TinkerSmeltery.scorchedLantern).setWeight(weight)
                                              .apply(SetFluidLootFunction.builder(new FluidStack(TinkerFluids.blazingBlood.get(), FluidValues.LANTERN_CAPACITY)))
                                              .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                              .build());
          }
          break;
        }

          // randomly swap vanilla tool for a tinkers tool
        case "chests/spawn_bonus_chest": {
          int weight = Config.COMMON.tinkerToolBonusChest.get();
          if (weight > 0) {
            RandomMaterial randomHead = RandomMaterial.random(HeadMaterialStats.ID).tier(1).build();
            RandomMaterial firstHandle = RandomMaterial.firstWithStat(HandleMaterialStats.ID); // should be wood
            RandomMaterial randomBinding = RandomMaterial.random(ExtraMaterialStats.ID).tier(1).build();
            injectInto(event, "main", LootItem.lootTableItem(TinkerTools.handAxe.get())
                                              .setWeight(weight)
                                              .apply(AddToolDataFunction.builder()
                                                               .addMaterial(randomHead)
                                                               .addMaterial(firstHandle)
                                                               .addMaterial(randomBinding))
                                              .build());
            injectInto(event, "pool1", LootItem.lootTableItem(TinkerTools.pickaxe.get())
                                               .setWeight(weight)
                                               .apply(AddToolDataFunction.builder()
                                                               .addMaterial(randomHead)
                                                               .addMaterial(firstHandle)
                                                               .addMaterial(randomBinding))
                                               .build());
          }
          break;
        }
      }
    }
  }


  /* Heads */

  @SubscribeEvent
  static void livingVisibility(LivingVisibilityEvent event) {
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    LivingEntity entity = event.getEntity();
    ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
    Item item = helmet.getItem();
    if (item != Items.AIR && TinkerWorld.headItems.contains(item)) {
      if (lookingEntity.getType() == ((TinkerHeadType)((SkullBlock)((BlockItem)item).getBlock()).getType()).getType()) {
        event.modifyVisibility(0.5f);
      }
    }
  }

  @SubscribeEvent
  static void creeperKill(LivingDropsEvent event) {
    DamageSource source = event.getSource();
    if (source != null) {
      Entity entity = source.getEntity();
      if (entity instanceof Creeper creeper) {
        if (creeper.canDropMobsSkull()) {
          LivingEntity dying = event.getEntity();
          TinkerHeadType headType = TinkerHeadType.fromEntityType(dying.getType());
          if (headType != null && Config.COMMON.headDrops.get(headType).get()) {
            creeper.increaseDroppedSkulls();
            event.getDrops().add(dying.spawnAtLocation(TinkerWorld.heads.get(headType)));
          }
        }
      }
    }
  }
}
