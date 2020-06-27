package slimeknights.tconstruct.tables.client.inventory.table;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.ToolPartItem;
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
    this.drawIconEmpty(this.container.getSlot(1), Icons.PATTERN);
    this.drawIconEmpty(this.container.getSlot(2), Icons.INGOT);

    this.minecraft.getTextureManager().bindTexture(BACKGROUND);

    this.blit(this.cornerX + 126, this.cornerY + 15 + (int) (41.0F * this.sliderProgress), 176 + (this.canScroll() ? 0 : 12), 0, 12, 15);
    this.drawRecipesBackground(mouseX, mouseY, this.cornerX + 51, this.cornerY + 15, this.recipeIndexOffset + 12);
    this.drawRecipesItems(this.cornerX + 51, this.cornerY + 15, this.recipeIndexOffset + 12);

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
      if (output.getItem() instanceof ToolPartItem) {
        ToolPartItem toolPartItem = (ToolPartItem) output.getItem();
        IMaterial material = MaterialItem.getMaterialFromStack(output);

        if(!toolPartItem.canUseMaterial(material)) {
          String materialName = material.getEncodedTextColor() + new TranslationTextComponent(material.getTranslationKey()).getFormattedText() + TextFormatting.WHITE;
          String error = new TranslationTextComponent("gui.tconstruct.error.useless_tool_part", materialName, (new ItemStack(toolPartItem)).getDisplayName()).getFormattedText();
          this.warning(error);
        }
        // Material is OK, display material properties
        else {
          this.setDisplayForMaterial(material);
        }
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

    List<ITextComponent> stats = Lists.newLinkedList();
    List<ITextComponent> tips = Lists.newArrayList();

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
      List<ITextComponent> info = stat.getLocalizedInfo();

      if (info != null && !info.isEmpty()) {
        stats.add(stat.getLocalizedName().applyTextStyle(TextFormatting.UNDERLINE));
        stats.addAll(info);
        stats.add(new StringTextComponent(""));
        tips.add(new StringTextComponent(""));
        tips.addAll(stat.getLocalizedDescriptions());
        tips.add(new StringTextComponent(""));
      }
    }

    if (!stats.isEmpty() && stats.get(stats.size() - 1).getFormattedText().isEmpty()) {
      // last empty line
      stats.remove(stats.size() - 1);
      tips.remove(tips.size() - 1);
    }

    this.infoPanelScreen.setText(stats, tips);
  }

  private void drawRecipesBackground(int mouseX, int mouseY, int left, int top, int recipeIndexOffsetMax) {
    for (int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.getPartRecipeListSize(); ++i) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * 18;
      int y = top + (relative / 4) * 18;
      int u = this.ySize;
      if (i == this.container.getSelectedPartRecipe()) {
        u += 18;
      } else if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
        u += 36;
      }
      this.blit(x, y, 0, u, 18, 18);
    }
  }

  private void drawRecipesItems(int left, int top, int recipeIndexOffsetMax) {
    List<PartRecipe> list = this.container.getPartRecipeList();

    for (int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.getPartRecipeListSize(); ++i) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * 18 + 1;
      int y = top + (relative / 4) * 18 + 1;

      PartRecipe recipe = list.get(i);
      ResourceLocation pattern = recipe.getPattern();
      try {
        // render the GUI model for the pattern
        ItemRenderer renderer = this.minecraft.getItemRenderer();
        renderer.zLevel += 50.0F;
        IBakedModel model = this.minecraft.getModelManager().getModel(new ResourceLocation(pattern.getNamespace(), "gui/part/" + pattern.getPath()));
        renderer.renderItemModelIntoGUI(recipe.getRecipeOutput(), x, y, model);
      } catch (Throwable throwable) {
        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item model");
        CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
        crashreportcategory.addDetail("Item Model", () -> String.valueOf(pattern));
        throw new ReportedException(crashreport);
      }
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    this.clickedOnScrollBar = false;

    if (this.infoPanelScreen.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    if (this.hasPatternInPatternSlot) {
      int x = this.cornerX + 51;
      int y = this.cornerY + 15;
      int maxIndex = Math.min((this.recipeIndexOffset + 12), this.container.getPartRecipeListSize());
      for (int l = this.recipeIndexOffset; l < maxIndex; ++l) {
        int relative = l - this.recipeIndexOffset;
        double buttonX = mouseX - (double) (x + relative % 4 * 18);
        double buttonY = mouseY - (double) (y + relative / 4 * 18);

        if (buttonX >= 0.0D && buttonY >= 0.0D && buttonX < 18.0D && buttonY < 18.0D && this.container.enchantItem(this.minecraft.player, l)) {
          Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
          this.minecraft.playerController.sendEnchantPacket((this.container).windowId, l);
          return true;
        }
      }
      // scrollbar position
      x = this.cornerX + 126;
      y = this.cornerY + 15;
      if (mouseX >= x && mouseX < (x + 12) && mouseY >= y && mouseY < (y + 54)) {
        this.clickedOnScrollBar = true;
      }
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unknown) {
    if (this.infoPanelScreen.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    if (this.clickedOnScrollBar && this.canScroll()) {
      int i = this.cornerY + 14;
      int j = i + 54;
      this.sliderProgress = ((float) mouseY - i - 7.5F) / ((float) (j - i) - 15.0F);
      this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((this.sliderProgress * this.getHiddenRows()) + 0.5D) * 4;
      return true;
    } else {
      return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unknown);
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (this.infoPanelScreen.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    if (this.canScroll()) {
      int i = this.getHiddenRows();
      this.sliderProgress = (float) ((double) this.sliderProgress - delta / (double) i);
      this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((double) (this.sliderProgress * (float) i) + 0.5D) * 4;
    }

    return true;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.infoPanelScreen.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    return super.mouseReleased(mouseX, mouseY, state);
  }

  private boolean canScroll() {
    return this.hasPatternInPatternSlot && this.container.getPartRecipeListSize() > 12;
  }

  protected int getHiddenRows() {
    return (this.container.getPartRecipeListSize() + 4 - 1) / 4 - 3;
  }
}
