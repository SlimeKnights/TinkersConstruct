package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Capability to make it easy for modifiers to store common data on the player, primarily used for armor
 */
public class EntityModifierDataCapability implements Capability.IStorage<ModDataNBT> {
  private EntityModifierDataCapability() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("modifier_data");
  /** Instance of the capability storage because forge requires it */
  private static final EntityModifierDataCapability INSTANCE = new EntityModifierDataCapability();
  /** Capability type */
  @CapabilityInject(ModDataNBT.class)
  public static Capability<ModDataNBT> CAPABILITY = null;

  /** Registers this capability */
  public static void register() {
    CapabilityManager.INSTANCE.register(ModDataNBT.class, INSTANCE, () -> ModDataNBT.readFromNBT(new CompoundNBT()));
    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, EntityModifierDataCapability::attachCapability);
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof LivingEntity) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }


  /* Required methods */

  @Nullable
  @Override
  public INBT writeNBT(Capability<ModDataNBT> capability, ModDataNBT instance, Direction side) {
    return null;
  }

  @Override
  public void readNBT(Capability<ModDataNBT> capability, ModDataNBT instance, Direction side, INBT nbt) {}

  /** Capability provider instance */
  private static class Provider implements ICapabilityProvider, Runnable {
    private LazyOptional<ModDataNBT> data;
    private Provider() {
      // TODO: do I want something that does not have slots? there is not much meaning of modifier slots on the player
      this.data = LazyOptional.of(() -> ModDataNBT.readFromNBT(new CompoundNBT()));
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CAPABILITY.orEmpty(cap, data);
    }

    @Override
    public void run() {
      // called when capabilities invalidate, create a new cap just in case they are revived later
      data.invalidate();
      data = LazyOptional.of(() -> ModDataNBT.readFromNBT(new CompoundNBT()));
    }
  }
}
