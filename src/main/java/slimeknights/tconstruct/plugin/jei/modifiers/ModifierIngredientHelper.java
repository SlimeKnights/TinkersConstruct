package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.TinkerTags.Modifiers;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

public class ModifierIngredientHelper implements IIngredientHelper<ModifierEntry> {
  @Override
  public IIngredientType<ModifierEntry> getIngredientType() {
    return TConstructJEIConstants.MODIFIER_TYPE;
  }

  @Override
  public String getDisplayName(ModifierEntry entry) {
    return entry.getDisplayName().getString();
  }

  @Override
  public String getUniqueId(ModifierEntry entry, UidContext context) {
    return entry.getId().toString();
  }

  @Override
  public ResourceLocation getResourceLocation(ModifierEntry entry) {
    return entry.getId();
  }

  @Override
  public ModifierEntry copyIngredient(ModifierEntry entry) {
    // immutable, so we never need to copy
    return entry;
  }

  @Override
  public String getErrorInfo(@Nullable ModifierEntry entry) {
    if (entry == null) {
      return "null";
    }
    return entry.getId().toString();
  }

  @Override
  public ItemStack getCheatItemStack(ModifierEntry ingredient) {
    if (!ModifierManager.isInTag(ingredient.getId(), TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST)) {
      return ModifierCrystalItem.withModifier(ingredient.getId());
    }
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isValidIngredient(ModifierEntry entry) {
    return entry.isBound();
  }

  /* Levels */

  @Override
  public long getAmount(ModifierEntry entry) {
    return entry.getLevel();
  }

  @Override
  public ModifierEntry copyWithAmount(ModifierEntry entry, long amount) {
    return entry.withLevel((int)amount);
  }

  @Override
  public ModifierEntry normalizeIngredient(ModifierEntry entry) {
    if (entry.getLevel() == 1) {
      return entry;
    }
    return entry.withLevel(1);
  }


  /* Tags */

  @Override
  public Stream<ResourceLocation> getTagStream(ModifierEntry entry) {
    return ModifierManager.getTagKeys(entry.getId()).map(TagKey::location);
  }

  @Override
  public boolean isHiddenFromRecipeViewersByTags(ModifierEntry entry) {
    return ModifierManager.isInTag(entry.getId(), Modifiers.HIDDEN_FROM_RECIPE_VIEWERS);
  }

  @Override
  public Optional<TagKey<?>> getTagKeyEquivalent(Collection<ModifierEntry> entries) {
    List<Modifier> values = entries.stream().map(ModifierEntry::getModifier).toList();
    return ModifierManager.getAllTags()
                          .filter(entry -> entry.equals(values))
                          .<TagKey<?>>map(Entry::getKey).findFirst();
  }


  /* Other misc methods */

  @Override
  public Iterable<Integer> getColors(ModifierEntry ingredient) {
    return List.of(0xFF000000 | ingredient.getModifier().getColor());
  }
}
