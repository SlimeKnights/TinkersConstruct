package slimeknights.tconstruct.tables.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
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
  public boolean matches(ITinkerStationInventory inv, World world) {
    if (!TinkerTags.Items.MODIFIABLE.contains(inv.getTinkerableStack().getItem())) {
      return false;
    }
    // must find at least one input, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
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
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    // how much did we actually consume?
    int damageTaken = ToolStack.from(result).getDamage() - ToolStack.from(inv.getTinkerableStack()).getDamage();
    IncrementalModifierRecipe.updateInputs(inv, ingredient, damageTaken, damageAmount, ItemStack.EMPTY);
  }

  /** @deprecated Use {@link #getValidatedResult(ITinkerStationInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationDamagingSerializer.get();
  }

  /** Serializer logic */
  public static class Serializer extends RecipeSerializer<TinkerStationDamagingRecipe> {
    @Override
    public TinkerStationDamagingRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
      int restoreAmount = JSONUtils.getInt(json, "damage_amount");
      return new TinkerStationDamagingRecipe(id, ingredient, restoreAmount);
    }

    @Nullable
    @Override
    public TinkerStationDamagingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient ingredient = Ingredient.read(buffer);
      int damageAmount = buffer.readVarInt();
      return new TinkerStationDamagingRecipe(id, ingredient, damageAmount);
    }

    @Override
    public void write(PacketBuffer buffer, TinkerStationDamagingRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeVarInt(recipe.damageAmount);
    }
  }

  /** Builder for datagen */
  @RequiredArgsConstructor(staticName = "damage")
  public static class Builder extends AbstractRecipeBuilder<Builder> {
    private final Ingredient ingredient;
    private final int damageAmount;

    @Override
    public void build(Consumer<IFinishedRecipe> consumer) {
      ItemStack[] stacks = ingredient.getMatchingStacks();
      if (stacks.length == 0) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      build(consumer, Objects.requireNonNull(stacks[0].getItem().getRegistryName()));
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
      if (ingredient == Ingredient.EMPTY) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
      consumer.accept(new FinishedRecipe(id, advancementId));
    }

    private class FinishedRecipe extends AbstractFinishedRecipe {
      public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serialize(JsonObject json) {
        json.add("ingredient", ingredient.serialize());
        json.addProperty("damage_amount", damageAmount);
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return TinkerTables.tinkerStationDamagingSerializer.get();
      }
    }
  }
}
