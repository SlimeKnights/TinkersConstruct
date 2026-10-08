package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.IDisplayMaterialRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.SimpleCache;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.melting.AbstractMeltingCategory;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.awt.Color;
import java.util.List;

/** Category for displaying generic recipes to create materials. */
public class MaterialCategory extends AbstractRecipeCategory<IDisplayMaterialRecipe> {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "materials.title");
  /** Tooltip for materials marked craftable */
  private static final Component CRAFTABLE = TConstruct.makeTranslation("jei", "materials.craftable");
  /** Tooltip for materials marked uncraftable */
  private static final Component UNCRAFTABLE = TConstruct.makeTranslation("jei", "materials.uncraftable");
  /** Tooltip for the leftover item stack */
  private static final Component LEFTOVER = TConstruct.makeTranslation("jei", "materials.leftover").withStyle(ChatFormatting.GRAY);
  /** Tooltip for the composite material icon */
  private static final Component COMPOSITE = TConstruct.makeTranslation("jei", "materials.composite").withStyle(ChatFormatting.GRAY);
  /** Key for the item value */
  private static final String KEY_VALUE = TConstruct.makeTranslationKey("jei", "materials.value");
  /** Tooltip for the item value */
  private static final Component VALUE_TOOLTIP = TConstruct.makeTranslation("jei", "materials.value.tooltip");
  /** Key for the fluid value */
  private static final String KEY_AMOUNT = TConstruct.makeTranslationKey("jei", "materials.amount");
  /** Tooltip for the fluid value */
  private static final Component AMOUNT_TOOLTIP = TConstruct.makeTranslation("jei", "materials.amount.tooltip");
  /** Texture for drawables */
  protected static final ResourceLocation BACKGROUND_LOC = AbstractMeltingCategory.BACKGROUND_LOC;
  /** Name of the fluid slot to find for the amount string */
  private static final String FLUID_SLOT = "fluid";

  /** Tooltip for the leftover slot */
  private static final IRecipeSlotRichTooltipCallback LEFTOVER_TOOLTIP = (slot, tooltip) -> tooltip.add(LEFTOVER);
  /** Tooltip for the input material */
  private static  final IRecipeSlotRichTooltipCallback COMPOSITE_TOOLTIP = (slot, tooltip) -> tooltip.add(COMPOSITE);

  /** Renderer for the material name */
  private final MaterialTitleIngredientRenderer materialTitleRenderer = new MaterialTitleIngredientRenderer(95, 10);

  private final IDrawableStatic fluidBar;
  private final IDrawableStatic itemWithFluid;
  private final IDrawableStatic partBuilder;
  private final IDrawableStatic castingTable;
  private final Font font;
  public MaterialCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.MATERIALS, TITLE, helper.createDrawableItemStack(TinkerToolParts.largePlate.get().withMaterialForDisplay(MaterialIds.cobalt)), 132, 36);
    this.fluidBar = helper.createDrawable(BACKGROUND_LOC, 3, 3, 14, 34);
    this.itemWithFluid = helper.createDrawable(BACKGROUND_LOC, 164, 0, 18, 20);
    this.partBuilder = helper.createDrawable(BACKGROUND_LOC, 182, 0, 16, 16);
    this.castingTable = helper.createDrawable(BACKGROUND_LOC, 198, 0, 16, 16);
    this.font = Minecraft.getInstance().font;
  }

  // display tools

  /** List of all tools for display */
  private final List<IModifiable> toolItems = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.MULTIPART_TOOL)
    .filter(item -> item instanceof IModifiable).map(item -> (IModifiable) item).toList();
  /** List of all tools for display */
  private final List<IMaterialItem> partItems = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.TOOL_PARTS)
    .filter(item -> item instanceof IMaterialItem).map(item -> (IMaterialItem) item).toList();

  /** Cache of display tools for each material */
  private final SimpleCache<MaterialVariant,List<ItemStack>> tools = new SimpleCache<>(material ->
    toolItems.stream()
      .map(item -> ToolBuildHandler.createSingleMaterial(item, material))
      .filter(stack -> !stack.isEmpty())
      .toList());
  /** Cache of display parts for each material */
  private final SimpleCache<MaterialVariantId,List<ItemStack>> toolParts = new SimpleCache<>(material ->
    partItems.stream()
      .filter(item -> item.canUseMaterial(material.getId()))
      .map(item -> item.withMaterialForDisplay(material))
      .toList());

  @Override
  public void draw(IDisplayMaterialRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
    if (!recipe.getFluids().isEmpty()) {
      fluidBar.draw(graphics, 3, 1);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayMaterialRecipe recipe, IFocusGroup focuses) {
    List<ItemStack> items = recipe.getDisplayItems();
    List<FluidStack> fluids = recipe.getFluids();
    int fluidHeight = 32;
    // draw items
    if (!items.isEmpty()) {
      fluidHeight = 15;
      IRecipeSlotBuilder itemSlot;
      // if we have both, draw the background for items differently
      if (fluids.isEmpty()) {
        itemSlot = builder.addInputSlot(2,  9).setStandardSlotBackground();
      } else {
        itemSlot = builder.addInputSlot(2, 19).setBackground(itemWithFluid, -1, -3);
      }
      itemSlot.addItemStacks(items);
      ItemStack leftover = recipe.getLeftover();
      if (!leftover.isEmpty()) {
        builder.addOutputSlot(39, 20).addItemStack(leftover).addRichTooltipCallback(LEFTOVER_TOOLTIP);
      }
    }
    // draw fluids
    if (!fluids.isEmpty()) {
      builder.addInputSlot(4, 2)
        .addIngredients(ForgeTypes.FLUID_STACK, fluids)
        .setFluidRenderer(100, false, 12, fluidHeight)
        .setSlotName(FLUID_SLOT);
    }

    // materials
    MaterialVariant material = recipe.getMaterial();
    builder.addOutputSlot(21, 0)
      .setCustomRenderer(TConstructJEIConstants.MATERIAL_TYPE, materialTitleRenderer)
      .addIngredient(TConstructJEIConstants.MATERIAL_TYPE, material);
    // composite base
    MaterialVariant input = recipe.getInput();
    if (input != null) {
      int x = 21;
      if (!items.isEmpty()) x += recipe.hasLeftover() ? 36 : 18; // craftable icon and leftover icon offsets
      // while it shouldn't happen, offset if we also have a leftover
      builder.addInputSlot(x, 20)
        .addRichTooltipCallback(COMPOSITE_TOOLTIP)
        .addIngredient(TConstructJEIConstants.MATERIAL_TYPE, input);
    }

    // display tools and parts
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 116,  0).addItemStacks(toolParts.apply(material.getVariant()));
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 116, 20).addItemStacks(tools.apply(material));
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayMaterialRecipe recipe, IFocusGroup focuses) {
    int value = recipe.getValue();
    if (value > 0) {
      builder.addText(Component.translatable(KEY_VALUE, value), 111, 9)
        .setPosition(21, 11).setColor(Color.GRAY.getRGB())
        .setTooltip(VALUE_TOOLTIP);
    } else {
      IRecipeSlotView fluid = CategoryUtil.findSlot(builder.getRecipeSlots().getSlots(), FLUID_SLOT);
      if (fluid != null) {
        builder.addWidget(new FluidAmountWidget(new ScreenRectangle(21, 11, 111, 9), font, fluid));
      }
    }
    // craftable icon
    if (!recipe.getDisplayItems().isEmpty()) {
      if (recipe.getMaterial().get().isCraftable()) {
        builder.addDrawableWidget(partBuilder).setPosition(21, 20).setTooltip(CRAFTABLE);
      } else {
        builder.addDrawableWidget(castingTable).setPosition(21, 20).setTooltip(UNCRAFTABLE);
      }
    }
  }

  /** Widget showing liquid fuel duration, animating the amount */
  private record FluidAmountWidget(ScreenRectangle rectangle, Font font, IRecipeSlotView fluidSlot) implements IRecipeWidget {
    @Override
    public ScreenPosition getPosition() {
      return rectangle.position();
    }

    @Override
    public @Nullable ScreenRectangle getScreenRectangle() {
      return rectangle;
    }

    @Override
    public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
      FluidStack fluid = fluidSlot.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      if (!fluid.isEmpty()) {
        Component string = Component.translatable(KEY_AMOUNT, fluid.getAmount());
        graphics.drawString(font, string, 0, 0, Color.GRAY.getRGB(), false);
      }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
      tooltip.add(AMOUNT_TOOLTIP);
    }
  }
}
