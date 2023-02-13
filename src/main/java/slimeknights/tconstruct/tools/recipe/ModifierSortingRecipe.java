package slimeknights.tconstruct.tools.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/** Reorders modifiers ion a tool */
public class ModifierSortingRecipe extends AbstractWorktableRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "modifier_sorting.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "modifier_sorting.description");
  private static final Component NOT_ENOUGH_MODIFIERS = TConstruct.makeTranslation("recipe", "modifier_sorting.not_enough_modifiers");

  public ModifierSortingRecipe(ResourceLocation id, List<SizedIngredient> inputs) {
    super(id, inputs);
  }

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.MODIFIABLE)) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null && inv.getTinkerable().getUpgrades().getModifiers().size() < 2) {
      return NOT_ENOUGH_MODIFIERS;
    }
    return DESCRIPTION;
  }

  @Override
  public RecipeResult<ToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier) {
    ToolStack tool = inv.getTinkerable();

    // find the modifier to remove
    List<ModifierEntry> upgrades = tool.getUpgrades().getModifiers();
    int toMove = IntStream.range(0, upgrades.size())
                          .filter(i -> upgrades.get(i).matches(modifier.getId()))
                          .findFirst().orElse(-1);
    // if no change, no need to do anything
    if (toMove == -1) {
      return RecipeResult.pass();
    }

    // reorder
    tool = tool.copy();
    List<ModifierEntry> newUpgrades = new ArrayList<>(upgrades);
    Collections.swap(newUpgrades, toMove, toMove == upgrades.size() - 1 ? 0 : toMove + 1);
    tool.setUpgrades(new ModifierNBT(newUpgrades));

    // no need to validate, its the same modifiers
    return RecipeResult.success(tool);
  }

  @Override
  public int toolResultSize() {
    return 64;
  }

  @Override
  public void updateInputs(IToolStackView result, ITinkerableContainer.Mutable inv, boolean isServer) {
    // input is not consumed
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSortingSerializer.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<ModifierSortingRecipe> {
    @Override
    public ModifierSortingRecipe fromJson(ResourceLocation id, JsonObject json) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      return new ModifierSortingRecipe(id, ingredients);
    }

    @Nullable
    @Override
    public ModifierSortingRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      return new ModifierSortingRecipe(id, builder.build());
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, ModifierSortingRecipe recipe) {
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
    }
  }

  @RequiredArgsConstructor(staticName = "sorting")
  public static class Builder extends AbstractSizedIngredientRecipeBuilder<Builder> {
    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
      save(consumer, Objects.requireNonNull(inputs.get(0).getMatchingStacks().get(0).getItem().getRegistryName()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (inputs.isEmpty()) {
        throw new IllegalStateException("Must have at least one ingredient");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
      consumer.accept(new Finished(id, advancementId));
    }

    private class Finished extends SizedFinishedRecipe {
      public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerModifiers.modifierSortingSerializer.get();
      }
    }
  }
}
