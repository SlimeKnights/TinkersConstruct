package slimeknights.tconstruct.shared.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Objects;

/** Criteria that triggers when a container is opened */
public class BlockContainerOpenedTrigger extends SimpleCriterionTrigger<BlockContainerOpenedTrigger.Instance> {
  private static final ResourceLocation ID = TConstruct.getResource("block_container_opened");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext pDeserializationContext) {
    ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "type"));
    BlockEntityType<?> type = ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(id);
    if (type == null) {
      throw new JsonSyntaxException("Unknown tile entity '" + id + "'");
    }
    return new Instance(predicate, type);
  }

  /** Triggers this criteria */
  public void trigger(@Nullable BlockEntity tileEntity, @Nullable Inventory inv) {
    if (tileEntity != null && inv != null && inv.player instanceof ServerPlayer) {
      this.trigger((ServerPlayer)inv.player, instance -> instance.test(tileEntity.getType()));
    }
  }

  public static class Instance extends AbstractCriterionTriggerInstance {
    private final BlockEntityType<?> type;
    public Instance(ContextAwarePredicate predicate, BlockEntityType<?> type) {
      super(ID, predicate);
      this.type = type;
    }

    public static Instance container(BlockEntityType<?> type) {
      return new Instance(ContextAwarePredicate.ANY, type);
    }

    /** Tests if this instance matches */
    public boolean test(BlockEntityType<?> type) {
      return this.type == type;
    }

    @SuppressWarnings("deprecation")  // no forge, your registries are deprecated, you just don't realize it yet
    @Override
    public JsonObject serializeToJson(SerializationContext conditions) {
      JsonObject json = super.serializeToJson(conditions);
      json.addProperty("type", Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString());
      return json;
    }
  }
}
