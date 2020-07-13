package slimeknights.tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.function.BiFunction;

/**
 * Holds all the NBT Tag keys used by the standard tinkers stuff.
 */
public final class Tags {
  /**
   * The base data of the tinker item. What it is built from.
   */
  public static final String BASE = "TinkerData";

  /**
   * Tool/Gear related sublevels under base stats
   */

  public static final String DATA = "Data"; //Extra data on a tool or gear, like open modifier slots
  public static final String MATERIALS = "Materials"; //Base materials the item is made out of
  public static final String MODIFIERS = "Modifiers"; //Arbitrary data
  public static final String RENDERING = "Rendering"; //Useful for models

  /**
   * Standard tool/gear stat tags. These change at runtime when tools are built or modified
   */
  public static final String ATTACK = "attack";
  public static final String ATTACK_SPEED = "attack_speed";
  public static final String DURABILITY = "durability";
  public static final String MINING_SPEED = "mining_speed";
  public static final String HARVEST_LEVEL = "harvest_level";
  public static final String BROKEN = "is_broken";

  //Bow related
  public static final String DRAWSPEED = "draw_speed";
  public static final String RANGE = "range";
  public static final String PROJECTILE_BONUS_DAMAGE = "ProjectileBonusDamage";
  public static final String ACCURACY = "accuracy";
  
  public static final String REPAIR_COUNT = "repair_count";
  public static final String MINING_SPEED_MULTIPLIER = "mining_speed_multiplier";
  public static final String ATTACK_SPEED_MULTIPLIER = "attack_speed_multiplier";
  public static final String BONUS_DURABILITY = "bonus_durability";
  public static final String BONUS_DURABILITY_MULTIPLIER = "bonus_durability_multiplier";

  public static final String FREE_UPGRADE_SLOTS = "upgrade_slots";
  public static final String FREE_ABILITY_SLOTS = "ability_slots";
  public static final String FREE_ARMOR_SLOTS = "armor_slots";
  public static final String FREE_TRAIT_SLOTS = "trait_slots";
  /**
   * Standard material tags
   */

  /**
   * Standard modifier tags
   */

  /**
   * Standard rendering tags
   */
  /**
   * An integer indicating how many free modifiers have been used
   */
  public static final String BASE_USED_MODIFIERS = "UsedModifiers";
  /**
   * Extra-data that is specific to this Itemstack and is used to build the item. An example would be if a special
   * pickaxe had 100 more durability, it'd be stored in here.
   */
  public static final String TINKER_EXTRA = "Special";
  public static final String EXTRA_CATEGORIES = "Categories";

  //public static final String TOOL_RENDER = "Render";
  /**
   * The tag that contains all the actual calculated runtime-information of the tools
   */
  //public static final String TOOL_STATS = "Stats";
  public static final String TOOL_DATA_ORIG = "StatsOriginal";

  //public static final String TOOL_MODIFIERS = "Modifiers";
  public static final String TOOL_TRAITS = "Traits";

  /**
   * The tag that saves the material information on toolparts
   */
  public static final String PART_MATERIAL = "Material";

  // tools

  // bows

  // projectile

  // Extra
  public static final String ENCHANT_EFFECT = "EnchantEffect";
  public static final String RESET_FLAG = "ResetFlag";

  public static final String NO_RENAME = "NoRename";

  // Tank
  public static final String TANK = "tank";

  public static CompoundNBT getTinkerTag(ItemStack stack)
  {
    return null;
  }


  /**
   * Helper methods for accessing serialized tags
   */
  public static int getIntFromTagOrDefault(CompoundNBT nbt, String key, int defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getInt);
  }

  public static float getFloatFromTagOrDefault(CompoundNBT nbt, String key, float defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getFloat);
  }

  public static boolean getBoolFromTagOrDefault(CompoundNBT nbt, String key, boolean defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getBoolean);
  }

  public static <T> T getFromTagOrDefault(CompoundNBT nbt, String key, T defaultValue, BiFunction<CompoundNBT, String, T> valueGetter) {
    if(nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC)) {
      return valueGetter.apply(nbt, key);
    }
    return defaultValue;
  }
  private Tags() {
  }
}
