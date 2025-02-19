package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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

import javax.annotation.Nullable;
import java.util.Objects;

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
      ItemStack stack = ItemStack.EMPTY;
      if (block != null) {
        stack = new ItemStack(block);
      } else {
        LivingEntity entity = context.getEntity();
        if (entity != null) {
          // either hand is fine, allows using the tool from offhand or mainhand
          for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = entity.getItemInHand(hand);
            if (!held.isEmpty() && held.getItem() instanceof BlockItem blockItem) {
              block = blockItem.getBlock();
              stack = held;
              break;
            }
          }
        }
      }
      // no block was found, means we either lack an entity or are holding nothing
      if (block == null) {
        return 0;
      }
      // build the context
      Player player = context.getPlayer();
      Level world = context.getLevel();
      BlockPlaceContext placeContext = new BlockPlaceContext(world, player, InteractionHand.MAIN_HAND, stack, context.getHitResult());
      BlockPos clicked = placeContext.getClickedPos();
      if (placeContext.canPlace()) {
        // if we have a blockitem, we can offload a lot of the logic to it
        if (block.asItem() instanceof BlockItem blockItem) {
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
          }
          // simulating is trickier but the methods exist
          placeContext = blockItem.updatePlacementContext(placeContext);
          if (placeContext == null) {
            return 0;
          }
        }
        // following code is based on block item, with notably differences of not calling block item methods (as if we had one we'd use it above)
        // we do notably call this logic in simulation as we need to stop the block item logic early, differences are noted in comments with their vanilla impacts

        // simulate note: we don't ask the block item for its state for placement as that method is protected, this notably affects signs/banners (unlikely need)
        BlockState state = block.getStateForPlacement(placeContext);
        if (state == null) {
          return 0;
        }
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
    }
    return 0;
  }
}
