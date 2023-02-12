package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Modifier recipe that changes max level and slot behavior each level. Used for a single input recipe that has multiple slot requirements
 */
public class MultilevelModifierRecipe extends ModifierRecipe implements IMultiRecipe<IDisplayModifierRecipe> {
  /** Error for when the tool is below the min level */
  protected static final String KEY_MIN_LEVEL = TConstruct.makeTranslationKey("recipe", "modifier.min_level");

  private final List<LevelEntry> levels;
  protected MultilevelModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierId result, boolean allowCrystal, List<LevelEntry> levels) {
    super(id, inputs, toolRequirement, maxToolSize, requirements, requirementsError, new ModifierEntry(result, 1), levels.get(0).maxLevel() + 1, levels.get(0).slots(), allowCrystal);
    this.levels = levels;
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ItemStack tinkerable = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(tinkerable);

    // check requirements first, easy check
    ValidatedResult requirements = validateRequirements(tool);
    if (requirements.hasError()) {
      return requirements;
    }

    // next few checks depend on the current level to decide
    int newLevel = tool.getModifierLevel(result.getModifier()) + 1;
    LevelEntry levelEntry = null;
    for (LevelEntry check : levels) {
      if (check.matches(newLevel)) {
        levelEntry = check;
        break;
      }
    }
    // no entry means our level is above the max, so done now
    if (levelEntry == null) {
      // if the level is below the minimum, then display a different error
      if (newLevel < levels.get(0).minLevel()) {
        return ValidatedResult.failure(KEY_MIN_LEVEL, result.getModifier().getDisplayName(), levels.get(0).minLevel() - 1);
      }
      return ValidatedResult.failure(KEY_MAX_LEVEL, result.getModifier().getDisplayName(), levels.get(levels.size() - 1).maxLevel());
    }

    // found our level entry, time to validate slots
    SlotCount slots = levelEntry.slots();
    requirements = checkSlots(tool, slots);
    if (requirements.hasError()) {
      return requirements;
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    if (slots != null) {
      persistentData.addSlots(slots.getType(), -slots.getCount());
    }

    // add modifier
    tool.addModifier(result.getId(), result.getLevel());

    // ensure no modifier problems
    ValidatedResult toolValidation = tool.validate();
    if (toolValidation.hasError()) {
      return toolValidation;
    }

    return ValidatedResult.success(tool.createStack(Math.min(tinkerable.getCount(), shrinkToolSlotBy())));
  }


  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.multilevelModifierSerializer.get();
  }


  /* JEI display */

  private List<IDisplayModifierRecipe> displayRecipes = null;

  @Override
  public List<IDisplayModifierRecipe> getRecipes() {
    // no inputs means this recipe is to handle internal crystal stuff
    if (inputs.isEmpty()) {
      return Collections.emptyList();
    }
    if (displayRecipes == null) {
      // this instance is a proper display recipe for the first level entry, for the rest build display instances with unique requirements keys
      List<ItemStack> toolWithoutModifier = getToolWithoutModifier();
      List<ItemStack> toolWithModifier = getToolWithModifier();
      String requirementsError = getRequirementsError();
      displayRecipes = Streams.concat(
        Stream.of(this),
        levels.stream().skip(1).map(levelEntry ->
          new DisplayModifierRecipe(inputs, toolWithoutModifier, toolWithModifier, requirementsError + ".level_" + levelEntry.minLevel, result, levelEntry.maxLevel, levelEntry.slots))
      ).toList();
    }

    return displayRecipes;
  }

  /** Entry in the levels list */
  record LevelEntry(@Nullable SlotCount slots, int minLevel, int maxLevel) {
    /** Checks if this entry matches the given level */
    public boolean matches(int level) {
      return minLevel <= level && level <= maxLevel;
    }

    /** Parses the object from JSON */
    public static LevelEntry parse(JsonObject json) {
      int min, max;
      if (json.has("level")) {
        min = max = GsonHelper.getAsInt(json, "level");
      } else {
        min = GsonHelper.getAsInt(json, "min_level", 1);
        max = GsonHelper.getAsInt(json, "max_level", Short.MAX_VALUE);
        if (min > max) {
          throw new JsonSyntaxException("min_level must be less than or equal to max_level");
        }
      }
      SlotCount slots = null;
      if (json.has("slots")) {
        slots = SlotCount.fromJson(GsonHelper.getAsJsonObject(json, "slots"));
      }
      return new LevelEntry(slots, min, max);
    }

    /** Serializes this object to JSON */
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      if (slots != null) {
        JsonObject slotJson = new JsonObject();
        slotJson.addProperty(slots.getType().getName(), slots.getCount());
        json.add("slots", slotJson);
      }
      if (minLevel == maxLevel) {
        json.addProperty("level", minLevel);
      } else {
        json.addProperty("min_level", minLevel);
        if (maxLevel < Short.MAX_VALUE) {
          json.addProperty("max_level", maxLevel);
        }
      }
      return json;
    }

    /** Parses the object from the buffer */
    public static LevelEntry read(FriendlyByteBuf buffer) {
      SlotCount slots = SlotCount.read(buffer);
      int min = buffer.readVarInt();
      int max = buffer.readVarInt();
      return new LevelEntry(slots, min, max);
    }

    /** Writes the object to the buffer */
    public void write(FriendlyByteBuf buffer) {
      SlotCount.write(slots, buffer);
      buffer.writeVarInt(minLevel);
      buffer.writeVarInt(maxLevel);
    }
  }

  public static class Serializer extends LoggingRecipeSerializer<MultilevelModifierRecipe> {
    @Override
    public MultilevelModifierRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient toolRequirement = Ingredient.fromJson(json.get("tools"));
      int maxToolSize = GsonHelper.getAsInt(json, "max_tool_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
      ModifierId result = new ModifierId(JsonHelper.getResourceLocation(json, "result"));
      List<LevelEntry> levels = JsonHelper.parseList(json, "levels", LevelEntry::parse);

      // inputs is optional, as long as we allow the crystal
      List<SizedIngredient> ingredients = Collections.emptyList();
      if (json.has("inputs")) {
        ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      }
      boolean allowCrystal = GsonHelper.getAsBoolean(json, "allow_crystal", true);
      if (ingredients.isEmpty() && !allowCrystal) {
        throw new JsonSyntaxException("Must either have inputs or allow crystal");
      }

      // requirements only really matter on the first level, but we need the error string for higher levels
      // can skip if we have no inputs though, no one will see it then
      ModifierMatch requirements = ModifierMatch.ALWAYS;
      // need requirements error if either we have requirements, or we have inputs
      String requirementsError = "";
      if (json.has("requirements")) {
        JsonObject reqJson = GsonHelper.getAsJsonObject(json, "requirements");
        requirements = ModifierMatch.deserialize(reqJson);
        requirementsError = GsonHelper.getAsString(reqJson, "error");
      } else if (!ingredients.isEmpty()) {
        requirementsError = GsonHelper.getAsString(json, "level_error");
      }

      return new MultilevelModifierRecipe(id, ingredients, toolRequirement, maxToolSize, requirements, requirementsError, result, allowCrystal, levels);
    }

    @Nullable
    @Override
    protected MultilevelModifierRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient toolRequirement = Ingredient.fromNetwork(buffer);
      int maxToolSize = buffer.readVarInt();
      ModifierMatch requirements = ModifierMatch.read(buffer);
      String requirementsError = buffer.readUtf(Short.MAX_VALUE);
      ModifierId result = new ModifierId(buffer.readResourceLocation());
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> inputs = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        inputs.add(SizedIngredient.read(buffer));
      }
      boolean allowCrystal = buffer.readBoolean();
      size = buffer.readVarInt();
      ImmutableList.Builder<LevelEntry> levels = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        levels.add(LevelEntry.read(buffer));
      }
      return new MultilevelModifierRecipe(id, inputs.build(), toolRequirement, maxToolSize, requirements, requirementsError, result, allowCrystal, levels.build());
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, MultilevelModifierRecipe recipe) {
      recipe.toolRequirement.toNetwork(buffer);
      buffer.writeVarInt(recipe.maxToolSize);
      recipe.requirements.write(buffer);
      buffer.writeUtf(recipe.requirementsError);
      buffer.writeResourceLocation(recipe.result.getId());
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeBoolean(recipe.allowCrystal);
      buffer.writeVarInt(recipe.levels.size());
      for (LevelEntry level : recipe.levels) {
        level.write(buffer);
      }
    }
  }
}
