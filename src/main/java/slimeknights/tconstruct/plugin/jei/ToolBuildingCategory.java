package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.plugin.jei.util.RecipeTooltipWidget;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe.SLOT_SIZE;
import static slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe.X_OFFSET;
import static slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe.Y_OFFSET;

public class ToolBuildingCategory extends AbstractRecipeCategory<ToolBuildingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "tinkering.tool_building");
  private static final Component ANVIL = TConstruct.makeTranslation("jei", "tinkering.tool_building.anvil");
  private final IDrawable anvil, slotBg, slotBorder;
  private final IDrawable itemCover;
  private static final int WIDTH = 134;
  private static final int HEIGHT = 66;
  private static final int ITEM_SIZE = 16;

  public ToolBuildingCategory(IGuiHelper guiHelper) {
    super(TConstructJEIConstants.TOOL_BUILDING, TITLE, guiHelper.createDrawableItemStack(TinkerTools.pickaxe.get().getRenderTool()), WIDTH, HEIGHT);
    this.slotBg = guiHelper.createDrawable(BACKGROUND_LOC, 144, 59, SLOT_SIZE, SLOT_SIZE);
    this.slotBorder = guiHelper.createDrawable(BACKGROUND_LOC, 162, 59, SLOT_SIZE, SLOT_SIZE);
    this.anvil = guiHelper.createDrawable(BACKGROUND_LOC, 128, 61, ITEM_SIZE, ITEM_SIZE);
    this.itemCover = guiHelper.createDrawable(BACKGROUND_LOC, 122, 77, 70, 60);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, ToolBuildingRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrow().setPosition(74, 22);
    if (recipe.requiresAnvil()) {
      builder.addWidget(new RecipeTooltipWidget(anvil, 76, 44, ANVIL));
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ToolBuildingRecipe recipe, IFocusGroup focuses) {
    List<List<ItemStack>> partsAndExtras = Stream.concat(recipe.getAllToolParts().stream(),
      recipe.getExtraRequirements().stream().map(ingredient -> Arrays.asList(ingredient.getItems()))).toList();
    List<LayoutSlot> layoutSlots = recipe.getLayoutSlots();

    int missingSlots = partsAndExtras.size() - layoutSlots.size();

    if (missingSlots < 0) {
      partsAndExtras = new ArrayList<>(partsAndExtras);
      for (int additionalItem = 0; additionalItem > missingSlots; additionalItem--){
        // just add nothing to fill the empty slots
        partsAndExtras.add(List.of(ItemStack.EMPTY));
      }
    }

    IRecipeSlotBuilder firstSlot = null;
    for (int i = 0; i < layoutSlots.size(); i++) {
      IRecipeSlotBuilder slot = builder.addInputSlot(layoutSlots.get(i).getX() + X_OFFSET, layoutSlots.get(i).getY() + Y_OFFSET)
             .addItemStacks(partsAndExtras.get(i));
      if (i == 0) {
        firstSlot = slot;
      }
    }

    // create a focus link between result and first slot if same size
    List<ItemStack> result = recipe.getDisplayOutput();
    IRecipeSlotBuilder resultSlot = builder.addOutputSlot(WIDTH - 26, 23)
      .addItemStacks(result).setOutputSlotBackground();
    if (result.size() > 1 && partsAndExtras.get(0).size() == result.size()) {
      builder.createFocusLink(resultSlot, firstSlot);
    }

    // add hidden materials as a hidden input for recipe lookup
    List<ItemStack> hiddenInputs = recipe.getHiddenInputs();
    if (!hiddenInputs.isEmpty()) {
      builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(hiddenInputs);
    }
  }

  @Override
  public void draw(ToolBuildingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
    // first, draw the item background
    ItemStack outputStack = recipe.getOutput() instanceof IModifiableDisplay modifiable ? modifiable.getRenderTool() : recipe.getOutput().asItem().getDefaultInstance();
    PoseStack renderPose = graphics.pose();
    renderPose.pushPose();
    renderPose.translate(5, 6.5, 0);
    renderPose.scale(3.7f, 3.7f, 1.0f);
    graphics.renderItem(outputStack, 0, 0);
    renderPose.popPose();

    // next, overlay the item with transparent grey, makes it appear transparent
    RenderSystem.enableBlend();
    RenderSystem.disableDepthTest();
    RenderSystem.setShaderColor(1, 1, 1, 0.82f);
    itemCover.draw(graphics, 5, 6);

    // next, draw slot backgrounds very transparent
    RenderSystem.setShaderColor(1, 1, 1, 0.28f);
    for (LayoutSlot layoutSlot : recipe.getLayoutSlots()) {
      // need to offset by 1 because the inventory slot icons are 18x18
      this.slotBg.draw(graphics, layoutSlot.getX() + X_OFFSET - 1, layoutSlot.getY() + Y_OFFSET - 1);
    }
    // finally, draw slot borders opaque
    RenderSystem.setShaderColor(1, 1, 1, 1);
    for (LayoutSlot layoutSlot : recipe.getLayoutSlots()) {
      // need to offset by 1 because the inventory slot icons are 18x18
      this.slotBorder.draw(graphics, layoutSlot.getX() + X_OFFSET - 1, layoutSlot.getY() + Y_OFFSET - 1);
    }
    RenderSystem.disableBlend();
    RenderSystem.enableDepthTest();
  }

  @Override
  public ResourceLocation getRegistryName(ToolBuildingRecipe recipe) {
    return recipe.getId();
  }
}
