package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;

/**
 * Recipe to craft an ordinary item using the part builder
 */
@RequiredArgsConstructor
public class ItemPartRecipe implements IDisplayPartBuilderRecipe {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final MaterialId materialId;
  @Nullable
  private IMaterial material = null;
  @Getter
  private final Pattern pattern;
  @Getter
  private final int cost;
  private final ItemOutput result;

  /** Gets the material used in this recipe */
  @Override
  public IMaterial getMaterial() {
    if (material == null) {
      material = MaterialRegistry.getMaterial(materialId);
    }
    return material;
  }

  @Override
  public boolean partialMatch(IPartBuilderInventory inv) {
    // first, must have a pattern
    if (inv.getPatternStack().getItem() != TinkerTables.pattern.get()) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    if (!inv.getStack().isEmpty()) {
      MaterialRecipe materialRecipe = inv.getMaterial();
      return materialRecipe != null && materialRecipe.getMaterial() == getMaterial();
    }
    // no material item? return match in case we get one later
    return true;
  }

  @Override
  public boolean matches(IPartBuilderInventory inv, World worldIn) {
    MaterialRecipe materialRecipe = inv.getMaterial();
    return materialRecipe != null && materialRecipe.getMaterial() == getMaterial()
           && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return result.get();
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.itemPartBuilderSerializer.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<ItemPartRecipe> {
    @Override
    public ItemPartRecipe read(ResourceLocation id, JsonObject json) {
      MaterialId materialId = MaterialRecipeSerializer.getMaterial(json, "material");
      Pattern pattern = new Pattern(JSONUtils.getString(json, "pattern"));
      int cost = JSONUtils.getInt(json, "cost");
      ItemOutput result = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      return new ItemPartRecipe(id, materialId, pattern, cost, result);
    }

    @Nullable
    @Override
    protected ItemPartRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      MaterialId materialId = new MaterialId(buffer.readString(Short.MAX_VALUE));
      Pattern pattern = new Pattern(buffer.readString(Short.MAX_VALUE));
      int cost = buffer.readVarInt();
      ItemOutput result = ItemOutput.read(buffer);
      return new ItemPartRecipe(id, materialId, pattern, cost, result);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, ItemPartRecipe recipe) {
      buffer.writeString(recipe.materialId.toString());
      buffer.writeString(recipe.pattern.toString());
      buffer.writeVarInt(recipe.cost);
      recipe.result.write(buffer);
    }
  }
}
