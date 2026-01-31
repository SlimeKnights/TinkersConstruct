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
    BlockItem getBlockItem(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, @Nullable ItemStack stack);

    /**
     * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
     */
    void consume(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, @Nullable ItemStack stack);

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
        public @Nullable BlockItem getBlockItem(ItemStack stack, @Nullable LivingEntity entity) {
            return base.getBlockItem(tool, modifier, entity, stack);
        }

        @Override
        public void consume(ItemStack stack, @Nullable LivingEntity entity) {
            base.consume(tool, modifier, entity, stack);
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
