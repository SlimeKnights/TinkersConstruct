package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySlimeFx extends EntityBreakingFX {

  public EntitySlimeFx(World worldIn, double posXIn, double posYIn, double posZIn, Item item, int meta) {
    super(worldIn, posXIn, posYIn, posZIn, item, meta);
  }

  public EntitySlimeFx(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, Item item, int meta) {
    super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, item, meta);
  }
}
