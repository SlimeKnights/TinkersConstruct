package slimeknights.tconstruct.library.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;

import org.lwjgl.util.Point;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialGUI;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;

public class ToolBuildGuiInfo {

  public static final Material RenderMaterials[];

  public final ItemStack tool;
  // the positions where the slots are located
  public final List<Point> positions = Lists.newArrayList();

  public ToolBuildGuiInfo() {
    // for repairing
    this.tool = null;
  }

  public ToolBuildGuiInfo(TinkersItem tool) {
    List<Material> mats = Lists.newLinkedList();
    for(int i = 0; i < tool.getRequiredComponents().size(); i++) {
      mats.add(RenderMaterials[i % RenderMaterials.length]);
    }

    this.tool = tool.buildItemForRendering(mats);
  }

  public static ToolBuildGuiInfo default3Part(TinkersItem tool) {
    ToolBuildGuiInfo info = new ToolBuildGuiInfo(tool);
    info.addSlotPosition(33 - 20, 42 + 20);
    info.addSlotPosition(33 + 20, 42 - 20);
    info.addSlotPosition(33, 42);
    return info;
  }

  /**
   * Add another slot at the specified position for the tool.
   * The positions are usually located between:
   * X: 7 - 69
   * Y: 18 - 64
   */
  public void addSlotPosition(int x, int y) {
    positions.add(new Point(x, y));
  }

  static {
    RenderMaterials = new Material[4];
    RenderMaterials[0] = new MaterialGUI("_internal_render1");
    RenderMaterials[0].setRenderInfo(0x684e1e);
    RenderMaterials[1] = new MaterialGUI("_internal_render2");
    RenderMaterials[1].setRenderInfo(0xc1c1c1);
    RenderMaterials[2] = new MaterialGUI("_internal_render3");
    RenderMaterials[2].setRenderInfo(0x2376dd);
    RenderMaterials[3] = new MaterialGUI("_internal_render4");
    RenderMaterials[3].setRenderInfo(0x7146b0);

    for(Material mat : RenderMaterials) {
      // yes, these will only be registered clientside
      // but it shouldn't matter because they're never used serverside and we don't use indices
      TinkerRegistry.addMaterial(mat);
    }

    TinkerRegistry.tabTools.setDisplayIcon(TinkerHarvestTools.pickaxe.buildItemForRendering(ImmutableList.of(RenderMaterials[0], RenderMaterials[1], RenderMaterials[2])));
    TinkerRegistry.tabParts.setDisplayIcon(TinkerTools.pickHead.getItemstackWithMaterial(RenderMaterials[2]));
  }
}
