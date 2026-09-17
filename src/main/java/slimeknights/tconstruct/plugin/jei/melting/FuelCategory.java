package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;

import java.awt.Color;
import java.util.List;

import static slimeknights.tconstruct.plugin.jei.melting.AbstractMeltingCategory.KEY_MULTIPLIER;
import static slimeknights.tconstruct.plugin.jei.melting.AbstractMeltingCategory.KEY_TEMPERATURE;

/** Category showing smeltery fuels */
public class FuelCategory extends AbstractRecipeCategory<MeltingFuel> {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "fuel.title");
  private static final Component SOLID = TConstruct.makeTranslation("jei", "fuel.solid");
  private static final Component UP_TO = TConstruct.makeTranslation("jei", "fuel.duration.up_to");
  private static final String FUEL_SLOT = "fuel";

  private final IDrawableStatic fuelBar;
  public FuelCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.FUEL, TITLE, helper.createDrawableItemLike(TinkerFluids.blazingBlood), 132, 40);
    this.fuelBar = helper.createDrawable(AbstractMeltingCategory.BACKGROUND_LOC, 3, 3, 14, 34);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MeltingFuel recipe, IFocusGroup focuses) {
    List<FluidStack> fluids = recipe.getInputs();
    // no fluids mean this is the solid fuel info
    if (!fluids.isEmpty()) {
      builder.addInputSlot(4, 4)
        .addIngredients(ForgeTypes.FLUID_STACK, recipe.getInputs())
        .setFluidRenderer(100, false, 12, 32)
        .setBackground(fuelBar, -1, -1)
        .setSlotName(FUEL_SLOT);
    } else {
      // if focusing on a fuel, show it
      IFocus<ItemStack> focus = focuses.getFocuses(VanillaTypes.ITEM_STACK).findFirst().orElse(null);
      // otherwise, show our examples
      List<ItemStack> fuels = focus != null ? List.of(focus.getTypedValue().getIngredient()) : MeltingFuelHandler.SOLID_FUELS.get();
      builder.addInputSlot(2, 12)
        .addItemStacks(fuels)
        .setStandardSlotBackground()
        .setSlotName(FUEL_SLOT);
      // add all fuels as an invisible ingredient for the sake of recipe lookup
      builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(MeltingFuelHandler.getAllSolidFuels());
    }
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MeltingFuel recipe, IFocusGroup focuses) {
    List<FluidStack> fluids = recipe.getInputs();
    Component title = SOLID;
    IRecipeSlotView fuel = CategoryUtil.findSlot(builder.getRecipeSlots().getSlots(), FUEL_SLOT);
    ScreenPosition fuelPosition = new ScreenPosition(21, 31);
    Font font = Minecraft.getInstance().font;
    if (!fluids.isEmpty()) {
      FluidStack fluid = fluids.get(0);
      title = fluid.getDisplayName();
      if (fuel != null) {
        builder.addWidget(new LiquidFuelWidget(fuelPosition, 105, font, fuel, recipe.getDuration() / 5));
      }
    } else if (fuel != null) {
      builder.addWidget(new SolidFuelWidget(fuelPosition, 105, font, fuel));
    }
    builder.addText(title, 111, 9).setPosition(21, 0).setColor(Color.WHITE.getRGB()).setShadow(true);
    // show temperature and multiplier on the same line
    builder.addText(Component.translatable(KEY_TEMPERATURE, recipe.getTemperature()), 111, 9)
      .setPosition(21, 11).setColor(Color.GRAY.getRGB());
    builder.addText(Component.translatable(KEY_MULTIPLIER, recipe.getRate() / 10f), 111, 9)
      .setPosition(21, 21).setColor(Color.GRAY.getRGB());
  }



  /** Widget showing liquid fuel duration, animating the amount */
  private record LiquidFuelWidget(ScreenPosition getPosition, int width, Font font, IRecipeSlotView fuel, int duration) implements IRecipeWidget {
    private static final String KEY_DURATION = TConstruct.makeTranslationKey("jei", "fuel.duration.liquid");

    @Override
    public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
      FluidStack fluid = fuel.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      if (!fluid.isEmpty()) {
        graphics.drawString(font, UP_TO, 0, 0, Color.GRAY.getRGB(), false);
        Component string = Component.translatable(KEY_DURATION, duration, fluid.getAmount());
        graphics.drawString(font, string, width - font.width(string), 0, Color.GRAY.getRGB(), false);
      }
    }
  }

  /** Widget showing solid fuel duration, animating the duration */
  @RequiredArgsConstructor
  private static class SolidFuelWidget implements IRecipeWidget {
    private static final String KEY_DURATION = TConstruct.makeTranslationKey("jei", "fuel.duration.solid");

    @Getter
    private final ScreenPosition position;
    private final int width;
    private final Font font;
    private final IRecipeSlotView fuel;
    private ItemStack lastStack = null;
    private int lastDuration = 0;

    /** Gets the last burn time, using the cached value if its the same stack as previously */
    private int getBurnTime(ItemStack stack) {
      if (stack == lastStack) {
        return lastDuration;
      }
      lastStack = stack;
      lastDuration = ForgeHooks.getBurnTime(stack, TinkerRecipeTypes.FUEL.get()) / 20;
      return lastDuration;
    }

    @Override
    public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
      ItemStack stack = fuel.getDisplayedItemStack().orElse(ItemStack.EMPTY);
      if (!stack.isEmpty()) {
        graphics.drawString(font, UP_TO, 0, 0, Color.GRAY.getRGB(), false);
        Component string = Component.translatable(KEY_DURATION, getBurnTime(stack));
        graphics.drawString(font, string, width - font.width(string), 0, Color.GRAY.getRGB(), false);
      }
    }
  }
}
