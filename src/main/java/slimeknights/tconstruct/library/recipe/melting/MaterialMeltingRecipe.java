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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.recipe.IMultiRecipe;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  @Getter
  private final ResourceLocation id;
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
          Ingredient.fromStacks(item.getItemstackWithMaterial(mat)),
          new FluidStack(mat.getFluid(), amount),
          mat.getTemperature()
        );
      }).collect(Collectors.toList());
    }
    return multiRecipes;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MaterialMeltingRecipe> {
    @Override
    public MaterialMeltingRecipe read(ResourceLocation id, JsonObject json) {
      IMaterialItem item = RecipeUtil.deserializeMaterialItem(JSONUtils.getString(json, "item"), "item");
      int amount = JSONUtils.getInt(json, "amount");
      return new MaterialMeltingRecipe(id, item, amount);
    }

    @Nullable
    @Override
    public MaterialMeltingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      try {
        IMaterialItem item = RecipeUtil.readItem(buffer, IMaterialItem.class);
        int amount = buffer.readInt();
        return new MaterialMeltingRecipe(id, item, amount);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, MaterialMeltingRecipe recipe) {
      try {
        RecipeUtil.writeItem(buffer, recipe.item);
        buffer.writeInt(recipe.amount);
      } catch(Exception e) {
        TConstruct.log.error("Error reading material melting recipe from packet.", e);
        throw e;
      }
    }
  }
}
