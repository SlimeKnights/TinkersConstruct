package slimeknights.tconstruct.shared.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.events.teleport.ReturningTeleportEvent;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TeleportHelper;

public class ReturningEffect extends TinkerEffect {
  private static final ResourceLocation KEY = TConstruct.getResource("returning");
  public ReturningEffect() {
    super(MobEffectCategory.NEUTRAL, 0xa92dff, true);
    NeoForge.EVENT_BUS.addListener((MobEffectEvent.Added event) -> this.onEffectAdded(event));
  }

  /** Called to set the return position when the effect is added */
  private void onEffectAdded(MobEffectEvent.Added event) {
    // store entity's current position when the effect is added
    LivingEntity entity = event.getEntity();
    if (!entity.level().isClientSide() && event.getOldEffectInstance() == null && event.getEffectInstance().getEffect().value() == this) {
      ModDataNBT data = PersistentDataCapability.getOrWarn(entity);
      CompoundTag tag = new CompoundTag();
      tag.put("pos", NbtUtils.writeBlockPos(entity.blockPosition()));
      tag.putString("dimension", entity.level().dimension().location().toString());
      data.put(KEY, tag);
    }
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return duration == 1;
  }

  @Override
  public boolean applyEffectTick(LivingEntity living, int amplifier) {
    ModDataNBT data = PersistentDataCapability.getOrWarn(living);
    if (data.contains(KEY, Tag.TAG_COMPOUND)) {
      CompoundTag tag = data.getCompound(KEY);
      ResourceLocation dimension = ResourceLocation.tryParse(tag.getString("dimension"));
      // no teleporting if you switched dimensions
      // TODO: look into cross dimensional teleport, its doable with entity#teleportTo
      if (dimension != null && dimension.equals(living.level().dimension().location())) {
        NbtUtils.readBlockPos(tag, "pos").ifPresent(pos -> TeleportHelper.tryTeleport(new ReturningTeleportEvent(living, pos.getX(), pos.getY(), pos.getZ())));
      }
    }
    return true;
  }
}
