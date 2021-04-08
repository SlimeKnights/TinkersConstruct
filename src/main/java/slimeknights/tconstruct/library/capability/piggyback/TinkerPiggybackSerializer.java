package slimeknights.tconstruct.library.capability.piggyback;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.Nonnull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

public class TinkerPiggybackSerializer implements ICapabilitySerializable<CompoundTag> {

  private final PlayerEntity player;
  private final ITinkerPiggyback piggyback;
  private final LazyOptional<ITinkerPiggyback> providerCap;

  public TinkerPiggybackSerializer(@Nonnull PlayerEntity player) {
    this.player = player;
    this.piggyback = new TinkerPiggybackHandler();
    this.piggyback.setRiddenPlayer(player);
    this.providerCap = LazyOptional.of(() -> this.piggyback);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityTinkerPiggyback.PIGGYBACK) {
      return this.providerCap.cast();
    }
    return LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag compoundNBT = new CompoundTag();
    ListTag riderList = new ListTag();

    // save riders
    for (Entity entity : this.player.getPassengersDeep()) {
      String id = entity.getSavedEntityId();
      if (id != null && !"".equals(id)) {
        CompoundTag entityTag = new CompoundTag();
        CompoundTag entityDataTag = new CompoundTag();
        entity.toTag(entityDataTag);
        entityDataTag.putString("id", entity.getSavedEntityId());
        entityTag.putUuid("Attach", entity.getVehicle().getUuid());
        entityTag.put("Entity", entityDataTag);
        riderList.add(entityTag);
      }
    }

    compoundNBT.put("riders", riderList);

    if (riderList.isEmpty()) {
      return new CompoundTag();
    }

    return compoundNBT;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    ListTag riderList = nbt.getList("riders", 10);

    Map<UUID, Entity> attachedTo = Maps.newHashMap();

    if (this.player.getEntityWorld() instanceof ServerWorld) {
      ServerWorld serverWorld = (ServerWorld) this.player.getEntityWorld();

      for (int i = 0; i < riderList.size(); i++) {
        CompoundTag entityTag = riderList.getCompound(i);
        Entity entity = EntityType.loadEntityWithPassengers(entityTag.getCompound("Entity"), serverWorld, (p_217885_1_) -> {
          return !serverWorld.tryLoadEntity(p_217885_1_) ? null : p_217885_1_;
        });
        if (entity != null) {
          UUID uuid = entityTag.getUuid("Attach");

          attachedTo.put(uuid, entity);
        }
      }
    }
  }
}
