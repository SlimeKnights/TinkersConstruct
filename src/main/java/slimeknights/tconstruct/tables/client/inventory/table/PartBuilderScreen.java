package slimeknights.tconstruct.tables.client.inventory.table;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tables.recipe.part.PartRecipe;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import java.util.List;

public class PartBuilderScreen extends TinkerStationScreen<PartBuilderTileEntity, PartBuilderContainer> {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  protected PartInfoPanelScreen infoPanelScreen;

  private float sliderProgress = 0.0F;

  /**
   * Is {@code true} if the player clicked on the scroll wheel in the GUI.
   */
  private boolean clickedOnScrollBar;

  /**
   * The index of the first recipe to display.
   * The number of recipes displayed at any time is 12 (4 recipes per row, and 3 rows). If the player scrolled down one
   * row, this value would be 4 (representing the index of the first slot on the second row).
   */
  private int recipeIndexOffset = 0;
  private boolean hasPatternInPatternSlot;

  public PartBuilderScreen(PartBuilderContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.infoPanelScreen = new PartInfoPanelScreen(this, container, playerInventory, title);
    this.infoPanelScreen.ySize = this.ySize;
    this.addModule(this.infoPanelScreen);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(BACKGROUND);

    // draw slot icons
    this.drawIconEmpty(this.container.getSlot(1), Icons.PATTERN_ICON);
    this.drawIconEmpty(this.container.getSlot(2), Icons.INGOT_ICON);

    this.minecraft.getTextureManager().bindTexture(BACKGROUND);

    this.blit(this.cornerX + 119, this.cornerY + 15 + (int) (41.0F * this.sliderProgress), 176 + (this.canScroll() ? 0 : 12), 0, 12, 15);
    this.drawRecipesBackground(mouseX, mouseY, this.cornerX + 52, this.cornerY + 14, this.recipeIndexOffset + 12);
    this.drawRecipesItems(this.cornerX + 52, this.cornerY + 14, this.recipeIndexOffset + 12);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  @Override
  public void updateDisplay() {
    this.hasPatternInPatternSlot = this.container.hasPatternInPatternSlot();

    if (!this.hasPatternInPatternSlot) {
      this.sliderProgress = 0.0F;
      this.recipeIndexOffset = 0;
    }

    ItemStack output = this.container.getSlot(0).getStack();

    if (!(this.container.getPartRecipeList().isEmpty()) && this.container.getSelectedPartRecipe() != -1) {
      PartRecipe partRecipe = this.container.getPartRecipeList().get(this.container.getSelectedPartRecipe());
      this.infoPanelScreen.setPatternCost(new TranslationTextComponent("gui.tconstruct.part_builder.cost", partRecipe.getCost()).getFormattedText());
    } else {
      this.infoPanelScreen.setPatternCost("");
    }

    if (!output.isEmpty()) {
      if (output.getItem() instanceof MaterialItem) {
        MaterialItem materialItem = (MaterialItem) output.getItem();
        IMaterial material = MaterialItem.getMaterialFromStack(output);

        //todo fix
        /*if(!materialItem.canUseMaterial(material)) {
          String materialName = material.getLocalizedNameColored() + TextFormatting.WHITE;
          String error = I18n.translateToLocalFormatted("gui.tconstruct.error.useless_tool_part", materialName, (new ItemStack(toolPart)).getDisplayName());
          warning(error);
        }
        // Material is OK, display material properties
        else {
          this.setDisplayForMaterial(material);
        }*/

        this.setDisplayForMaterial(material);
      }
    } else {
      MaterialRecipe materialRecipe = this.container.getMaterialRecipe();

      if (materialRecipe != null) {
        IMaterial material = materialRecipe.getMaterial();

        this.setDisplayForMaterial(material);
      } else {
        this.infoPanelScreen.setCaption(this.getTitle().getFormattedText());
        this.infoPanelScreen.setText(new TranslationTextComponent("gui.tconstruct.part_builder.info").getFormattedText());
        this.infoPanelScreen.setMaterialValue("");
      }
    }
  }

  @Override
  public void error(String message) {
    this.infoPanelScreen.setCaption(new TranslationTextComponent("gui.tconstruct.error").getFormattedText());
    this.infoPanelScreen.setText(message);
  }

  @Override
  public void warning(String message) {
    this.infoPanelScreen.setCaption(new TranslationTextComponent("gui.tconstruct.warning").getFormattedText());
    this.infoPanelScreen.setText(message);
  }

  protected void setDisplayForMaterial(IMaterial material) {
    this.infoPanelScreen.setCaption(material.getEncodedTextColor() + new TranslationTextComponent(material.getTranslationKey()).getFormattedText());

    List<String> stats = Lists.newLinkedList();
    List<String> tips = Lists.newArrayList();

    MaterialRecipe materialRecipe = this.container.getMaterialRecipe();

    if (materialRecipe != null) {
      int totalValue = this.container.getSlot(2).getStack().getCount() * materialRecipe.getValue();
      float needed = totalValue / (float) materialRecipe.getNeeded();
      String amount = Util.df.format(needed);

      if (!(this.container.getPartRecipeList().isEmpty()) && this.container.getSelectedPartRecipe() != -1) {
        PartRecipe partRecipe = this.container.getPartRecipeList().get(this.container.getSelectedPartRecipe());

        if (needed < partRecipe.getCost()) {
          amount = TextFormatting.DARK_RED + amount + TextFormatting.RESET;
        }
      }

      this.infoPanelScreen.setMaterialValue(new TranslationTextComponent("gui.tconstruct.part_builder.material_value", amount).getFormattedText());
    }

    for (IMaterialStats stat : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
      stats.add(stat.getIdentifier().toString());
      /*List<String> info = stat.getLocalizedInfo();

      if(!info.isEmpty()) {
        stats.add(TextFormatting.UNDERLINE + stat.getLocalizedName());
        stats.addAll(info);
        stats.add(null);
        tips.add(null);
        tips.addAll(stat.getLocalizedDesc());
        tips.add(null);
      }*/
    }

    if (!stats.isEmpty() && stats.get(stats.size() - 1) == null) {
      // last empty line
      stats.remove(stats.size() - 1);
      tips.remove(tips.size() - 1);
    }

    this.infoPanelScreen.setText(stats, tips);
  }

  private void drawRecipesBackground(int mouseX, int mouseY, int left, int top, int recipeIndexOffsetMax) {
    for (int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.getPartRecipeListSize(); ++i) {
      int j = i - this.recipeIndexOffset;
      int k = left + j % 4 * 16;
      int l = j / 4;
      int i1 = top + l * 18 + 2;
      int j1 = this.ySize;
      if (i == this.container.getSelectedPartRecipe()) {
        j1 += 18;
      } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
        j1 += 36;
      }

      this.blit(k, i1 - 1, 0, j1, 16, 18);
    }
  }

