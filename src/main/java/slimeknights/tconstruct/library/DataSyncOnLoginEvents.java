package slimeknights.tconstruct.library;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.network.UpdateMaterialsPacket;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataSyncOnLoginEvents {

  @SubscribeEvent
  public static void onLogin(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent) {
    TinkerNetwork.instance.network.send(PacketDistributor.ALL.noArg(), new UpdateMaterialsPacket(MaterialRegistry.getMaterials()));
  }
}
