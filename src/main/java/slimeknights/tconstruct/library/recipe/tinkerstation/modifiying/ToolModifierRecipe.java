package slimeknights.tconstruct.library.recipe.tinkerstation.modifiying;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierUtils;
import slimeknights.tconstruct.library.recipe.ValidationResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tables.TinkerTables;

public class ToolModifierRecipe implements ITinkerStationRecipe {

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  protected final Ingredient ingredient;
  @Getter
  protected final int cost;
  @Getter
  protected final ModifierId modifierId;
  private final LazyValue<IModifier> modifier;

  public ToolModifierRecipe(ResourceLocation id, String group, Ingredient ingredient, int modifierCost, ModifierId modifierId) {
    this.id = id;
    this.group = group;
    this.ingredient = ingredient;
    this.cost = modifierCost;
    this.modifierId = modifierId;
    this.modifier = new LazyValue<>(() -> ModifierUtils.getModifier(modifierId));
  }

  /**
   * Returns a material instance for this recipe
   * @return Material for the recipe
   */
  public IModifier getModifier() {
    return this.modifier.getValue();
  }

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    // must be modifiable
    ItemStack tinkerable = inv.getTinkerableStack();

    if (tinkerable.isEmpty() || !(tinkerable.getItem() instanceof IModifiable)) {
      return false;
    }

    boolean passed = false;

    for (int i = 0; i < inv.getInputCount(); i++) {
      // skip empty slots
      ItemStack stack = inv.getInput(i);

      if (stack.isEmpty()) {
        continue;
      }

      if (!this.ingredient.test(stack)) {
        passed = false;
      }
      else {
        passed = true;
      }
    }

    return passed;
  }

  @Override
  public ValidationResult validate(ITinkerStationInventory inv) {
    ItemStack repairable = inv.getTinkerableStack();

    if(ToolData.from(repairable).getModifiers().hasModifier(this.modifierId)) {
      return ValidationResult.failure("modifier exists on tool");
    }

    return ValidationResult.SUCCESS;
  }

  @Override
  public ItemStack getCraftingResult(ITinkerStationInventory inv) {
    ItemStack tinkerable = inv.getTinkerableStack();

    if (!(tinkerable.getItem() instanceof IModifiable)) {
      return ItemStack.EMPTY;
    }

    ItemStack tinkerableCopy = tinkerable.copy();

    if (this.getModifier() != null) {
      ToolData toolData = ToolData.from(tinkerableCopy);

      toolData.createNewDataWithFreeModifiers(toolData.getStats().freeModifiers - this.cost).updateStack(tinkerableCopy);

      return this.getModifier().apply(tinkerableCopy);
    }

    return tinkerable;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.toolModifierRecipeSerializer.get();
  }

  /** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }
}
