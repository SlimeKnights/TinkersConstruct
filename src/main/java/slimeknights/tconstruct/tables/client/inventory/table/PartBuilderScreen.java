package slimeknights.tconstruct.tables.client.inventory.table;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.client.inventory.module.PartCrafterButtonsScreen;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

public class PartBuilderScreen extends TinkerStationScreen<PartBuilderTileEntity, TinkerStationContainer<PartBuilderTileEntity>> {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  protected PartCrafterButtonsScreen partCrafterButtonsScreen;
  protected InfoPanelScreen infoPanelScreen;
  protected SideInventoryScreen sideInventoryScreen;
  protected PatternChestContainer.DynamicChestInventory dynamicChestInventory;

  public static final int COLUMN_COUNT = 4;

  public PartBuilderScreen(TinkerStationContainer<PartBuilderTileEntity> container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    if (this.container instanceof PartBuilderContainer) {
      PartBuilderContainer partBuilderContainer = (PartBuilderContainer) this.container;

      if (partBuilderContainer.isPartCrafter()) {
        ITextComponent inventoryName = title;

        if (partBuilderContainer.patternChest instanceof INamedContainerProvider) {
          inventoryName = ((INamedContainerProvider) partBuilderContainer.patternChest).getDisplayName();
        }

        this.partCrafterButtonsScreen = new PartCrafterButtonsScreen(this, container, playerInventory, inventoryName, partBuilderContainer.patternChest);
        this.addModule(this.partCrafterButtonsScreen);
      } else {
        this.dynamicChestInventory = container.getSubContainer(PatternChestContainer.DynamicChestInventory.class);

        if (this.dynamicChestInventory != null) {
          ITextComponent inventoryName = title;

          if (this.dynamicChestInventory.getTileEntity() != null) {
            inventoryName = ((INamedContainerProvider) this.dynamicChestInventory.getTileEntity()).getDisplayName();
          }

          this.sideInventoryScreen = new SideInventoryScreen(this, this.dynamicChestInventory, playerInventory, inventoryName, this.dynamicChestInventory.getSlotCount(), this.dynamicChestInventory.columns);
          this.addModule(this.sideInventoryScreen);
        }
      }

      this.infoPanelScreen = new InfoPanelScreen(this, container, playerInventory, title);
      this.infoPanelScreen.ySize = this.ySize;
      this.addModule(this.infoPanelScreen);
    }
  }

  @Override
  public ITextComponent getTitle() {
    if (this.container instanceof PartBuilderContainer) {
      PartBuilderContainer partBuilderContainer = (PartBuilderContainer) this.container;

      if (partBuilderContainer.isPartCrafter()) {
        return new TranslationTextComponent("gui.tconstruct.part_crafter");
      }
    }

    return super.getTitle();
  }

  @Override
  public void drawSlot(Slot slotIn) {
    if (this.container instanceof PartBuilderContainer) {
      PartBuilderContainer partBuilderContainer = (PartBuilderContainer) this.container;

      if (partBuilderContainer.isPartCrafter() && slotIn.inventory == partBuilderContainer.patternChest) {
        return;
      }
    }

    super.drawSlot(slotIn);
  }

