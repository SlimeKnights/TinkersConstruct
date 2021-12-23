package slimeknights.tconstruct.library.recipe.modifiers.spilling;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Recipe to determine effects for a fluid in the spilling modifier
 */
@RequiredArgsConstructor
public class SpillingRecipe implements ICustomOutputRecipe<IEmptyInventory> {
  @Getter
  private final ResourceLocation id;
  private final FluidIngredient ingredient;
  private final List<ISpillingEffect> effects;

  /**
   * Checks if the recipe matches the given fluid. Does not consider fluid amount
   * @param fluid  Fluid to test
   * @return  True if this recipe handles the given fluid
   */
  public boolean matches(Fluid fluid) {
    return ingredient.test(fluid);
  }

  /**
   * Applies any effects for the given recipe
   * @param fluid    Fluid used to perform the recipe, safe to modify
   * @param level    Modifier level
   * @param context  Modifier attack context
   * @return  Fluid stack after applying this recipe
   */
  public FluidStack applyEffects(FluidStack fluid, int level, ToolAttackContext context) {
    int needed = ingredient.getAmount(fluid.getFluid());
    int maxFluid = level * needed;
    float scale = level;
    if (fluid.getAmount() < maxFluid) {
      scale = fluid.getAmount() / (float)maxFluid;
    }
    for (ISpillingEffect effect : effects) {
      effect.applyEffects(fluid, scale, context);
    }
    fluid.shrink(maxFluid);
    return fluid;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.spillingSerializer.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.SPILLING;
  }

  /** @deprecated use {@link #matches(Fluid)} */
  @Deprecated
  @Override
  public boolean matches(IEmptyInventory inv, World worldIn) {
    return false;
  }

  public static class Serializer extends LoggingRecipeSerializer<SpillingRecipe> {
    @Override
    public SpillingRecipe read(ResourceLocation id, JsonObject json) {
      FluidIngredient ingredient = FluidIngredient.deserialize(json, "fluid");
      List<ISpillingEffect> effects = JsonHelper.parseList(json, "effects", SpillingRecipeLookup::deserializeEffect);
      return new SpillingRecipe(id, ingredient, effects);
    }

    @Nullable
    @Override
    protected SpillingRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      FluidIngredient ingredient = FluidIngredient.read(buffer);
      ImmutableList.Builder<ISpillingEffect> effects = ImmutableList.builder();
      int max = buffer.readVarInt();
      for (int i = 0; i < max; i++) {
        effects.add(SpillingRecipeLookup.readEffect(buffer));
      }
      return new SpillingRecipe(id, ingredient, effects.build());
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, SpillingRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeVarInt(recipe.effects.size());
      for (ISpillingEffect effect : recipe.effects) {
        SpillingRecipeLookup.writeEffect(effect, buffer);
      }
    }
  }
}
