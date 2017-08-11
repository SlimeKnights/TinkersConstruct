package slimeknights.tconstruct.gadgets.tileentity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

@Mod.EventBusSubscriber(modid = TConstruct.modID)
public class TileBouncePad extends TileSlimeChannel {
  public static class BounceCap {
    private boolean flag;
    
    public void set(boolean flag) { this.flag = flag; }
    
    public boolean get() { return flag; }
    
    public static class Storage implements Capability.IStorage<BounceCap> {
      protected Storage() {}
      
      protected static final Storage INSTANCE = new Storage();
      
      @Override
      public NBTTagByte writeNBT(Capability<BounceCap> capability, BounceCap instance, EnumFacing side) {
        return new NBTTagByte((byte)(instance.flag ? 1 : 0));
      }

      @Override
      public void readNBT(Capability<BounceCap> capability, BounceCap instance, EnumFacing side, NBTBase nbt) {
        instance.flag = nbt instanceof NBTTagByte && ((NBTTagByte)nbt).getByte() != 0;
      }
      
    }
    
    public static class Provider implements ICapabilitySerializable<NBTTagByte> {
      private final BounceCap cap = new BounceCap();

      @Override
      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == bounce_cap;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == bounce_cap) {
          return (T)cap;
        }
        return null;
      }

      @Override
      public NBTTagByte serializeNBT() {
        return Storage.INSTANCE.writeNBT(bounce_cap, cap, null);
      }

      @Override
      public void deserializeNBT(NBTTagByte nbt) {
        Storage.INSTANCE.readNBT(bounce_cap, cap, null, nbt);
      }
      
    }
  }
  
  static {
    CapabilityManager.INSTANCE.register(BounceCap.class, BounceCap.Storage.INSTANCE, BounceCap::new);
  }
  
  @CapabilityInject(BounceCap.class)
  public static final Capability<BounceCap> bounce_cap = null;
  
  public void addToBounce(EntityLivingBase elb) {
    if (elb.hasCapability(bounce_cap, null)) {
      elb.getCapability(bounce_cap, null).set(true);
    }
  }
  
  @SubscribeEvent
  public static void attachCap(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof EntityLivingBase) {
      event.addCapability(Util.getResource("bounce_cap"), new BounceCap.Provider());
    }
  }
  
  @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
  public static void onLivingFall(LivingFallEvent event) {
    if (event.getEntityLiving().hasCapability(bounce_cap, null)) {
      final BounceCap cap = event.getEntityLiving().getCapability(bounce_cap, null);
      if (cap.get()) {
        event.setCanceled(true);
        cap.set(false);
      }
    }
  }
}
