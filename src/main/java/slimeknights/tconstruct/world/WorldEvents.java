package slimeknights.tconstruct.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.json.AddToolDataFunction;
import slimeknights.tconstruct.library.json.RandomMaterial;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Arrays;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {
  @SubscribeEvent
  static void onBiomeLoad(BiomeLoadingEvent event) {
    BiomeGenerationSettingsBuilder generation = event.getGeneration();

    BiomeCategory category = event.getCategory();
    if (category == BiomeCategory.NETHER) {
      if (Config.COMMON.generateCobalt.get()) {
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_SMALL);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_LARGE);
      }
      // ichor can be anywhere
      if (Config.COMMON.ichorGeodes.get()) {
        generation.addFeature(Decoration.LOCAL_MODIFICATIONS, TinkerWorld.ichorGeode.getPlacedGeode());
      }
    }
    else if (category == BiomeCategory.THEEND) {
      // slime spawns anywhere, uses the grass
      event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TinkerWorld.enderSlimeEntity.get(), 10, 2, 4));
      ResourceLocation name = event.getName();
      // geodes only on outer islands
      if (Config.COMMON.enderGeodes.get() && !Biomes.THE_END.location().equals(name) && !Biomes.THE_VOID.location().equals(name)) {
        generation.addFeature(Decoration.LOCAL_MODIFICATIONS, TinkerWorld.enderGeode.getPlacedGeode());
      }
    }
    else {
      // slime spawns anywhere, uses the grass
      event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TinkerWorld.earthSlimeEntity.get(), 100, 2, 4));
      event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TinkerWorld.skySlimeEntity.get(), 100, 2, 4));

      // earth spawns anywhere, sky does not spawn in ocean (looks weird)
      if (Config.COMMON.earthGeodes.get()) {
        generation.addFeature(Decoration.LOCAL_MODIFICATIONS, TinkerWorld.earthGeode.getPlacedGeode());
      }
      if (Config.COMMON.skyGeodes.get() && category != BiomeCategory.OCEAN && category != BiomeCategory.RIVER && category != BiomeCategory.SWAMP) {
        generation.addFeature(Decoration.LOCAL_MODIFICATIONS, TinkerWorld.skyGeode.getPlacedGeode());
      }
    }
  }


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
  private static LootPoolEntryContainer makeSeed(SlimeType type, int weight) {
    return LootItem.lootTableItem(TinkerWorld.slimeGrassSeeds.get(type)).setWeight(weight)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).build();
  }

  /** Makes a sapling injection loot entry */
  private static LootPoolEntryContainer makeSapling(SlimeType type, int weight) {
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
            injectInto(event, "pool1", makeSeed(SlimeType.EARTH, 3), makeSeed(SlimeType.SKY, 7));
            injectInto(event, "main", makeSapling(SlimeType.EARTH, 3), makeSapling(SlimeType.SKY, 7));
          }
          break;
        // ichor
        case "chests/nether_bridge":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "main", makeSeed(SlimeType.BLOOD, 5));
          }
          break;
        case "chests/bastion_bridge":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "pool2", makeSapling(SlimeType.BLOOD, 1));
          }
          break;
        // ender
        case "chests/end_city_treasure":
          if (Config.COMMON.slimyLootChests.get()) {
            injectInto(event, "main", makeSeed(SlimeType.ENDER, 5), makeSapling(SlimeType.ENDER, 3));
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

  @SubscribeEvent
  static void serverStarting(ServerAboutToStartEvent event) {
    TinkerStructures.addStructures();
  }


  /* Heads */

  @SubscribeEvent
  static void livingVisibility(LivingVisibilityEvent event) {
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    LivingEntity entity = event.getEntityLiving();
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
          LivingEntity dying = event.getEntityLiving();
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
