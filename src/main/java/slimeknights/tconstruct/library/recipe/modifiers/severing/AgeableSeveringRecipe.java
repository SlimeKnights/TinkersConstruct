package slimeknights.tconstruct.library.recipe.modifiers.severing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class AgeableSeveringRecipe extends SeveringRecipe {
  /** Loader instance */
  public static final RecordLoadable<AgeableSeveringRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), ENTITY_FIELD,
    ItemOutput.Loadable.REQUIRED_STACK.requiredField("adult_result", r -> r.output),
    ItemOutput.Loadable.OPTIONAL_STACK.emptyField("child_result", r -> r.childOutput),
    AgeableSeveringRecipe::new);

  private final ItemOutput childOutput;
  public AgeableSeveringRecipe(ResourceLocation id, EntityIngredient ingredient, ItemOutput adultOutput, ItemOutput childOutput) {
    super(id, ingredient, adultOutput);
    this.childOutput = childOutput;
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof LivingEntity && ((LivingEntity) entity).isBaby()) {
      return childOutput.get().copy();
    }
    return getOutput().copy();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.ageableSeveringSerializer.get();
  }
}
