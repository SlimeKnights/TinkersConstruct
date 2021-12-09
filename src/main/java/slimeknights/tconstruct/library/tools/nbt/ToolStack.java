package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Class handling parsing all tool related NBT
 */
@RequiredArgsConstructor(staticName = "from")
public class ToolStack implements IModifierToolStack {
  /** Error messages for when there are not enough remaining modifiers */
  private static final String KEY_VALIDATE_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.validate_slots");

  /** Volatile mod data key for the durability before modifiers */
  @Deprecated
  public static final ResourceLocation ORIGINAL_DURABILITY_KEY = TConstruct.getResource("durability");

  // persistent NBT
  /** Tag for list of materials */
  public static final String TAG_MATERIALS = "tic_materials";
  /** Tag for extra arbitrary modifier data */
  public static final String TAG_PERSISTENT_MOD_DATA = "tic_persistent_data";
  /** Tag for recipe based modifier */
  public static final String TAG_UPGRADES = "tic_upgrades";
  /** Tag marking a tool as broken */
  public static final String TAG_BROKEN = "tic_broken";

  // volatile NBT
  /** Tag for calculated stats */
  protected static final String TAG_STATS = "tic_stats";
  /** Tag for tool stat global multipliers */
  protected static final String TAG_MULTIPLIERS = "tic_multipliers";
  /** Tag for arbitrary modifier data rebuilt on stat rebuild */
  public static final String TAG_VOLATILE_MOD_DATA = "tic_volatile_data";
  /** Tag for merged modifiers of upgrades and traits */
  public static final String TAG_MODIFIERS = "tic_modifiers";

  // vanilla tags
  protected static final String TAG_DAMAGE = "Damage";
  public static final String TAG_UNBREAKABLE = "Unbreakable";

  /** List of tags to disallow editing for the relevant modifier hooks, disallows all tags we touch. Ignores unbreakable as we only look at that tag for vanilla compat */
  private static final Set<String> RESTRICTED_TAGS = ImmutableSet.of(TAG_MATERIALS, TAG_STATS, TAG_MULTIPLIERS, TAG_PERSISTENT_MOD_DATA, TAG_VOLATILE_MOD_DATA, TAG_UPGRADES, TAG_MODIFIERS, TAG_BROKEN, TAG_DAMAGE, ModifierUtil.TAG_ENCHANTMENTS, ModifierUtil.TAG_HIDE_FLAGS);

  /** Item representing this tool */
  @Getter
  private final Item item;
  /** Tool definition, describing part count and alike */
  @Getter
  private final ToolDefinition definition;
  /** Original tool NBT */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT nbt;
  /** Public view of the internal NBT, to give to modifier hooks */
  private RestrictedCompoundTag restrictedNBT;

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
  /** Data object containing stat multipliers for each stat */
  @Nullable
  private StatsNBT multipliers;
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
    ToolDefinition definition = item instanceof IModifiable
                                ? ((IModifiable)item).getToolDefinition()
                                : ToolDefinition.EMPTY;
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
    // add slots
    definition.getData().buildSlots(tool.getPersistentData());
    // update the materials
    tool.setMaterials(materials);
    return tool;
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

  /** Clears all cached data, used with capabilities to prevent cached data from being out of sync due to external changes */
  public void clearCache() {
    this.damage = -1;
    this.broken = null;
    this.materials = null;
    this.upgrades = null;
    this.modifiers = null;
    this.stats = null;
    this.multipliers = null;
    this.volatileModData = null;
    this.persistentModData = null;
  }

  /** Creates an item stack from this tool stack */
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

