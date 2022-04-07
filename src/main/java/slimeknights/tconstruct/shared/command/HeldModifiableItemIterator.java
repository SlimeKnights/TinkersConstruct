package slimeknights.tconstruct.shared.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Helper to iterate target entities in a command context */
public class HeldModifiableItemIterator {
  private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "held_modifiable.failed"));
  private static final DynamicCommandExceptionType NONLIVING_ENTITY_EXCEPTION = new DynamicCommandExceptionType(error -> TConstruct.makeTranslation("command", "held_modifiable.failed.nonliving", error));
  private static final DynamicCommandExceptionType ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(error -> TConstruct.makeTranslation("command", "held_modifiable.failed.no_item", error));
  private static final DynamicCommandExceptionType INVALID_ITEM = new DynamicCommandExceptionType(error -> TConstruct.makeTranslation("command", "held_modifiable.failed.invalid_item", error));

  /** Applies for the given context */
  public static List<LivingEntity> apply(CommandContext<CommandSourceStack> context, HeldModifiableBehavior behavior) throws CommandSyntaxException {
    return apply(EntityArgument.getEntities(context, "targets"), behavior);
  }

  /** Applies the iterator to the given entities */
  public static List<LivingEntity> apply(Collection<? extends Entity> targets, HeldModifiableBehavior behavior) throws CommandSyntaxException {
    // apply to all entities
    List<LivingEntity> successes = new ArrayList<>();
    for (Entity entity : targets) {
      if (entity instanceof LivingEntity living) {
        ItemStack stack = living.getMainHandItem();
        if (!stack.isEmpty()) {
          if (stack.is(TinkerTags.Items.MODIFIABLE)) {
            if (behavior.accept(living, stack)) {
              successes.add(living);
            }
          } else {
            throw INVALID_ITEM.create(entity.getName().getString());
          }
        } else {
          throw ITEMLESS_EXCEPTION.create(entity.getName().getString());
        }
      } else {
        throw NONLIVING_ENTITY_EXCEPTION.create(entity.getName().getString());
      }
    }

    // nothing happened? means the command failed to run
    if (successes.isEmpty()) {
      throw FAILED_EXCEPTION.create();
    }
    return successes;
  }

  /** BiConsumer that throws command syntax exceptions */
  public interface HeldModifiableBehavior {
    /**
     * Runs the command for the given entity and stack
     * @return true if successful
     */
    boolean accept(LivingEntity living, ItemStack stack) throws CommandSyntaxException;
  }
}
