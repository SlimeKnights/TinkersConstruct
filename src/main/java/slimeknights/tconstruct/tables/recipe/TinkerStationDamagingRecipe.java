package slimeknights.tconstruct.tables.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TinkerStationDamagingRecipe implements ITinkerStationRecipe {
  private static final ValidatedResult BROKEN = ValidatedResult.failure(TConstruct.makeTranslationKey("recipe", "damaging.broken"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int damageAmount;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.DURABILITY)) {
      return false;
    }
    // must find at least one input, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    if (ToolDamageUtil.isBroken(inv.getTinkerableStack())) {
      return BROKEN;
    }
    // simply damage the tool directly
    ToolStack tool = ToolStack.copyFrom(inv.getTinkerableStack());
    int maxDamage = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, damageAmount);
    ToolDamageUtil.directDamage(tool, maxDamage, null, inv.getTinkerableStack());
    return ValidatedResult.success(tool.createStack());
  }

  @Override
  public int shrinkToolSlotBy() {
    return 1;
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // how much did we actually consume?
    int damageTaken = ToolStack.from(result).getDamage() - ToolStack.from(inv.getTinkerableStack()).getDamage();
    IncrementalModifierRecipe.updateInputs(inv, ingredient, damageTaken, damageAmount, ItemStack.EMPTY);
  }

  /** @deprecated Use {@link #getValidatedResult(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationDamagingSerializer.get();
  }

  /** Serializer logic */
  public static class Serializer extends AbstractRecipeSerializer<TinkerStationDamagingRecipe> {
    @Override
    public TinkerStationDamagingRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
      int restoreAmount = GsonHelper.getAsInt(json, "damage_amount");
      return new TinkerStationDamagingRecipe(id, ingredient, restoreAmount);
    }

    @Nullable
    @Override
    public TinkerStationDamagingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient ingredient = Ingredient.fromNetwork(buffer);
      int damageAmount = buffer.readVarInt();
      return new TinkerStationDamagingRecipe(id, ingredient, damageAmount);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, TinkerStationDamagingRecipe recipe) {
      recipe.ingredient.toNetwork(buffer);
      buffer.writeVarInt(recipe.damageAmount);
    }
  }

  /** Builder for datagen */
  @RequiredArgsConstructor(staticName = "damage")
  public static class Builder extends AbstractRecipeBuilder<Builder> {
    private final Ingredient ingredient;
    private final int damageAmount;

    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
      ItemStack[] stacks = ingredient.getItems();
      if (stacks.length == 0) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      save(consumer, Objects.requireNonNull(stacks[0].getItem().getRegistryName()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (ingredient == Ingredient.EMPTY) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
      consumer.accept(new Finished(id, advancementId));
    }

    private class Finished extends AbstractFinishedRecipe {
      public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serializeRecipeData(JsonObject json) {
        json.add("ingredient", ingredient.toJson());
        json.addProperty("damage_amount", damageAmount);
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerTables.tinkerStationDamagingSerializer.get();
      }
    }
  }
}
