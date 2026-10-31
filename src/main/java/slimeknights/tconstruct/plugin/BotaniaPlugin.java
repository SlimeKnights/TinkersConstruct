package slimeknights.tconstruct.plugin;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.BlockItemProviderCapability;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.BlockProvider;

/** Events to register when botania is loaded */
public class BotaniaPlugin {
  @SubscribeEvent
  public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
    // TODO: filter with tag
    event.addCapability(BlockProviderTranslatorProvider.BlockProviderTranslator.ID, new BlockProviderTranslatorProvider(event.getObject()));
  }


  static final class BlockProviderTranslatorProvider implements ICapabilityProvider, NonNullConsumer<LazyOptional<BlockProvider>> {
    private final ItemStack stack;
    @Nullable
    private LazyOptional<BlockProviderTranslator> lazy;

    BlockProviderTranslatorProvider(ItemStack stack) {
      this.stack = stack;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
      if (cap != BlockItemProviderCapability.CAPABILITY) return LazyOptional.empty();
      if (lazy != null) return lazy.cast();

      var baseCap = stack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER);
      // add listener first cause if it is empty then listener is called immediately
      baseCap.addListener(this);
      lazy = baseCap.lazyMap(BlockProviderTranslator::new);
      return lazy.cast();
    }

    @Override // called when the base capability is invalidated
    public void accept(LazyOptional<BlockProvider> blockProviderLazyOptional) {
      if (lazy != null) {
        lazy.invalidate();
        lazy = null;
      }
    }

    record BlockProviderTranslator(BlockProvider baseCap) implements BlockItemProviderCapability {
      final static ResourceLocation ID = TConstruct.getResource("botania_block_provider_translator");

      @Override
      public ItemStack getBlockItemStack(ItemStack stack, @Nullable LivingEntity entity) {
        if (!(entity instanceof Player player)) return ItemStack.EMPTY;

        Block block = baseCap.getProvidedBlock(player, stack);
        if (block == null) return ItemStack.EMPTY;

        Item item = block.asItem();
        if (!(item instanceof BlockItem)) return ItemStack.EMPTY;

        return new ItemStack(block);
      }

      @Override
      public void consume(ItemStack stack, ItemStack backingStack, @Nullable LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        BlockItem blockItem = (BlockItem) backingStack.getItem();
        baseCap.provideBlock(player, backingStack, blockItem.getBlock(), true);
      }
    }
  }
}
