package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** A hook used to provide BlockItems through the {@link BlockItemProviderCapability}, for modifiers such as exchanging */
public interface ToolBlockItemProviderHook {
    /**
     * @return the {@link BlockItem} that this provides, or {@code null} if this cannot provide more block items (for example if the stack has been depleted)
     */
    @Nullable
    BlockItem getBlockItem(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity);

    /**
     * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @param toolStack The tool that this hook is attached to
     * @param modifier The modifier that provided this hook
     * @param entity The entity holding this tool. May be null if there is no entity
     * @return {@code true} if this hook consumed, otherwise {@code false} indicating that another modifier needs
     */
    boolean consumeBlockItem(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, BlockItem item, ItemStack backingStack, @Nullable LivingEntity entity);

    /**
     * Get the stack backing the provided BlockItem. The returned stack will be not be modified as it is copied immediately.
     * The returned stack is primarily used to determine placement state and placement permissions (for adventure mode players).
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @return {@link ItemStack#EMPTY} if there is no backing item, otherwise an {@link ItemStack} instance holding at least one of {@code item}.
     */
    default ItemStack getBackingStack(IToolStackView tool, ModifierEntry modifier, BlockItem item, @Nullable LivingEntity entity) {
        return ItemStack.EMPTY;
    }

    record CapabilityImpl(IToolStackView tool) implements BlockItemProviderCapability {

        @Nullable
        @Override
        public BlockItem getBlockItem(ItemStack capStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook provider = entry.getModifier().getHooks().getOrNull(ModifierHooks.BLOCK_ITEM_PROVIDER);
                if (provider != null) {
                    BlockItem item = provider.getBlockItem(tool, entry, entity);
                    if (item != null) {
                        return item;
                    }
                }
            }
            return null;
        }

        @Override
        public void consume(ItemStack capStack, BlockItem item, ItemStack backingStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook provider = entry.getModifier().getHooks().getOrNull(ModifierHooks.BLOCK_ITEM_PROVIDER);
                if (provider != null && provider.consumeBlockItem(tool, capStack, entry, item, backingStack, entity)) {
                    return;
                }
            }
            TConstruct.LOG.warn("Could not find a modifier to consume {} from after providing it from ToolBlockItemProviderHook. This is likely causing a duplication glitch! Stack nbt: {}", BuiltInRegistries.ITEM.getKey(item), backingStack.getTag());
        }

        @Override
        public ItemStack getBackingStack(ItemStack capStack, BlockItem item, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook provider = entry.getModifier().getHooks().getOrNull(ModifierHooks.BLOCK_ITEM_PROVIDER);
                if (provider != null) {
                    ItemStack backingStack = provider.getBackingStack(tool, entry, item, entity);
                    if (!backingStack.isEmpty()) {
                        return backingStack;
                    }
                }
            }
            return ItemStack.EMPTY;
        }
    }

    class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
        // lazy lazy optional as it depends on a modifier hook which we only bother querying when this cap is fetched
        private LazyOptional<BlockItemProviderCapability> lazy;
        public Provider() {}

        @Override
        public void clearCache() {
            lazy = null;
        }

        @Override
        public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
            if (cap == BlockItemProviderCapability.CAPABILITY) {
                LazyOptional<BlockItemProviderCapability> lo = lazy;
                if (lo == null) {
                    lo = lazy = LazyOptional.of(() -> new CapabilityImpl(tool));
                }
                return lo.cast();
            }
            return LazyOptional.empty();
        }


    }
}
