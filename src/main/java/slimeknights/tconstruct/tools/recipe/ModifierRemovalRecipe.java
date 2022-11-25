package slimeknights.tconstruct.tools.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
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

public class ModifierRemovalRecipe extends AbstractWorktableRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "remove_modifier.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "remove_modifier.description");
  private static final Component NO_MODIFIERS = TConstruct.makeTranslation("recipe", "remove_modifier.no_modifiers");

  private final List<ItemStack> leftovers;
  public ModifierRemovalRecipe(ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> leftovers) {
    super(id, inputs);
    this.leftovers = leftovers;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null && inv.getTinkerable().getUpgrades().isEmpty()) {
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
      modifier.beforeRemoved(tool, tool.getRestrictedNBT());
    }

    // remove the actual modifier
    tool.removeModifier(modifierId, 1);

    // second remove hook, useful for removing modifier specific state data
    if (newLevel <= 0) {
      modifier.onRemoved(tool);
    }

    // ensure the tool is still valid
    Component error = tool.tryValidate();
    if (error != null) {
      return RecipeResult.failure(error);
    }
    // if this was the last level, validate the tool is still valid without it
    if (newLevel <= 0) {
      ValidatedResult validated = modifier.validate(tool, 0);
      if (validated.hasError()) {
        return RecipeResult.failure(validated.getMessage());
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

  public static class Serializer extends LoggingRecipeSerializer<ModifierRemovalRecipe> {

    @Override
    public ModifierRemovalRecipe fromJson(ResourceLocation id, JsonObject json) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      List<ItemStack> leftovers = Collections.emptyList();
      if (json.has("leftovers")) {
        leftovers = JsonHelper.parseList(json, "leftovers", JsonUtils::convertToItemStack);
      }
      return new ModifierRemovalRecipe(id, ingredients, leftovers);
    }

    @Nullable
    @Override
    public ModifierRemovalRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
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
      return new ModifierRemovalRecipe(id, ingredients.build(), leftovers.build());
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, ModifierRemovalRecipe recipe) {
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeVarInt(recipe.leftovers.size());
      for (ItemStack itemStack : recipe.leftovers) {
        buffer.writeItem(itemStack);
      }
    }
  }

  @RequiredArgsConstructor(staticName = "removal")
  public static class Builder extends AbstractSizedIngredientRecipeBuilder<Builder> {
    private final List<ItemStack> leftovers = new ArrayList<>();

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
        if (!leftovers.isEmpty()) {
          JsonArray array = new JsonArray();
          for (ItemStack stack : leftovers) {
            array.add(JsonUtils.serializeItemStack(stack));
          }
          json.add("leftovers", array);
        }
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerModifiers.removeModifierSerializer.get();
      }
    }
  }
}
