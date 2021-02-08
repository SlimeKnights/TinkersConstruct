package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRequirementLookup;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Class handling parsing all tool related NBT
 */
@RequiredArgsConstructor(staticName = "from")
public class ToolStack implements IModifierToolStack {
  /** Error messages for when there are not enough remaining modifiers */
  private static final ValidatedResult NOT_ENOUGH_UPGRADES = ValidatedResult.failure(Util.makeTranslationKey("recipe", "modifier.not_enough_upgrades"));
  private static final ValidatedResult NOT_ENOUGH_ABILITIES = ValidatedResult.failure(Util.makeTranslationKey("recipe", "modifier.not_enough_abilities"));


  /** Volatile mod data key for the durability before modifiers */
  public static final ResourceLocation ORIGINAL_DURABILITY_KEY = Util.getResource("durability");

  protected static final String TAG_MATERIALS = "tic_materials";
  protected static final String TAG_STATS = "tic_stats";
  protected static final String TAG_PERSISTENT_MOD_DATA = "tic_persistent_data";
  protected static final String TAG_VOLATILE_MOD_DATA = "tic_volatile_data";
  protected static final String TAG_UPGRADES = "tic_upgrades";
  protected static final String TAG_MODIFIERS = "tic_modifiers";
  public static final String TAG_BROKEN = "tic_broken";
  // vanilla tags
  protected static final String TAG_DAMAGE = "Damage";
  public static final String TAG_UNBREAKABLE = "Unbreakable";
  protected static final String TAG_ENCHANTMENTS = "Enchantments";
  private static final String TAG_HIDE_FLAGS = "HideFlags";
  // modifier values
  private static final String TAG_ID = "id";
  private static final String TAG_LEVEL = "lvl";

  /** Item representing this tool */
  @Getter
  private final Item item;
  /** Tool definition, describing part count and alike */
  @Getter
  private final ToolDefinition definition;
  /** Original tool NBT */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT nbt;

  // durability
  /** Current damage of the tool, -1 means unloaded */
  private int damage = -1;
  /** If true, tool is broken. Null means unloaded */
  @Nullable
  private Boolean broken;

  // tool data: these properties describe the tool
  /** Data object containing materials */
  @Nullable
  private MaterialNBT materials;
  /** Upgrades are modifiers that come from recipes. Abilities are included with these in NBT */
  @Nullable
  private ModifierNBT upgrades;
  /** Data object containing modifier data that persists on stat rebuild */
  @Nullable
  private ModDataNBT persistentModData;

  // nbt cache: these values are calculated tool data
  /** Combination of modifiers from upgrades and material traits */
  @Nullable
  private ModifierNBT modifiers;
  /** Data object containing the original tool stats */
  @Nullable
  private StatsNBT stats;
  /** Data object containing modifier data that is recreated when the modifier list changes */
  @Nullable
  private IModDataReadOnly volatileModData;

  /* Creating */

  /**
   * Creates a tool stack from an item stack
   * @param stack    Base stack
   * @param copyNbt  If true, NBT is copied from the stack
   * @return  Tool stack
   */
  private static ToolStack from(ItemStack stack, boolean copyNbt) {
    Item item = stack.getItem();
    ToolDefinition definition = ToolDefinition.EMPTY;
    if (item instanceof ToolCore) {
      definition = ((ToolCore)item).getToolDefinition();
    }
    CompoundNBT nbt = stack.getTag();
    if (nbt == null) {
      nbt = new CompoundNBT();
      if (!copyNbt) {
        stack.setTag(nbt);
      }
    } else if (copyNbt) {
      nbt = nbt.copy();
    }
    return from(item, definition, nbt);
  }

  /**
   * Creates a tool stack from the given item stack, not copying NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack from(ItemStack stack) {
    return from(stack, false);
  }

  /**
   * Creates a tool stack from the given item stack, copying the NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack copyFrom(ItemStack stack) {
    return from(stack, true);
  }

  /**
   * Creates a copy of this tool to prevent modifications to the original.
   * Will copy over cached parsed NBT when possible, making this more efficient than calling {@link #copyFrom(ItemStack)}.
   * @return  Copy of this tool
   */
  public ToolStack copy() {
    ToolStack tool = from(item, definition, nbt.copy());
    // copy over relevant loaded data
    tool.damage = this.damage;
    tool.broken = this.broken;
    tool.materials = this.materials;
    tool.upgrades = this.upgrades;
    tool.modifiers = this.modifiers;
    tool.stats = this.stats;
    // skipping mod data as those are mutable, so not safe to share the same instance
    return tool;
  }

