package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
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
public class SpecializedRepairRecipeSerializer<T extends IRecipe<?> & ISpecializedRepairRecipe> extends LoggingRecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T read(ResourceLocation id, JsonObject json) {
    Ingredient tool = Ingredient.deserialize(JsonHelper.getElement(json, "tool"));
    MaterialId repairMaterial = MaterialRecipeSerializer.getMaterial(json, "repair_material");
    return factory.create(id, tool, repairMaterial);
  }

  @Nullable
  @Override
  protected T readSafe(ResourceLocation id, PacketBuffer buffer) {
    Ingredient tool = Ingredient.read(buffer);
    MaterialId repairMaterial = new MaterialId(buffer.readString(Short.MAX_VALUE));
    return factory.create(id, tool, repairMaterial);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, T recipe) {
    recipe.getTool().write(buffer);
    buffer.writeString(recipe.getRepairMaterialID().toString());
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
  public interface IFactory<T extends IRecipe<?> & ISpecializedRepairRecipe> {
    T create(ResourceLocation id, Ingredient tool, MaterialId repairMaterial);
  }
}
