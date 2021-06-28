package slimeknights.tconstruct.shared.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nullable;
import java.util.Objects;

/** Criteria that triggers when a container is opened */
public class BlockContainerOpenedTrigger extends AbstractCriterionTrigger<BlockContainerOpenedTrigger.Instance> {
  private static final ResourceLocation ID = Util.getResource("block_container_opened");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected Instance deserializeTrigger(JsonObject json, AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
    ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "type"));
    TileEntityType<?> type = ForgeRegistries.TILE_ENTITIES.getValue(id);
    if (type == null) {
      throw new JsonSyntaxException("Unknown tile entity '" + id + "'");
    }
    return new Instance(entityPredicate, type);
  }

  /** Triggers this criteria */
  public void trigger(@Nullable TileEntity tileEntity, @Nullable PlayerInventory inv) {
    if (tileEntity != null && inv != null && inv.player instanceof ServerPlayerEntity) {
      this.triggerListeners((ServerPlayerEntity)inv.player, instance -> instance.test(tileEntity.getType()));
    }
  }

  public static class Instance extends CriterionInstance {
    private final TileEntityType<?> type;
    public Instance(AndPredicate playerCondition, TileEntityType<?> type) {
      super(ID, playerCondition);
      this.type = type;
    }

    public static Instance container(TileEntityType<?> type) {
      return new Instance(AndPredicate.ANY_AND, type);
    }

    /** Tests if this instance matches */
    public boolean test(TileEntityType<?> type) {
      return this.type == type;
    }

    @Override
    public JsonObject serialize(ConditionArraySerializer conditions) {
      JsonObject json = super.serialize(conditions);
      json.addProperty("type", Objects.requireNonNull(type.getRegistryName()).toString());
      return json;
    }
  }
}