  @Override
  public boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
    if (this.container instanceof PartBuilderContainer) {
      PartBuilderContainer partBuilderContainer = (PartBuilderContainer) this.container;

      if (partBuilderContainer.isPartCrafter() && slotIn.inventory == partBuilderContainer.patternChest) {
        return false;
      }
    }
    return super.isSlotSelected(slotIn, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(BACKGROUND);

    if (this.sideInventoryScreen != null) {
      this.sideInventoryScreen.updateSlotCount(dynamicChestInventory.getSizeInventory());
    }

    // draw slot icons
    this.drawIconEmpty(this.container.getSlot(1), Icons.ICON_Shard);
    this.drawIconEmpty(this.container.getSlot(2), Icons.ICON_Pattern);
    this.drawIconEmpty(this.container.getSlot(3), Icons.ICON_Ingot);
    this.drawIconEmpty(this.container.getSlot(4), Icons.ICON_Block);

    // draw material info
    String amount = null;
    /*
    TODO: FIX
    Material material = this.getMaterial(container.getSlot(3).getStack(), container.getSlot(4).getStack());

    if(material != null) {
      int count = 0;
      Optional<RecipeMatch.Match> matchOptional = material.matchesRecursively(ListUtil.getListFrom(container.getSlot(3).getStack(), container.getSlot(4).getStack()));
      if(matchOptional.isPresent()) {
        int matchAmount = matchOptional.get().amount;
        amount = Util.df.format(matchAmount / (float) MaterialValues.VALUE_Ingot);

        Item part = Pattern.getPartFromTag(container.getSlot(2).getStack());
        if(part instanceof IToolPart && matchAmount < ((IToolPart) part).getCost()) {
          amount = TextFormatting.DARK_RED + amount + TextFormatting.RESET;
        }
      }
    }

    if(amount != null) {
      int x = this.cornerX + this.realWidth / 2;
      int y = this.cornerY + 63;
      String text = Util.translateFormatted("gui.tconstruct.part_builder.material_value", amount, material.getLocalizedName());
      x -= this.font.getStringWidth(text) / 2;
      this.font.drawString(text, x, y, 0x777777);
    }*/

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  @Override
  public void updateDisplay() {
    /*
    TODO FIX
    // check if we have an output
    ItemStack output = container.getSlot(0).getStack();
    if(!output.isEmpty()) {
      if(output.getItem() instanceof ToolPart) {
        ToolPart toolPart = (ToolPart) output.getItem();
        Material material = toolPart.getMaterial(output);
        // Material for the toolpart does not make sense, can't build anything out of it!
        if(!toolPart.canUseMaterial(material)) {
          String materialName = material.getLocalizedNameColored() + TextFormatting.WHITE;
          String error = I18n.translateToLocalFormatted("gui.tconstruct.error.useless_tool_part", materialName, (new ItemStack(toolPart)).getDisplayName());
          warning(error);
        }
        // Material is OK, display material properties
        else {
          setDisplayForMaterial(material);
        }
      }
    }
    // no output, check input
    else {
      // is our input a material item?
      Material material = getMaterial(container.getSlot(3).getStack(), container.getSlot(4).getStack());
      if(material != null) {
        setDisplayForMaterial(material);
      }
      // no, display general usage information
      else {
        this.infoPanelScreen.setCaption(container.getTileEntity().getDisplayName().getFormattedText());
        this.infoPanelScreen.setText(new TranslationTextComponent("gui.tconstruct.part_builder.info").getFormattedText());
      }
    }*/

    this.infoPanelScreen.setCaption(this.getTitle().getFormattedText());
    this.infoPanelScreen.setText(new TranslationTextComponent("gui.tconstruct.part_builder.info").getFormattedText());
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

  public void updateButtons() {
    if (this.partCrafterButtonsScreen != null) {
      Minecraft.getInstance().execute(() -> this.partCrafterButtonsScreen.updatePosition(cornerX, cornerY, realWidth, realHeight));
    }
  }

  /*
  TODO FIX
  protected void setDisplayForMaterial(Material material) {
    infoPanelScreen.setCaption(material.getLocalizedNameColored());

    List<String> stats = Lists.newLinkedList();
    List<String> tips = Lists.newArrayList();
    for(IMaterialStats stat : material.getAllStats()) {
      List<String> info = stat.getLocalizedInfo();
      if(!info.isEmpty()) {
        stats.add(TextFormatting.UNDERLINE + stat.getLocalizedName());
        stats.addAll(info);
        stats.add(null);
        tips.add(null);
        tips.addAll(stat.getLocalizedDesc());
        tips.add(null);
      }
    }

    // Traits
    for(ITrait trait : material.getAllTraits()) {
      if(!trait.isHidden()) {
        stats.add(material.getTextColor() + trait.getLocalizedName());
        tips.add(material.getTextColor() + trait.getLocalizedDesc());
      }
    }

    if(!stats.isEmpty() && stats.get(stats.size() - 1) == null) {
      // last empty line
      stats.remove(stats.size() - 1);
      tips.remove(tips.size() - 1);
    }

    infoPanelScreen.setText(stats, tips);
  }

  protected Material getMaterial(ItemStack... stacks) {
    for(ItemStack stack : stacks) {
      if(stack.isEmpty()) {
        continue;
      }
      // material-item?
      if(stack.getItem() instanceof IMaterialItem) {
        return ((IMaterialItem) stack.getItem()).getMaterial(stack);
      }
    }

    // regular item, check if it belongs to a material
    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.matches(stacks).isPresent()) {
        return material;
      }
    }

    // no material found
    return null;
  }*/
}
