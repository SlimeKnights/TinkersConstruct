package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class PurpleSlimeSlingItem extends BaseSlimeSlingItem {

  public PurpleSlimeSlingItem(Properties props) {
    super(props);
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;

    int i = this.getUseDuration(stack) - timeLeft;
    float f = getForce(i);

    Vector3d look = player.getLookVec();
    double offX = look.x * f;
    double offY = look.y * f;
    double offZ = look.z * f;

    player.setPosition(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ);

    if (player instanceof ServerPlayerEntity) {
      ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
      TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
    }

    player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
  }
}
