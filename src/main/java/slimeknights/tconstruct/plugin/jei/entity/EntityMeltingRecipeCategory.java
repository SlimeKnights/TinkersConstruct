package slimeknights.tconstruct.plugin.jei.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entity melting display in JEI
 */
public class EntityMeltingRecipeCategory implements IRecipeCategory<EntityMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/melting.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "entity_melting.title");
  private static final String KEY_PER_HEARTS = Util.makeTranslationKey("jei", "entity_melting.per_hearts");
  private static final ITextComponent TOOLTIP_PER_HEART = new TranslationTextComponent(Util.makeTranslationKey("jei", "entity_melting.per_heart")).mergeStyle(TextFormatting.GRAY);

  private static final Int2ObjectMap<ITooltipCallback<FluidStack>> TOOLTIP_MAP = new Int2ObjectOpenHashMap<>();

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawable arrow;
  private final IDrawable tank;

  public EntityMeltingRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 41, 150, 62);
    this.icon = helper.createDrawable(BACKGROUND_LOC, 174, 41, 16, 16);
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 150, 41, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 150, 74, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.entityMelting;
  }

  @Override
  public Class<? extends EntityMeltingRecipe> getRecipeClass() {
    return EntityMeltingRecipe.class;
  }

  @Override
  public void setIngredients(EntityMeltingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(JEIPlugin.ENTITY_TYPE, recipe.getDisplayInputs());
    ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getInputs().stream().map(SpawnEggItem::getEgg).filter(Objects::nonNull).map(ItemStack::new).collect(Collectors.toList())));
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(EntityMeltingRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    arrow.draw(matrices, 71, 21);

    // draw damage string next to the heart icon
    String damage = Float.toString(recipe.getDamage() / 2f);
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 84 - fontRenderer.getStringWidth(damage);
    fontRenderer.drawString(matrices, damage, x, 8, Color.RED.getRGB());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, EntityMeltingRecipe recipe, IIngredients ingredients) {
    // inputs
    // if we have a spawn egg focus, filter the displayed entities
    IGuiIngredientGroup<EntityType> entityTypes = layout.getIngredientsGroup(JEIPlugin.ENTITY_TYPE);
    entityTypes.init(0, true, entityRenderer, 19, 11, 32, 32, 0, 0);
    entityTypes.set(ingredients);
    EntityIngredientHelper.setFocus(layout, entityTypes, recipe.getInputs(), 0);

    // output
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.addTooltipCallback(TOOLTIP_MAP.computeIfAbsent(recipe.getDamage(), FluidTooltip::new));
    fluids.init(1, false, 115, 11, 16, 32, MaterialValues.INGOT, false, null);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    fluids.init(2, true, 75, 43, 16, 16, 1, false, tank);
    fluids.set(2, MeltingFuelHandler.getUsableFuels(1));
  }

  @RequiredArgsConstructor
  private static class FluidTooltip implements ITooltipCallback<FluidStack> {
    private final int damage;

    @Override
    public void onTooltip(int index, boolean input, FluidStack fluid, List<ITextComponent> list) {
      ITextComponent name = list.get(0);
      ITextComponent modId = list.get(list.size() - 1);
      list.clear();
      list.add(name);
      // add fluid units
      if (index != 2) {
        FluidTooltipHandler.appendMaterial(fluid, list);
      }
      // output rate
      if (index == 1) {
        if (damage == 2) {
          list.add(TOOLTIP_PER_HEART);
        } else {
          list.add(new TranslationTextComponent(KEY_PER_HEARTS, damage / 2f).mergeStyle(TextFormatting.GRAY));
        }
      }
      list.add(modId);
    }
  }
}
