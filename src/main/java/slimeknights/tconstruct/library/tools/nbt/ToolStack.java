package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
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
public class ToolStack implements IToolStackView {
  /** Error messages for when there are not enough remaining modifiers */
  private static final String KEY_VALIDATE_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.validate_slots");
  /** flag to set in persistent data to mark a tool as needing persistent data, by default any tools with no persistent data are initialized */
  public static final ResourceLocation NEEDS_SLOTS_BUILT = TConstruct.getResource("needs_slots_built");

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
  public static final String TAG_HIDE_FLAGS = "HideFlags";

  /** Flags for vanilla tooltip parts to hide */
  private static final int HIDE_FLAGS = TooltipPart.ENCHANTMENTS.getMask() | TooltipPart.MODIFIERS.getMask();

  /** List of tags to disallow editing for the relevant modifier hooks, disallows all tags we touch. Ignores unbreakable as we only look at that tag for vanilla compat */
  private static final Set<String> RESTRICTED_TAGS = ImmutableSet.of(TAG_MATERIALS, TAG_STATS, TAG_MULTIPLIERS, TAG_PERSISTENT_MOD_DATA, TAG_VOLATILE_MOD_DATA, TAG_UPGRADES, TAG_MODIFIERS, TAG_BROKEN, TAG_DAMAGE, ModifierUtil.TAG_ENCHANTMENTS, TAG_HIDE_FLAGS);

