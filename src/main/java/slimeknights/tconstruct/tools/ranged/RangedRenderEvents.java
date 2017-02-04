package slimeknights.tconstruct.tools.ranged;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;


@Mod.EventBusSubscriber(Side.CLIENT)
public class RangedRenderEvents {

  @SubscribeEvent
  public static void onRenderPlayer(RenderLivingEvent.Pre<EntityPlayer> event) {
    if(!(event.getEntity() instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer player = (EntityPlayer) event.getEntity();

    EnumHand right = EnumHand.MAIN_HAND;
    EnumHand left = EnumHand.OFF_HAND;

    if(player instanceof EntityPlayerSP && player.getPrimaryHand() == EnumHandSide.LEFT) {
      right = EnumHand.OFF_HAND;
      left = EnumHand.MAIN_HAND;
    }

    if (event.getRenderer().getMainModel() instanceof ModelBiped) {
      if(isCarryingLoadedCrossbow(player, right)) {
        ((ModelBiped) event.getRenderer().getMainModel()).rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
      }
      else if(isCarryingLoadedCrossbow(player, left)) {
        ((ModelBiped) event.getRenderer().getMainModel()).leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
      }
    }
  }

  private static boolean isCarryingLoadedCrossbow(EntityPlayer entityPlayer, final EnumHand hand) {
    return Optional.ofNullable(entityPlayer)
        .map(player -> player.getHeldItem(hand))
        .filter(stack -> stack.getItem() == TinkerRangedWeapons.crossBow)
        .map(stack -> TinkerRangedWeapons.crossBow.isLoaded(stack))
        .orElse(false);
  }
}
