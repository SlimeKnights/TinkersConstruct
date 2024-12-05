package slimeknights.tconstruct.library.recipe.modifiers;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

/**
 * Shared logic for main types of salvage recipes
 */
public class ModifierSalvage implements ICustomOutputRecipe<Container> {
  public static final RecordLoadable<ModifierSalvage> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("tools", r -> r.toolIngredient),
    IntLoadable.FROM_ONE.defaultField("max_tool_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, r -> r.maxToolSize), // TODO 1.20: max tool size is unused, remove it
    ModifierId.PARSER.requiredField("modifier", r -> r.modifier),
    ModifierEntry.VALID_LEVEL.defaultField("level", r -> r.level),
    SlotCount.LOADABLE.nullableField("slots", r -> r.slots), // TODO 1.20: make non-nullable
    // TODO: should this have check_trait_level?
    ModifierSalvage::new);

  @Getter
  protected final ResourceLocation id;
  /** Ingredient determining tools matched by this */
  protected final Ingredient toolIngredient;
  /** Max size of the tool for this modifier. If the tool size is smaller, the salvage bonus will be reduced */
  @Getter
  protected final int maxToolSize;
  /** Modifier represented by this recipe */
  @Getter
  protected final ModifierId modifier;
  /** Level for this to be applicable */
  protected final IntRange level;
  /** Slots restored by this recipe, if null no slots are restored */
  @Nullable
  protected final SlotCount slots;

  public ModifierSalvage(ResourceLocation id, Ingredient toolIngredient, int maxToolSize, ModifierId modifier, IntRange level, @Nullable SlotCount slots) {
    this.id = id;
    this.toolIngredient = toolIngredient;
    this.maxToolSize = maxToolSize;
    this.modifier = modifier;
    this.level = level;
    this.slots = slots;
    if (slots != null) {
      ModifierRecipeLookup.addSalvage(this);
    }
  }

  /**
   * Checks if the given tool stack and level are applicable for this salvage
   * @param stack         Tool item stack
   * @param tool          Tool stack instance, for potential extensions
   * @param originalLevel Level to check
   * @return True if this salvage is applicable
   */
  @SuppressWarnings("unused")
  public boolean matches(ItemStack stack, IToolStackView tool, int originalLevel) {
    return this.level.test(originalLevel) && toolIngredient.test(stack);
  }

  /**
   * Updates the tool data in light of removing this modifier
   * @param tool  Tool instance
   */
  public void updateTool(IToolStackView tool) {
    if (slots != null) {
      tool.getPersistentData().addSlots(slots.type(), slots.count());
    }
  }

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.DATA.get();
  }

  /** @deprecated Use {@link #matches(ItemStack, IToolStackView, int)} */
  @Deprecated
  @Override
  public boolean matches(Container inv, Level level) {
    return false;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSalvageSerializer.get();
  }
}
