package tconstruct.library.tinkering.modifiers;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModifier {

  String getIdentifier();
  // todo: expand

  @SideOnly(Side.CLIENT)
  boolean hasTexturePerMaterial();
}
