package slimeknights.tconstruct.library.recipe.tinkerstation.modifiying;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;

public class ToolModifierRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ToolModifierRecipe> {

  @Override
  public ToolModifierRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
    int cost = JSONUtils.getInt(json, "cost");

    String modifierId = JSONUtils.getString(json, "modifier");

    if (modifierId.isEmpty()) {
      throw new JsonSyntaxException("Recipe modifier must not empty.");
    }

    return new ToolModifierRecipe(recipeId, group, ingredient, cost, new ModifierId(modifierId));
  }

  @Nullable
  @Override
  public ToolModifierRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient ingredient = Ingredient.read(buffer);
      int cost = buffer.readInt();
      String modifierId = buffer.readString(Short.MAX_VALUE);

      return new ToolModifierRecipe(recipeId, group, ingredient, cost, new ModifierId(modifierId));
    }
    catch (Exception e) {
      TConstruct.log.error("Error reading tool modifier recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, ToolModifierRecipe recipe) {
    try {
      buffer.writeString(recipe.group);
      recipe.ingredient.write(buffer);
      buffer.writeInt(recipe.cost);
      buffer.writeString(recipe.modifierId.toString());
    }
    catch (Exception e) {
      TConstruct.log.error("Error writing tool modifier recipe to packet.", e);
      throw e;
    }
  }
}
