package slimeknights.tconstruct.library.component.piggyback;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.fabricmc.fabric.api.util.NbtType;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.network.TinkerNetwork;

public class TinkerPiggybackComponent implements ITinkerPiggyback, ComponentV3 {
	private PlayerEntity riddenPlayer;
	private List<Entity> lastPassengers;

	public TinkerPiggybackComponent(@NotNull PlayerEntity player) {
		this.riddenPlayer = player;
		this.setRiddenPlayer(player);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		ListTag riderList = new ListTag();

		for (Entity entity : this.riddenPlayer.getPassengersDeep()) {
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

		if(!riderList.isEmpty()) tag.put("riders", riderList);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		ListTag riderList = tag.getList("riders", NbtType.COMPOUND);

		Map<UUID, Entity> attachedTo = Maps.newHashMap();

		if (this.riddenPlayer.getEntityWorld() instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) this.riddenPlayer.getEntityWorld();

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

	@Override
	public void setRiddenPlayer(PlayerEntity player) {
		this.riddenPlayer = player;
	}

	@Override
	public void updatePassengers() {
		if (this.riddenPlayer != null) {
			// tell the player itself if his riders changed serverside
			if (!this.riddenPlayer.getPassengerList().equals(this.lastPassengers)) {
				if (this.riddenPlayer instanceof ServerPlayerEntity) {
					TinkerNetwork.getInstance().sendVanillaPacket(this.riddenPlayer, new EntityPassengersSetS2CPacket(this.riddenPlayer));
				}
			}
			this.lastPassengers = this.riddenPlayer.getPassengerList();
		}
	}
}
