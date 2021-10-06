package slimeknights.tconstruct.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.EquipmentChangeWatcher.PlayerLastEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Capability to make it easy for modifiers to store common data on the player, primarily used for armor
 */
public class EquipmentChangeWatcher implements Capability.IStorage<PlayerLastEquipment> {
  private EquipmentChangeWatcher() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("equipment_watcher");
  /** Instance of the capability storage because forge requires it */
  private static final EquipmentChangeWatcher INSTANCE = new EquipmentChangeWatcher();
  /** Capability type */
  @CapabilityInject(PlayerLastEquipment.class)
  public static Capability<PlayerLastEquipment> CAPABILITY = null;

  /** Registers this capability */
  public static void register() {
    CapabilityManager.INSTANCE.register(PlayerLastEquipment.class, INSTANCE, () -> new PlayerLastEquipment(null));

    // equipment change is used on both sides
    MinecraftForge.EVENT_BUS.addListener(EquipmentChangeWatcher::onEquipmentChange);

    // only need to use the cap and the player tick on the client
    if (FMLEnvironment.dist == Dist.CLIENT) {
      MinecraftForge.EVENT_BUS.addListener(EquipmentChangeWatcher::onPlayerTick);
      MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, EquipmentChangeWatcher::attachCapability);
    }
  }


  /* Events */

  /** Serverside modifier hooks */
  private static void onEquipmentChange(LivingEquipmentChangeEvent event) {
    runModifierHooks(event.getEntityLiving(), event.getSlot(), event.getFrom(), event.getTo());
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if (entity.getEntityWorld().isRemote && entity instanceof PlayerEntity) {
      PlayerLastEquipment provider = new PlayerLastEquipment((PlayerEntity) entity);
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Client side modifier hooks */
  private static void onPlayerTick(PlayerTickEvent event) {
    // only run for client side players every 5 ticks
    if (event.phase == Phase.END && event.side == LogicalSide.CLIENT && event.player.ticksExisted % 5 == 0) {
      event.player.getCapability(CAPABILITY).ifPresent(PlayerLastEquipment::update);
    }
  }


  /* Helpers */

  /** Shared modifier hook logic */
  private static void runModifierHooks(LivingEntity entity, EquipmentSlotType changedSlot, ItemStack original, ItemStack replacement) {
    EquipmentChangeContext context = new EquipmentChangeContext(entity, changedSlot, original, replacement);

    // first, fire event to notify an item was removed
    IModifierToolStack tool = context.getOriginalTool();
    if (tool != null) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().onUnequip(tool, entry.getLevel(), context);
      }
    }

    // next, fire event to notify an item was added
    tool = context.getReplacementTool();
    if (tool != null) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().onEquip(tool, entry.getLevel(), context);
      }
    }

    // finally, fire events on all other slots to say something changed
    for (EquipmentSlotType otherSlot : EquipmentSlotType.values()) {
      if (otherSlot != changedSlot) {
        tool = context.getToolInSlot(otherSlot);
        if (tool != null) {
          for (ModifierEntry entry : tool.getModifierList()) {
            entry.getModifier().onEquipmentChange(tool, entry.getLevel(), context, otherSlot);
          }
        }
      }
    }
  }

  /* Required methods */

  @Nullable
  @Override
  public INBT writeNBT(Capability<PlayerLastEquipment> capability, PlayerLastEquipment instance, Direction side) {
    return null;
  }

  @Override
  public void readNBT(Capability<PlayerLastEquipment> capability, PlayerLastEquipment instance, Direction side, INBT nbt) {}

  /** Data class that runs actual update logic */
  protected static class PlayerLastEquipment implements ICapabilityProvider, Runnable {
    @Nullable
    private final PlayerEntity player;
    private final Map<EquipmentSlotType,ItemStack> lastItems = new EnumMap<>(EquipmentSlotType.class);
    private LazyOptional<PlayerLastEquipment> capability;

    private PlayerLastEquipment(@Nullable PlayerEntity player) {
      this.player = player;
      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
        lastItems.put(slot, ItemStack.EMPTY);
      }
      this.capability = LazyOptional.of(() -> this);
    }

    /** Called on player tick to update the stacks and run the event */
    public void update() {
      // run once a second, should be plenty fast enough
      if (player != null && player.ticksExisted % 20 == 0) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
          ItemStack newStack = player.getItemStackFromSlot(slot);
          ItemStack oldStack = lastItems.get(slot);
          if (!ItemStack.areItemStacksEqual(oldStack, newStack)) {
            lastItems.put(slot, newStack.copy());
            runModifierHooks(player, slot, oldStack, newStack);
          }
        }
      }
    }

    /** Called on capability invalidate to invalidate */
    @Override
    public void run() {
      capability.invalidate();
      capability = LazyOptional.of(() -> this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CAPABILITY.orEmpty(cap, capability);
    }
  }
}
