package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearingModifier extends Modifier {
  /** Container for melting recipe lookup */
  private static final SearingContainer CONTAINER = new SearingContainer();
  /** Cache of item forms of blocks which have a boost */
  private static final Map<Item, Boolean> BOOSTED_BLOCKS = new ConcurrentHashMap<>();
  static {
    RecipeCacheInvalidator.addReloadListener(client -> BOOSTED_BLOCKS.clear());
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, FluidValues.BRICK);
  }

  /** Checks if the modifier is effective on the given block state */
  private static boolean isEffective(Level world, Item item) {
    CONTAINER.setStack(new ItemStack(item));
    boolean effective = world.getRecipeManager().getRecipeFor(RecipeTypes.MELTING, CONTAINER, world).isPresent();
    CONTAINER.setStack(ItemStack.EMPTY);
    return effective;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      BlockState state = event.getState();
      Item item = state.getBlock().asItem();
      if (item != Items.AIR) {
        Level world = event.getPlayer().level;
        // +7 per level if it has a melting recipe, cache to save lookup time
        // TODO: consider whether we should use getCloneItemStack, problem is I don't want a position based logic and its possible the result is BE based
        if (BOOSTED_BLOCKS.computeIfAbsent(item, i -> isEffective(world, i)) == Boolean.TRUE) {
          event.setNewSpeed(event.getNewSpeed() + level * 6 * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier);
        }
      }
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 7 * level, tooltip);
  }

  /** Container implementation for recipe lookup */
  private static class SearingContainer implements IMeltingContainer {
    @Getter @Setter
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public IOreRate getOreRate() {
      return Config.COMMON.melterOreRate;
    }
  }
}
