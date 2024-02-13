package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

/**
 * Module to cover the ground in the given block
 * @param state      State used to cover the ground
 * @param radius     Radius to cover
 * @param condition  Standard module condition
 */
public record CoverGroundWalkerModule(BlockState state, LevelingValue radius, ModifierModuleCondition condition) implements ModifierModule, ArmorWalkRadiusModule<Void> {
  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (condition.matches(tool, modifier)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target) && state.canSurvive(world, target)) {
      world.setBlockAndUpdate(target, state);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<CoverGroundWalkerModule> LOADER = new IGenericLoader<>() {
    @Override
    public CoverGroundWalkerModule deserialize(JsonObject json) {
      return new CoverGroundWalkerModule(
        JsonHelper.convertToBlockState(json),
        LevelingValue.deserialize(GsonHelper.getAsJsonObject(json, "radius")),
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(CoverGroundWalkerModule object, JsonObject json) {
      object.condition.serializeInto(json);
      JsonHelper.serializeBlockState(object.state, json);
      json.add("radius", object.radius.serialize(new JsonObject()));
    }

    @Override
    public CoverGroundWalkerModule fromNetwork(FriendlyByteBuf buffer) {
      return new CoverGroundWalkerModule(
        Block.stateById(buffer.readVarInt()),
        LevelingValue.fromNetwork(buffer),
        ModifierModuleCondition.fromNetwork(buffer)
      );
    }

    @Override
    public void toNetwork(CoverGroundWalkerModule object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(Block.getId(object.state));
      object.radius.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /** Creates a builder instance for the given state */
  public static Builder state(BlockState state) {
    return new Builder(state);
  }

  /** Creates a builder instance for the given block */
  public static Builder block(Block block) {
    return state(block.defaultBlockState());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> implements LevelingValue.Builder<CoverGroundWalkerModule> {
    private final BlockState state;

    @Override
    public CoverGroundWalkerModule amount(float flat, float eachLevel) {
      return new CoverGroundWalkerModule(state, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
