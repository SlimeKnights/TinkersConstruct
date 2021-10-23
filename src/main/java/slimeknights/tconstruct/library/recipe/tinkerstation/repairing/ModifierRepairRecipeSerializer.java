package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer.IModifierRepairRecipe;

import javax.annotation.Nullable;

/**
 * Serializer for the recipe
 */
@RequiredArgsConstructor
public class ModifierRepairRecipeSerializer<T extends IRecipe<?> & IModifierRepairRecipe> extends LoggingRecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T read(ResourceLocation id, JsonObject json) {
    Modifier modifier = ModifierEntry.deserializeModifier(json, "modifier");
    Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
    int repairAmount = JSONUtils.getInt(json, "repair_amount");
    return factory.create(id, modifier, ingredient, repairAmount);
  }

  @Nullable
  @Override
  protected T readSafe(ResourceLocation id, PacketBuffer buffer) {
    Modifier modifier = buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS);
    Ingredient ingredient = Ingredient.read(buffer);
    int repairAmount = buffer.readVarInt();
    return factory.create(id, modifier, ingredient, repairAmount);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, T recipe) {
    buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, recipe.getModifier());
    recipe.getIngredient().write(buffer);
    buffer.writeVarInt(recipe.getRepairAmount());
  }

  /** Interface for serializing the recipe */
  public interface IModifierRepairRecipe {
    /** Gets the modifier needed to perform this recipe */
    Modifier getModifier();
    /** Gets the ingredient used to repair this item */
    Ingredient getIngredient();
    /** Gets the amount repaired per item */
    int getRepairAmount();
  }

  /** Factory constructor for this serializer */
  @FunctionalInterface
  public interface IFactory<T extends IRecipe<?> & IModifierRepairRecipe> {
    T create(ResourceLocation id, Modifier modifier, Ingredient ingredient, int repairAmount);
  }
}
