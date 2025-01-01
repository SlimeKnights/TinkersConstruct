package slimeknights.tconstruct.plugin.jei.modifiers;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.stats.SkullStats;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierRecipeCategory implements IRecipeCategory<IDisplayModifierRecipe> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "modifiers.title");

  // translation
  private static final List<Component> TEXT_FREE = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.free"));
  private static final List<Component> TEXT_INCREMENTAL = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.incremental"));
  private static final String KEY_SLOT = TConstruct.makeTranslationKey("jei", "modifiers.slot");
  private static final String KEY_SLOTS = TConstruct.makeTranslationKey("jei", "modifiers.slots");
  private static final String KEY_MIN = TConstruct.makeTranslationKey("jei", "modifiers.level.min");
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.level.max");
  private static final String KEY_RANGE = TConstruct.makeTranslationKey("jei", "modifiers.level.range");
  private static final String KEY_EXACT = TConstruct.makeTranslationKey("jei", "modifiers.level.exact");

  private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124, 10);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable requirements, incremental;
  private final IDrawable[] slotIcons;
  private final Map<SlotType,TextureAtlasSprite> slotTypeSprites = new HashMap<>();
  public ModifierRecipeCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE));
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = helper.createDrawable(BACKGROUND_LOC, 128, 33, 16, 16);
  }

  @Override
  public RecipeType<IDisplayModifierRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIERS;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  /** Draws a single slot icon */
  private void drawSlot(GuiGraphics graphics, IDisplayModifierRecipe recipe, int slot, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(slot);
    if (stacks.isEmpty()) {
      // -1 as the item list includes the output slot, we skip that
      slotIcons[slot].draw(graphics, x + 1, y + 1);
    }
  }

  /** Draws the icon for the given slot type */
  private void drawSlotType(GuiGraphics graphics, @Nullable SlotType slotType, int x, int y) {
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite;
    if (slotTypeSprites.containsKey(slotType)) {
      sprite = slotTypeSprites.get(slotType);
    } else {
      ModelManager modelManager = minecraft.getModelManager();
      // gets the model for the item, its a sepcial one that gives us texture info
      BakedModel model = minecraft.getItemRenderer().getItemModelShaper().getItemModel(TinkerModifiers.creativeSlotItem.get());
      if (model != null && model.getOverrides() instanceof NBTKeyModel.Overrides) {
        Material material = ((NBTKeyModel.Overrides)model.getOverrides()).getTexture(slotType == null ? "slotless" : slotType.getName());
        sprite = modelManager.getAtlas(material.atlasLocation()).getSprite(material.texture());
      } else {
        // failed to use the model, use missing texture
        sprite = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(MissingTextureAtlasSprite.getLocation());
      }
      slotTypeSprites.put(slotType, sprite);
    }
    graphics.blit(x, y, 0, 16, 16, sprite);
  }

  @Override
  public void draw(IDisplayModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
    drawSlot(graphics, recipe, 0,  2, 32);
    drawSlot(graphics, recipe, 1, 24, 14);
    drawSlot(graphics, recipe, 2, 46, 32);
    drawSlot(graphics, recipe, 3, 42, 57);
    drawSlot(graphics, recipe, 4,  6, 57);

    // draw info icons
    ModifierEntry result = recipe.getDisplayResult();
    if (result.getHook(ModifierHooks.REQUIREMENTS).requirementsError(result) != null) {
      requirements.draw(graphics, 66, 58);
    }
    if (recipe.isIncremental()) {
      incremental.draw(graphics, 83, 59);
    }

    // draw level requirements
    Font fontRenderer = Minecraft.getInstance().font;
    Component levelText = null;
    Component variant = recipe.getVariant();
    if (variant != null) {
      levelText = variant;
    } else {
      IntRange level = recipe.getLevel();
      int min = level.min();
      int max = level.max();
      // min being 1 means we only have a max level, we check this first as Max Level is better than exact typiclly
      if (min == 1) {
        if (max < ModifierEntry.VALID_LEVEL.max()) {
          levelText = Component.translatable(KEY_MAX, max);
        }
      } else if (min == max) {
        levelText = Component.translatable(KEY_EXACT, min);
      } else if (max == ModifierEntry.VALID_LEVEL.max()) {
        levelText = Component.translatable(KEY_MIN, min);
      } else {
        levelText = Component.translatable(KEY_RANGE, min, max);
      }
    }
    if (levelText != null) {
      // center string
      graphics.drawString(fontRenderer, levelText, 86 - fontRenderer.width(levelText) / 2, 16, Color.GRAY.getRGB(), false);
    }

    // draw slot cost
    SlotCount slots = recipe.getSlots();
    if (slots == null) {
      drawSlotType(graphics, null, 110, 58);
    } else {
      drawSlotType(graphics, slots.type(), 110, 58);
      String text = Integer.toString(slots.count());
      graphics.drawString(fontRenderer, text, 111 - fontRenderer.width(text), 63, Color.GRAY.getRGB(), false);
    }
  }

  @Override
  public List<Component> getTooltipStrings(IDisplayModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    int checkX = (int) mouseX;
    int checkY = (int) mouseY;
    ModifierEntry result = recipe.getDisplayResult();
    if (GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16)) {
      Component requirements = result.getHook(ModifierHooks.REQUIREMENTS).requirementsError(result);
      if (requirements != null) {
        return Collections.singletonList(requirements);
      }
    }
    if (recipe.isIncremental() && GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16)) {
      return TEXT_INCREMENTAL;
    }
    if (GuiUtil.isHovered(checkX, checkY, 98, 58, 24, 16)) {
      // slot tooltip over icon
      SlotCount slots = recipe.getSlots();
      if (slots != null) {
        int count = slots.count();
        if (count == 1) {
          return Collections.singletonList(Component.translatable(KEY_SLOT, slots.type().getDisplayName()));
        } else if (count > 1) {
          return Collections.singletonList(Component.translatable(KEY_SLOTS, slots.count(), slots.type().getDisplayName()));
        }
      } else {
        return TEXT_FREE;
      }
    }
    
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayModifierRecipe recipe, IFocusGroup focuses) {
    // inputs
    builder.addSlot(RecipeIngredientRole.INPUT,  3, 33).addItemStacks(recipe.getDisplayItems(0));
    builder.addSlot(RecipeIngredientRole.INPUT, 25, 15).addItemStacks(recipe.getDisplayItems(1));
    builder.addSlot(RecipeIngredientRole.INPUT, 47, 33).addItemStacks(recipe.getDisplayItems(2));
    builder.addSlot(RecipeIngredientRole.INPUT, 43, 58).addItemStacks(recipe.getDisplayItems(3));
    builder.addSlot(RecipeIngredientRole.INPUT,  7, 58).addItemStacks(recipe.getDisplayItems(4));
    // modifiers
    builder.addSlot(RecipeIngredientRole.OUTPUT, 3, 3)
           .setCustomRenderer(TConstructJEIConstants.MODIFIER_TYPE, modifierRenderer)
           .addIngredient(TConstructJEIConstants.MODIFIER_TYPE, recipe.getDisplayResult());
    // tool
    List<ItemStack> toolWithoutModifier = recipe.getToolWithoutModifier();
    List<ItemStack> toolWithModifier = recipe.getToolWithModifier();

    // hack: if any slimeskull is selected, add all known variants to the recipe lookup
    Item slimeskull = TinkerTools.slimesuit.get(ArmorItem.Type.HELMET);
    for (ItemStack stack : toolWithoutModifier) {
      if (stack.is(slimeskull)) {
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addItemStacks(getSlimeskullHelmets());
        break;
      }
    }

    // JEI is currently being dumb and using ingredient subtypes within recipe focuses
    // we use a more strict subtype for tools in ingredients so they all show in JEI, but do not care in recipes
    // thus, manually handle the focuses
    IFocus<ItemStack> focus = focuses.getFocuses(VanillaTypes.ITEM_STACK).filter(f -> f.getRole() == RecipeIngredientRole.CATALYST).findFirst().orElse(null);
    if (focus != null) {
      Item item = focus.getTypedValue().getIngredient().getItem();
      for (ItemStack stack : toolWithoutModifier) {
        if (stack.is(item)) {
          toolWithoutModifier = List.of(stack);
          break;
        }
      }
      for (ItemStack stack : toolWithModifier) {
        if (stack.is(item)) {
          toolWithModifier = List.of(stack);
          break;
        }
      }
    }
    builder.addSlot(RecipeIngredientRole.CATALYST,  25, 38).addItemStacks(toolWithoutModifier);
    builder.addSlot(RecipeIngredientRole.CATALYST, 105, 34).addItemStacks(toolWithModifier);
  }


  /* Slimeskull workaround */
  /** internal list of slimeskulls for the sake of ingredient lookup, needed since they are technically distinct but modifiers treat them as the same */
  private static List<ItemStack> SLIMESKULL_HELMETS = null;

  /** called to clear the cache on ingredient reload as materials may have changed. TODO: why is this never called? */
  public static void clearSlimeskullCache() {
    SLIMESKULL_HELMETS = null;
  }

  /** gets the list of slimeskull helmets, loading it if needed */
  private static List<ItemStack> getSlimeskullHelmets() {
    if (SLIMESKULL_HELMETS == null) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      IModifiable slimeskull = TinkerTools.slimesuit.get(ArmorItem.Type.HELMET);
      SLIMESKULL_HELMETS = registry.getAllMaterials().stream()
                                   .filter(material -> registry.getMaterialStats(material.getIdentifier(), SkullStats.ID).isPresent())
                                   .map(material -> ToolBuildHandler.buildItemFromMaterials(slimeskull, MaterialNBT.of(material)))
                                   .toList();
    }
    return SLIMESKULL_HELMETS;
  }
}
