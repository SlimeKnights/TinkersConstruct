package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** A hook used to provide BlockItems through the {@link BlockItemProviderCapability}, for modifiers such as exchanging */
public interface ToolBlockItemProviderHook {
    /**
     * Get a {@link BlockItem} to provide, wrapped as an ItemStack with any required placement NBT data. Can be randomised, if desired.
     * <br>
     * <br>
     * <b>The returned stack must have {@link ItemStack#getItem} return an instance of {@link BlockItem}, or be {@link ItemStack#EMPTY}!</b>
     * @param tool      The tool that this hook is attached to, as a tool stack view
     * @param modifier  The modifier that provided this hook
     * @param entity    The entity holding this tool. May be null if there is no entity
     * @return stack containing a {@link BlockItem} that this provides, or {@link ItemStack#EMPTY} if this cannot provide more block items (for example if the stack has been depleted)
     */
    ItemStack getBlockItemStack(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity);

    /**
     * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
     *
     * @param tool         The tool that this hook is attached to, as a tool stack view
     * @param modifier     The modifier that provided this hook
     * @param backingStack Stack that was returned by {@link #getBlockItemStack(IToolStackView, ModifierEntry, LivingEntity)}. Should be validated to be your stack before you consume.
     * @param entity       The entity holding this tool. May be null if there is no entity
     * @return {@code true} if this hook consumed, otherwise {@code false} indicating that another modifier needs to consume.
     */
    boolean consumeBlockItem(IToolStackView tool, ModifierEntry modifier, ItemStack backingStack, @Nullable LivingEntity entity);

    record CapabilityImpl(IToolStackView tool) implements BlockItemProviderCapability {
        @Override
        public ItemStack getBlockItemStack(ItemStack capStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook hook = entry.getHook(ModifierHooks.BLOCK_ITEM_PROVIDER);
                ItemStack stack = hook.getBlockItemStack(tool, entry, entity);
                if (!stack.isEmpty()) {
                    Item item = stack.getItem();
                    if (item instanceof BlockItem) {
                        return stack;
                    } else {
                        TConstruct.LOG.warn("ToolBlockItemProviderHook implementation tried to return a non-empty, non-blockitem stack! Hook: {}, Hook Class: {}, Provided Item: {}", hook, hook.getClass().getName(), BuiltInRegistries.ITEM.getId(item));
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void consume(ItemStack capStack, ItemStack backingStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                if (entry.getHook(ModifierHooks.BLOCK_ITEM_PROVIDER).consumeBlockItem(tool, entry, backingStack, entity)) {
                    return;
                }
            }
            TConstruct.LOG.warn("Could not find a modifier to consume {} from after providing it from ToolBlockItemProviderHook. This is likely causing a duplication glitch! Stack nbt: {}", BuiltInRegistries.ITEM.getKey(backingStack.getItem()), backingStack.getTag());
        }
    }

    class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
        private final LazyOptional<BlockItemProviderCapability> lazy;
        public Provider(Supplier<? extends IToolStackView> tool) {
            lazy = LazyOptional.of(() -> new CapabilityImpl(tool.get()));
        }

        @Override
        public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
            return BlockItemProviderCapability.CAPABILITY.orEmpty(cap, lazy);
        }
    }
}
