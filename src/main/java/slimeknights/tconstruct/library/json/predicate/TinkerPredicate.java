package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeLookup;

import javax.annotation.Nullable;

/** Additional living predicates added by Tinkers, Mantle controls the loader we use these days */
public class TinkerPredicate {
  private TinkerPredicate() {}

  /** Entities that are in the air, notably does not count you as airborne if swimming, riding, or climbing */
  public static LivingEntityPredicate AIRBORNE = LivingEntityPredicate.simple(entity -> !entity.isOnGround() && !entity.onClimbable() && !entity.isInWater() && !entity.isPassenger());

  /** Predicate matching any arrows */
  public static ItemPredicate ARROW = ItemPredicate.simple(item -> item instanceof ArrowItem);

  /** Predicate matching meltable blocks */
  public static BlockPredicate CAN_MELT_BLOCK = BlockPredicate.simple(state -> MeltingRecipeLookup.canMelt(state.getBlock()));
  /** Predicate matching meltable items */
  public static ItemPredicate CAN_MELT_ITEM = ItemPredicate.simple(MeltingRecipeLookup::canMelt);

  /** Helper for dealing with the common case of nullable entities, often used when they are entity but not living. */
  public static boolean matches(IJsonPredicate<LivingEntity> predicate, @Nullable LivingEntity entity) {
    if (entity == null) {
      return predicate == LivingEntityPredicate.ANY;
    }
    return predicate.matches(entity);
  }

  /** Checks if the condition matches in a tooltip context */
  public static boolean matchesInTooltip(IJsonPredicate<LivingEntity> predicate, @Nullable LivingEntity entity, TooltipKey tooltipKey) {
    return tooltipKey != TooltipKey.SHIFT || matches(predicate, entity);
  }

  /** Helper for dealing with the common case of nullable entities, often used when they are entity but not living. */
  public static boolean matches(IJsonPredicate<DamageSource> predicate, @Nullable DamageSource source) {
    if (source == null) {
      return predicate == DamageSourcePredicate.ANY;
    }
    return predicate.matches(source);
  }
}
