package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.materials.Material;

import java.util.List;

/**
 * All classes implementing this interface represent a item that has tinkers data. Usually also used for modifiers to
 * access the data.
 */
public interface ITinkerable {

  /** The default tooltip for the item */
  void getTooltip(ItemStack stack, List<String> tooltips);

  /** Detailed info about the tool. Displayed when Shift is held */
  void getTooltipDetailed(ItemStack stack, List<String> tooltips);

  /** What the tool is made out of. Displayed whet Ctrl is held */
  void getTooltipComponents(ItemStack stack, List<String> tooltips);
  
  //From TinkersItem
  public List<PartMaterialType> getRequiredComponents();
  //not sure why these both exist but I'll include both just in case
  public List<PartMaterialType> getToolBuildComponents();
  
  public ItemStack buildItemFromStacks(ItemStack[] stacks);
  
  public ItemStack buildItem(List<Material> materials);
  
  public NBTTagCompound buildItemNBT(List<Material> materials);
  
  public NBTTagCompound buildData(List<Material> materials);
  
  public ItemStack buildItemForRendering(List<Material> materials);
  
  public ItemStack buildItemForRenderingInGui();
  
  @SideOnly(Side.CLIENT)
  public Material getMaterialForPartForGuiRendering(int index);
  
  public NBTTagCompound buildTag(List<Material> materials);
  
  public void addMaterialTraits(NBTTagCompound root, List<Material> materials);
  
  public int[] getRepairParts();
  
  public float getRepairModifierForPart(int index);
}