  private void drawRecipesItems(int left, int top, int recipeIndexOffsetMax) {
    List<PartRecipe> list = this.container.getPartRecipeList();

    for (int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.getPartRecipeListSize(); ++i) {
      int j = i - this.recipeIndexOffset;
      int k = left + j % 4 * 16;
      int l = j / 4;
      int i1 = top + l * 18 + 2;
      this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(list.get(i).getRecipeOutput(), k, i1);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    this.clickedOnScrollBar = false;

    if (this.hasPatternInPatternSlot) {
      int i = this.cornerX + 52;
      int j = this.cornerY + 14;
      int k = this.recipeIndexOffset + 12;

      for (int l = this.recipeIndexOffset; l < k; ++l) {
        int i1 = l - this.recipeIndexOffset;
        double d0 = mouseX - (double) (i + i1 % 4 * 16);
        double d1 = mouseY - (double) (j + i1 / 4 * 18);

        if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.container.enchantItem(this.minecraft.player, l)) {
          Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
          this.minecraft.playerController.sendEnchantPacket((this.container).windowId, l);
          return true;
        }
      }

      i = this.cornerX + 119;
      j = this.cornerY + 9;

      if (mouseX >= (double) i && mouseX < (double) (i + 12) && mouseY >= (double) j && mouseY < (double) (j + 54)) {
        this.clickedOnScrollBar = true;
      }
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unknown) {
    if (this.clickedOnScrollBar && this.canScroll()) {
      int i = this.cornerY + 14;
      int j = i + 54;
      this.sliderProgress = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
      this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((double) (this.sliderProgress * (float) this.getHiddenRows()) + 0.5D) * 4;
      return true;
    } else {
      return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unknown);
    }
  }

  @Override
  public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
    if (this.canScroll()) {
      int i = this.getHiddenRows();
      this.sliderProgress = (float) ((double) this.sliderProgress - p_mouseScrolled_5_ / (double) i);
      this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((double) (this.sliderProgress * (float) i) + 0.5D) * 4;
    }

    return true;
  }

  private boolean canScroll() {
    return this.hasPatternInPatternSlot && this.container.getPartRecipeListSize() > 12;
  }

  protected int getHiddenRows() {
    return (this.container.getPartRecipeListSize() + 4 - 1) / 4 - 3;
  }
}
