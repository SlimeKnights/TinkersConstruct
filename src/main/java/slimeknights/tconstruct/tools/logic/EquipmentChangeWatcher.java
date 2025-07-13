package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Capability to make it easy for modifiers to store common data on the player, primarily used for armor
 */
public class EquipmentChangeWatcher {
  private EquipmentChangeWatcher() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("equipment_watcher");
  /** Capability type */
  public static final Capability<PlayerLastEquipment> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Registers this capability */
  public static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, event -> event.register(PlayerLastEquipment.class));

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
    runModifierHooks(event.getEntity(), event.getSlot(), event.getFrom(), event.getTo());
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if (entity.getCommandSenderWorld().isClientSide && entity instanceof Player) {
      PlayerLastEquipment provider = new PlayerLastEquipment((Player) entity);
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Client side modifier hooks */
  private static void onPlayerTick(PlayerTickEvent event) {
    // only run for client side players every 5 ticks
    if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
      event.player.getCapability(CAPABILITY).ifPresent(PlayerLastEquipment::update);
    }
  }


  /* Helpers */

  /** Shared modifier hook logic */
  private static void runModifierHooks(LivingEntity entity, EquipmentSlot changedSlot, ItemStack original, ItemStack replacement) {
    EquipmentChangeContext context = new EquipmentChangeContext(entity, changedSlot, original, replacement);

    // first, fire event to notify an item was removed
    IToolStackView tool = context.getOriginalTool();
    if (tool != null && ModifierUtil.validArmorSlot(tool, changedSlot)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onUnequip(tool, entry, context);
      }
    }

    // next, fire event to notify an item was added
    tool = context.getReplacementTool();
    if (tool != null && ModifierUtil.validArmorSlot(tool, changedSlot)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onEquip(tool, entry, context);
      }
    }

    // finally, fire events on all other slots to say something changed
    for (EquipmentSlot otherSlot : EquipmentSlot.values()) {
      if (otherSlot != changedSlot) {
        tool = context.getValidTool(otherSlot);
        if (tool != null) {
          for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onEquipmentChange(tool, entry, context, otherSlot);
          }
        }
      }
    }
    // fire event for modifiers that want to watch equipment when not equipped
    MinecraftForge.EVENT_BUS.post(new ToolEquipmentChangeEvent(context));
  }

  /* Required methods */

  /** Data class that runs actual update logic */
  protected static class PlayerLastEquipment implements ICapabilityProvider, Runnable {
    @Nullable
    private final Player player;
    private final Map<EquipmentSlot,ItemStack> lastItems = new EnumMap<>(EquipmentSlot.class);
    private LazyOptional<PlayerLastEquipment> capability;

    private PlayerLastEquipment(@Nullable Player player) {
      this.player = player;
      for (EquipmentSlot slot : EquipmentSlot.values()) {
        lastItems.put(slot, ItemStack.EMPTY);
      }
      this.capability = LazyOptional.of(() -> this);
    }

    /** Called on player tick to update the stacks and run the event */
    public void update() {
      // run twice a second, should be plenty fast enough
      if (player != null) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
          ItemStack newStack = player.getItemBySlot(slot);
          ItemStack oldStack = lastItems.get(slot);
          if (!ItemStack.matches(oldStack, newStack)) {
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CAPABILITY.orEmpty(cap, capability);
    }
  }
}
