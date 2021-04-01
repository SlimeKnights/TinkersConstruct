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
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class ModifierRecipeCategory implements IRecipeCategory<IDisplayModifierRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "modifiers.title");

  // translation
  private static final List<ITextComponent> TEXT_FREE = Collections.singletonList(Util.makeTranslation("jei", "modifiers.free"));
  private static final List<ITextComponent> TEXT_SINGLE_UPGRADE = Collections.singletonList(Util.makeTranslation("jei", "modifiers.upgrade"));
  private static final List<ITextComponent> TEXT_INCREMENTAL = Collections.singletonList(Util.makeTranslation("jei", "modifiers.incremental"));
  private static final String KEY_UPGRADES = Util.makeTranslationKey("jei", "modifiers.upgrades");
  private static final List<ITextComponent> TEXT_SINGLE_ABILITY = Collections.singletonList(Util.makeTranslation("jei", "modifiers.ability"));
  private static final String KEY_ABILITIES = Util.makeTranslationKey("jei", "modifiers.abilities");
  private static final String KEY_MAX = Util.makeTranslationKey("jei", "modifiers.max");

  private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final String maxPrefix;
  private final IDrawable requirements, incremental;
  private final IDrawable[] slotIcons;
  private final IDrawable slotUpgrade, slotAbility, slotFree;
  public ModifierRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.maxPrefix = ForgeI18n.getPattern(KEY_MAX);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerModifiers.creativeUpgradeItem));
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = helper.createDrawable(BACKGROUND_LOC, 128, 33, 16, 16);
    this.slotUpgrade = helper.createDrawable(BACKGROUND_LOC, 144, 17, 8, 8);
    this.slotAbility = helper.createDrawable(BACKGROUND_LOC, 152, 17, 8, 8);
    this.slotFree    = helper.createDrawable(BACKGROUND_LOC, 160, 17, 8, 8);
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
    ingredients.setInputLists(VanillaTypes.ITEM, recipe.getDisplayItems());
    ingredients.setOutput(JEIPlugin.MODIFIER_TYPE, recipe.getDisplayResult());
  }

  /** Draws a single slot icon */
  private void drawSlot(MatrixStack matrices, List<List<ItemStack>> inputs, int slot, int x, int y) {
    if (slot >= inputs.size() || inputs.get(slot).isEmpty()) {
      // -1 as the item list includes the output slot, we skip that
      slotIcons[slot - 1].draw(matrices, x + 1, y + 1);
    }
  }

  @Override
  public void draw(IDisplayModifierRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    List<List<ItemStack>> inputs = recipe.getDisplayItems();
    drawSlot(matrices, inputs, 2,  2, 32);
    drawSlot(matrices, inputs, 3, 24, 14);
    drawSlot(matrices, inputs, 4, 46, 32);
    drawSlot(matrices, inputs, 5, 42, 57);
    drawSlot(matrices, inputs, 6,  6, 57);

    // draw info icons
    if (recipe.hasRequirements()) {
      requirements.draw(matrices, 66, 58);
    }
    if (recipe.isIncremental()) {
      incremental.draw(matrices, 83, 59);
    }

    // draw max count
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int max = recipe.getMaxLevel();
    if (max > 0) {
      fontRenderer.drawString(matrices, maxPrefix + max, 66, 16, Color.GRAY.getRGB());
    }

    // draw slot cost
    int upgrades = recipe.getUpgradeSlots();
    int abilities = recipe.getAbilitySlots();
    IDrawable icon;
    String text = null;
    // ability takes precedence, not that both is ever set
    if (abilities > 0) {
      icon = slotAbility;
      text = Integer.toString(abilities);
    } else if (upgrades > 0) {
      icon = slotUpgrade;
      text = Integer.toString(upgrades);
    } else {
      icon = slotFree;
    }
    // draw number for quick info, free has no number
    icon.draw(matrices, 114, 61);
    if (text != null) {
      int x = 112 - fontRenderer.getStringWidth(text);
      fontRenderer.drawString(matrices, text, x, 62, Color.GRAY.getRGB());
    }
  }

  @Override
  public List<ITextComponent> getTooltipStrings(IDisplayModifierRecipe recipe, double mouseX, double mouseY) {
    int checkX = (int) mouseX;
    int checkY = (int) mouseY;
    if (recipe.hasRequirements() && GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16)) {
      return Collections.singletonList(new TranslationTextComponent(recipe.getRequirementsError()));
    } else if (recipe.isIncremental() && GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16)) {
      return TEXT_INCREMENTAL;
    } else if (GuiUtil.isHovered(checkX, checkY, 98, 61, 24, 8)) {
      // slot tooltip over icon
      int upgrades = recipe.getUpgradeSlots();
      int abilities = recipe.getAbilitySlots();
      // ability take precedence again, not that both can be set
      if (abilities > 0) {
        return abilities == 1 ? TEXT_SINGLE_ABILITY : Collections.singletonList(new TranslationTextComponent(KEY_ABILITIES, abilities));
      } else if (upgrades > 0) {
        return upgrades == 1 ? TEXT_SINGLE_UPGRADE : Collections.singletonList(new TranslationTextComponent(KEY_UPGRADES, upgrades));
      } else {
        return TEXT_FREE;
      }
    }
    
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayout layout, IDisplayModifierRecipe recipe, IIngredients ingredients) {
    IGuiIngredientGroup<ModifierEntry> modifiers = layout.getIngredientsGroup(JEIPlugin.MODIFIER_TYPE);

    // set items for display
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 104, 33);
    items.init(1, true, 24, 37);
    items.init(2, true,  2, 32);
    items.init(3, true, 24, 14);
    items.init(4, true, 46, 32);
    items.init(5, true, 42, 57);
    items.init(6, true,  6, 57);
    items.set(ingredients);

    // if focusing on a tool, filter out other tools
    IFocus<ItemStack> focus = layout.getFocus(VanillaTypes.ITEM);
    if (focus != null && focus.getValue().getItem().isIn(TinkerTags.Items.MULTIPART_TOOL)) {
      List<List<ItemStack>> allItems = recipe.getDisplayItems();
      if (allItems.size() >= 2) {
        Item item = focus.getValue().getItem();
        allItems.get(0).stream().filter(stack -> stack.getItem() == item)
                .findFirst().ifPresent(stack -> items.set(0, stack));
        allItems.get(1).stream().filter(stack -> stack.getItem() == item)
                .findFirst().ifPresent(stack -> items.set(1, stack));
      }
    }

    // set modifiers for display
    modifiers.init(7, false, modifierRenderer, 2, 2, 124, 10, 0, 0);
    modifiers.set(ingredients);
  }
}
