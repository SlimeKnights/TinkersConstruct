package slimeknights.tconstruct.tools.modifiers.armor;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class FireProtectionModifier extends IncrementalModifier {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<FireData> FIRE_DATA = TConstruct.createKey("fire_protection");

  public FireProtectionModifier() {
    super(0x4F4A47);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && source.isFireDamage()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }


  /*
   * Equipping, these hooks let us determine our max level and the vanilla level without parsing NBT every tick
   */

  /** Gets the level of fire aspect for a particular slot */
  private static int getFireAspectLevel(EquipmentChangeContext context, EquipmentSlotType slotType) {
    if (context.getToolInSlot(slotType) == null) {
      return EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, context.getEntity().getItemStackFromSlot(slotType));
    }
    return 0;
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlotType slot = context.getChangedSlot();
    if (slot.getSlotType() == Group.ARMOR && !entity.getEntityWorld().isRemote) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData != null) {
          // clear level
          fireData.setLevel(slot, 0);

          // if this was the last fire protection, stop tracking
          // no need to update vanilla here, the slot in charge will take care of it in equipment change
          if (fireData.maxLevel == 0) {
            data.remove(FIRE_DATA);
          }

        }
      });
    }
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlotType slot = context.getChangedSlot();
    if (!entity.getEntityWorld().isRemote && slot.getSlotType() == Group.ARMOR && !tool.isBroken()) {
      float scaledLevel = getScaledLevel(tool, level);
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData == null) {
          // not calculated yet? add all vanilla values to the tracker
          fireData = new FireData();
          for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
            fireData.setVanilla(slotType, getFireAspectLevel(context, slotType));
          }
          data.put(FIRE_DATA, fireData);
        }
        // add ourself to the data
        fireData.setLevel(slot, scaledLevel);
        fireData.setVanilla(slot, 0);
      });
    }
  }

  @Override
  public void onEquipmentChange(IModifierToolStack tool, int level, EquipmentChangeContext context, EquipmentSlotType slotType) {
    LivingEntity entity = context.getEntity();
    EquipmentSlotType slot = context.getChangedSlot();
    if (!entity.getEntityWorld().isRemote && slot.getSlotType() == Group.ARMOR) {
      // so another slot changed, update vanilla fire data
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        // only need to check if we are in charge of fire data
        if (fireData != null && fireData.maxSlot == slotType) {
          fireData.setVanilla(slot, getFireAspectLevel(context, slot));
        }
      });
    }
  }

  /* Ticking
   * checks each tick if the player has more fire than we remember, if so means they are recently on fire and fire should be reduced
   * does not handle forced fire ticks well, but all vanilla uses of that method are forcing it to 1
   */

  @SubscribeEvent
  static void livingTick(LivingUpdateEvent event) {
    // handled on entity tick as we don't need any tool data, its all cached on the player
    // plus, saves us doing slot checks every tool with the modifier
    LivingEntity entity = event.getEntityLiving();
    // no need to run clientside
    if (!entity.getEntityWorld().isRemote) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        FireData fireData = data.get(FIRE_DATA);
        if (fireData != null) {
          // if the vanilla level is greater than ours, nothing to do
          if (fireData.maxVanilla < fireData.maxLevel) {
            // if the entity is not on fire, make sure fire finish is before the current time
            // if not, could miss getting lit on fire later
            int currentFire = entity.getFireTimer();
            if (currentFire <= 0) {
              if (fireData.finish > entity.ticksExisted) {
                fireData.finish = 0;
              }
              // entity is on fire, we determine new fire by the end of the fire being larger than our predicted end
              // if its smaller, we assume this is not new fire
            } else if (currentFire + entity.ticksExisted > fireData.finish) {
              // alright, time to reduce fire amount, if vanilla already did it reduce less
              int newFire = currentFire;
              if (fireData.maxVanilla > 0) {
                // we already removed 15% of fire per vanilla level, we want to remove an additional 15% of the original per our level
                // means calculating the fire before vanilla made changes and then taking the difference between us and vanilla out
                newFire -= MathHelper.floor((currentFire / (1 - fireData.maxVanilla * 0.15f)) * (fireData.maxLevel - fireData.maxVanilla) * 0.15f);
              } else {
                // remove 15% of fire per level
                newFire -= MathHelper.floor(currentFire * fireData.maxLevel * 0.15f);
              }
              entity.forceFireTicks(newFire);
              fireData.finish = newFire + entity.ticksExisted + 1; // set 1 higher make it slightly faster if increasing fire multiple times in the same tick (fire blocks)
            }
          }
        }
      });
    }
  }

  /** Data class for fire protection */
  private static class FireData {
    @Nullable
    private EquipmentSlotType maxSlot = null;

    /** Level for each slot, for tracking the largest */
    private final float[] tinkerLevels = new float[4];
    /** Highest modifier level */
    float maxLevel;

    /** Vanilla level for each slot, for tracking the largest */
    private final int[] vanillaLevels = new int[4];
    /** Level of the vanilla modifier */
    int maxVanilla = 0;

    /** Tick time when the fire is expected to finish, to determine if fire changed */
    int finish = 0;

    /** Sets the given value in the structure */
    void setLevel(EquipmentSlotType slot, float level) {
      float oldLevel = tinkerLevels[slot.getIndex()];
      if (level != oldLevel) {
        // first, update level
        tinkerLevels[slot.getIndex()] = level;
        // if larger than max, new max
        if (level >= maxLevel) {
          maxLevel = level;
          maxSlot = slot;
        } else if (slot == maxSlot) {
          // if the old level was max, find new max
          maxLevel = 0;
          for (EquipmentSlotType armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
            float value = tinkerLevels[armorSlot.getIndex()];
            if (value > maxLevel) {
              maxLevel = value;
              maxSlot = armorSlot;
            }
          }
        }
      }
    }

    /** Sets the given vanilla level in the structure */
    void setVanilla(EquipmentSlotType slot, int level) {
      int oldLevel = vanillaLevels[slot.getIndex()];
      if (level != oldLevel) {
        vanillaLevels[slot.getIndex()] = level;
        // if new max, update max
        if (level > maxVanilla) {
          maxVanilla = level;
        } else if (maxVanilla == oldLevel) {
          // if was max before, search for replacement max
          maxVanilla = 0;
          for (int value : vanillaLevels) {
            if (value > maxVanilla) {
              maxVanilla = value;
            }
          }
        }
      }
    }
  }
}
