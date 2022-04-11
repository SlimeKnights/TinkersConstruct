package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

/** Generic modifier hooks that don't quite fit elsewhere */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModifierUtil {
  /** Vanilla enchantments tag */
  public static final String TAG_ENCHANTMENTS = "Enchantments";

  /**
   * Adds all enchantments from tools. Separate method as tools don't have enchants all the time.
   * Typically called before actions which involve loot, such as breaking blocks or attacking mobs.
   * @param tool     Tool instance
   * @param stack    Base stack instance
   * @param context  Tool harvest context
   * @return  Old tag if enchants were applied
   */
  @Nullable
  public static ListTag applyHarvestEnchantments(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    ListTag originalEnchants = null;
    Player player = context.getPlayer();
    if (player == null || !player.isCreative()) {
      Map<Enchantment, Integer> enchantments = new HashMap<>();
      BiConsumer<Enchantment,Integer> enchantmentConsumer = (ench, add) -> {
        if (ench != null && add != null) {
          Integer level = enchantments.get(ench);
          if (level != null) {
            add += level;
          }
          enchantments.put(ench, add);
        }
      };
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().applyHarvestEnchantments(tool, entry.getLevel(), context, enchantmentConsumer);
      }
      // lucky pants
      if (player != null) {
        ItemStack pants = player.getItemBySlot(EquipmentSlot.LEGS);
        if (pants.is(TinkerTags.Items.LEGGINGS)) {
          ToolStack pantsTool = ToolStack.from(pants);
          for (ModifierEntry entry : pantsTool.getModifierList()) {
            IArmorLootModifier leggingLuck = entry.getModifier().getModule(IArmorLootModifier.class);
            if (leggingLuck != null) {
              leggingLuck.applyHarvestEnchantments(tool, entry.getLevel(), context, enchantmentConsumer);
            }
          }
        }
      }
      if (!enchantments.isEmpty()) {
        // note this returns a new list if there is no tag, this is intentional as we need non-null to tell the tool to remove the tag
        originalEnchants = stack.getEnchantmentTags();
        EnchantmentHelper.setEnchantments(enchantments, stack);
      }
    }
    return originalEnchants;
  }

  /**
   * Restores the original enchants to the given stack
   * @param stack        Stack to clear enchants
   * @param originalTag  Original list of enchantments. If empty, will remove the tag
   */
  public static void restoreEnchantments(ItemStack stack, ListTag originalTag) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      if (originalTag.isEmpty()) {
        nbt.remove(TAG_ENCHANTMENTS);
      } else {
        nbt.put(TAG_ENCHANTMENTS, originalTag);
      }
    }
  }

  /**
   * Gets the looting value for the given tool
   * @param tool           Tool used
   * @param holder         Entity holding the tool
   * @param target         Target being looted
   * @param damageSource   Damage source for looting, may ben null if no attack
   * @return  Looting value for the tool
   */
  public static int getLootingLevel(IToolStackView tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource) {
    if (tool.isBroken()) {
      return 0;
    }
    int looting = 0;
    for (ModifierEntry entry : tool.getModifierList()) {
      looting = entry.getModifier().getLootingValue(tool, entry.getLevel(), holder, target, damageSource, looting);
    }
    return looting;
  }

  /**
   * Gets the looting value for the leggings
   * @param holder         Entity holding the tool
   * @param target         Target being looted
   * @param damageSource   Damage source for looting, may ben null if no attack
   * @param toolLooting    Looting from the tool
   * @return  Looting value for the tool
   */
  public static int getLeggingsLootingLevel(LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int toolLooting) {
    ItemStack pants = holder.getItemBySlot(EquipmentSlot.LEGS);
    if (!pants.isEmpty() && pants.is(TinkerTags.Items.LEGGINGS)) {
      ToolStack pantsTool = ToolStack.from(pants);
      if (!pantsTool.isBroken()) {
        for (ModifierEntry entry : pantsTool.getModifierList()) {
          IArmorLootModifier leggingLuck = entry.getModifier().getModule(IArmorLootModifier.class);
          if (leggingLuck != null) {
            toolLooting = leggingLuck.getLootingValue(pantsTool, entry.getLevel(), holder, target, damageSource, toolLooting);
          }
        }
      }
    }
    return toolLooting;
  }

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
    if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE) && !ToolDamageUtil.isBroken(stack)) {
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

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addTotalArmorModifierLevel(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken) {
    if (context.getChangedSlot().getType() == Type.ARMOR && (allowBroken || !tool.isBroken())) {
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
    if (context.getChangedSlot().getType() == Type.ARMOR && !tool.isBroken()) {
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

  /** Checks if the entity has aqua affinity from either enchants or modifiers */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean hasAquaAffinity(LivingEntity living) {
    return ModifierUtil.getTotalModifierLevel(living, TinkerDataKeys.AQUA_AFFINITY) > 0 || EnchantmentHelper.hasAquaAffinity(living);
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
        if (entry.getModifier().canPerformAction(tool, entry.getLevel(), action)) {
          return true;
        }
      }
    }
    return false;
  }
}
