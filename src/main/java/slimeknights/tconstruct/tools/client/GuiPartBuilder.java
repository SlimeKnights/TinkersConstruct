package slimeknights.tconstruct.tools.client;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.client.module.GuiButtonsPartCrafter;
import slimeknights.tconstruct.tools.client.module.GuiInfoPanel;
import slimeknights.tconstruct.tools.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.inventory.ContainerPartBuilder;
import slimeknights.tconstruct.tools.inventory.ContainerPatternChest;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;
import slimeknights.tconstruct.tools.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.tileentity.TilePartBuilder;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  public static final int Column_Count = 4;

  protected GuiButtonsPartCrafter buttons;
  protected GuiInfoPanel info;
  protected GuiSideInventory sideInventory;
  protected ContainerSideInventory chestContainer;

  public GuiPartBuilder(InventoryPlayer playerInv, World world, BlockPos pos, TilePartBuilder tile) {
    super(world, pos, (ContainerTinkerStation) tile.createContainer(playerInv, world, pos));

    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;

      // has part crafter buttons?
      if(container.isPartCrafter()) {
        buttons = new GuiButtonsPartCrafter(this, container, container.patternChest);
        this.addModule(buttons);
      }
      else {
        // has pattern chest inventory?
        chestContainer = container.getSubContainer(ContainerPatternChest.DynamicChestInventory.class);
        if(chestContainer != null) {
          sideInventory = new GuiSideInventory(this, chestContainer, chestContainer.getSlotCount(), chestContainer.columns);
          this.addModule(sideInventory);
        }
      }

      info = new GuiInfoPanel(this, container);
      info.ySize = 150;
      info.yOffset += 5;
      this.addModule(info);
    }
  }

  @Override
  public void drawSlot(Slot slotIn) {
    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
      if(container.isPartCrafter() && slotIn.inventory == container.patternChest)
        return;
    }

    super.drawSlot(slotIn);
  }

  @Override
  public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
      if(container.isPartCrafter() && slotIn.inventory == container.patternChest)
        return false;
    }
    return super.isMouseOverSlot(slotIn, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    if(sideInventory != null) {
      sideInventory.updateSlotCount(chestContainer.getSizeInventory());
    }

    // draw slot icons
    drawIconEmpty(container.getSlot(1), ICON_Shard);
    drawIconEmpty(container.getSlot(2), ICON_Pattern);
    drawIconEmpty(container.getSlot(3), ICON_Ingot);
    drawIconEmpty(container.getSlot(4), ICON_Block);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  @Override
  public void updateDisplay() {
    // check if we have an output
    ItemStack output = container.getSlot(0).getStack();
    if(output != null) {
      if(output.getItem() instanceof ToolPart) {
        ToolPart toolPart = (ToolPart) output.getItem();
        Material material = toolPart.getMaterial(output);
        // Material for the toolpart does not make sense, can't build anything out of it!
        if(!toolPart.canUseMaterial(material)) {
          String materialName = material.getLocalizedNameColored() + EnumChatFormatting.WHITE;
          String error = StatCollector.translateToLocalFormatted("gui.error.useless_tool_part", materialName, (new ItemStack(toolPart)).getDisplayName());
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
        info.setCaption(container.getInventoryDisplayName().getFormattedText());
        info.setText(StatCollector.translateToLocal("gui.partbuilder.info"));
      }
    }
  }

  @Override
  public void error(String message) {
    info.setCaption(StatCollector.translateToLocal("gui.error"));
    info.setText(message);
  }

  @Override
  public void warning(String message) {
    info.setCaption(StatCollector.translateToLocal("gui.warning"));
    info.setText(message);
  }

  public void updateButtons() {
    if(buttons != null) {
      // this needs to be done threadsafe, since the buttons may be getting rendered currently
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
        @Override
        public void run() {
          buttons.updatePosition(cornerX, cornerY, realWidth, realHeight);
        }
      });
    }
  }

  protected void setDisplayForMaterial(Material material) {
    info.setCaption(material.getLocalizedNameColored());

    List<String> stats = Lists.newLinkedList();
    List<String> tips = Lists.newArrayList();
    for(IMaterialStats stat : material.getAllStats()) {
      stats.addAll(stat.getLocalizedInfo());
      stats.add(null);
      tips.addAll(stat.getLocalizedDesc());
      tips.add(null);
    }

    // Traits
    for(ITrait trait : material.getAllTraits()) {
      if(!trait.isHidden()) {
        stats.add(material.getTextColor() + trait.getLocalizedName());
        tips.add(material.getTextColor() + trait.getLocalizedDesc());
      }
    }

    if(!stats.isEmpty() && stats.get(stats.size()-1) == null) {
      // last empty line
      stats.remove(stats.size()-1);
      tips.remove(tips.size()-1);
    }

    info.setText(stats, tips);
  }

  protected Material getMaterial(ItemStack... stacks) {
    for(ItemStack stack : stacks) {
      if(stack == null || stack.getItem() == null) {
        continue;
      }
      // material-item?
      if(stack.getItem() instanceof IMaterialItem) {
        return ((IMaterialItem) stack.getItem()).getMaterial(stack);
      }
    }

    // regular item, check if it belongs to a material
    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.matches(stacks) != null) {
        return material;
      }
    }

    // no material found
    return null;
  }

  private Material getMaterialItem(ItemStack stack) {

    return null;
  }
}
