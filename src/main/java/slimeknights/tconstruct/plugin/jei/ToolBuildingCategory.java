package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolBuildingCategory implements IRecipeCategory<ToolBuildingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "tinkering.tool_building");
  @Getter
  private final IDrawable icon;
  @Getter
  private final IDrawable background;
  private final IDrawable anvil, slot;
  private static final int WIDTH = 134;
  private static final int HEIGHT = 66;
  private static final int X_OFFSET = -6;
  private static final int Y_OFFSET = -15;
  private static final int SLOT_SIZE = 18;
  private static final int ITEM_SIZE = 16;

  public ToolBuildingCategory(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableItemStack(TinkerTools.pickaxe.get().getRenderTool());
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 122, 77, WIDTH, HEIGHT);
    this.slot = guiHelper.createDrawable(BACKGROUND_LOC, 24, 14, SLOT_SIZE, SLOT_SIZE);
    this.anvil = guiHelper.createDrawable(BACKGROUND_LOC, 128, 61, ITEM_SIZE, ITEM_SIZE);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ToolBuildingRecipe recipe, IFocusGroup focuses) {
    List<IToolPart> parts = recipe.getToolParts();
    List<List<ItemStack>> extras = recipe.getExtraRequirements().stream().map(ingredient -> Arrays.asList(ingredient.getItems())).toList();
    List<LayoutSlot> layoutSlots = recipe.getLayoutSlots();

    if (parts.size() + extras.size() > layoutSlots.size()) {
      TConstruct.LOG.error(String.format("Tool part count is greater than layout slot count for %s!", recipe.getId()));
      int additionalSlots = 0;
      layoutSlots = new ArrayList<>(layoutSlots);
      while (parts.size() + extras.size() > layoutSlots.size()) {
        layoutSlots.add(new LayoutSlot(null, null, additionalSlots * SLOT_SIZE, 0, null));
        additionalSlots++;
      }
    }

    if (parts.size() + extras.size() < layoutSlots.size()) {
      TConstruct.LOG.error(String.format("Tool part count is less than layout slot count for %s!", recipe.getId()));
      extras = new ArrayList<>(extras);
      while (parts.size() + extras.size() < layoutSlots.size()) {
        extras.add(List.of(Items.BARRIER.getDefaultInstance()));
      }
    }

    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getAllToolParts());

    for (int i = 0; i < layoutSlots.size(); i++) {
      IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, layoutSlots.get(i).getX() + X_OFFSET, layoutSlots.get(i).getY() + Y_OFFSET);
      if (i < parts.size()) {
        slotBuilder.addItemStack(ToolBuildHandler.getDisplayPart(parts.get(i), i));
      } else {
        slotBuilder.addItemStacks(extras.get(i - parts.size()));
      }
    }

    ItemStack outputStack = recipe.getOutput() instanceof IModifiableDisplay modifiable ? modifiable.getRenderTool() : recipe.getOutput().asItem().getDefaultInstance();
      builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 26, 23)
        .addItemStack(outputStack);
  }

  @Override
  public void draw(ToolBuildingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
    if (recipe.requiresAnvil()) {
      this.anvil.draw(stack, 76, 44);
    }

    for (LayoutSlot layoutSlot : recipe.getLayoutSlots()) {
      // need to offset by 1 because the inventory slot icons are 18x18
      this.slot.draw(stack, layoutSlot.getX() + X_OFFSET - 1, layoutSlot.getY() + Y_OFFSET - 1);
    }
  }

  @Override
  public List<Component> getTooltipStrings(ToolBuildingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    return recipe.requiresAnvil() && GuiUtil.isHovered((int) mouseX, (int) mouseY, 76, 44, ITEM_SIZE, ITEM_SIZE) ?
      List.of(TConstruct.makeTranslation("jei", "tinkering.tool_building.anvil")) :
      List.of();
  }

  @Nonnull
  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Nonnull
  @Override
  public RecipeType<ToolBuildingRecipe> getRecipeType() {
    return TConstructJEIConstants.TOOL_BUILDING;
  }
}
