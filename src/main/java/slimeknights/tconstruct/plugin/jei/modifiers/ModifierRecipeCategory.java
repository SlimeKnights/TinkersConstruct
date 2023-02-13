package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeI18n;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
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
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.max");

  private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124, 10);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final String maxPrefix;
  private final IDrawable requirements, incremental;
  private final IDrawable[] slotIcons;
  private final Map<SlotType,TextureAtlasSprite> slotTypeSprites = new HashMap<>();
  public ModifierRecipeCategory(IGuiHelper helper) {
    this.maxPrefix = ForgeI18n.getPattern(KEY_MAX);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE));
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = helper.createDrawable(BACKGROUND_LOC, 128, 33, 16, 16);
  }

  @SuppressWarnings("removal")
  @Override
  public ResourceLocation getUid() {
    return TConstructJEIConstants.MODIFIERS.getUid();
  }

  @Override
  public RecipeType<IDisplayModifierRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIERS;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @SuppressWarnings("removal")
  @Override
  public Class<? extends IDisplayModifierRecipe> getRecipeClass() {
    return IDisplayModifierRecipe.class;
  }

  /** Draws a single slot icon */
  private void drawSlot(PoseStack matrices, IDisplayModifierRecipe recipe, int slot, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(slot);
    if (stacks.isEmpty()) {
      // -1 as the item list includes the output slot, we skip that
      slotIcons[slot].draw(matrices, x + 1, y + 1);
    }
  }

  /** Draws the icon for the given slot type */
  private void drawSlotType(PoseStack matrices, @Nullable SlotType slotType, int x, int y) {
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
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

    Screen.blit(matrices, x, y, 0, 16, 16, sprite);
  }

  @Override
  public void draw(IDisplayModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrices, double mouseX, double mouseY) {
    drawSlot(matrices, recipe, 0,  2, 32);
    drawSlot(matrices, recipe, 1, 24, 14);
    drawSlot(matrices, recipe, 2, 46, 32);
    drawSlot(matrices, recipe, 3, 42, 57);
    drawSlot(matrices, recipe, 4,  6, 57);

    // draw info icons
    if (recipe.hasRequirements()) {
      requirements.draw(matrices, 66, 58);
    }
    if (recipe.isIncremental()) {
      incremental.draw(matrices, 83, 59);
    }

    // draw max count
    Font fontRenderer = Minecraft.getInstance().font;
    int max = recipe.getMaxLevel();
    if (max > 0) {
      fontRenderer.draw(matrices, maxPrefix + max, 66, 16, Color.GRAY.getRGB());
    }

    // draw slot cost
    SlotCount slots = recipe.getSlots();
    if (slots == null) {
      drawSlotType(matrices, null, 110, 58);
    } else {
      drawSlotType(matrices, slots.getType(), 110, 58);
      String text = Integer.toString(slots.getCount());
      int x = 111 - fontRenderer.width(text);
      fontRenderer.draw(matrices, text, x, 63, Color.GRAY.getRGB());
    }
  }

  @Override
  public List<Component> getTooltipStrings(IDisplayModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    int checkX = (int) mouseX;
    int checkY = (int) mouseY;
    if (recipe.hasRequirements() && GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16)) {
      return Collections.singletonList(new TranslatableComponent(recipe.getRequirementsError()));
    } else if (recipe.isIncremental() && GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16)) {
      return TEXT_INCREMENTAL;
    } else if (GuiUtil.isHovered(checkX, checkY, 98, 58, 24, 16)) {
      // slot tooltip over icon
      SlotCount slots = recipe.getSlots();
      if (slots != null) {
        int count = slots.getCount();
        if (count == 1) {
          return Collections.singletonList(new TranslatableComponent(KEY_SLOT, slots.getType().getDisplayName()));
        } else if (count > 1) {
          return Collections.singletonList(new TranslatableComponent(KEY_SLOTS, slots, slots.getType().getDisplayName()));
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
    Item slimeskull = TinkerTools.slimesuit.get(ArmorSlotType.HELMET);
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

  /** called to clear the cache on ingredient reload as materials may have changed */
  public static void clearSlimeskullCache() {
    SLIMESKULL_HELMETS = null;
  }

  /** gets the list of slimeskull helmets, loading it if needed */
  private static List<ItemStack> getSlimeskullHelmets() {
    if (SLIMESKULL_HELMETS == null) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      IModifiable slimeskull = TinkerTools.slimesuit.get(ArmorSlotType.HELMET);
      SLIMESKULL_HELMETS = registry.getAllMaterials().stream()
                                   .filter(material -> registry.getMaterialStats(material.getIdentifier(), SkullStats.ID).isPresent())
                                   .map(material -> ToolBuildHandler.buildItemFromMaterials(slimeskull, MaterialNBT.of(material)))
                                   .toList();
    }
    return SLIMESKULL_HELMETS;
  }
}
