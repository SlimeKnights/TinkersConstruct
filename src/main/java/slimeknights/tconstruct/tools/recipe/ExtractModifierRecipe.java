package slimeknights.tconstruct.tools.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer.Mutable;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.List;

/** Recipe that removes a modifier, placing it on a crystal for reapplication */
public class ExtractModifierRecipe extends ModifierRemovalRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "extract_modifier.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "extract_modifier.description");
  private static final Component NO_MODIFIERS = TConstruct.makeTranslation("recipe", "extract_modifier.no_modifiers");

  /** @deprecated use {#link #ExtractModifierRecipe(ResourceLocation, SizedIngredient, List, List, IJsonPredicate} */
  @Deprecated
  public ExtractModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id, inputs, leftovers, modifierPredicate);
  }

  public ExtractModifierRecipe(ResourceLocation id, SizedIngredient toolRequirements, List<SizedIngredient> inputs, List<ItemStack> leftovers, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id,toolRequirements, inputs, leftovers, modifierPredicate);
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null) {
      IToolStackView tool = inv.getTinkerable();
      if (filter(tool, tool.getModifierList()).isEmpty()) {
        return NO_MODIFIERS;
      }
    }
    return DESCRIPTION;
  }

  @Override
  protected List<ModifierEntry> filter(@Nullable IToolStackView tool, List<ModifierEntry> modifiers) {
    if (tool != null) {
      // filter out incremental modifiers at level 1 with only a partial level to prevent exploiting the recipe to get a lot of crystals
      return modifiers.stream().filter(entryPredicate).filter(entry -> {
        if (entry.getLevel() == 1) {
          int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(entry.getId());
          return neededPerLevel <= 0 || IncrementalModifier.getAmount(tool, entry.getId()) >= neededPerLevel;
        }
        return true;
      }).toList();
    }
    return super.filter(tool, modifiers);
  }

  @Override
  public void updateInputs(IToolStackView result, Mutable inv, ModifierEntry selected, boolean isServer) {
    super.updateInputs(result, inv, isServer);
    if (isServer) {
      // just 1 crystal in this version as just 1 level was removed
      inv.giveItem(ModifierCrystalItem.withModifier(selected.getId()));
    }
  }

  @Override
  public boolean isModifierOutput() {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.extractModifierSerializer.get();
  }
}
