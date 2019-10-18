package slimeknights.tconstruct.common;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.particle.SlimeFxParticle;
import slimeknights.tconstruct.items.CommonItems;

public class ClientProxy extends ServerProxy {

  public static final ResourceLocation BOOK_MODIFY = Util.getResource("textures/screen/book/modify.png");

  private static final Minecraft mc = Minecraft.getInstance();

  @Override
  public void spawnSlimeParticle(World world, double x, double y, double z) {
    mc.particles.addEffect(new SlimeFxParticle(world, x, y, z, new ItemStack(CommonItems.blue_slime_ball)));
  }
}
