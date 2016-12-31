package slimeknights.tconstruct.library.capability.piggyback;

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

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

// This capability and serializer takes care of:
// * Saving carried entities for SSP (otherwise they'd vanish)
// * Tell the player when stuff riding him stops (otherwise other players dismounting wouldn't get dismounted for the carrying player)
public class TinkerPiggybackSerializer implements ICapabilitySerializable<NBTTagCompound> {

  private final EntityPlayer player;
  private final ITinkerPiggyback piggyback;

  public TinkerPiggybackSerializer(@Nonnull EntityPlayer player) {
    this.player = player;
    piggyback = new TinkerPiggybackHandler();
    piggyback.setRiddenPlayer(player);
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return capability == CapabilityTinkerPiggyback.PIGGYBACK;
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if(capability == CapabilityTinkerPiggyback.PIGGYBACK) {
      return (T) piggyback;
    }
    return null;
  }

  @Override
  public NBTTagCompound serializeNBT() {
    NBTTagCompound tagCompound = new NBTTagCompound();
    NBTTagList riderList = new NBTTagList();
    // save riders
    for(Entity entity : player.getRecursivePassengers()) {
      String id = EntityList.getEntityString(entity);
      if(id != null && !"".equals(id)) {
        NBTTagCompound entityTag = new NBTTagCompound();
        NBTTagCompound entityDataTag = new NBTTagCompound();
        entity.writeToNBT(entityDataTag);
        entityDataTag.setString("id", EntityList.getEntityString(entity));
        entityTag.setUniqueId("Attach", entity.getRidingEntity().getUniqueID());
        entityTag.setTag("Entity", entityDataTag);
        riderList.appendTag(entityTag);
      }
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
      Entity entity = AnvilChunkLoader.readWorldEntity(entityTag.getCompoundTag("Entity"), player.getEntityWorld(), true);
      if(entity != null) {
        UUID uuid = entityTag.getUniqueId("Attach");

        attachedTo.put(uuid, entity);
      }
    }
  }
}
