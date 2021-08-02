package slimeknights.tconstruct.gadgets;

import net.minecraft.block.SkullBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;

public class GadgetEvents {
  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity == null) {
      return;
    }

    // do not care about client handles of this event except for players
    boolean isPlayer = entity instanceof PlayerEntity;
    boolean isClient = entity.getEntityWorld().isRemote;
    if (isClient && !isPlayer) {
      return;
    }

    // some entities are natively bouncy
    if (isPlayer || !TinkerTags.EntityTypes.BOUNCY.contains(entity.getType())) {
      // otherwise, is the thing is wearing slime boots?
      ItemStack feet = entity.getItemStackFromSlot(EquipmentSlotType.FEET);
      if (!(feet.getItem() instanceof SlimeBootsItem)) {
        return;
      }
    }

    // let's get bouncyyyyy
    if (event.getDistance() > 2) {
      // if crouching, take damage
      if (entity.isCrouching()) {
        event.setDamageMultiplier(0.2f);
      } else {
        event.setDamageMultiplier(0);
        entity.fallDistance =  0.0F;

        // players only bounce on the client, due to movement rules
        if (!isPlayer || isClient) {
          double f = 0.91d + 0.04d;
          // only slow down half as much when bouncing
          entity.setMotion(entity.getMotion().x / f, entity.getMotion().y * -0.9, entity.getMotion().z / f);
          entity.isAirBorne = true;
          entity.setOnGround(false);
        }
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
        entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
        SlimeBounceHandler.addBounceHandler(entity, entity.getMotion().y);
      }
    }
  }

  @SubscribeEvent
  public void livingVisibility(LivingVisibilityEvent event) {
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    LivingEntity entity = event.getEntityLiving();
    Item helmet = entity.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem();
    Item item = helmet.getItem();
    if (item != Items.AIR && TinkerGadgets.headItems.contains(item)) {
      if (lookingEntity.getType() == ((TinkerHeadType)((SkullBlock)((BlockItem)item).getBlock()).skullType).getType()) {
        event.modifyVisibility(0.5f);
      }
      EntityType<?> lookingType = lookingEntity.getType();
    }
  }

  @SubscribeEvent
  public void creeperKill(LivingDropsEvent event) {
    DamageSource source = event.getSource();
    if (source != null) {
      Entity entity = source.getTrueSource();
      if (entity instanceof CreeperEntity) {
        CreeperEntity creeper = (CreeperEntity)entity;
        if (creeper.ableToCauseSkullDrop()) {
          LivingEntity dying = event.getEntityLiving();
          TinkerHeadType headType = TinkerHeadType.fromEntityType(dying.getType());
          if (headType != null && Config.COMMON.headDrops.get(headType).get()) {
            creeper.incrementDroppedSkulls();
            event.getDrops().add(dying.entityDropItem(TinkerGadgets.heads.get(headType)));
          }
        }
      }
    }
  }
}
