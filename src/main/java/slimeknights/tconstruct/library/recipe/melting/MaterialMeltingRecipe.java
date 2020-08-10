package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Melting recipe for melting a material item into the proper fluid
 */
@RequiredArgsConstructor
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  private final IMaterialItem item;
  private final int amount;
  private List<MeltingRecipe> multiRecipes;

  @Override
  public boolean matches(ISingleItemInventory inv, World worldIn) {
    // must be a item, and the item must have something to melt into
    ItemStack stack = inv.getStack();
    return stack.getItem() == item && item.getMaterial(stack).getFluid() != Fluids.EMPTY;
  }

  @Override
  public FluidStack getOutput(ISingleItemInventory inv) {
    return new FluidStack(item.getMaterial(inv.getStack()).getFluid(), amount);
  }

  @Override
  public int getTemperature(ISingleItemInventory inv) {
    return IMeltingRecipe.calcTemperature(item.getMaterial(inv.getStack()).getTemperature(), amount);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }

  @Override
  public List<MeltingRecipe> getRecipes() {
    if (multiRecipes == null) {
      multiRecipes = MaterialRegistry.getMaterials().stream()
                                     .filter(mat -> mat.getFluid() != Fluids.EMPTY)
                                     .map(mat -> {
        ResourceLocation matId = mat.getIdentifier();
        return new MeltingRecipe(
          new ResourceLocation(id.getNamespace(), String.format("%s/%s/%s", id.getPath(), matId.getNamespace(), matId.getPath())),
          group,
          Ingredient.fromStacks(item.getItemstackWithMaterial(mat)),
          new FluidStack(mat.getFluid(), amount),
          mat.getTemperature()
        );
      }).collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /**
   * Serializer for {@link MaterialMeltingRecipe}
   */
  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MaterialMeltingRecipe> {
    @Override
    public MaterialMeltingRecipe read(ResourceLocation id, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      IMaterialItem item = RecipeHelper.deserializeItem(JSONUtils.getString(json, "item"), "item", IMaterialItem.class);
      int amount = JSONUtils.getInt(json, "amount");
      return new MaterialMeltingRecipe(id, group, item, amount);
    }

    @Nullable
    @Override
    public MaterialMeltingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        IMaterialItem item = RecipeHelper.readItem(buffer, IMaterialItem.class);
        int amount = buffer.readInt();
        return new MaterialMeltingRecipe(id, group, item, amount);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, MaterialMeltingRecipe recipe) {
      try {
        buffer.writeString(recipe.group);
        RecipeHelper.writeItem(buffer, recipe.item);
        buffer.writeInt(recipe.amount);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }
  }
}
