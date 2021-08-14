package slimeknights.tconstruct.tools.modifiers.armor;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.capability.EntityModifierDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;

@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class FireProtectionModifier extends IncrementalModifier {
  /** Entity data key for the data associated with this modifier */
  private static final ResourceLocation FIRE_DATA = TConstruct.getResource("fire_protection");
  /** NBT key for the index of the slot in charge of handling fire tick reduction */
  private static final String SLOT = "slot";
  /** NBT key for the level of this modifier in SLOT, for determining the max level */
  private static final String LEVEL = "level";
  /** NBT key for the vanilla modifier on the entity, to determine how much reduction needs to be done */
  private static final String VANILLA = "vanilla";
  /** Time when the last fire effect would finish, to check if fire length increased */
  private static final String FIRE_FINISH = "fire_finish";
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
   * Equipping, these hooks let us
   * (a) make sure only one piece of armor with our modifier is checking for fire each tick (prevents doubly reducing it and alike)
   * (b) reduce the work needed to determine the vanilla enchantment level
   */

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    // remove fire data if this slot is in charge of it, mostly keeps the data clean, but also allows another tool to take charge if this level was highest
    context.getEntity().getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      if (data.contains(FIRE_DATA, NBT.TAG_COMPOUND)) {
        CompoundNBT fireData = data.getCompound(FIRE_DATA);
        if (fireData.getInt(SLOT) == context.getChangedSlot().getIndex()) {
          data.remove(FIRE_DATA);
        }
      }
    });
  }

  /**
   * Gets the max fire protection level, ignoring all tools in the context
   * @param livingEntity  Living entity
   * @param context       Context to ignore
   * @return  Max vanilla enchant level
   */
  private static int getMaxFireLevelIgnoring(LivingEntity livingEntity, EquipmentChangeContext context) {
    int max = 0;
    for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      // save effort, ignore our tools (cannot be enchanted)
      if (context.getToolInSlot(slotType) == null) {
        ItemStack stack = livingEntity.getItemStackFromSlot(slotType);
        if (!stack.isEmpty()) {
          int check = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack);
          if (check > max) {
            max = check;
          }
        }
      }
    }
    return max;
  }

  /** Attempts to mark this slot as in charge of fire data, succeeds if this is the largest level of the modifier */
  private static void attemptTakeControl(LivingEntity entity, EquipmentChangeContext context, EquipmentSlotType slotType, float level) {
    entity.getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      CompoundNBT fireData;
      if (data.contains(FIRE_DATA, NBT.TAG_COMPOUND)) {
        // if already present, take over if we are a higher level
        fireData = data.getCompound(FIRE_DATA);
        // if we are already in charge, update vanilla fire level
        if (fireData.getInt(SLOT) == slotType.getIndex()) {
          fireData.putInt(VANILLA, getMaxFireLevelIgnoring(entity, context));
          return;
        }
        // if our level is smaller than in charge, nothing to do
        if (fireData.getFloat(LEVEL) >= level) {
          return;
        }
      } else {
        // if missing, add new data
        fireData = new CompoundNBT();
        data.put(FIRE_DATA, fireData);
        // only fetch fire timer if new, its already set by another slot if someone else had control
        int fireTimer = entity.getFireTimer();
        if (fireTimer > 0) {
          fireData.putInt(FIRE_FINISH, entity.ticksExisted + fireTimer + 1);
        }
      }
      fireData.putInt(SLOT, slotType.getIndex());
      fireData.putFloat(LEVEL, level);
      // update vanilla fire level, this may be run multiple times if charge is taken multiple times, but predicting when its needed is harder than just doing it
      fireData.putInt(VANILLA, getMaxFireLevelIgnoring(entity, context));
    });
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      attemptTakeControl(context.getEntity(), context, context.getChangedSlot(), getScaledLevel(tool, level));
    }
  }

  @Override
  public void onEquipmentChange(IModifierToolStack tool, int level, EquipmentChangeContext context, EquipmentSlotType slotType) {
    if (!tool.isBroken()) {
      attemptTakeControl(context.getEntity(), context, slotType, getScaledLevel(tool, level));
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
    entity.getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      if (data.contains(FIRE_DATA, NBT.TAG_COMPOUND)) {
        CompoundNBT fireData = data.getCompound(FIRE_DATA);
        // if the vanilla level is greater than ours or we are not in charge, nothing to do (vanilla will handle it)
        int vanilla = fireData.getInt(VANILLA);
        float level = fireData.getFloat(LEVEL);
        if (vanilla < level) {
          // if the entity is not on fire, make sure fire finish is before the current time
          // if not, could miss getting lit on fire later
          int fireFinish = fireData.getInt(FIRE_FINISH);
          int currentFire = entity.getFireTimer();
          if (currentFire <= 0) {
            if (fireFinish > entity.ticksExisted) {
              fireData.putInt(FIRE_FINISH, 0);
            }
            // entity is on fire, we determine new fire by the end of the fire being larger than our predicted end
            // if its smaller, we assume this is not new fire
          } else if (currentFire + entity.ticksExisted > fireFinish) {
            // alright, time to reduce fire amount, if vanilla already did it reduce less
            int newFire = currentFire;
            if (vanilla > 0) {
              // we already removed 15% of fire per vanilla level, we want to remove an additional 15% of the original per our level
              // means calculating the fire before vanilla made changes and then taking the difference between us and vanilla out
              newFire -= MathHelper.floor((currentFire / (1 - vanilla * 0.15f)) * (level - vanilla) * 0.15f);
            } else {
              // remove 15% of fire per level
              newFire -= MathHelper.floor(currentFire * level * 0.15f);
            }
            entity.forceFireTicks(newFire);
            fireData.putInt(FIRE_FINISH, newFire + entity.ticksExisted + 1); // set 1 higher make it slightly faster if increasing fire multiple times in the same tick (fire blocks)
          }
        }
      }
    });
  }
}
