package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.casting.potion.ModifierPotionCastingRecipe;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Arrays;
import java.util.List;

/**
 * Casting recipe clearing the potion from a tool.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.recipe.casting.potion}
 */
public class TipClearingCastingRecipe extends ModifierPotionCastingRecipe {
  public static final RecordLoadable<TipClearingCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP,
    TOOL_FIELD, FLUID_FIELD, COOLING_TIME_FIELD, MODIFIER_FIELD, TipClearingCastingRecipe::new);

  public TipClearingCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient tool, FluidIngredient fluid, int coolingTime, ModifierId modifier) {
    super(serializer, id, group, tool, fluid, Items.AIR, coolingTime, modifier);
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    // must have the modifier, and the potion set
    return super.matches(inv, level) && !ModifierUtil.getPersistentString(inv.getStack(), modifier).isEmpty();
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    ItemStack result = inv.getStack().copy();
    ToolStack.from(result).getPersistentData().remove(modifier);
    return result;
  }


  /* JEI */
  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<FluidStack> fluids = fluid.getFluids();
      List<String> potions = getPotionIds();

      // create a recipe per tool
      displayRecipes = Arrays.stream(bottle.getItems()).map(stack -> {
        // start with just the tool with the modifier
        ItemStack withModifier = addModifier(stack);
        return DisplayCastingRecipe.from(this)
          .casts(addPotion(withModifier, potions)).consumed()
          .result(withModifier).unlinkOutput()
          .fluids(fluids).coolingTime(coolingTime).build();
      }).toList();
    }
    return displayRecipes;
  }
}
