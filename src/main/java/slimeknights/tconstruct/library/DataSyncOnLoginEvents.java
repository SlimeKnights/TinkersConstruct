package slimeknights.tconstruct.library;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.network.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.network.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataSyncOnLoginEvents {

  @SubscribeEvent
  public static void onLogin(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent) {
    Collection<IMaterial> allMaterials = MaterialRegistry.getMaterials();

    TinkerNetwork.instance.network.send(PacketDistributor.ALL.noArg(), new UpdateMaterialsPacket(allMaterials));

    Map<MaterialId, Collection<BaseMaterialStats>> materialStats = allMaterials.stream()
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        material -> MaterialRegistry.getAllStats(material.getIdentifier())
      ));

    TinkerNetwork.instance.network.send(PacketDistributor.ALL.noArg(), new UpdateMaterialStatsPacket(materialStats));
  }
}
