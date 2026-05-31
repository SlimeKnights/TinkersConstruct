package slimeknights.tconstruct.world;

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
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.world.logic.AncientToolItemListing;

import java.util.Collections;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class WorldEvents {
  /* Heads */

  @SubscribeEvent
  static void livingVisibility(LivingVisibilityEvent event) {
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    ItemStack helmet = event.getEntity().getItemBySlot(EquipmentSlot.HEAD);
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

  @SubscribeEvent
  static void wanderingTrades(WandererTradesEvent event) {
    // add ancient tools to the wandering trader table
    int weight = Config.COMMON.wandererAncientToolWeight.get();
    if (weight > 0) {
      event.getRareTrades().addAll(Collections.nCopies(weight, AncientToolItemListing.INSTANCE));
    }
  }
}