  /**
   * Gets a restricted view of the tools NBT
   * @return  Tool NBT without access to internal tags
   */
  public RestrictedCompoundTag getRestrictedNBT() {
    if (restrictedNBT == null) {
      restrictedNBT = new RestrictedCompoundTag(nbt, RESTRICTED_TAGS);
    }
    return restrictedNBT;
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

  @Override
  public boolean isUnbreakable() {
    return nbt.getBoolean(TAG_UNBREAKABLE);
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
    setDamage(getStats().getInt(ToolStats.DURABILITY));
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
    int durability = getStats().getInt(ToolStats.DURABILITY);
    if (isBroken()) {
      return durability;
    }
    // ensure we never return a number larger than max
    return Math.min(getDamageRaw(), durability - 1);
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
    return Math.max(0, getStats().getInt(ToolStats.DURABILITY) - getDamageRaw());
  }

  /**
   * Sets the tools damage
   * @param  damage  New damage
   */
  @Override
  public void setDamage(int damage) {
    int durability = getStats().getInt(ToolStats.DURABILITY);
    if (damage >= durability) {
      damage = Math.max(0, durability);
      setBrokenRaw(true);
    } else {
      setBrokenRaw(false);
    }
    this.damage = damage;
    nbt.putInt(TAG_DAMAGE, damage);
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
   * Sets the tool stats, and stores it in NBT
   * @param stats  Stats instance
   */
  protected void setStats(StatsNBT stats) {
    this.stats = stats;
    nbt.put(TAG_STATS, stats.serializeToNBT());
    // if we no longer have enough durability, decrease the damage and mark it broken
    int newMax = getStats().getInt(ToolStats.DURABILITY);
    if (getDamageRaw() >= newMax) {
      setDamage(newMax);
    }
  }

  /**
   * Gets the tool stats if parsed, or parses from NBT if not yet parsed
   * @return stats
   */
  protected StatsNBT getMultipliers() {
    if (multipliers == null) {
      multipliers = StatsNBT.readFromNBT(nbt.get(TAG_MULTIPLIERS));
    }
    return multipliers;
  }

  /**
   * Sets the tool multipliers, and stores it in NBT
   * @param multipliers  Stats instance
   */
  protected void setMultipliers(StatsNBT multipliers) {
    this.multipliers = multipliers;
    nbt.put(TAG_MULTIPLIERS, multipliers.serializeToNBT());
  }

  @Override
  public float getModifier(FloatToolStat stat) {
    StatsNBT multipliers = getMultipliers();
    if (multipliers.hasStat(stat)) {
      return multipliers.getFloat(stat);
    }
    return 1.0f;
  }

  /* Materials */

  @Override
  public MaterialNBT getMaterials() {
    if (!getDefinition().isMultipart()) {
      return MaterialNBT.EMPTY;
    }
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
    if (materials == MaterialNBT.EMPTY) {
      this.nbt.remove(TAG_MATERIALS);
    } else {
      this.nbt.put(TAG_MATERIALS, materials.serializeToNBT());
    }
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
    setMaterials(materials.isEmpty() ? MaterialNBT.EMPTY : new MaterialNBT(materials));
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
  @Override
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

  /**
   * Removes a single modifier to this tool
   * @param modifier  Modifier to remove
   * @param level     Level to remove
   */
  public void removeModifier(Modifier modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above 0");
    }
    ModifierNBT newModifiers = getUpgrades().withoutModifier(modifier, level);
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
    for (SlotType slotType : SlotType.getAllSlotTypes()) {
      if (getFreeSlots(slotType) < 0) {
        return ValidatedResult.failure(KEY_VALIDATE_SLOTS, slotType.getDisplayName());
      }
    }
    // next, ensure modifiers validate
    ValidatedResult result;
    for (ModifierEntry entry : getModifierList()) {
      result = entry.getModifier().validate(this, entry.getLevel());
      if (result.hasError()) {
        return result;
      }
    }
    return ValidatedResult.PASS;
  }

  /**
   * Recalculates any relevant cached data. Called after either the materials or modifiers list changes
   */
  public void rebuildStats() {
    // first, rebuild the list of all modifiers
    ModifierNBT.Builder modBuilder = ModifierNBT.builder();
    modBuilder.add(getUpgrades());
    modBuilder.add(getDefinition().getData().getTraits());
    List<PartRequirement> parts = getDefinition().getData().getParts();
    List<IMaterial> materials = getMaterialsList();
    int max = Math.min(materials.size(), parts.size());
    for (int i = 0; i < max; i++) {
      modBuilder.add(MaterialRegistry.getInstance().getTraits(materials.get(i).getIdentifier(), parts.get(i).getStatType()));
    }
    ModifierNBT allMods = modBuilder.build();
    setModifiers(allMods);

    // pass in the list to stats, note for no part tools this should always be empty
    StatsNBT stats = definition.buildStats(materials);
    ModifierStatsBuilder statBuilder = ModifierStatsBuilder.builder();
    definition.getData().buildStatMultipliers(statBuilder);

    // next, update modifier related properties
    List<ModifierEntry> modifierList = allMods.getModifiers();
    if (modifierList.isEmpty()) {
      // if no modifiers, clear out data that only exists with modifiers
      nbt.remove(TAG_VOLATILE_MOD_DATA);
      volatileModData = IModDataReadOnly.EMPTY;
    } else {
      ModDataNBT volatileData = new ModDataNBT();

      // build persistent data first, its a parameter to the other two hooks
      IModDataReadOnly persistentData = getPersistentData();
      ToolDefinition toolDefinition = getDefinition();
      for (ModifierEntry entry : modifierList) {
        entry.getModifier().addVolatileData(item, toolDefinition, stats, persistentData, entry.getLevel(), volatileData);
      }

      // regular stats last so we can include volatile data
      for (ModifierEntry entry : modifierList) {
        Modifier mod = entry.getModifier();
        int level = entry.getLevel();
        mod.addToolStats(item, toolDefinition, stats, persistentData, volatileData, level, statBuilder);
      }

      // set into NBT
      setVolatileModData(volatileData);
    }
    // build stats from the tool stats
    setStats(statBuilder.build(stats));
    setMultipliers(statBuilder.buildMultipliers());

    // finally, update raw data, called last to make the parameters more convenient mostly, plus no other hooks should be responding to this data
    for (ModifierEntry entry : modifierList) {
      entry.getModifier().addRawData(this, entry.getLevel(), getRestrictedNBT());
    }
  }


  /* Static helpers */

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param stack  Stack to check
   * @return  True if initialized
   */
  public static boolean isInitialized(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    return nbt != null && nbt.contains(TAG_STATS, NBT.TAG_COMPOUND);
  }

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param stack  Stack to check
   * @return  True if initialized
   */
  public static boolean hasMaterials(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    return nbt != null && nbt.contains(TAG_MATERIALS, NBT.TAG_LIST);
  }

  /**
   * Checks if the given tool NBT needs initialization
   * @param nbt         NBT instance
   * @param definition  Tool definition
   * @return  If true, initialization is needed
   */
  private static boolean needsInitialization(@Nullable CompoundNBT nbt, ToolDefinition definition) {
    // cannot initialize if datapacks are not loaded
    if (definition.getData() == definition.getStatProvider().getDefaultData()) {
      return false;
    }
    // no NBT? initialize if we don't need more data
    if (nbt == null) {
      return !definition.isMultipart();
    }
    // have data but no materials? cannot initialize yet
    if (definition.isMultipart() && !nbt.contains(TAG_MATERIALS, NBT.TAG_LIST)) {
      return false;
    }
    // no stats? not initialized
    return !nbt.contains(TAG_STATS, NBT.TAG_COMPOUND);
  }

  /**
   * Ensures the given item stack is initialized. Called in crafting hooks
   * @param stack ItemStack to initialize
   */
  public static void ensureInitialized(ItemStack stack) {
    if (stack.getItem() instanceof IModifiable) {
      ensureInitialized(stack, ((IModifiable) stack.getItem()).getToolDefinition());
    }
  }

  /**
   * Ensures the given item stack is initialized. Intended to be called in {@link Item#onCreated(ItemStack, World, PlayerEntity)}
   * @param stack           ItemStack to initialize
   * @param toolDefinition  Tool definition
   */
  public static void ensureInitialized(ItemStack stack, ToolDefinition toolDefinition) {
    if (!ToolStack.isInitialized(stack)) {
      // if the tool is multipart, do nothing without materials
      if (needsInitialization(stack.getTag(), toolDefinition)) {
        ToolStack tool = ToolStack.from(stack);
        toolDefinition.getData().buildSlots(tool.getPersistentData());
        tool.rebuildStats();
      }
    }
  }

  /**
   * Rebuilds the item stack when loaded from NBT
   * stops things from being wrong if modifiers or materials change
   * @param item        Item to build
   * @param compound    Full stack NBT including item
   * @param definition  Tool definition
   */
  public static void verifyTag(Item item, CompoundNBT compound, ToolDefinition definition) {
    // skip if no definition data loaded
    if (definition.getData() != definition.getStatProvider().getDefaultData() && compound.contains("tag", NBT.TAG_COMPOUND)) {
      CompoundNBT nbt = compound.getCompound("tag");
      // if the stack has materials, resolve all material redirects
      if (nbt.contains(ToolStack.TAG_MATERIALS, NBT.TAG_LIST)) {
        MaterialIdNBT stored = MaterialIdNBT.readFromNBT(nbt.getList(ToolStack.TAG_MATERIALS, NBT.TAG_STRING));
        MaterialIdNBT resolved = stored.resolveRedirects();
        if (resolved != stored) {
          resolved.updateNBT(nbt);
        }
      }
      ToolStack tool = ToolStack.from(item, definition, nbt);
      // if uninitialized, add slots
      if (needsInitialization(nbt, definition)) {
        definition.getData().buildSlots(tool.getPersistentData());
      }
      // build stats regardless
      tool.rebuildStats();
    }
  }
}
