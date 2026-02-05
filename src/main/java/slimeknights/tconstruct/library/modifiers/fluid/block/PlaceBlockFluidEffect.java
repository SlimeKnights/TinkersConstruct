package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.tools.capability.BlockItemProviderCapability;

import javax.annotation.Nullable;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.capability.BlockItemProviderCapability.getBlockProvider;

/** Effect to place a block in using logic similar to block item placement. */
public record PlaceBlockFluidEffect(@Nullable Block block, @Nullable SoundEvent sound) implements FluidEffect<FluidEffectContext.Block> {
  public static final RecordLoadable<PlaceBlockFluidEffect> LOADER = RecordLoadable.create(
    Loadables.BLOCK.nullableField("block", PlaceBlockFluidEffect::block),
    Loadables.SOUND_EVENT.nullableField("sound", PlaceBlockFluidEffect::sound),
    PlaceBlockFluidEffect::new);

  public PlaceBlockFluidEffect(@Nullable Block block) {
    this(block, null);
  }

  @Override
  public RecordLoadable<PlaceBlockFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    if (level.isFull()) {
      // if we have no block, then use the block held by the player
      // its a bit magic, but eh, some fluids are magic
      Block block = this.block;
      InteractionHand useHand = InteractionHand.MAIN_HAND;
      LivingEntity entity = context.getEntity();
      Player player = context.getPlayer();
      Level world = context.getLevel();

      // we have four paths to go here.
      // 1. there is a block provided and it has a block item
      // 2. there is a block probided but it has no block item
      // 3. there is an item being held that provides a block item
      // 4. there is an item in the context that provides a block item

      if (block != null) {
        ItemStack stack = new ItemStack(block);
        if (player == null || player.mayBuild()) {
          BlockPlaceContext placeContext = new BlockPlaceContext(world, player, useHand, stack, context.getHitResult());
          if (stack.getItem() instanceof BlockItem blockItem) {
            // path 1: there is a block provided, and it has a block item
            return placeBlockItem(blockItem, context, action, block, placeContext);
          } else {
            // path 2:  there is a block provided, but it has no block item
            return placeNonBlockItem(context, action, block, placeContext);
          }
        } else {
          return 0;
        }
      }

      if (entity != null) {
        // either hand is fine, allows using the tool from offhand or mainhand
        // iterate in reverse order so that we prefer the offhand
        for (InteractionHand hand : new InteractionHand[]{InteractionHand.OFF_HAND, InteractionHand.MAIN_HAND}) {
          // path 3: check if there is an item being held that provides a block item
          ItemStack held = entity.getItemInHand(hand);
          Integer result = maybePlaceFrom(context, action, held, useHand);
          if (result != null) return result;
        }

      } else if (!context.placeRestricted(context.getStack())) {
        // path 4: there is an item in the context that provides a block item
        ItemStack held = context.getStack();
        Integer result = maybePlaceFrom(context, action, held, useHand);
        return result == null ? 0 : result;
      }
    }
    return 0;
  }

  /**
   * Attempt to place from an item that may or may not provide a {@link BlockItem} via {@link BlockItemProviderCapability}
   * @return {@code null} if the item couldn't provide a {@link BlockItem} to place. {@code 0} if placement failed, {@code 1} if it succeeded.
   */
  @Nullable
  private Integer maybePlaceFrom(FluidEffectContext.Block context, FluidAction action, ItemStack held, InteractionHand useHand) {
    LivingEntity entity = context.getEntity();
    BlockItemProviderCapability cap = getBlockProvider(held);
    if (cap == null) return null;
    ItemStack backingStack = cap.getBlockItemStack(held, entity);
    if (backingStack.isEmpty()) return null;

    BlockItem blockItem = BlockItemProviderCapability.verifyBlockItem(backingStack, cap);
    if (blockItem == null) return null;
    // immediately do a defensive copy of the stack.
    ItemStack stack = backingStack.copyWithCount(1);
    if (stack.isEmpty()) stack = new ItemStack(blockItem);

    BlockPlaceContext placeContext = new BlockPlaceContext(context.getLevel(), context.getPlayer(), useHand, stack, context.getHitResult());

    int result = placeBlockItem(blockItem, context, action, blockItem.getBlock(), placeContext);
    if (stack.isEmpty()) {
      cap.consume(held, backingStack, entity);
    }
    return result;
  }

  private int placeBlockItem(BlockItem blockItem, FluidEffectContext.Block context, FluidAction action, Block block, BlockPlaceContext placeContext) {
    Level world = context.getLevel();
    BlockPos clicked = placeContext.getClickedPos();
    Player player = context.getPlayer();

    if (context.placeRestricted(placeContext.getItemInHand())) return 0;

    if (action.execute()) {
      if (blockItem.place(placeContext).consumesAction()) {
        if (player instanceof ServerPlayer serverPlayer) {
          BlockState placed = world.getBlockState(clicked);
          SoundType soundType = placed.getSoundType(world, clicked, player);
          serverPlayer.connection.send(new ClientboundSoundPacket(
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(Objects.requireNonNullElse(sound, soundType.getPlaceSound())),
            SoundSource.BLOCKS, clicked.getX(), clicked.getY(), clicked.getZ(), (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, TConstruct.RANDOM.nextLong()));
        }
        return 1;
      }
      return 0;
    } else {
      // simulating is trickier but the methods exist
      placeContext = blockItem.updatePlacementContext(placeContext);
      if (placeContext == null) {
        return 0;
      }
      // we cannot simulate anything more with a BlockItem, so delegate to the same way as regular blocks
      return placeNonBlockItem(context, action, block, placeContext);
    }
  }

  private int placeNonBlockItem(FluidEffectContext.Block context, FluidAction action, Block block, BlockPlaceContext placeContext) {

    // following code is based on block item, with notably differences of not calling block item methods (as if we had one we'd use it above)
    // we do notably call this logic in simulation as we need to stop the block item logic early, differences are noted in comments with their vanilla impacts

    // simulate note: we don't ask the block item for its state for placement as that method is protected, this notably affects signs/banners (unlikely need)
    BlockState state = block.getStateForPlacement(placeContext);
    if (state == null) {
      return 0;
    }

    Level world = context.getLevel();
    BlockPos clicked = placeContext.getClickedPos();
    Player player = context.getPlayer();
    // simulate note: we don't call BlockItem#canPlace as its protected, though never overridden in vanilla
    if (!state.canSurvive(world, clicked) || !world.isUnobstructed(state, clicked, player == null ? CollisionContext.empty() : CollisionContext.of(player))) {
      return 0;
    }
    // at this point the only check we are missing on simulate is actually placing the block failing
    if (action.execute()) {
      // actually place the block
      if (!world.setBlock(clicked, state, Block.UPDATE_ALL_IMMEDIATE)) {
        return 0;
      }
      // if its the expected block, run some criteria stuffs
      BlockState placed = world.getBlockState(clicked);
      ItemStack stack = placeContext.getItemInHand();
      if (placed.is(block)) {
        // difference from BlockItem: do not update block state or block entity from tag as we have no tag
        // it might however be worth passing in a set of properties to set here as part of JSON
        // setPlacedBy only matters when placing from held item
        block.setPlacedBy(world, clicked, placed, player, stack);
        if (player instanceof ServerPlayer serverPlayer) {
          CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, clicked, stack);
        }
      }

      // resulting events
      LivingEntity placer = context.getEntity(); // possible that living is nonnull when player is null
      world.gameEvent(GameEvent.BLOCK_PLACE, clicked, GameEvent.Context.of(placer, placed));
      SoundType sound = placed.getSoundType(world, clicked, placer);
      world.playSound(null, clicked, Objects.requireNonNullElse(this.sound, sound.getPlaceSound()), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

      // stack might be empty if we failed to find an item form; only matters in null block form anyways
      if ((player == null || !player.getAbilities().instabuild) && !stack.isEmpty()) {
        stack.shrink(1);
      }
    }
    return 1;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    String translationKey = FluidEffect.getTranslationKey(getLoader());
    if (block == null) {
      return Component.translatable(translationKey + ".held");
    }
    return Component.translatable(translationKey, Component.translatable(block.getDescriptionId()));
  }
}
