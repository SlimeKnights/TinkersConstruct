package slimeknights.tconstruct.weapons.ranged.item;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.tools.TinkerTools;

public class Arrow extends ProjectileCore {

  public Arrow() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.knifeBlade),
          PartMaterialType.extra(TinkerTools.binding));
  }

  @Override
  public float damagePotential() {
    return 0.1f;
  }

  @Override
  public double attackSpeed() {
    return 1;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT toolNBT = new ToolNBT();
    return toolNBT.get();
  }
}
