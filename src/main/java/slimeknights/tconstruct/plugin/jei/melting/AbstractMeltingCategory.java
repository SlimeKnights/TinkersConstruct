package slimeknights.tconstruct.plugin.jei.melting;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.FluidTooltipHandler;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

/** Shared logic between melting and foundry */
public abstract class AbstractMeltingCategory implements IRecipeCategory<MeltingRecipe> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "melting.time");
  protected static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  protected static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  protected static final ITextComponent TOOLTIP_ORE = new TranslationTextComponent(TConstruct.makeTranslationKey("jei", "melting.ore"));

  @Getter
  private final IDrawable background;
  protected final IDrawableStatic tankOverlay;
  protected final IDrawableStatic plus;
  protected final LoadingCache<Integer,IDrawableAnimated> cachedArrows;

  public AbstractMeltingCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.plus = helper.drawableBuilder(BACKGROUND_LOC, 132, 34, 6, 6).build();
    this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer,IDrawableAnimated>() {
      @Override
      public IDrawableAnimated load(Integer meltingTime) {
        return helper.drawableBuilder(BACKGROUND_LOC, 150, 41, 24, 17).buildAnimated(meltingTime, StartDirection.LEFT, false);
      }
    });
  }

  @Override
  public Class<? extends MeltingRecipe> getRecipeClass() {
    return MeltingRecipe.class;
  }

  @Override
  public void setIngredients(MeltingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutputLists(VanillaTypes.FLUID, recipe.getDisplayOutput());
  }

  @Override
  public void draw(MeltingRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    // draw the arrow
    cachedArrows.getUnchecked(recipe.getTime() * 5).draw(matrices, 56, 18);
    if (recipe.isOre()) {
      plus.draw(matrices, 87, 31);
    }

    // temperature
    int temperature = recipe.getTemperature();
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    String tempString = I18n.format(KEY_TEMPERATURE, temperature);
    int x = 56 - fontRenderer.getStringWidth(tempString) / 2;
    fontRenderer.drawString(matrices, tempString, x, 3, Color.GRAY.getRGB());
  }

  @Override
  public List<ITextComponent> getTooltipStrings(MeltingRecipe recipe, double mouseXD, double mouseYD) {
    int mouseX = (int)mouseXD;
    int mouseY = (int)mouseYD;
    if (recipe.isOre() && GuiUtil.isHovered(mouseX, mouseY, 87, 31, 16, 16)) {
      return Collections.singletonList(TOOLTIP_ORE);
    }
    // time tooltip
    if (GuiUtil.isHovered(mouseX, mouseY, 56, 18, 24, 17)) {
      return Collections.singletonList(new TranslationTextComponent(KEY_COOLING_TIME, recipe.getTime() / 4));
    }
    return Collections.emptyList();
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  public static abstract class MeltingFluidCallback implements ITooltipCallback<FluidStack> {
    private final boolean isOre;

    /**
     * Adds teh tooltip for ores
     * @param stack  Fluid to draw
     * @param list   Tooltip so far
     * @return  True if the shift message should display
     */
    protected abstract boolean addOreTooltip(FluidStack stack, List<ITextComponent> list);

    @Override
    public void onTooltip(int index, boolean input, FluidStack stack, List<ITextComponent> list) {
      ITextComponent name = list.get(0);
      ITextComponent modId = list.get(list.size() - 1);
      list.clear();
      list.add(name);

      // outputs show amounts
      if (index != -1) {
        if (isOre && index == 0) {
          if (addOreTooltip(stack, list)) {
            FluidTooltipHandler.appendShift(list);
          }
        } else {
          FluidTooltipHandler.appendMaterial(stack, list);
        }
      }

      // fuels show temperature and quality
      if (index == -1) {
        MeltingFuelHandler.getTemperature(stack.getFluid()).ifPresent(temperature -> {
          list.add(new TranslationTextComponent(KEY_TEMPERATURE, temperature).mergeStyle(TextFormatting.GRAY));
          list.add(new TranslationTextComponent(KEY_MULTIPLIER, temperature / 1000f).mergeStyle(TextFormatting.GRAY));
        });
      }
      list.add(modId);
    }
  }
}
