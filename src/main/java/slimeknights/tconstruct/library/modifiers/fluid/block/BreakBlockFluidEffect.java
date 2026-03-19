package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import java.util.Map;

/** Breaks a block using a fluid */
public record BreakBlockFluidEffect(float hardness, Map<Enchantment,Integer> enchantments) implements FluidEffect<FluidEffectContext.Block> {
  public static final RecordLoadable<BreakBlockFluidEffect> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.defaultField("hardness", 0f, false, BreakBlockFluidEffect::hardness),
    Loadables.ENCHANTMENT.mapWithValues(IntLoadable.FROM_ONE, 0).defaultField("enchantments", Map.of(), BreakBlockFluidEffect::enchantments),
    BreakBlockFluidEffect::new);

  public BreakBlockFluidEffect(float hardness) {
    this(hardness, Map.of());
  }

  public BreakBlockFluidEffect(float hardness, Enchantment enchantment, int level) {
    this(hardness, Map.of(enchantment, level));
  }

  @Override
  public RecordLoadable<BreakBlockFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    // compare our hardness to the block's hardness
    BlockState state = context.getBlockState();
    if (state.isAir()) {
      return 0;
    }
    Level world = context.getLevel();
    BlockPos pos = context.getBlockPos();
    float requirement = state.getDestroySpeed(world, pos);
    if (requirement < 0) {
      return 0;
    }

    // disallow acting if adventure mode and no proper item stack tags
    if (context.breakRestricted()) {
      return 0;
    }

    // 0 hardness means break any block, ignoring hardness
    if (hardness == 0) {
      requirement = 1;
    } else {
      requirement /= hardness;
    }
    // if we had enough level to destroy it, return how much fluid we used
    if (requirement <= level.value()) {
      if (action.execute() && world instanceof ServerLevel server) {
        // handle enchantments by making a fake items stack
        // actual item identity doesn't matter, we are past the point of asking if we can break it
        ItemStack fakeTool = ItemStack.EMPTY;
        if (!enchantments.isEmpty()) {
          fakeTool = new ItemStack(Items.STICK);
          EnchantmentHelper.setEnchantments(enchantments, fakeTool);
        }

        // ensures tile entity is fetched so its around for afterBlockBreak
        BlockEntity te = world.getBlockEntity(pos);
        Block block = state.getBlock();

        // remove the block
        Player player = context.getPlayer();
        boolean removed;
        if (player != null) {
          removed = state.onDestroyedByPlayer(world, pos, player, true, world.getFluidState(pos));
          if (removed) {
            player.awardStat(Stats.BLOCK_MINED.get(block));
          }
        } else {
          removed = world.setBlock(pos, world.getFluidState(pos).createLegacyBlock(), 3);
        }

        // drop resources
        if (removed) {
          state.getBlock().destroy(world, pos, state);

          // determine who to blame for this block breaking, projectile or original entity
          Entity source = context.getProjectile();
          if (source == null) {
            source = context.getEntity();
          }
          LootParams.Builder lootParams = new Builder(server)
            .withParameter(LootContextParams.ORIGIN, context.getHitResult().getLocation())
            .withParameter(LootContextParams.TOOL, fakeTool)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, te)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, source);
          state.getDrops(lootParams).forEach(stack -> Block.popResource(world, pos, stack));
          state.spawnAfterBreak(server, pos, fakeTool, player != null);
          world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }
      }
      return requirement;
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    String translationKey = FluidEffect.getTranslationKey(getLoader());
    if (enchantments.isEmpty()) {
      if (hardness == 0) {
        return Component.translatable(translationKey);
      }
      return Component.translatable(translationKey + ".hardness", hardness);
    } else {
      translationKey += ".enchanted";
      Component enchantments = enchantments().entrySet().stream().<Component>map(entry -> {
        Enchantment enchantment = entry.getKey();
        MutableComponent component = Component.translatable(enchantment.getDescriptionId());
        if (enchantment.getMaxLevel() != 1) {
          component.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + entry.getValue()));
        }
        return component;
      }).reduce(MERGE_COMPONENT_LIST).orElse(Component.empty());
      if (hardness == 0) {
        return Component.translatable(translationKey, enchantments);
      }
      return Component.translatable(translationKey + ".hardness", hardness, enchantments);
    }
  }
}
