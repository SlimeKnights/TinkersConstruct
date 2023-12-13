package slimeknights.tconstruct.tools.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifierRemovalRecipe extends AbstractWorktableRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "remove_modifier.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "remove_modifier.description");
  private static final Component NO_MODIFIERS = TConstruct.makeTranslation("recipe", "remove_modifier.no_modifiers");

  private final SizedIngredient sizedTool;
  private final List<ItemStack> leftovers;
  private final IJsonPredicate<ModifierId> modifierPredicate;

  protected final Predicate<ModifierEntry> entryPredicate;
  private List<ModifierEntry> displayModifiers;

  public ModifierRemovalRecipe(ResourceLocation id, SizedIngredient toolRequirement, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id, Ingredient.EMPTY, inputs);
    this.sizedTool = toolRequirement;
    this.leftovers = leftovers;
    this.modifierPredicate = modifierPredicate;
    this.entryPredicate = mod -> modifierPredicate.matches(mod.getId());
  }

  /** @deprecated use {#link #ModifierRemovalRecipe(ResourceLocation, SizedIngredient, List, List, IJsonPredicate} */
  @Deprecated
  public ModifierRemovalRecipe(ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
    this(id, SizedIngredient.fromTag(TinkerTags.Items.MODIFIABLE), inputs, leftovers, modifierPredicate);
  }

  /** @deprecated use {@link #ModifierRemovalRecipe(ResourceLocation, SizedIngredient, List, List, IJsonPredicate)} */
  @Deprecated
  public ModifierRemovalRecipe(ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> leftovers) {
    this(id, inputs, leftovers, ModifierPredicate.ALWAYS);
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!sizedTool.test(inv.getTinkerableStack())) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  /** Filters the given modifier list */
  protected List<ModifierEntry> filter(@Nullable IToolStackView tool, List<ModifierEntry> modifiers) {
    if (modifierPredicate != ModifierPredicate.ALWAYS) {
      return modifiers.stream().filter(entryPredicate).toList();
    }
    return modifiers;
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv == null) {
      if (displayModifiers == null) {
        displayModifiers = filter(null, ModifierRecipeLookup.getRecipeModifierList());
      }
      return displayModifiers;
    }
    return filter(inv.getTinkerable(), inv.getTinkerable().getUpgrades().getModifiers());
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null && inv.getTinkerable().getUpgrades().getModifiers().stream().noneMatch(entryPredicate)) {
      return NO_MODIFIERS;
    }
    return DESCRIPTION;
  }

  @Override
  public RecipeResult<ToolStack> getResult(ITinkerableContainer inv, ModifierEntry entry) {
    ToolStack tool = inv.getTinkerable();

    // salvage
    tool = tool.copy();
    ModifierId modifierId = entry.getId();
    ModifierSalvage salvage = ModifierRecipeLookup.getSalvage(inv.getTinkerableStack(), tool, modifierId, entry.getLevel());

    // restore the slots
    if (salvage != null) {
      salvage.updateTool(tool);
    }

    // first remove hook, primarily for removing raw NBT which is highly discouraged using
    int newLevel = tool.getModifierLevel(modifierId) - 1;
    Modifier modifier = entry.getModifier();
    if (newLevel <= 0) {
      modifier.getHook(TinkerHooks.RAW_DATA).removeRawData(tool, modifier, tool.getRestrictedNBT());
    }

    // remove the actual modifier
    tool.removeModifier(modifierId, 1);

    // ensure the tool is still valid
    Component error = tool.tryValidate();
    if (error != null) {
      return RecipeResult.failure(error);
    }
    // if this was the last level, validate the tool is still valid without it
    if (newLevel <= 0) {
      error = modifier.getHook(TinkerHooks.REMOVE).onRemoved(tool, modifier);
      if (error != null) {
        return RecipeResult.failure(error);
      }
    }

    // check the modifier requirements
    ValidatedResult validated = ModifierRecipeLookup.checkRequirements(inv.getTinkerableStack(), tool);
    if (validated.hasError()) {
      return RecipeResult.failure(validated.getMessage());
    }

    // successfully removed
    return RecipeResult.success(tool);
  }

  @Override
  public int toolResultSize() {
    return 64;
  }

  @Override
  public void updateInputs(IToolStackView result, ITinkerableContainer.Mutable inv, boolean isServer) {
    super.updateInputs(result, inv, isServer);
    if (isServer) {
      for (ItemStack stack : leftovers) {
        inv.giveItem(stack.copy());
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.removeModifierSerializer.get();
  }



  /* JEI */

  /** Gets a list of tools to display */
  @Override
  public List<ItemStack> getInputTools() {
    if (tools == null) {
      tools = sizedTool.getMatchingStacks().stream().map(stack -> {
        ItemStack tool = IModifiableDisplay.getDisplayStack(stack.getItem());
        if (stack.getCount() > 1) {
          tool = ItemHandlerHelper.copyStackWithSize(tool, stack.getCount());
        }
        return tool;
      }).toList();
    }
    return tools;
  }

  /** @deprecated use {@link Factory} */
  @Deprecated
  @FunctionalInterface
  public interface ModifierRemovalRecipeFactory extends Factory {
    ModifierRemovalRecipe create(ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate);

    @Override
    default ModifierRemovalRecipe create(ResourceLocation id, SizedIngredient toolRequirement, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
      return create(id, inputs, leftovers, modifierPredicate);
    }
  }

  /** Factory interface for modifier removal recipes */
  @FunctionalInterface
  public interface Factory {
    ModifierRemovalRecipe create(ResourceLocation id, SizedIngredient toolRequirement, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate);
  }

  @RequiredArgsConstructor
  public static class Serializer extends LoggingRecipeSerializer<ModifierRemovalRecipe> {
    private final Factory factory;

    /** @deprecated use {@link #Serializer(Factory)} */
    @Deprecated
    public Serializer(ModifierRemovalRecipeFactory factory) {
      this((Factory)factory);
    }

    @Override
    public ModifierRemovalRecipe fromJson(ResourceLocation id, JsonObject json) {
      SizedIngredient tool;
      if (json.has("tools")) {
        tool = SizedIngredient.deserialize(GsonHelper.getAsJsonObject(json, "tools"));
      } else {
        tool = SizedIngredient.fromTag(TinkerTags.Items.MODIFIABLE);
      }
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      List<ItemStack> leftovers = Collections.emptyList();
      if (json.has("leftovers")) {
        leftovers = JsonHelper.parseList(json, "leftovers", JsonUtils::convertToItemStack);
      }
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ALWAYS;
      if (json.has("modifier_predicate")) {
        modifierPredicate = ModifierPredicate.LOADER.getAndDeserialize(json, "modifier_predicate");
      }
      return factory.create(id, tool, ingredients, leftovers, modifierPredicate);
    }

    @Nullable
    @Override
    public ModifierRemovalRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      SizedIngredient tool = SizedIngredient.read(buffer);
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> ingredients = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        ingredients.add(SizedIngredient.read(buffer));
      }
      size = buffer.readVarInt();
      ImmutableList.Builder<ItemStack> leftovers = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        leftovers.add(buffer.readItem());
      }
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.LOADER.fromNetwork(buffer);
      return factory.create(id, tool, ingredients.build(), leftovers.build(), modifierPredicate);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, ModifierRemovalRecipe recipe) {
      recipe.sizedTool.write(buffer);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeVarInt(recipe.leftovers.size());
      for (ItemStack itemStack : recipe.leftovers) {
        buffer.writeItem(itemStack);
      }
      ModifierPredicate.LOADER.toNetwork(recipe.modifierPredicate, buffer);
    }
  }

  @RequiredArgsConstructor(staticName = "removal")
  public static class Builder extends AbstractSizedIngredientRecipeBuilder<Builder> {
    private final RecipeSerializer<? extends ModifierRemovalRecipe> serializer;
    private final List<ItemStack> leftovers = new ArrayList<>();
    private SizedIngredient tools = SizedIngredient.EMPTY;
    @Setter @Accessors(fluent = true)
    private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ALWAYS;

    public static Builder removal() {
      return removal(TinkerModifiers.removeModifierSerializer.get());
    }

    /** Sets the tool requirement for this recipe */
    public Builder setTools(SizedIngredient ingredient) {
      this.tools = ingredient;
      return this;
    }

    /** Sets the tool requirement for this recipe */
    public Builder setTools(Ingredient ingredient) {
      return setTools(SizedIngredient.of(ingredient));
    }

    /** Adds a leftover stack to the recipe */
    public Builder addLeftover(ItemStack stack) {
      leftovers.add(stack);
      return this;
    }

    /** Adds a leftover stack to the recipe */
    public Builder addLeftover(ItemLike item) {
      return addLeftover(new ItemStack(item));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
      save(consumer, Objects.requireNonNull(leftovers.get(0).getItem().getRegistryName()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (inputs.isEmpty()) {
        throw new IllegalStateException("Must have at least one input");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
      consumer.accept(new Finished(id, advancementId));
    }

    private class Finished extends SizedFinishedRecipe {
      public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serializeRecipeData(JsonObject json) {
        super.serializeRecipeData(json);
        SizedIngredient ingredient = tools;
        if (ingredient == SizedIngredient.EMPTY) {
          ingredient = SizedIngredient.fromTag(TinkerTags.Items.MODIFIABLE);
        }
        json.add("tools", ingredient.serialize());
        if (!leftovers.isEmpty()) {
          JsonArray array = new JsonArray();
          for (ItemStack stack : leftovers) {
            array.add(JsonUtils.serializeItemStack(stack));
          }
          json.add("leftovers", array);
        }
        json.add("modifier_predicate", ModifierPredicate.LOADER.serialize(modifierPredicate));
      }

      @Override
      public RecipeSerializer<?> getType() {
        return serializer;
      }
    }
  }
}
