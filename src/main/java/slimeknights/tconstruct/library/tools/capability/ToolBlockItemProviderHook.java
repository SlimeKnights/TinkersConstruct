package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
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
    BlockItem getBlockItem(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, @Nullable LivingEntity entity);

    /**
     * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @param toolStack The tool that this hook is attached to
     * @param modifier The modifier that provided this hook
     * @param entity The entity holding this tool. May be null if there is no entity
     */
    void consumeBlockItem(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, ItemStack backingStack, @Nullable LivingEntity entity);

    /**
     * Get the stack backing the provided BlockItem. The returned stack will be not be modified as it is copied immediately.
     * The returned stack is primarily used to determine placement state and placement permissions (for adventure mode players).
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @param toolStack The tool that this hook is attached to
     * @return {@link ItemStack#EMPTY} if there is no backing item, otherwise an {@link ItemStack} instance holding at least one of {@code item}.
     */
    default ItemStack getBackingStack(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, BlockItem item, @Nullable LivingEntity entity) {
        return ItemStack.EMPTY;
    }


    @Nullable
    static BlockItemProviderCapability getHookAsCapability(IToolStackView tool) {
        for (ModifierEntry entry : tool.getModifiers()) {
            ToolBlockItemProviderHook provider = entry.getModifier().getHooks().getOrNull(ModifierHooks.BLOCK_ITEM_PROVIDER);
            if (provider != null) return new CapabilityImpl(provider, tool, entry);
        }
        return null;
    }

    record CapabilityImpl(ToolBlockItemProviderHook base, IToolStackView tool, ModifierEntry modifier) implements BlockItemProviderCapability {

        @Override
        public @Nullable BlockItem getBlockItem(ItemStack capStack, @Nullable LivingEntity entity) {
            return base.getBlockItem(tool, capStack, modifier, entity);
        }

        @Override
        public void consume(ItemStack capStack, BlockItem item, ItemStack backingStack, @Nullable LivingEntity entity) {
            base.consumeBlockItem(tool, capStack, modifier, backingStack, entity);
        }

        @Override
        public ItemStack getBackingStack(ItemStack capStack, BlockItem item, @Nullable LivingEntity entity) {
            return base.getBackingStack(tool, capStack, modifier, item, entity);
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
                    // Iterate modifiers to see if any of them actually provide this hook so we don't provide the cap unless we actually have the hook
                    BlockItemProviderCapability capability = getHookAsCapability(tool);
                    if (capability == null)
                        return LazyOptional.empty();
                    lo = lazy = LazyOptional.of(() -> capability);
                }
                return lo.cast();
            }
            return LazyOptional.empty();
        }


    }
}
