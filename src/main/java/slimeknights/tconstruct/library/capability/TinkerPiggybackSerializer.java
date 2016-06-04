package slimeknights.tconstruct.library.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

public class TinkerPiggybackSerializer implements ICapabilitySerializable<NBTTagCompound> {

  private final EntityPlayer player;

  public TinkerPiggybackSerializer(@Nonnull EntityPlayer player) {
    this.player = player;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return player.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    return player.getCapability(capability, facing);
  }

  @Override
  public NBTTagCompound serializeNBT() {
    NBTTagCompound tagCompound = new NBTTagCompound();
    NBTTagList riderList = new NBTTagList();
    // save riders
    for(Entity entity : player.getRecursivePassengers()) {
      NBTTagCompound entityTag = new NBTTagCompound();
      NBTTagCompound entityDataTag = new NBTTagCompound();
      entity.writeToNBT(entityDataTag);
      entityDataTag.setString("id", EntityList.getEntityString(entity));
      entityTag.setUniqueId("Attach", entity.getRidingEntity().getUniqueID());
      entityTag.setTag("Entity", entityDataTag);
      riderList.appendTag(entityTag);
    }

    tagCompound.setTag("riders", riderList);
    if(riderList.hasNoTags()) {
      return new NBTTagCompound();
    }
    return tagCompound;
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    NBTTagList riderList = nbt.getTagList("riders", 10);

    Map<UUID, Entity> attachedTo = Maps.newHashMap();
    for(int i = 0; i < riderList.tagCount(); i++) {
      NBTTagCompound entityTag = riderList.getCompoundTagAt(i);
      Entity entity = AnvilChunkLoader.readWorldEntity(entityTag.getCompoundTag("Entity"), player.worldObj, true);
      if(entity != null) {
        UUID uuid = entityTag.getUniqueId("Attach");

        attachedTo.put(uuid, entity);
      }
    }
/*
    List<Entity> entities = Lists.newArrayList();
    entities.addAll(attachedTo.values());
    entities.add(player);

    // now that all entities are created, attach them to each other
    for(Entity entity : entities) {
      Entity rider = attachedTo.get(entity.getUniqueID());
      if(rider != null) {
        rider.startRiding(entity, true);
      }
    }*/
  }
}
