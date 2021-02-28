package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;

public class ModifierIngredientHelper implements IIngredientHelper<ModifierEntry> {

  @Nullable
  @Override
  public ModifierEntry getMatch(Iterable<ModifierEntry> iterable, ModifierEntry check) {
    for (ModifierEntry entry : iterable) {
      if (entry.getModifier() == check.getModifier()) {
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
  public String getUniqueId(ModifierEntry entry) {
    return entry.getModifier().getId().toString();
  }

  @Override
  public String getModId(ModifierEntry entry) {
    return entry.getModifier().getId().getNamespace();
  }

  @Override
  public String getResourceId(ModifierEntry entry) {
    return entry.getModifier().getId().getPath();
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
    ResourceLocation id = entry.getModifier().getRegistryName();
    return id == null ? "unregistered" : id.toString();
  }
}
