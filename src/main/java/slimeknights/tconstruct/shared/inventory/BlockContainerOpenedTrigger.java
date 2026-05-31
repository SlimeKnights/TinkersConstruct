package slimeknights.tconstruct.shared.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.Optional;

/** Criteria that triggers when a container is opened */
public class BlockContainerOpenedTrigger extends SimpleCriterionTrigger<BlockContainerOpenedTrigger.Instance> {
  @Override
  public Codec<Instance> codec() {
    return Instance.CODEC;
  }

  /** Triggers this criteria */
  public void trigger(@Nullable BlockEntity tileEntity, @Nullable Inventory inv) {
    if (tileEntity != null && inv != null && inv.player instanceof ServerPlayer) {
      this.trigger((ServerPlayer)inv.player, instance -> instance.test(tileEntity.getType()));
    }
  }

  public record Instance(Optional<ContextAwarePredicate> player, Holder<BlockEntityType<?>> type) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
      BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(Instance::type)
    ).apply(instance, Instance::new));

    public static Criterion<Instance> container(BlockEntityType<?> type) {
      return TinkerCommons.CONTAINER_OPENED_TRIGGER.createCriterion(new Instance(Optional.empty(), type.builtInRegistryHolder()));
    }

    /** Tests if this instance matches */
    public boolean test(BlockEntityType<?> type) {
      return this.type.value() == type;
    }
  }
}
