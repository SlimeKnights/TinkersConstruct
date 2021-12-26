package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Shared logic between modifier and incremental modifier recipes */
public abstract class AbstractModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  /** Error for when the tool is at the max modifier level */
  protected static final String KEY_MAX_LEVEL = TConstruct.makeTranslationKey("recipe", "modifier.max_level");
  /** Error for when the tool has too few upgrade slots */
  protected static final String KEY_NOT_ENOUGH_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slots");
  /** Error for when the tool has too few upgrade slots from a single slot */
  protected static final String KEY_NOT_ENOUGH_SLOT = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slot");
  /** Generic requirements error, for if a proper error is missing */
  protected static final ValidatedResult REQUIREMENTS_ERROR = ModifierRecipeLookup.DEFAULT_ERROR;

  @Getter
  private final ResourceLocation id;
  /** Ingredient representing the required tool, typically a tag */
  protected final Ingredient toolRequirement;
  /** Modifiers that must match for this recipe */
  protected final ModifierMatch requirements;
  /** Error message to display if the requirements do not match */
  protected final String requirementsError;
  /** Modifier this recipe is adding */
  protected final ModifierEntry result;
  /** Maximum level of this modifier allowed */
  @Getter
  private final int maxLevel;
  /** Gets the slots required by this recipe. If null, no slots required */
  @Getter
  @Nullable
  private final SlotCount slots;

  protected AbstractModifierRecipe(ResourceLocation id, Ingredient toolRequirement, ModifierMatch requirements,
                                   String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
    this.id = id;
    this.toolRequirement = toolRequirement;
    this.requirements = requirements;
    this.requirementsError = requirementsError;
    this.result = result;
    this.maxLevel = maxLevel;
    this.slots = slots;
    ModifierRecipeLookup.addRequirements(toolRequirement, result, requirements, requirementsError);
  }

  @Override
  public abstract ValidatedResult getValidatedResult(ITinkerStationInventory inv);

  /** @deprecated */
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }


  /* JEI display */
  /** Cache of input items shared between result and input */
  @Nullable
  private List<ItemStack> toolInputs = null;

  /** Gets or builds the list of tool inputs */
  private List<ItemStack> getToolInputs() {
    if (toolInputs == null) {
      toolInputs = Arrays.stream(this.toolRequirement.getItems()).map(stack -> {
        if (stack.getItem() instanceof IModifiableDisplay) {
          return ((IModifiableDisplay)stack.getItem()).getRenderTool();
        }
        return stack;
      }).collect(Collectors.toList());
    }
    return toolInputs;
  }

  /** Cache of display tool inputs */
  private List<ItemStack> displayInputs = null;

  /** Cache of display output */
  private List<ItemStack> toolWithModifier = null;

  /** Display result, may be a higher level than real result */
  private ModifierEntry displayResult;

  @Override
  public ModifierEntry getDisplayResult() {
    if (displayResult == null) {
      // if the recipe has a minimum level of this modifier, add that min level to the display result
      int min = requirements.getMinLevel(result.getModifier());
      if (min > 0) {
        displayResult = new ModifierEntry(result.getModifier(), result.getLevel() + min);
      } else {
        displayResult = result;
      }
    }
    return displayResult;
  }

  /**
   * Add extra ingredients for display in JEI
   * @param builder  Ingredient list builder
   */
  protected abstract void addIngredients(Consumer<List<ItemStack>> builder);

  @Override
  public List<List<ItemStack>> getDisplayItems() {
    if (displayInputs == null) {
      displayInputs = getToolInputs().stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, null)).collect(Collectors.toList());
    }
    // if empty requirement, assume any modifiable
    ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
    // inputs
    builder.add(displayInputs);
    addIngredients(builder::add);
    return builder.build();
  }

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      toolWithModifier = getToolInputs().stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result)).collect(Collectors.toList());
    }
    return toolWithModifier;
  }

  @Override
  public boolean hasRequirements() {
    return requirements != ModifierMatch.ALWAYS;
  }

  @Override
  public String getRequirementsError() {
    if (requirementsError.isEmpty()) {
      return ModifierRecipeLookup.DEFAULT_ERROR_KEY;
    }
    return requirementsError;
  }


  /* Helpers */

  /** Gets the modifiers list for a tool, ignoring partial levels from incremental modifiers */
  public static List<ModifierEntry> getModifiersIgnoringPartial(ToolStack toolStack) {
    ImmutableList.Builder<ModifierEntry> finalList = ImmutableList.builder();
    IModDataReadOnly persistentData = toolStack.getPersistentData();
    for (ModifierEntry entry : toolStack.getModifierList()) {
      Modifier modifier = entry.getModifier();
      // if the modifier is not incremental, or does not has the key set, nothing to do
      int needed = ModifierRecipeLookup.getNeededPerLevel(modifier);
      if (needed == 0 || !persistentData.contains(modifier.getId(), NBT.TAG_ANY_NUMERIC)) {
        finalList.add(entry);
      } else {
        // if the modifier has enough, nothing to do
        // if not enough, decrease level by 1, skipping if now at 0
        int has = persistentData.getInt(modifier.getId());
        if (has >= needed) {
          finalList.add(entry);
        } else if (entry.getLevel() > 1) {
          finalList.add(new ModifierEntry(modifier, entry.getLevel() - 1));
        }
      }
    }
    return finalList.build();
  }

  /**
   * Validates that this tool meets the modifier requirements, is not too high of a level, and has enough upgrade/ability slots
   * @param tool           Tool stack instance
   * @return  Validated result with error, or pass if no error
   */
  protected ValidatedResult validatePrerequisites(ToolStack tool) {
    // validate modifier prereqs, skip building fancy list for always
    if (requirements != ModifierMatch.ALWAYS && !requirements.test(getModifiersIgnoringPartial(tool))) {
      return requirementsError.isEmpty() ? REQUIREMENTS_ERROR : ValidatedResult.failure(requirementsError);
    }
    // max level of modifier
    if (maxLevel != 0 && tool.getUpgrades().getLevel(result.getModifier()) + result.getLevel() > maxLevel) {
      return ValidatedResult.failure(KEY_MAX_LEVEL, result.getModifier().getDisplayName(), maxLevel);
    }
    // ensure we have enough slots
    if (slots != null) {
      int count = slots.getCount();
      if (tool.getFreeSlots(slots.getType()) < count) {
        if (count == 1) {
          return ValidatedResult.failure(KEY_NOT_ENOUGH_SLOT, slots.getType().getDisplayName());
        } else {
          return ValidatedResult.failure(KEY_NOT_ENOUGH_SLOTS, count, slots.getType().getDisplayName());
        }
      }
    }
    return ValidatedResult.PASS;
  }

  /** Shared serializer logic */
  public static abstract class Serializer<T extends AbstractModifierRecipe> extends LoggingRecipeSerializer<T> {
    /**
     * Reads any remaining data from the modifier recipe
     * @return  Full recipe instance
     */
    public abstract T read(ResourceLocation id, JsonObject json, Ingredient toolRequirement, ModifierMatch requirements,
                           String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots);

    /**
     * Reads any remaining data from the modifier recipe
     * @return  Full recipe instance
     */
    public abstract T read(ResourceLocation id, PacketBuffer buffer, Ingredient toolRequirement, ModifierMatch requirements,
                  String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots);

    @Override
    public final T fromJson(ResourceLocation id, JsonObject json) {
      Ingredient toolRequirement = Ingredient.fromJson(json.get("tools"));
      ModifierMatch requirements = ModifierMatch.ALWAYS;
      String requirementsError = "";
      if (json.has("requirements")) {
        JsonObject reqJson = JSONUtils.getAsJsonObject(json, "requirements");
        requirements = ModifierMatch.deserialize(reqJson);
        requirementsError = JSONUtils.getAsString(reqJson, "error", "");
      }
      ModifierEntry result = ModifierEntry.fromJson(JSONUtils.getAsJsonObject(json, "result"));
      int maxLevel = JSONUtils.getAsInt(json, "max_level", 0);
      if (maxLevel < 0) {
        throw new JsonSyntaxException("max must be non-negative");
      }
      SlotCount slots = null;
      if (json.has("slots")) {
        slots = SlotCount.fromJson(JSONUtils.getAsJsonObject(json, "slots"));
      } else {
        // legacy support
        if (json.has("upgrade_slots") && json.has("ability_slots")) {
          throw new JsonSyntaxException("Cannot set both upgrade_slots and ability_slots");
        }
        if (json.has("upgrade_slots")) {
          slots = new SlotCount(SlotType.UPGRADE, JsonUtils.getIntMin(json, "upgrade_slots", 0));
          TConstruct.LOG.warn("Using deprecated modifier recipe key upgrade_slots for recipe " + id);
        } else if (json.has("ability_slots")) {
          slots = new SlotCount(SlotType.ABILITY, JsonUtils.getIntMin(json, "ability_slots", 0));
          TConstruct.LOG.warn("Using deprecated modifier recipe key ability_slots for recipe " + id);
        }
      }
      return read(id, json, toolRequirement, requirements, requirementsError, result, maxLevel, slots);
    }

    @Override
    protected final T readSafe(ResourceLocation id, PacketBuffer buffer) {
      Ingredient toolRequirement = Ingredient.fromNetwork(buffer);
      ModifierMatch requirements = ModifierMatch.read(buffer);
      String requirementsError = buffer.readUtf(Short.MAX_VALUE);
      ModifierEntry result = ModifierEntry.read(buffer);
      int maxLevel = buffer.readVarInt();
      SlotCount slots = SlotCount.read(buffer);
      return read(id, buffer, toolRequirement, requirements, requirementsError, result, maxLevel, slots);
    }

    /** Writes relevant packet data. When overriding, call super first for consistency with {@link #fromJson(ResourceLocation, JsonObject)} */
    @Override
    protected void writeSafe(PacketBuffer buffer, T recipe) {
      recipe.toolRequirement.toNetwork(buffer);
      recipe.requirements.write(buffer);
      buffer.writeUtf(recipe.requirementsError);
      recipe.result.write(buffer);
      buffer.writeVarInt(recipe.getMaxLevel());
      SlotCount.write(recipe.getSlots(), buffer);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '{' + id + '}';
  }
}
