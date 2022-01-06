package slimeknights.tconstruct.plugin.jei.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entity melting display in JEI
 */
public class EntityMeltingRecipeCategory implements IRecipeCategory<EntityMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "entity_melting.title");
  private static final String KEY_PER_HEARTS = TConstruct.makeTranslationKey("jei", "entity_melting.per_hearts");
  private static final Component TOOLTIP_PER_HEART = new TranslatableComponent(TConstruct.makeTranslationKey("jei", "entity_melting.per_heart")).withStyle(ChatFormatting.GRAY);

  private static final Int2ObjectMap<ITooltipCallback<FluidStack>> TOOLTIP_MAP = new Int2ObjectOpenHashMap<>();

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable arrow;
  private final IDrawable tank;

  public EntityMeltingRecipeCategory(IGuiHelper helper) {
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
  public Component getTitle() {
    return TITLE;
  }

  /**
   * Maps a list of entities to a list of item stacks
   * @param entities  Entities
   * @return  Input item stack list
   */
  public static List<List<ItemStack>> getSpawnEggs(Stream<EntityType<?>> entities) {
    return ImmutableList.of(entities.map(ForgeSpawnEggItem::fromEntityType).filter(Objects::nonNull).map(ItemStack::new).collect(Collectors.toList()));
  }

  @Override
  public void setIngredients(EntityMeltingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(JEIPlugin.ENTITY_TYPE, recipe.getDisplayInputs());
    ingredients.setInputLists(VanillaTypes.ITEM, getSpawnEggs(recipe.getInputs().stream()));
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(EntityMeltingRecipe recipe, PoseStack matrices, double mouseX, double mouseY) {
    arrow.draw(matrices, 71, 21);

    // draw damage string next to the heart icon
    String damage = Float.toString(recipe.getDamage() / 2f);
    Font fontRenderer = Minecraft.getInstance().font;
    int x = 84 - fontRenderer.width(damage);
    fontRenderer.draw(matrices, damage, x, 8, Color.RED.getRGB());
  }

  @SuppressWarnings("rawtypes")
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
    fluids.init(1, false, 115, 11, 16, 32, FluidValues.INGOT * 2, false, null);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    fluids.init(2, true, 75, 43, 16, 16, 1, false, tank);
    fluids.set(2, MeltingFuelHandler.getUsableFuels(1));
  }

  private record FluidTooltip(int damage) implements ITooltipCallback<FluidStack> {
    @Override
    public void onTooltip(int index, boolean input, FluidStack fluid, List<Component> list) {
      Component name = list.get(0);
      Component modId = list.get(list.size() - 1);
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
          list.add(new TranslatableComponent(KEY_PER_HEARTS, damage / 2f).withStyle(ChatFormatting.GRAY));
        }
      }
      list.add(modId);
    }
  }
}
