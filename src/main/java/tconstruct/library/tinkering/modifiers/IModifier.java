package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModifier {

  String getIdentifier();

  /** Returns true if the modifier can be applied to the given itemstack */
  boolean canApply(ItemStack stack);

  /** Apply the modifier to that itemstack */
  void apply(ItemStack stack);

  /** Used for specific modifiers that need a texture variant for each material */
  @SideOnly(Side.CLIENT)
  boolean hasTexturePerMaterial();
}
