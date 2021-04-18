package slimeknights.tconstruct.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.FlintShurikenEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.QuartzShurikenEntity;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

  @Shadow
  private ClientWorld world;

  @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
  private void onCustomEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z) {
    EntityType<?> entityType = packet.getEntityTypeId();

    ThrownItemEntity entity = null;

    if (entityType == TinkerGadgets.eflnEntity) {
      entity = new EflnBallEntity(this.world, x, y, z);
      entity.setItem(new ItemStack(TinkerGadgets.efln.asItem()));
    }

    if (entityType == TinkerGadgets.flintShurikenEntity) {
      entity = new FlintShurikenEntity(this.world, x, y, z);
    }

    if (entityType == TinkerGadgets.quartzShurikenEntity) {
      entity = new QuartzShurikenEntity(this.world, x, y, z);
    }

    if(entity != null) {
      int packetId = packet.getId();
      entity.updateTrackedPosition(x, y, z);
      entity.refreshPositionAfterTeleport(x, y, z);
      entity.pitch = (float) (packet.getPitch() * 360) / 256.0F;
      entity.yaw = (float) (packet.getYaw() * 360) / 256.0F;
      entity.setEntityId(packetId);
      entity.setUuid(packet.getUuid());
      this.world.addEntity(packetId, entity);
    }
  }
}