  /** Item representing this tool */
  @Getter
  private final Item item;
  /** Tool definition, describing part count and alike */
  @Getter
  private final ToolDefinition definition;
  /** Original tool NBT */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundTag nbt;
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
  private MultiplierNBT multipliers;
  /** Data object containing modifier data that is recreated when the modifier list changes */
  @Nullable
  private IModDataView volatileModData;

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
    CompoundTag nbt = stack.getTag();
    if (nbt == null) {
      nbt = new CompoundTag();
      if (!copyNbt) {
        // bypass the setter as vanilla insists on setting damage values there, along with verifying the tag
        // both are things we will do later, doing so now causes us to recursively call this method (though not infinite)
        stack.tag = nbt;
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
  public static ToolStack createTool(Item item, ToolDefinition definition, MaterialNBT materials) {
    ToolStack tool = from(item, definition, new CompoundTag());
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
  public ItemStack createStack(int size) {
    ItemStack stack = new ItemStack(item, size);
    stack.setTag(nbt);
    return stack;
  }

  /** Creates an item stack from this tool stack */
  public ItemStack createStack() {
    return createStack(1);
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
  protected MultiplierNBT getMultipliers() {
    if (multipliers == null) {
      multipliers = MultiplierNBT.readFromNBT(nbt.get(TAG_MULTIPLIERS));
    }
    return multipliers;
  }

  /**
   * Sets the tool multipliers, and stores it in NBT
   * @param multipliers  Stats instance
   */
  protected void setMultipliers(MultiplierNBT multipliers) {
    this.multipliers = multipliers;
    nbt.put(TAG_MULTIPLIERS, multipliers.serializeToNBT());
  }

  @Override
  public float getMultiplier(INumericToolStat<?> stat) {
    MultiplierNBT multipliers = getMultipliers();
    if (multipliers.hasStat(stat)) {
      return multipliers.get(stat);
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
   * Replaces the material at the given index
   * @param index        Index to replace
   * @param replacement  New material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public void replaceMaterial(int index, MaterialVariantId replacement) {
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
   * Updates the upgrades list on the tool
   * @param modifiers  New upgrades
   */
  public void setUpgrades(ModifierNBT modifiers) {
    this.upgrades = modifiers;
    nbt.put(TAG_UPGRADES, modifiers.serializeToNBT());
    rebuildStats();
  }

  /**
   * Adds a single modifier to this tool
   * @param modifier  Modifier to add
   * @param level     Level to add
   */
  public void addModifier(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above 0");
    }
    setUpgrades(getUpgrades().withModifier(modifier, level));
  }

  /**
   * Removes a single modifier to this tool
   * @param modifier  Modifier to remove
   * @param level     Level to remove
   */
  public void removeModifier(ModifierId modifier, int level) {
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
      if (nbt.contains(TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND)) {
        persistentModData = ModDataNBT.readFromNBT(nbt.getCompound(TAG_PERSISTENT_MOD_DATA));
      } else {
        // if no tag exists, create it
        CompoundTag tag = new CompoundTag();
        nbt.put(TAG_PERSISTENT_MOD_DATA, tag);
        persistentModData = ModDataNBT.readFromNBT(tag);
      }
    }
    return persistentModData;
  }

  @Override
  public IModDataView getVolatileData() {
    if (volatileModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_VOLATILE_MOD_DATA, Tag.TAG_COMPOUND)) {
        volatileModData = ModDataNBT.readFromNBT(nbt.getCompound(TAG_VOLATILE_MOD_DATA));
      } else {
        // if no tag exists, return empty
        volatileModData = IModDataView.EMPTY;
      }
    }
    return volatileModData;
  }

  /**
   * Updates the volatile mod data in NBT, called in {@link #rebuildStats()}
   * @param modData  New data
   */
  protected void setVolatileModData(ModDataNBT modData) {
    CompoundTag data = modData.getData();
    if (data.isEmpty()) {
      volatileModData = IModDataView.EMPTY;
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

  /** Initializes modifier slots on the tool if needed */
  public void ensureSlotsBuilt() {
    // no persistent data means no slots added yet, so time to build them
    // note that empty persistent data will not trigger this, which is important when a tool has no slots remaining
    if (!nbt.contains(TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND) || getPersistentData().getBoolean(NEEDS_SLOTS_BUILT)) {
      ModDataNBT persistentData = getPersistentData();
      persistentData.remove(NEEDS_SLOTS_BUILT);
      definition.getData().buildSlots(persistentData);
    }
  }

  /** Called on inventory tick to ensure the tool has all required data including materials, prevents tools with no stats from existing */
  public void ensureHasData() {
    if (!definition.isDataLoaded()) {
      return;
    }
    ensureSlotsBuilt();

    // if the tool has stats already, nothing more to do
    if (isInitialized(nbt)) {
      return;
    }
    // need materials to build stats, randomize them if missing
    if (definition.isMultipart() && !nbt.contains(TAG_MATERIALS, Tag.TAG_LIST)) {
      setMaterialsRaw(ToolBuildHandler.randomMaterials(definition.getData(), definition.getDefaultMaxTier(), false));
    }
    rebuildStats();
  }

  /**
   * Recalculates any relevant cached data. Called after either the materials or modifiers list changes
   */
  public void rebuildStats() {
    // hide enchants and attributes, both are added ourself (filtered)
    nbt.putInt(TAG_HIDE_FLAGS, HIDE_FLAGS);

    // first, rebuild the list of all modifiers
    ModifierNBT.Builder modBuilder = ModifierNBT.builder();
    modBuilder.add(getUpgrades());
    modBuilder.add(getDefinition().getData().getTraits());
    List<PartRequirement> parts = getDefinition().getData().getParts();
    MaterialNBT materials = getMaterials();
    int max = Math.min(materials.size(), parts.size());
    for (int i = 0; i < max; i++) {
      modBuilder.add(MaterialRegistry.getInstance().getTraits(materials.get(i).getId(), parts.get(i).getStatType()));
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
      volatileModData = IModDataView.EMPTY;
    } else {
      ModDataNBT volatileData = new ModDataNBT();

      // context for further modifier hooks
      ToolRebuildContext context = new ToolRebuildContext(item, getDefinition(), getMaterials(), getUpgrades(), allMods, stats, getPersistentData(), volatileData);

      // build persistent data first, its a parameter to the other two hooks
      for (ModifierEntry entry : modifierList) {
        entry.getModifier().addVolatileData(context, entry.getLevel(), volatileData);
      }

      // regular stats last so we can include volatile data
      for (ModifierEntry entry : modifierList) {
        entry.getModifier().addToolStats(context, entry.getLevel(), statBuilder);
      }

      // set into NBT
      setVolatileModData(volatileData);
    }
    // build stats from the tool stats
    setStats(statBuilder.build(stats, item));
    setMultipliers(statBuilder.buildMultipliers(item));

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
    CompoundTag tag = stack.getTag();
    return tag != null && isInitialized(tag);
  }

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param tag  Tag to check
   * @return  True if initialized
   */
  public static boolean isInitialized(CompoundTag tag) {
    return tag.contains(TAG_STATS, Tag.TAG_COMPOUND);
  }

  /**
   * Checks if the given tool stats have been initialized, used as a marker to indicate slots are not yet applied
   * @param stack  Stack to check
   * @return  True if initialized
   */
  public static boolean hasMaterials(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.contains(TAG_MATERIALS, Tag.TAG_LIST);
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
   * Ensures the given item stack is initialized. Intended to be called in {@link Item#onCraftedBy(ItemStack, Level, Player)}
   * @param stack           ItemStack to initialize
   * @param toolDefinition  Tool definition
   */
  public static void ensureInitialized(ItemStack stack, ToolDefinition toolDefinition) {
    // must be loaded
    if (!toolDefinition.isDataLoaded()) {
      return;
    }
    CompoundTag tag = stack.getTag();
    // already initialized? nothing to do
    if (tag != null && isInitialized(tag)) {
      return;
    }
    // time to initialize
    ToolStack.from(stack).ensureHasData();
  }

  /**
   * Rebuilds the item stack when loaded from NBT
   * stops things from being wrong if modifiers or materials change
   * @param item        Item to build
   * @param tag         Stack tag
   * @param definition  Tool definition
   */
  public static void verifyTag(Item item, CompoundTag tag, ToolDefinition definition) {
    // this function is sometimes called before datapack contents load, do nothing then
    if (!definition.isDataLoaded() || tag.getBoolean(TooltipUtil.KEY_DISPLAY)) {
      return;
    }

    // resolve all material redirects
    boolean hasMaterials = tag.contains(ToolStack.TAG_MATERIALS, Tag.TAG_LIST);
    if (hasMaterials) {
      MaterialIdNBT stored = MaterialIdNBT.readFromNBT(tag.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING));
      MaterialIdNBT resolved = stored.resolveRedirects();
      if (resolved != stored) {
        resolved.updateNBT(tag);
      }
    }
    // rebuild stats
    ToolStack tool = ToolStack.from(item, definition, tag);
    tool.ensureSlotsBuilt();
    if (hasMaterials || !definition.isMultipart()) {
      tool.rebuildStats();
    }
  }
}