  /**
   * Creates a new tool stack for a completely new tool
   * @param item        Item
   * @param definition  Tool definition
   * @return  Tool stack
   */
  public static ToolStack createTool(Item item, ToolDefinition definition, List<IMaterial> materials) {
    ToolStack tool = from(item, definition, new CompoundNBT());
    // set cached to empty, saves a NBT lookup or two
    tool.damage = 0;
    tool.broken = false;
    tool.upgrades = ModifierNBT.EMPTY;
    // update the materials
    tool.setMaterials(materials);
    // update modifier data
    ToolBaseStatDefinition baseStats = definition.getBaseStatDefinition();
    ModDataNBT data = tool.getPersistentData();
    data.setUpgrades(baseStats.getDefaultModifiers());
    data.setAbilities(baseStats.getDefaultAbilities());
    return tool;
  }

  /**
   * Creates an item stack from this tool stack
   */
  public ItemStack createStack() {
    ItemStack stack = new ItemStack(item, 1);
    stack.setTag(nbt);
    return stack;
  }

  /**
   * Sets the NBT on the given stack
   * @param stack  Stack instance
   * @return  New NBT
   */
  public ItemStack updateStack(ItemStack stack) {
    if (stack.getItem() != item) {
      throw new IllegalArgumentException("Wrong item in stack");
    }
    stack.setTag(nbt.copy());
    return stack;
  }


  /* Damaging */

  /**
   * Checks if this tool is currently broken
   * @return  True if broken
   */
  @Override
  public boolean isBroken() {
    if (broken == null) {
      broken = nbt.getBoolean(TAG_BROKEN);
    }
    return broken;
  }

  /**
   * Sets the broken state on the tool
   * @param broken  New broken value
   */
  protected void setBrokenRaw(boolean broken) {
    this.broken = broken;
    nbt.putBoolean(TAG_BROKEN, broken);
  }

  /**
   * Breaks the tool
   */
  protected void breakTool() {
    setDamage(getStats().getDurability());
  }

  /**
   * Gets damage, ignoring broken checks
   * @return  Damage ignoring broken state
   */
  protected int getDamageRaw() {
    if (damage == -1) {
      damage = nbt.getInt(TAG_DAMAGE);
    }
    return damage;
  }

  /**
   * Gets the tools current damage from NBT
   * @return  Current damage
   */
  @Override
  public int getDamage() {
    // if broken, return full damage
    if (isBroken()) {
      return getStats().getDurability();
    }
    // ensure we never return a number larger than max
    return Math.min(getDamageRaw(), getStats().getDurability() - 1);
  }

  /**
   * Gets the current durability remaining for this tool
   * @return  Tool durability
   */
  @Override
  public int getCurrentDurability() {
    if (isBroken()) {
      return 0;
    }
    // ensure we never return a number smaller than 0
    return Math.max(0, getStats().getDurability() - getDamageRaw());
  }

  /**
   * Sets the tools damage
   * @param  damage  New damage
   */
  public void setDamage(int damage) {
    int durability = getStats().getDurability();
    if (damage >= durability) {
      damage = Math.max(0, durability);
      setBrokenRaw(true);
    } else {
      setBrokenRaw(false);
    }
    this.damage = damage;
    nbt.putInt(TAG_DAMAGE, damage);
  }

  @Override
  public boolean damage(int amount, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
    if (amount <= 0 || isBroken() || nbt.getBoolean(TAG_UNBREAKABLE)) {
      return false;
    }

    // try each modifier
    for (ModifierEntry entry : getModifierList()) {
      amount = entry.getModifier().onDamageTool(this, entry.getLevel(), amount);
      // if no more damage, done
      if (amount < 0) {
        return false;
      }
    }

    int durability = getStats().getDurability();
    int damage = getDamage();
    int current = durability - damage;
    amount = Math.min(amount, current);
    if (amount > 0) {
      // criteria updates
      int newDamage = damage + amount;
      // TODO: needed?
      if (entity instanceof ServerPlayerEntity) {
        if (stack == null) {
          stack = createStack();
        }
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity)entity, stack, newDamage);
      }

