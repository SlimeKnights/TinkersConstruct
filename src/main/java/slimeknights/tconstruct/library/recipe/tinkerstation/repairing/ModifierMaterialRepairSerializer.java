package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairSerializer.IModifierMaterialRepairRecipe;

import javax.annotation.Nullable;

/**
 * Serializer for the recipe
 */
@RequiredArgsConstructor
public class ModifierMaterialRepairSerializer<T extends Recipe<?> & IModifierMaterialRepairRecipe> extends LoggingRecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T fromJson(ResourceLocation id, JsonObject json) {
    ModifierId modifier = new ModifierId(JsonHelper.getResourceLocation(json, "modifier"));
    MaterialId repairMaterial = MaterialId.fromJson(json, "repair_material");
    return factory.create(id, modifier, repairMaterial);
  }

  @Nullable
  @Override
  protected T fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
    ModifierId modifierId = new ModifierId(buffer.readUtf(Short.MAX_VALUE));
    MaterialId repairMaterial = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
    return factory.create(id, modifierId, repairMaterial);
  }

  @Override
  protected void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
    buffer.writeUtf(recipe.getModifier().toString());
    buffer.writeUtf(recipe.getRepairMaterial().toString());
  }

  /** Interface for serializing the recipe */
  public interface IModifierMaterialRepairRecipe {
    /** Gets the modifier required to apply this repair */
    ModifierId getModifier();

    /** Gets the material ID from the recipe */
    MaterialId getRepairMaterial();
  }

  /** Factory constructor for this serializer */
  @FunctionalInterface
  public interface IFactory<T extends Recipe<?> & IModifierMaterialRepairRecipe> {
    T create(ResourceLocation id, ModifierId modifierId, MaterialId repairMaterial);
  }
}
