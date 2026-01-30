package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
 * Providers of this capability are encouraged to use a single instance for all objects that use
 * the same logic, as the stack and more context are provided in the relevant methods.
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
    if (event.getObject().getItem() instanceof BlockItem) {
      event.addCapability(SimpleBlockItem.ID, SimpleBlockItem.INSTANCE);
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
   * @param stack The {@link ItemStack} that this capability was attached to.
   * @param entity The {@link LivingEntity} (usually a {@link Player}) that is requesting a block.
   * @return the {@link BlockItem} that this provides, or {@code null} if this cannot provide more block items (for example if the stack has been depleted)
   */
  @Nullable
  BlockItem getBlockItem(ItemStack stack, @Nullable LivingEntity entity);

  /**
   * @param stack The {@link ItemStack} that this capability was attached to.
   * @param entity The {@link LivingEntity} (usually a {@link Player}) that has just consumed a block.
   * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
   */
  void consume(ItemStack stack, @Nullable LivingEntity entity);

  /**
   * A simple implementation of {@link BlockItemProviderCapability} that provides from an ItemStack holding a BlockItem
   */
  final class SimpleBlockItem implements BlockItemProviderCapability, ICapabilityProvider {
    public static final SimpleBlockItem INSTANCE = new SimpleBlockItem();
    private static final ResourceLocation ID = TConstruct.getResource("block_item_provider");

    private final LazyOptional<BlockItemProviderCapability> lazy = LazyOptional.of(() -> this);

    @Override
    @Nullable
    public BlockItem getBlockItem(ItemStack stack, @Nullable LivingEntity entity) {
      return stack.isEmpty() ? null : ((BlockItem) stack.getItem());
    }

    @Override
    public void consume(ItemStack stack, @Nullable LivingEntity entity) {
      stack.shrink(1);
    }

    // Because this is an incredibly simple capability it acts as provider and as the actual capability implementation.
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction dir) {
      return CAPABILITY.orEmpty(cap, lazy);
    }
  }
}
