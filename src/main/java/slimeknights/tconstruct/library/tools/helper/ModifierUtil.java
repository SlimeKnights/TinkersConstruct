package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nonnull;
import java.util.Random;

/** Generic modifier hooks that don't quite fit elsewhere */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModifierUtil {
  /** Drops an item at the entity position */
  public static void dropItem(Entity target, ItemStack stack) {
    if (!stack.isEmpty() && !target.level.isClientSide) {
      ItemEntity ent = new ItemEntity(target.level, target.getX(), target.getY() + 1, target.getZ(), stack);
      ent.setDefaultPickUpDelay();
      Random rand = target.level.random;
      ent.setDeltaMovement(ent.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1F,
                                                      rand.nextFloat() * 0.05F,
                                                      (rand.nextFloat() - rand.nextFloat()) * 0.1F));
      target.level.addFreshEntity(ent);
    }
  }

  /**
   * Direct method to get the level of a modifier from a stack. If you need to get multiple modifier levels, using {@link ToolStack} is faster
   * @param stack     Stack to check
   * @param modifier  Modifier to search for
   * @return  Modifier level, or 0 if not present or the stack is not modifiable
   */
  public static int getModifierLevel(ItemStack stack, ModifierId modifier) {
    if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE)) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null && nbt.contains(ToolStack.TAG_MODIFIERS, Tag.TAG_LIST)) {
        ListTag list = nbt.getList(ToolStack.TAG_MODIFIERS, Tag.TAG_COMPOUND);
        int size = list.size();
        if (size > 0) {
          String key = modifier.toString();
          for (int i = 0; i < size; i++) {
            CompoundTag entry = list.getCompound(i);
            if (key.equals(entry.getString(ModifierNBT.TAG_MODIFIER))) {
              return entry.getInt(ModifierNBT.TAG_LEVEL);
            }
          }
        }
      }
    }
    return 0;
  }

  /** Checks if the given stack has upgrades */
  public static boolean hasUpgrades(ItemStack stack) {
    if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE)) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && !nbt.getList(ToolStack.TAG_UPGRADES, Tag.TAG_COMPOUND).isEmpty();
    }
    return false;
  }

  /** Checks if the given slot may contain armor */
  public static boolean validArmorSlot(LivingEntity living, EquipmentSlot slot) {
    return slot.getType() == Type.ARMOR || living.getItemBySlot(slot).is(TinkerTags.Items.HELD);
  }

  /** Checks if the given slot may contain armor */
  public static boolean validArmorSlot(IToolStackView tool, EquipmentSlot slot) {
    return slot.getType() == Type.ARMOR || tool.hasTag(TinkerTags.Items.HELD);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addTotalArmorModifierLevel(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken) {
    if (validArmorSlot(tool, context.getChangedSlot()) && (allowBroken || !tool.isBroken())) {
      context.getTinkerData().ifPresent(data -> {
        int totalLevels = data.get(key, 0) + amount;
        if (totalLevels <= 0) {
          data.remove(key);
        } else {
          data.put(key, totalLevels);
        }
      });
    }
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addTotalArmorModifierLevel(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount) {
    addTotalArmorModifierLevel(tool, context, key, amount, false);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addTotalArmorModifierFloat(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Float> key, float amount) {
    if (validArmorSlot(tool, context.getChangedSlot()) && !tool.isBroken()) {
      context.getTinkerData().ifPresent(data -> {
        float totalLevels = data.get(key, 0f) + amount;
        if (totalLevels <= 0.005f) {
          data.remove(key);
        } else {
          data.put(key, totalLevels);
        }
      });
    }
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static int getTotalModifierLevel(LivingEntity living, TinkerDataKey<Integer> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0);
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static float getTotalModifierFloat(LivingEntity living, TinkerDataKey<Float> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0f);
  }

  /** Shortcut to get a volatile flag when the tool stack is not needed otherwise */
  public static boolean checkVolatileFlag(ItemStack stack, ResourceLocation flag) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_VOLATILE_MOD_DATA, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(ToolStack.TAG_VOLATILE_MOD_DATA).getBoolean(flag.toString());
    }
    return false;
  }

  /** Shortcut to get a volatile int value when the tool stack is not needed otherwise */
  public static int getVolatileInt(ItemStack stack, ResourceLocation flag) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_VOLATILE_MOD_DATA, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(ToolStack.TAG_VOLATILE_MOD_DATA).getInt(flag.toString());
    }
    return 0;
  }

  /** Shortcut to get a volatile int value when the tool stack is not needed otherwise */
  public static int getPersistentInt(ItemStack stack, ResourceLocation flag, int defealtValue) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND)) {
      CompoundTag persistent = nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA);
      String flagString = flag.toString();
      if (persistent.contains(flagString, Tag.TAG_INT)) {
        return persistent.getInt(flagString);
      }
    }
    return defealtValue;
  }

  /** Shortcut to get a persistent string value when the tool stack is not needed otherwise */
  public static String getPersistentString(ItemStack stack, ResourceLocation flag) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA).getString(flag.toString());
    }
    return "";
  }

  /** Checks if a tool can perform the given action */
  public static boolean canPerformAction(IToolStackView tool, ToolAction action) {
    if (!tool.isBroken()) {
      // can the tool do this action inherently?
      if (tool.getDefinition().getData().canPerformAction(action)) {
        return true;
      }
      for (ModifierEntry entry : tool.getModifierList()) {
        if (entry.getHook(TinkerHooks.TOOL_ACTION).canPerformAction(tool, entry, action)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Calculates inaccuracy from the conditional tool stat. TODO: reconsidering velocity impacting inaccuracy, remove parameter in 1.19 */
  @SuppressWarnings("unused")
  public static float getInaccuracy(IToolStackView tool, LivingEntity living, float velocity) {
    return 3 * (1 / ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.ACCURACY) - 1);
  }

  /** Interface used for {@link #foodConsumer} */
  public interface FoodConsumer {
    /** Called when food is eaten to notify compat that food was eaten */
    void onConsume(Player player, ItemStack stack, int hunger, float saturation);
  }

  /** Instance of the current food consumer, will be either no-op or an implementation calling the Diet API, never null. */
  @Nonnull
  public static FoodConsumer foodConsumer = (player, stack, hunger, saturation) -> {};
}
