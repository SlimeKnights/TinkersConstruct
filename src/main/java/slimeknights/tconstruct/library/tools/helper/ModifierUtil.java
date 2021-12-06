package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
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
  /** Vanilla tag to hide certain tooltips */
  public static final String TAG_HIDE_FLAGS = "HideFlags";

  /**
   * Adds all enchantments from tools. Separate method as tools don't have enchants all the time.
   * Typically called before actions which involve loot, such as breaking blocks or attacking mobs.
   * @param tool     Tool instance
   * @param stack    Base stack instance
   * @param context  Tool harvest context
   * @return  True if enchants were applied
   */
  public static boolean applyHarvestEnchants(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    boolean addedEnchants = false;
    PlayerEntity player = context.getPlayer();
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
        ItemStack pants = player.getItemStackFromSlot(EquipmentSlotType.LEGS);
        if (TinkerTags.Items.LEGGINGS.contains(pants.getItem())) {
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
        addedEnchants = true;
        EnchantmentHelper.setEnchantments(enchantments, stack);
        stack.getOrCreateTag().putInt(TAG_HIDE_FLAGS, TooltipDisplayFlags.ENCHANTMENTS.func_242397_a());
      }
    }
    return addedEnchants;
  }

  /**
   * Clears enchants from the given stack
   * @param stack  Stack to clear enchants
   */
  public static void clearEnchantments(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      nbt.remove(TAG_ENCHANTMENTS);
      nbt.remove(TAG_HIDE_FLAGS);
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
  public static int getLootingLevel(IModifierToolStack tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource) {
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
    ItemStack pants = holder.getItemStackFromSlot(EquipmentSlotType.LEGS);
    if (!pants.isEmpty() && TinkerTags.Items.LEGGINGS.contains(pants.getItem())) {
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
    World world = target.getEntityWorld();
    if (!stack.isEmpty() && !target.getEntityWorld().isRemote()) {
      ItemEntity ent = new ItemEntity(world, target.getPosX(), target.getPosY() + 1, target.getPosZ(), stack);
      ent.setDefaultPickupDelay();
      Random rand = target.world.rand;
      ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F,
                                        rand.nextFloat() * 0.05F,
                                        (rand.nextFloat() - rand.nextFloat()) * 0.1F));
      world.addEntity(ent);
    }
  }

  /**
   * Direct method to get the level of a modifier from a stack. If you need to get multiple modifier levels, using {@link ToolStack} is faster
   * @param stack     Stack to check
   * @param modifier  Modifier to search for
   * @return  Modifier level, or 0 if not present or the stack is not modifiable
   */
  public static int getModifierLevel(ItemStack stack, Modifier modifier) {
    if (!stack.isEmpty() && TinkerTags.Items.MODIFIABLE.contains(stack.getItem()) && !ToolDamageUtil.isBroken(stack)) {
      CompoundNBT nbt = stack.getTag();
      if (nbt != null && nbt.contains(ToolStack.TAG_MODIFIERS, NBT.TAG_LIST)) {
        ListNBT list = nbt.getList(ToolStack.TAG_MODIFIERS, NBT.TAG_COMPOUND);
        int size = list.size();
        if (size > 0) {
          String key = modifier.getId().toString();
          for (int i = 0; i < size; i++) {
            CompoundNBT entry = list.getCompound(i);
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
  public static void addTotalArmorModifierLevel(IModifierToolStack tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR && (allowBroken || !tool.isBroken())) {
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
  public static void addTotalArmorModifierLevel(IModifierToolStack tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount) {
    addTotalArmorModifierLevel(tool, context, key, amount, false);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addTotalArmorModifierFloat(IModifierToolStack tool, EquipmentChangeContext context, TinkerDataKey<Float> key, float amount) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR && !tool.isBroken()) {
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
  public static boolean hasAquaAffinity(LivingEntity living) {
    return ModifierUtil.getTotalModifierLevel(living, TinkerDataKeys.AQUA_AFFINITY) > 0 || EnchantmentHelper.hasAquaAffinity(living);
  }

  /** Shortcut to get a volatile flag when the tool stack is not needed otherwise */
  public static boolean checkVolatileFlag(ItemStack stack, ResourceLocation flag) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_VOLATILE_MOD_DATA, NBT.TAG_COMPOUND)) {
      return nbt.getCompound(ToolStack.TAG_VOLATILE_MOD_DATA).getBoolean(flag.toString());
    }
    return false;
  }

  /** Shortcut to get a volatile int value when the tool stack is not needed otherwise */
  public static int getVolatileInt(ItemStack stack, ResourceLocation flag) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null && nbt.contains(ToolStack.TAG_VOLATILE_MOD_DATA, NBT.TAG_COMPOUND)) {
      return nbt.getCompound(ToolStack.TAG_VOLATILE_MOD_DATA).getInt(flag.toString());
    }
    return 0;
  }
}
