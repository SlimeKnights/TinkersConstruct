package slimeknights.tconstruct.common;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.particle.SlimeFxParticle;
import slimeknights.tconstruct.shared.block.SlimeBlock;

// TODO: determine if we still need this
public class ClientProxy extends ServerProxy {

  public static final ResourceLocation BOOK_MODIFY = Util.getResource("textures/screen/book/modify.png");

  private static final Minecraft mc = Minecraft.getInstance();

  @Override
  public void spawnSlimeParticle(World world, double x, double y, double z) {
    mc.particles.addEffect(new SlimeFxParticle(world, x, y, z, new ItemStack(FoodItems.slime_ball.get(SlimeBlock.SlimeType.BLUE))));
  }
}
