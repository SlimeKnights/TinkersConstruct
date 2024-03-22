package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeSerializer.ISpecializedRepairRecipe;

import javax.annotation.Nullable;

/**
 * Serializer for the recipe
 */
@RequiredArgsConstructor
public class SpecializedRepairRecipeSerializer<T extends Recipe<?> & ISpecializedRepairRecipe> implements LoggingRecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T fromJson(ResourceLocation id, JsonObject json) {
    Ingredient tool = Ingredient.fromJson(JsonHelper.getElement(json, "tool"));
    MaterialId repairMaterial = MaterialId.PARSER.getAndDeserialize(json, "repair_material");
    return factory.create(id, tool, repairMaterial);
  }

  @Nullable
  @Override
  public T fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
    Ingredient tool = Ingredient.fromNetwork(buffer);
    MaterialId repairMaterial = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
    return factory.create(id, tool, repairMaterial);
  }

  @Override
  public void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
    recipe.getTool().toNetwork(buffer);
    buffer.writeUtf(recipe.getRepairMaterial().toString());
  }

  /** Interface for serializing the recipe */
  public interface ISpecializedRepairRecipe {
    /** Gets the tool ingredient from the recipe */
    Ingredient getTool();

    /** Gets the material ID from the recipe */
    MaterialId getRepairMaterial();
  }

  /** Factory constructor for this serializer */
  @FunctionalInterface
  public interface IFactory<T extends Recipe<?> & ISpecializedRepairRecipe> {
    T create(ResourceLocation id, Ingredient tool, MaterialId repairMaterial);
  }
}
