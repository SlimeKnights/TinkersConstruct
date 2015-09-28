package slimeknights.tconstruct.tools.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.block.ITinkerStationBlock;
import slimeknights.tconstruct.tools.client.module.GuiTinkerTabs;
import slimeknights.tconstruct.tools.network.TinkerStationTabPacket;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.tools.inventory.ContainerTinkerStation;

@SideOnly(Side.CLIENT)
// Takes care of the tinker station pseudo-multiblock
public class GuiTinkerStation extends GuiMultiModule {

  public static final ResourceLocation ICONS = Util.getResource("textures/gui/icons.png");

  public static final GuiElement ICON_Anvil = new GuiElement(18 * 3, 0, 18, 18, 256, 256);

  public static final GuiElement ICON_Pattern = new GuiElement(18 * 0, 18 * 12, 18, 18);
  public static final GuiElement ICON_Shard = new GuiElement(18 * 1, 18 * 12, 18, 18);
  public static final GuiElement ICON_Block = new GuiElement(18 * 2, 18 * 12, 18, 18);

  public static final GuiElement ICON_Pickaxe = new GuiElement(18 * 0, 18 * 13, 18, 18);
  public static final GuiElement ICON_Dust = new GuiElement(18 * 1, 18 * 13, 18, 18);
  public static final GuiElement ICON_Lapis = new GuiElement(18 * 2, 18 * 13, 18, 18);
  public static final GuiElement ICON_Ingot = new GuiElement(18 * 3, 18 * 13, 18, 18);
  public static final GuiElement ICON_Gem = new GuiElement(18 * 4, 18 * 13, 18, 18);
  public static final GuiElement ICON_Quartz = new GuiElement(18 * 5, 18 * 13, 18, 18);

  public static final GuiElement ICON_Button = new GuiElement(180, 216, 18, 18);
  public static final GuiElement ICON_ButtonHover = new GuiElement(180 + 18 * 2, 216, 18, 18);
  public static final GuiElement ICON_ButtonPressed = new GuiElement(180 - 18 * 2, 216, 18, 18);


  protected final ContainerMultiModule container;

  protected GuiTinkerTabs tinkerTabs;
  private final World world;

  public GuiTinkerStation(World world, BlockPos pos, ContainerTinkerStation container) {
    super(container);

    this.world = world;
    this.container = container;

    tinkerTabs = new GuiTinkerTabs(this, container);
    addModule(tinkerTabs);

    // add tab data
    if(container.hasCraftingStation) {
      for(Pair<BlockPos, IBlockState> pair : (List<Pair<BlockPos, IBlockState>>) container.tinkerStationBlocks) {
        IBlockState state = pair.getRight();
        BlockPos blockPos = pair.getLeft();
        ItemStack stack = state.getBlock().getDrops(world, blockPos, state, 0).get(0);
        tinkerTabs.addTab(stack, blockPos);
      }
    }

    // preselect the correct tab
    for(int i = 0; i < tinkerTabs.tabData.size(); i++) {
      if(tinkerTabs.tabData.get(i).equals(pos)) {
        tinkerTabs.tabs.selected = i;
      }
    }
  }

  protected void drawBackground(ResourceLocation background) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(background);
    this.drawTexturedModalRect(cornerX, cornerY, 0, 0, realWidth, realHeight);
  }

  protected void drawIcon(Slot slot, GuiElement element) {
    this.mc.getTextureManager().bindTexture(ICONS);
    element.draw(slot.xDisplayPosition + this.cornerX - 1, slot.yDisplayPosition + this.cornerY - 1);
  }

  protected void drawIconEmpty(Slot slot, GuiElement element) {
    if(slot.getHasStack())
      return;
    drawIcon(slot, element);
  }

  public void onTabSelection(int selection) {
    if(selection < 0 || selection > tinkerTabs.tabData.size()) {
      return;
    }

    BlockPos pos = tinkerTabs.tabData.get(selection);
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() instanceof ITinkerStationBlock) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof IInventoryGui) {
        TinkerNetwork.sendToServer(new TinkerStationTabPacket(pos));
      }

      // sound!
      mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }
  }

  public void error(String message) {}
  public void warning(String message) {}
  public void updateDisplay() {}
}
