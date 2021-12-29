package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeSerializer.ISpecializedRepairRecipe;

import javax.annotation.Nullable;

/**
 * Serializer for the recipe
 */
@RequiredArgsConstructor
public class SpecializedRepairRecipeSerializer<T extends Recipe<?> & ISpecializedRepairRecipe> extends LoggingRecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T fromJson(ResourceLocation id, JsonObject json) {
    Ingredient tool = Ingredient.fromJson(JsonHelper.getElement(json, "tool"));
    MaterialId repairMaterial = MaterialRecipeSerializer.getMaterial(json, "repair_material");
    return factory.create(id, tool, repairMaterial);
  }

  @Nullable
  @Override
  protected T readSafe(ResourceLocation id, FriendlyByteBuf buffer) {
    Ingredient tool = Ingredient.fromNetwork(buffer);
    MaterialId repairMaterial = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
    return factory.create(id, tool, repairMaterial);
  }

  @Override
  protected void writeSafe(FriendlyByteBuf buffer, T recipe) {
    recipe.getTool().toNetwork(buffer);
    buffer.writeUtf(recipe.getRepairMaterialID().toString());
  }

  /** Interface for serializing the recipe */
  public interface ISpecializedRepairRecipe {
    /** Gets the tool ingredient from the recipe */
    Ingredient getTool();

    /** Gets the material ID from the recipe */
    MaterialId getRepairMaterialID();
  }

  /** Factory constructor for this serializer */
  @FunctionalInterface
  public interface IFactory<T extends Recipe<?> & ISpecializedRepairRecipe> {
    T create(ResourceLocation id, Ingredient tool, MaterialId repairMaterial);
  }
}
