package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Melting recipe for melting a material item into the proper fluid
 */
@RequiredArgsConstructor
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  @Getter
  private final Identifier id;
  @Getter
  private final String group;
  private final IMaterialItem item;
  private final int cost;
  private List<MeltingRecipe> multiRecipes;

  @Override
  public boolean matches(IMeltingInventory inv, World worldIn) {
    // must be a item, and the item must have something to melt into
    ItemStack stack = inv.getStack();
    return stack.getItem() == item && item.getMaterial(stack).getFluid() != Fluids.EMPTY;
  }

  @Override
  public FluidVolume getOutput(IMeltingInventory inv) {
    IMaterial material = item.getMaterial(inv.getStack());
    return new FluidVolume(material.getFluid(), material.getFluidPerUnit() * cost);
  }

  @Override
  public int getTemperature(IMeltingInventory inv) {
    return item.getMaterial(inv.getStack()).getTemperature();
  }

  /** Gets the melting time for this recipe */
  private int getTime(IMaterial material) {
    return IMeltingRecipe.calcTimeForAmount(material.getTemperature(), material.getFluidPerUnit() * cost);
  }

  @Override
  public int getTime(IMeltingInventory inv) {
    return getTime(item.getMaterial(inv.getStack()));
  }

  @Override
  public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }

  @Override
  public List<MeltingRecipe> getRecipes() {
    if (multiRecipes == null) {
      multiRecipes = MaterialRegistry.getMaterials().stream()
                                     .filter(mat -> mat.getFluid() != Fluids.EMPTY)
                                     .map(mat -> {
        Identifier matId = mat.getIdentifier();
        return new MeltingRecipe(
          new Identifier(id.getNamespace(), String.format("%s/%s/%s", id.getPath(), matId.getNamespace(), matId.getPath())),
          group,
          Ingredient.ofStacks(item.getItemstackWithMaterial(mat)),
          new FluidVolume(mat.getFluid(), mat.getFluidPerUnit() * cost),
          mat.getTemperature(),
          getTime(mat)
        );
      }).collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /**
   * Serializer for {@link MaterialMeltingRecipe}
   */
  public static class Serializer extends RecipeSerializer<MaterialMeltingRecipe> {
    @Override
    public MaterialMeltingRecipe read(Identifier id, JsonObject json) {
      String group = JsonHelper.getString(json, "group", "");
      IMaterialItem item = RecipeHelper.deserializeItem(JsonHelper.getString(json, "item"), "item", IMaterialItem.class);
      int cost = JsonHelper.getInt(json, "item_cost");
      return new MaterialMeltingRecipe(id, group, item, cost);
    }

    @Nullable
    @Override
    public MaterialMeltingRecipe read(Identifier id, PacketByteBuf buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        IMaterialItem item = RecipeHelper.readItem(buffer, IMaterialItem.class);
        int amount = buffer.readVarInt();
        return new MaterialMeltingRecipe(id, group, item, amount);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketByteBuf buffer, MaterialMeltingRecipe recipe) {
      try {
        buffer.writeString(recipe.group);
        RecipeHelper.writeItem(buffer, recipe.item);
        buffer.writeVarInt(recipe.cost);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }
  }
}
