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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierRecipeCategory implements IRecipeCategory<IDisplayModifierRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_TITLE = TConstruct.makeTranslationKey("jei", "modifiers.title");

  // translation
  private static final List<ITextComponent> TEXT_FREE = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.free"));
  private static final List<ITextComponent> TEXT_INCREMENTAL = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.incremental"));
  private static final String KEY_SLOT = TConstruct.makeTranslationKey("jei", "modifiers.slot");
  private static final String KEY_SLOTS = TConstruct.makeTranslationKey("jei", "modifiers.slots");
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.max");

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
  private final Map<SlotType,TextureAtlasSprite> slotTypeSprites = new HashMap<>();
  public ModifierRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.maxPrefix = ForgeI18n.getPattern(KEY_MAX);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = helper.createDrawableIngredient(CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE));
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = helper.createDrawable(BACKGROUND_LOC, 128, 33, 16, 16);
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

  /** Draws the icon for the given slot type */
  private void drawSlotType(MatrixStack matrices, @Nullable SlotType slotType, int x, int y) {
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite;
    if (slotTypeSprites.containsKey(slotType)) {
      sprite = slotTypeSprites.get(slotType);
    } else {
      ModelManager modelManager = minecraft.getModelManager();
      // gets the model for the item, its a sepcial one that gives us texture info
      IBakedModel model = minecraft.getItemRenderer().getItemModelMesher().getItemModel(TinkerModifiers.creativeSlotItem.get());
      if (model != null && model.getOverrides() instanceof NBTKeyModel.Overrides) {
        RenderMaterial material = ((NBTKeyModel.Overrides)model.getOverrides()).getTexture(slotType == null ? "slotless" : slotType.getName());
        sprite = modelManager.getAtlasTexture(material.getAtlasLocation()).getSprite(material.getTextureLocation());
      } else {
        // failed to use the model, use missing texture
        sprite = modelManager.getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(MissingTextureSprite.getLocation());
      }
      slotTypeSprites.put(slotType, sprite);
    }
    minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    Screen.blit(matrices, x, y, 0, 16, 16, sprite);
  }

  @Override
  public void draw(IDisplayModifierRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    List<List<ItemStack>> inputs = recipe.getDisplayItems();
    drawSlot(matrices, inputs, 1,  2, 32);
    drawSlot(matrices, inputs, 2, 24, 14);
    drawSlot(matrices, inputs, 3, 46, 32);
    drawSlot(matrices, inputs, 4, 42, 57);
    drawSlot(matrices, inputs, 5,  6, 57);

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
    SlotCount slots = recipe.getSlots();
    if (slots == null) {
      drawSlotType(matrices, null, 110, 58);
    } else {
      drawSlotType(matrices, slots.getType(), 110, 58);
      String text = Integer.toString(slots.getCount());
      int x = 111 - fontRenderer.getStringWidth(text);
      fontRenderer.drawString(matrices, text, x, 63, Color.GRAY.getRGB());
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
    } else if (GuiUtil.isHovered(checkX, checkY, 98, 58, 24, 16)) {
      // slot tooltip over icon
      SlotCount slots = recipe.getSlots();
      if (slots != null) {
        int count = slots.getCount();
        if (count == 1) {
          return Collections.singletonList(new TranslationTextComponent(KEY_SLOT, slots.getType().getDisplayName()));
        } else if (count > 1) {
          return Collections.singletonList(new TranslationTextComponent(KEY_SLOTS, slots, slots.getType().getDisplayName()));
        }
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
    items.init(0, true, 24, 37);
    items.init(1, true,  2, 32);
    items.init(2, true, 24, 14);
    items.init(3, true, 46, 32);
    items.init(4, true, 42, 57);
    items.init(5, true,  6, 57);
    items.set(ingredients);

    // start up the output slot
    items.init(-1, false, 104, 33);

    // if focusing on a tool, filter out other tools
    IFocus<ItemStack> focus = layout.getFocus(VanillaTypes.ITEM);
    List<ItemStack> output = recipe.getToolWithModifier();
    items.set(-1, output);
    if (focus != null) {
      Item item = focus.getValue().getItem();
      if (item.isIn(TinkerTags.Items.MODIFIABLE)) {
        List<List<ItemStack>> allItems = recipe.getDisplayItems();
        if (allItems.size() >= 1) {
          allItems.get(0).stream().filter(stack -> stack.getItem() == item)
                  .findFirst().ifPresent(stack -> items.set(0, stack));
        }
        output.stream().filter(stack -> stack.getItem() == item).findFirst().ifPresent(stack -> items.set(-1, stack));
      }
    }

    // set modifiers for display
    modifiers.init(6, false, modifierRenderer, 2, 2, 124, 10, 0, 0);
    modifiers.set(ingredients);
  }
}
