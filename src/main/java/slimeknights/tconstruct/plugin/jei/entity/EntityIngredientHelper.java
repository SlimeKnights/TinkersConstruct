package slimeknights.tconstruct.plugin.jei.entity;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Handler for working with entity types as ingredients */
@SuppressWarnings("rawtypes")
public class EntityIngredientHelper implements IIngredientHelper<EntityType> {

  @Override
  public IIngredientType<EntityType> getIngredientType() {
    return TConstructJEIConstants.ENTITY_TYPE;
  }

  @Override
  public String getDisplayName(EntityType type) {
    return type.getDescription().getString();
  }

  @Override
  public String getUniqueId(EntityType type, UidContext context) {
    return getResourceLocation(type).toString();
  }

  @Override
  public ResourceLocation getResourceLocation(EntityType type) {
    return Registry.ENTITY_TYPE.getKey(type);
  }

  @Override
  public EntityType copyIngredient(EntityType type) {
    return type;
  }

  @Override
  public String getErrorInfo(@Nullable EntityType type) {
    if (type == null) {
      return "null";
    }
    return getResourceLocation(type).toString();
  }

  /** Applies the item focuses to the list of entities */
  public static List<EntityType> applyFocus(RecipeIngredientRole role, List<EntityType> displayInputs, IFocusGroup focuses) {
    return focuses.getFocuses(VanillaTypes.ITEM_STACK)
                  .filter(focus -> focus.getRole() == role)
                  .map(focus -> focus.getTypedValue().getIngredient().getItem())
                  .filter(item -> item instanceof SpawnEggItem)
                  .<EntityType>map(item -> ((SpawnEggItem) item).getType(null))
                  .filter(displayInputs::contains)
                  .map(Collections::singletonList)
                  .findFirst()
                  .orElse(displayInputs);
  }
}
