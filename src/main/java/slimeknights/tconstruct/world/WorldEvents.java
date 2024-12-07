package slimeknights.tconstruct.world;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {
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

  private static final RandomMaterial RANDOM_MATERIAL = RandomMaterial.random().build();
  private static final List<RandomMaterial> ANCIENT_MATERIALS = List.of(RANDOM_MATERIAL, RANDOM_MATERIAL);

  // ancient tool equipment
  @SubscribeEvent
  static void livingSpawn(SpecialSpawn event) {
    Mob mob = event.getEntity();
    EntityType<?> type = mob.getType();
    // 5% chance for a zombie piglin to spawn with a battle sign, doesn't mean they drop it though
    if ((type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.PIGLIN || type == EntityType.PIGLIN_BRUTE)
        && event.getLevel() instanceof ServerLevelAccessor level && level.getRandom().nextFloat() < 0.05f) {
      // forge event runs before finalize spawn so we can't just set our item now or it may get overwritten
      // instead, we cancel the event (which blocks vanilla finalize), then finalize ourself, then can set our item after
      event.setCanceled(true);
      mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), event.getSpawnReason(), null, null);

      // only replace golden sword or golden axes with our item, if they are holding nothing or a crossbow do nothing
      Item item = mob.getMainHandItem().getItem();
      if (item == Items.GOLDEN_SWORD || item == Items.GOLDEN_AXE) {
        // build ancient tool with random materials
        ModifiableItem weapon = TinkerTools.battlesign.get();
        ToolDefinition definition = weapon.getToolDefinition();
        ToolStack tool = ToolStack.createTool(weapon, definition, RandomMaterial.build(ToolMaterialHook.stats(definition), ANCIENT_MATERIALS, level.getRandom()));
        mob.setItemInHand(InteractionHand.MAIN_HAND, tool.createStack());
      }
    }
  }
}
