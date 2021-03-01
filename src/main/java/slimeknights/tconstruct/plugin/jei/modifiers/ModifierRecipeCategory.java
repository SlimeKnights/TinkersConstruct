package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Collections;
import java.util.List;

public class ModifierRecipeCategory implements IRecipeCategory<IDisplayModifierRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "modifiers.title");

  private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawable requirements;
  private final IDrawable[] slotIcons;
  public ModifierRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerTables.tinkerStation));
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.modifiers;
  }

  @Override
  public Class<? extends IDisplayModifierRecipe> getRecipeClass() {
    return IDisplayModifierRecipe.class;
  }

  @Override
  public void setIngredients(IDisplayModifierRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.ITEM, recipe.getDisplayInputs());
    ingredients.setOutputLists(VanillaTypes.ITEM, recipe.getDisplayOutput());
    ingredients.setOutput(JEIPlugin.MODIFIER_TYPE, recipe.getDisplayResult());
  }

  /** Draws a single slot icon */
  private void drawSlot(MatrixStack matrices, List<List<ItemStack>> inputs, int slot, int x, int y) {
    if (slot >= inputs.size() || inputs.get(slot).isEmpty()) {
      slotIcons[slot].draw(matrices, x + 1, y + 1);
    }
  }

  @Override
  public void draw(IDisplayModifierRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    List<List<ItemStack>> inputs = recipe.getDisplayInputs();
    drawSlot(matrices, inputs, 1,  2, 32);
    drawSlot(matrices, inputs, 2, 24, 14);
    drawSlot(matrices, inputs, 3, 46, 32);
    drawSlot(matrices, inputs, 4, 42, 57);
    drawSlot(matrices, inputs, 5,  6, 57);

    // draw requirements icon if needed
    if (recipe.hasRequirements()) {
      requirements.draw(matrices, 110, 58);
    }
  }

  @Override
  public List<ITextComponent> getTooltipStrings(IDisplayModifierRecipe recipe, double mouseX, double mouseY) {
    if (recipe.hasRequirements() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 110, 58, 16, 16)) {
      return Collections.singletonList(new TranslationTextComponent(recipe.getRequirementsError()));
    }
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayout layout, IDisplayModifierRecipe recipe, IIngredients ingredients) {
    IGuiIngredientGroup<ModifierEntry> modifiers = layout.getIngredientsGroup(JEIPlugin.MODIFIER_TYPE);

    // set items for display
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 24, 37);
    items.init(1, true,  2, 32);
    items.init(2, true, 24, 14);
    items.init(3, true, 46, 32);
    items.init(4, true, 42, 57);
    items.init(5, true,  6, 57);
    items.init(6, false, 104, 33);
    items.set(ingredients);

    // set modifiers for display
    modifiers.init(7, false, modifierRenderer, 2, 2, 124, 10, 0, 0);
    modifiers.set(ingredients);
  }
}
