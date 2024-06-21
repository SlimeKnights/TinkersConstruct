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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ToolBuildingCategory implements IRecipeCategory<ToolBuildingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "tinkering.tool_building");
  @Getter
  private final IDrawable icon;
  @Getter
  private final IDrawable background;
  private final IDrawable anvil, slot;
  private static final int WIDTH = 128;
  private static final int HEIGHT = 66;
  private static final int SLOT_SIZE = 18;
  private static final int ITEM_SIZE = 16;

  public ToolBuildingCategory(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableItemStack(TinkerTools.pickaxe.get().getRenderTool());
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 128, 77, WIDTH, HEIGHT);
    this.slot = guiHelper.createDrawable(BACKGROUND_LOC, 24, 14, SLOT_SIZE, SLOT_SIZE);
    this.anvil = guiHelper.createDrawable(BACKGROUND_LOC, 128, 61, ITEM_SIZE, ITEM_SIZE);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ToolBuildingRecipe recipe, IFocusGroup focuses) {
    List<IToolPart> parts = recipe.getToolParts();
    List<ItemStack> extraItems = recipe.getIngredients().stream().flatMap(ingredient -> Arrays.stream(ingredient.getItems())).toList();
    List<LayoutSlot> layoutSlots = recipe.getLayoutSlots();

    // every part and its material variants
    List<ItemStack> allInputParts = parts.stream()
        .flatMap(part -> MaterialRegistry.getInstance().getVisibleMaterials().stream()
          .filter(part::canUseMaterial)
          .flatMap(mat -> Stream.of(part.withMaterial(mat.getIdentifier()))))
        .toList();

    if (parts.size() + extraItems.size() != layoutSlots.size()) {
      TConstruct.LOG.error(String.format("Tool part count and layout slot count for %s do not match!", recipe.getId()));
      return;
    }

    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(allInputParts);
    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(extraItems);

    Vec2 offsets = getOffsets(layoutSlots);
    for (int i = 0; i < parts.size(); i++) {
      builder.addSlot(RecipeIngredientRole.INPUT, (int) (layoutSlots.get(i).getX() + offsets.x), (int) (layoutSlots.get(i).getY() + offsets.y))
             .addItemStack(getDisplayPart(parts.get(i), i));
    }

    if (recipe.getOutput() instanceof IModifiableDisplay modifiable) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 23, 23)
        .addItemStack(modifiable.getRenderTool());
    }
  }

  @Override
  public void draw(ToolBuildingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
    if (recipe.isBroadTool()) {
      this.anvil.draw(stack, 73, 42);
    }

    List<LayoutSlot> layoutSlots = recipe.getLayoutSlots();
    Vec2 offsets = getOffsets(layoutSlots);

    for (LayoutSlot slot : layoutSlots) {
      // need to offset by 1 because the inventory slot icons are 18x18
      this.slot.draw(stack, (int) (slot.getX() + offsets.x - 1), (int) (slot.getY() + offsets.y - 1));
    }
  }

  @Override
  public List<Component> getTooltipStrings(ToolBuildingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    return recipe.isBroadTool() && GuiUtil.isHovered((int) mouseX, (int) mouseY, 73, 42, ITEM_SIZE, ITEM_SIZE) ?
      List.of(TConstruct.makeTranslation("jei", "tinkering.tool_building.anvil")) :
      List.of();
  }

  // use display parts to be more consistent
  private ItemStack getDisplayPart(IToolPart toolPart, int i) {
      ItemStack item = toolPart.withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i));
      item.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
      return item;
  }

  private Vec2 getOffsets(List<LayoutSlot> slots) {
    int minX = slots.get(0).getX();
    int maxX = slots.get(0).getX();
    int minY = slots.get(0).getY();
    int maxY = slots.get(0).getY();

    for (int i = 1; i < slots.size(); i++) {
      minX = Math.min(slots.get(i).getX(), minX);
      maxX = Math.max(slots.get(i).getX(), maxX);
      minY = Math.min(slots.get(i).getY(), minY);
      maxY = Math.max(slots.get(i).getY(), maxY);
    }

    // centers slots vertically
    float yOffset = (HEIGHT - ITEM_SIZE - maxY + minY) / 2f - minY;
    // centers slots horizontally within square
    float xOffset = (HEIGHT - ITEM_SIZE - maxX + minX) / 2f - minX;

    return new Vec2(xOffset, yOffset);
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