      setDamage(newDamage);
      return newDamage >= durability;
    }
    return false;
  }

  /**
   * Repairs the given tool stack
   * @param amount  Amount to repair
   */
  public void repair(int amount) {
    if (amount <= 0) {
      return;
    }

    // if undamaged, nothing to do
    int damage = getDamage();
    if (damage == 0) {
      return;
    }

    // try each modifier
    for (ModifierEntry entry : getModifierList()) {
      amount = entry.getModifier().onRepairTool(this, entry.getLevel(), amount);
      // if no more damage, done
      if (amount < 0) {
        return;
      }
    }

    // ensure we never repair more than max durability
    int newDamage = damage - Math.min(amount, damage);
    setDamage(newDamage);
  }


  /* Stats */

  /**
   * Gets the tool stats if parsed, or parses from NBT if not yet parsed
   * @return stats
   */
  @Override
  public StatsNBT getStats() {
    if (stats == null) {
      stats = StatsNBT.readFromNBT(nbt.get(TAG_STATS));
    }
    return stats;
  }

  /**
   * Gets the tool stats if parsed, or parses if not yet parsed
   * @param stats  Stats instance
   */
  protected void setStats(StatsNBT stats) {
    this.stats = stats;
    nbt.put(TAG_STATS, stats.serializeToNBT());
    // if we no longer have enough durability, decrease the damage and mark it broken
    int newMax = stats.getDurability();
    if (getDamageRaw() >= newMax) {
      setDamage(newMax);
    }
  }


  /* Materials */

  @Override
  public MaterialNBT getMaterials() {
    if (materials == null) {
      materials = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));
    }
    return materials;
  }

  /**
   * Sets the materials without updating the tool stats
   * @param materials  New materials
   */
  protected void setMaterialsRaw(MaterialNBT materials) {
    this.materials = materials;
    this.nbt.put(TAG_MATERIALS, materials.serializeToNBT());
  }

  /**
   * Sets the materials on this tool stack, updating tool stats
   * @param materials  New materials NBT
   */
  public void setMaterials(MaterialNBT materials) {
    setMaterialsRaw(materials);
    rebuildStats();
  }

  /**
   * Sets the materials on this tool stack
   * @param materials  New materials NBT
   */
  public void setMaterials(List<IMaterial> materials) {
    setMaterials(new MaterialNBT(materials));
  }

  /**
   * Replaces the material at the given index
   * @param index        Index to replace
   * @param replacement  New material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public void replaceMaterial(int index, IMaterial replacement) {
    setMaterials(getMaterials().replaceMaterial(index, replacement));
  }


  /* Modifiers */

  /**
   * Gets a list of modifiers added from recipes.
   * In general you should use {@link #getModifiers()} when performing modifier actions to include traits.
   * @return  Recipe modifier list
   */
  public ModifierNBT getUpgrades() {
    if (upgrades == null) {
      upgrades = ModifierNBT.readFromNBT(nbt.get(TAG_UPGRADES));
    }
    return upgrades;
  }

  /**
   * Adds a single modifier to this tool
   * @param modifier  Modifier to add
   * @param level     Level to add
   */
  public void addModifier(Modifier modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above 0");
    }
    ModifierNBT newModifiers = getUpgrades().withModifier(modifier, level);
    this.upgrades = newModifiers;
    nbt.put(TAG_UPGRADES, newModifiers.serializeToNBT());
    rebuildStats();
  }

  @Override
  public ModifierNBT getModifiers() {
    if (modifiers == null) {
      modifiers = ModifierNBT.readFromNBT(nbt.get(TAG_MODIFIERS));
    }
    return modifiers;
  }

  /**
   * Updates the list of all modifiers in NBT, called in {@link #rebuildStats()}
   * @param modifiers  New modifiers
   */
  protected void setModifiers(ModifierNBT modifiers) {
    this.modifiers = modifiers;
    nbt.put(TAG_MODIFIERS, this.modifiers.serializeToNBT());
  }


  /* Data */

  @Override
  public ModDataNBT getPersistentData() {
    if (persistentModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_PERSISTENT_MOD_DATA, NBT.TAG_COMPOUND)) {
        persistentModData = ModDataNBT.readFromNBT(nbt.getCompound(TAG_PERSISTENT_MOD_DATA));
      } else {
        // if no tag exists, create it
        CompoundNBT tag = new CompoundNBT();
        nbt.put(TAG_PERSISTENT_MOD_DATA, tag);
        persistentModData = ModDataNBT.readFromNBT(tag);
      }
    }
    return persistentModData;
  }

  @Override
  public IModDataReadOnly getVolatileData() {
    if (volatileModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_VOLATILE_MOD_DATA, NBT.TAG_COMPOUND)) {
        volatileModData = ModDataNBT.readFromNBT(nbt.getCompound(TAG_VOLATILE_MOD_DATA));
      } else {
        // if no tag exists, return empty
        volatileModData = IModDataReadOnly.EMPTY;
      }
    }
    return volatileModData;
  }

  /**
   * Updates the volatile mod data in NBT, called in {@link #rebuildStats()}
   * @param modData  New data
   */
  protected void setVolatileModData(ModDataNBT modData) {
    CompoundNBT data = modData.getData();
    if (data.isEmpty()) {
      volatileModData = IModDataReadOnly.EMPTY;
      nbt.remove(TAG_VOLATILE_MOD_DATA);
    } else {
      volatileModData = modData;
      nbt.put(TAG_VOLATILE_MOD_DATA, data);
    }
  }


  /* Utilities */

  /**
   * Checks if this tool stack is in a valid state
   * @return  Pass if the tool is valid, failure result if invalid
   */
  public ValidatedResult validate() {
    // first check slot counts
    if (getFreeUpgrades() < 0) {
      return NOT_ENOUGH_UPGRADES;
    }
    if (getFreeAbilities() < 0) {
      return NOT_ENOUGH_ABILITIES;
    }
    // next, ensure modifiers validate
    ValidatedResult result;
    List<ModifierEntry> mods = getModifierList();
    for (ModifierEntry entry : mods) {
      result = entry.getModifier().validate(this, entry.getLevel());
      if (result.hasError()) {
        return result;
      }
    }
    return ModifierRequirementLookup.checkRequirements(getUpgrades().getModifiers(), mods);
  }

  /**
   * Recalculates any relevant cached data. Called after either the materials or modifiers list changes
   */
  public void rebuildStats() {
    // first, rebuild the list of all modifiers
    List<IMaterial> materials = getMaterialsList();
    ModifierNBT.Builder modBuilder = ModifierNBT.builder();
    modBuilder.add(getUpgrades());
    for (IMaterial material : materials) {
      modBuilder.add(material.getTraits());
    }
    ModifierNBT allMods = modBuilder.build();
    setModifiers(allMods);

    // next, update modifier related properties
    StatsNBT stats = definition.buildStats(materials);
    List<ModifierEntry> modifierList = allMods.getModifiers();
    if (modifierList.isEmpty()) {
      setStats(stats);
      // if no modifiers, clear out data that only exists with modifiers
      nbt.remove(TAG_ENCHANTMENTS);
      nbt.remove(TAG_VOLATILE_MOD_DATA);
      volatileModData = IModDataReadOnly.EMPTY;
    } else {
      ModDataNBT volatileData = new ModDataNBT();
      // consumer to add an enchantment
      Map<Enchantment,Integer> enchantments = new HashMap<>();
      BiConsumer<Enchantment,Integer> enchantmentConsumer = (ench, add) -> {
        if (ench != null && add != null) {
          Integer level = enchantments.get(ench);
          if (level != null) {
            add += level;
          }
          enchantments.put(ench, add);
        }
      };

      // store original durability in volatile data, allows mods to scale based on original durability and "overclock" based on mods
      // also used for overslime cap base
      volatileData.putInt(ORIGINAL_DURABILITY_KEY, stats.getDurability());

      // build persistent data first, its a parameter to the other two hooks
      IModDataReadOnly persistentData = getPersistentData();
      for (ModifierEntry entry : modifierList) {
        entry.getModifier().addVolatileData(persistentData, entry.getLevel(), volatileData);
      }

      // regular stats last so we can include volatile data
      ToolStatsModifierBuilder statBuilder = ToolStatsModifierBuilder.builder();
      for (ModifierEntry entry : modifierList) {
        Modifier mod = entry.getModifier();
        int level = entry.getLevel();
        mod.addToolStats(persistentData, volatileData, level, statBuilder);
        mod.addEnchantments(persistentData, volatileData, level, enchantmentConsumer);
      }

      // set into NBT
      setStats(statBuilder.build(stats));
      setVolatileModData(volatileData);
      setEnchantments(enchantments);
    }
  }

  /**
   * Sets the list of enchantments onto the tool NBT
   * @param enchantments  Enchantments list
   */
  protected void setEnchantments(Map<Enchantment,Integer> enchantments) {
    ListNBT list = new ListNBT();
    for(Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
      Enchantment enchantment = entry.getKey();
      if (enchantment != null) {
        int i = entry.getValue();
        CompoundNBT tag = new CompoundNBT();
        tag.putString(TAG_ID, Objects.requireNonNull(enchantment.getRegistryName()).toString());
        tag.putShort(TAG_LEVEL, (short)i);
        list.add(tag);
      }
    }
    if (list.isEmpty()) {
      nbt.remove(TAG_ENCHANTMENTS);
      nbt.remove(TAG_HIDE_FLAGS);
    } else {
      nbt.put(TAG_ENCHANTMENTS, list);
      nbt.putInt(TAG_HIDE_FLAGS, TooltipDisplayFlags.ENCHANTMENTS.func_242397_a());
    }
  }
}
