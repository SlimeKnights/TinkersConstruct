package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;

import javax.annotation.Nullable;

public class ModifierIngredientHelper implements IIngredientHelper<ModifierEntry> {
  @Override
  public IIngredientType<ModifierEntry> getIngredientType() {
    return TConstructJEIConstants.MODIFIER_TYPE;
  }

  @SuppressWarnings("removal")
  @Nullable
  @Override
  public ModifierEntry getMatch(Iterable<ModifierEntry> iterable, ModifierEntry check, UidContext context) {
    for (ModifierEntry entry : iterable) {
      if (entry.matches(check.getId())) {
        return entry;
      }
    }
    return null;
  }

  @Override
  public String getDisplayName(ModifierEntry entry) {
    return entry.getModifier().getDisplayName(entry.getLevel()).getString();
  }

  @Override
  public String getUniqueId(ModifierEntry entry, UidContext context) {
    return entry.getId().toString();
  }

  @SuppressWarnings("removal")
  @Deprecated
  @Override
  public String getModId(ModifierEntry entry) {
    return entry.getId().getNamespace();
  }

  @SuppressWarnings("removal")
  @Deprecated
  @Override
  public String getResourceId(ModifierEntry entry) {
    return entry.getId().getPath();
  }

  @Override
  public ResourceLocation getResourceLocation(ModifierEntry entry) {
    return entry.getId();
  }
  @Override
  public ModifierEntry copyIngredient(ModifierEntry entry) {
    return entry;
  }

  @Override
  public String getErrorInfo(@Nullable ModifierEntry entry) {
    if (entry == null) {
      return "null";
    }
    return entry.getId().toString();
  }
}
