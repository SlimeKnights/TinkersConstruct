package slimeknights.tconstruct.library.tools.capability.inventory;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/**
 * A capability that provides block items to things that place blocks, such as
 * the Exchanging modifier or some place block fluid effects like Ichor.
 * Providers of this capability should keep a reference to the stack provided from
 * and update it as needed in the consume method.
 */
public interface BlockItemProviderCapability {

  /** Capability ID */
  ResourceLocation ID = TConstruct.getResource("block_provider");
  /** Capability type */
  Capability<BlockItemProviderCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Registers this capability */
  static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, BlockItemProviderCapability::register);
    // receive the attach event on low priority, so that our default implementations do not override other mods.
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, EventPriority.LOW, BlockItemProviderCapability::attachCapability);
  }

  /** Registers the capability with the event bus */
  private static void register(RegisterCapabilitiesEvent event) {
    event.register(BlockItemProviderCapability.class);
  }

  /** Event listener to attach default implementation(s) of the capability */
  private static void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
    if (event.getObject().getItem() instanceof BlockItem block) {
      event.addCapability(ID, new SimpleBlockItem(event.getObject(), block));
    }
  }

  /**
   * @return The block provider for this stack, or null if this stack cannot provide blocks
   */
  @SuppressWarnings("DataFlowIssue")
  @Nullable
  static BlockItemProviderCapability getBlockProvider(ItemStack stack) {
    return stack.getCapability(CAPABILITY).orElse(null);
  }

  /**
   * @return the {@link BlockItem} that this provides, or {@code null} if this cannot provide more block items (for example if the stack has been depleted)
   */
  @Nullable
  BlockItem getBlockItem();

  /**
   * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the tank.
   */
  void consume();

  /**
   * A simple implementation of {@link BlockItemProviderCapability} that provides from an ItemStack holding a BlockItem
   */
  final class SimpleBlockItem implements BlockItemProviderCapability, ICapabilityProvider {

    private final ItemStack stack;
    private final BlockItem contained;
    @Nullable
    private LazyOptional<BlockItemProviderCapability> lazy;

    public SimpleBlockItem(ItemStack stack, BlockItem contained) {
      this.stack = stack;
      this.contained = contained;
    }

    @Override
    @Nullable
    public BlockItem getBlockItem() {
      return stack.isEmpty() ? null : contained;
    }

    @Override
    public void consume() {
      stack.shrink(1);
    }

    // Because this is an incredibly simple capability it acts as provider and as the actual capability implementation.
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction dir) {
      if (lazy == null)
        return CAPABILITY.orEmpty(cap, lazy = LazyOptional.of(() -> this));
      return CAPABILITY.orEmpty(cap, lazy);
    }
  }
}
