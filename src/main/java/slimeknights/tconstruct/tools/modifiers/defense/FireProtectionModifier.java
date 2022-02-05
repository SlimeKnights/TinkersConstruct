package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;
import slimeknights.tconstruct.tools.logic.VanillaMaxLevel;

import javax.annotation.Nullable;
import java.util.List;

public class FireProtectionModifier extends IncrementalModifier {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<FireData> FIRE_DATA = TConstruct.createKey("fire_protection");
  public FireProtectionModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingUpdateEvent.class, FireProtectionModifier::livingTick);
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && source.isFire()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }


  /*
   * Equipping, these hooks let us determine our max level and the vanilla level without parsing NBT every tick
   */

  /** Gets the level of fire aspect for a particular slot */
  private int getEnchantmentLevel(EquipmentChangeContext context, EquipmentSlot slotType) {
    if (context.getToolInSlot(slotType) == null) {
      return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION, context.getEntity().getItemBySlot(slotType));
    }
    return 0;
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (slot.getType() == Type.ARMOR && !entity.level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData != null) {
          // clear level
          fireData.modifier.set(slot, 0);

          // if this was the last, stop tracking
          // no need to update vanilla here, the slot in charge will take care of it in equipment change
          if (fireData.modifier.getMax() == 0) {
            data.remove(FIRE_DATA);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (!entity.level.isClientSide && slot.getType() == Type.ARMOR && !tool.isBroken()) {
      float scaledLevel = getScaledLevel(tool, level);
      context.getTinkerData().ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData == null) {
          // not calculated yet? add all vanilla values to the tracker
          fireData = new FireData();
          for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
            fireData.vanilla.set(slotType, getEnchantmentLevel(context, slotType));
          }
          // fetch fire timer as well
          int fireTimer = entity.getRemainingFireTicks();
          if (fireTimer > 0) {
            fireData.finish = entity.tickCount + fireTimer + 1;
          }
          data.put(FIRE_DATA, fireData);
        }
        // add ourself to the data
        fireData.modifier.set(slot, scaledLevel);
        fireData.vanilla.set(slot, 0);
      });
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, int level, EquipmentChangeContext context, EquipmentSlot slotType) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (!entity.level.isClientSide && slot.getType() == Type.ARMOR) {
      // so another slot changed, update vanilla fire data
      context.getTinkerData().ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        // only need to check if we are in charge of fire data
        if (fireData != null && fireData.modifier.getMaxSlot() == slotType) {
          fireData.vanilla.set(slot, getEnchantmentLevel(context, slot));
        }
      });
    }
  }

  /**
   * Ticking
   * checks each tick if the player has more fire than we remember, if so means they are recently on fire and fire should be reduced
   * does not handle forced fire ticks well, but all vanilla uses of that method are forcing it to 1
   */
  private static void livingTick(LivingUpdateEvent event) {
    // handled on entity tick as we don't need any tool data, its all cached on the player
    // plus, saves us doing slot checks every tool with the modifier
    LivingEntity entity = event.getEntityLiving();
    // no need to run clientside
    if (!entity.level.isClientSide && !entity.isSpectator()) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData != null) {
          // if the vanilla level is greater than ours, nothing to do
          int maxVanilla = fireData.vanilla.getMax();
          float maxLevel = fireData.modifier.getMax();
          if (maxVanilla < maxLevel) {
            // if the entity is not on fire, make sure fire finish is before the current time
            // if not, could miss getting lit on fire later
            int currentFire = entity.getRemainingFireTicks();
            if (currentFire <= 0) {
              if (fireData.finish > entity.tickCount) {
                fireData.finish = 0;
              }
              // entity is on fire, we determine new fire by the end of the fire being larger than our predicted end
              // if its smaller, we assume this is not new fire
            } else if (currentFire + entity.tickCount > fireData.finish) {
              // alright, time to reduce fire amount, if vanilla already did it reduce less
              int newFire = currentFire;
              if (maxVanilla > 0) {
                // we already removed 15% of fire per vanilla level, we want to remove an additional 15% of the original per our level
                // means calculating the fire before vanilla made changes and then taking the difference between us and vanilla out
                newFire -= Mth.floor((currentFire / (1 - maxVanilla * 0.15f)) * (maxLevel - maxVanilla) * 0.15f);
              } else {
                // remove 15% of fire per level
                newFire -= Mth.floor(currentFire * maxLevel * 0.15f);
              }
              if (newFire < 0) {
                newFire = 0;
                fireData.finish = 0;
              } else {
                fireData.finish = newFire + entity.tickCount + 1; // set 1 higher make it slightly faster if increasing fire multiple times in the same tick (fire blocks)
              }
              entity.setRemainingFireTicks(newFire);
            }
          }
        }
      });
    }
  }

  /** Data class for fire protection */
  protected static class FireData {
    /** Level of the modifier */
    public final ModifierMaxLevel modifier = new ModifierMaxLevel();
    /** Level of the vanilla enchantment */
    public final VanillaMaxLevel vanilla = new VanillaMaxLevel();
    /** Tick time when the fire is expected to finish, to determine if fire changed */
    int finish = 0;
  }
}
