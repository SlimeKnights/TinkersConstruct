package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.tinkerstation.ToolRecipes;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


// IDK where to put this so im just leaving it the main jei directory
public class ToolRecipesCategory implements IRecipeCategory<ToolRecipes> {
  private static  final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tool_recipes.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "tinkering.tool_recipes");
  @Getter
  private final IDrawable icon;
  @Getter
  private final IDrawable background;
  private final IDrawable ANVIL, SLOT;
  private final int WIDTH = 128;
  private final int HEIGHT = 66;
  private final int ITEM_SIZE = 16;

  public ToolRecipesCategory(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableItemStack(TinkerTools.pickaxe.get().getRenderTool());
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, WIDTH, HEIGHT);
    this.SLOT = guiHelper.createDrawable(BACKGROUND_LOC, 128, 0, ITEM_SIZE + 2, ITEM_SIZE + 2);
    this.ANVIL = guiHelper.createDrawable(BACKGROUND_LOC, 146, 0, ITEM_SIZE, ITEM_SIZE);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ToolRecipes recipe, IFocusGroup focuses) {
    recipe.getInputsParts().forEach(parts -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(parts));

    List<LayoutSlot> slots = recipe.getSlots();
    List<ItemStack> items = recipe.getDisplayParts();

    assert items.size() == slots.size();

    xy offsets = getOffsets(recipe);
    for (int i = 0; i < items.size(); i++) {
      builder.addSlot(RecipeIngredientRole.INPUT, slots.get(i).getX() + offsets.x, slots.get(i).getY() + offsets.y).addItemStack(items.get(i));
    }

    builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 23, 23).addItemStack(recipe.getOutputTool());
  }

  @Override
  public void draw(ToolRecipes recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
    if (recipe.isBroadTool()) {
      this.ANVIL.draw(stack, 73, 42);
    }

    xy offsets = getOffsets(recipe);
    for (LayoutSlot slot : recipe.getSlots()) {
      // need to offset by 1 because the inventory slot icons are 18x18
      this.SLOT.draw(stack, slot.getX() + offsets.x - 1, slot.getY() + offsets.y - 1);
    }
  }

  @Override
  public List<Component> getTooltipStrings(ToolRecipes recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    return recipe.isBroadTool() && GuiUtil.isHovered((int) mouseX, (int) mouseY, 73, 42, ITEM_SIZE, ITEM_SIZE) ?
      Collections.singletonList(MutableComponent.create(new LiteralContents("Broad tools require the Tinker's Anvil to be created!"))) :
      Collections.emptyList();
  }

  private xy getOffsets(ToolRecipes recipe) {
    List<LayoutSlot> slots = recipe.getSlots();

    int minX, maxX, minY, maxY;
    minX = slots.get(0).getX();
    maxX = slots.get(0).getX();
    minY = slots.get(0).getY();
    maxY = slots.get(0).getY();

    for (int i = 1; i < slots.size(); i++) {
      minX = Math.min(slots.get(i).getX(), minX);
      maxX = Math.max(slots.get(i).getX(), maxX);
      minY = Math.min(slots.get(i).getY(), minY);
      maxY = Math.max(slots.get(i).getY(), maxY);
    }

    // centers slots vertically
    int yOffset = (HEIGHT - (ITEM_SIZE + maxY - minY)) / 2 - minY;
    // centers slots horizontally within square
    int xOffset = (HEIGHT - (ITEM_SIZE + maxX - minX)) / 2 - minX;

    return new xy(xOffset, yOffset);
  }

  private record xy(int x, int y) {}

  @Nonnull
  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Nonnull
  @Override
  public RecipeType<ToolRecipes> getRecipeType() {
    return TConstructJEIConstants.TOOL_RECIPES;
  }

}
