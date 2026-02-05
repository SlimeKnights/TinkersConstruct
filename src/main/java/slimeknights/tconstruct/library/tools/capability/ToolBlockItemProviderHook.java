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
import java.util.function.Supplier;

/** A hook used to provide BlockItems through the {@link BlockItemProviderCapability}, for modifiers such as exchanging */
public interface ToolBlockItemProviderHook {
    /**
     * Get a {@link BlockItem} to provide, wrapped as an ItemStack with any required placement NBT data. Can be randomised, if desired.
     * <br>
     * <br>
     * <b>The returned stack must have {@link ItemStack#getItem} return an instance of {@link BlockItem}, or be {@link ItemStack#EMPTY}!</b>
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @param modifier The modifier that provided this hook
     * @param entity The entity holding this tool. May be null if there is no entity
     * @return the {@link BlockItem} that this provides, or {@code null} if this cannot provide more block items (for example if the stack has been depleted)
     */
    ItemStack getBlockItemStack(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity);

    /**
     * Consume a block from this provider. For example may decrease a contained stacks size or remove fluid from the stack's tank.
     * @param tool The tool that this hook is attached to, as a tool stack view
     * @param toolStack The tool that this hook is attached to
     * @param modifier The modifier that provided this hook
     * @param entity The entity holding this tool. May be null if there is no entity
     * @return {@code true} if this hook consumed, otherwise {@code false} indicating that another modifier needs
     */
    boolean consumeBlockItem(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, ItemStack backingStack, @Nullable LivingEntity entity);

    record CapabilityImpl(IToolStackView tool) implements BlockItemProviderCapability {

        @Override
        public ItemStack getBlockItemStack(ItemStack capStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook hook = entry.getHook(ModifierHooks.BLOCK_ITEM_PROVIDER);
                ItemStack item = hook.getBlockItemStack(tool, entry, entity);
                if (!item.isEmpty()) {
                    if (!(item.getItem() instanceof BlockItem)) {
                        TConstruct.LOG.warn("ToolBlockItemProviderHook implementation tried to return a non-empty, non-blockitem stack! Hook: {}, Hook Class: {}, Provided Item: {}", hook, hook.getClass().getName(), BuiltInRegistries.ITEM.getId(item.getItem()));
                    }
                    return item;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void consume(ItemStack capStack, ItemStack backingStack, @Nullable LivingEntity entity) {
            for (ModifierEntry entry : tool.getModifiers()) {
                ToolBlockItemProviderHook provider = entry.getModifier().getHooks().getOrNull(ModifierHooks.BLOCK_ITEM_PROVIDER);
                if (provider != null && provider.consumeBlockItem(tool, capStack, entry, backingStack, entity)) {
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
