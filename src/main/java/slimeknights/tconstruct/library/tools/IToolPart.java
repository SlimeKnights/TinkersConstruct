package slimeknights.tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {

  /**
   * Returns the cost to craft the tool. Values match the ingot values<br>
   * 72 = 1 shard<br>
   * 144 = 1 ingot<br>
   * etc.<br>
   * Check the Material class for values
   */
  int getCost();

  /**
   * Retruns true if the material can be used for this toolpart
   */
  boolean canUseMaterial(Material mat);

  /**
   * Workaround for dual-materials like crossbow-bolts.
   * E.g. Obsidian is not an "acceptable" material because those are only shaft materials
   * but we still need to generate the texture for it.
   */
  default boolean canUseMaterialForRendering(Material mat) {
    return canUseMaterial(mat);
  }

  boolean hasUseForStat(String stat);

  /** Return true if the toolpart should be registered for crafting in the stencil table, with a pattern */
  default boolean canBeCrafted() {
    return true;
  }

  /** Return true if the toolpart should be registered for casting, using a cast */
  default boolean canBeCasted() {
    return true;
  }

  @SideOnly(Side.CLIENT)
  default ItemStack getOutlineRenderStack() {
    return getItemstackWithMaterial(CustomTextureCreator.guiMaterial);
  }
}
