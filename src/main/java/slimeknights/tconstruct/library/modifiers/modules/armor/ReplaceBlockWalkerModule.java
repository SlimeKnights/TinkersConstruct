package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.function.Function;

import static slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.MODIFIER_LEVEL;

/**
 * Module to replace blocks with another block while walking.
 * @param replacements  List of replacements to perform
 * @param radius        Range to affect
 * @param tool          Tool condition
 */
public record ReplaceBlockWalkerModule(List<BlockReplacement> replacements, LevelingValue radius, IJsonPredicate<IToolContext> tool) implements ArmorWalkRadiusModule<Void>, ModifierModule {
  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (this.tool.matches(tool)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target)) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      int level = entry.getLevel();
      for (BlockReplacement replacement : replacements) {
        if (replacement.level.test(level)) {
          // target handles matching any desired states like fluid level
          BlockState state = replacement.state;
          if (replacement.target.matches(world.getBlockState(mutable))
              && state.canSurvive(world, mutable) && world.isUnobstructed(state, mutable, CollisionContext.empty())
              && !ForgeEventFactory.onBlockPlace(living, BlockSnapshot.create(world.dimension(), world, mutable), Direction.UP)) {
            world.setBlockAndUpdate(mutable, state);
            world.scheduleTick(mutable, state.getBlock(), Mth.nextInt(living.getRandom(), 60, 120));

            // stop after the first successful replacement, there is no reason to do multiple consecutive
            break;
          }
        }
      }
    }
  }

  /** Represents a single replacement handled by this module */
  private record BlockReplacement(IJsonPredicate<BlockState> target, BlockState state, IntRange level) {
    /** Deserializes this replacement from JSON */
    public static BlockReplacement deserialize(JsonObject json) {
      return new BlockReplacement(
        BlockPredicate.LOADER.getAndDeserialize(json, "target"),
        JsonHelper.convertToBlockState(json), // pulling from this object directly means the keys used are block and properties
        MODIFIER_LEVEL.getAndDeserialize(json, "modifier_level")
      );
    }

    /** Serializes this object to JSON */
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.add("target", BlockPredicate.LOADER.serialize(target));
      JsonHelper.serializeBlockState(state, json);
      MODIFIER_LEVEL.serializeInto(json, "modifier_level", level);
      return json;
    }

    /** Reads from the network */
    public static BlockReplacement fromNetwork(FriendlyByteBuf buffer) {
      return new BlockReplacement(BlockPredicate.LOADER.fromNetwork(buffer), Block.stateById(buffer.readVarInt()), IntRange.fromNetwork(buffer));
    }

    /** Writes this to the network */
    public void toNetwork(FriendlyByteBuf buffer) {
      BlockPredicate.LOADER.toNetwork(target, buffer);
      buffer.writeVarInt(Block.getId(state));
      level.toNetwork(buffer);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ReplaceBlockWalkerModule> LOADER = new IGenericLoader<>() {
    private static final Function<JsonObject, BlockReplacement> REPLACEMENT_PARSER = BlockReplacement::deserialize;

    @Override
    public ReplaceBlockWalkerModule deserialize(JsonObject json) {
      return new ReplaceBlockWalkerModule(
        JsonHelper.parseList(json, "replace", REPLACEMENT_PARSER),
        LevelingValue.deserialize(GsonHelper.getAsJsonObject(json, "radius")),
        ToolContextPredicate.LOADER.getAndDeserialize(json, "tool")
      );
    }

    @Override
    public void serialize(ReplaceBlockWalkerModule object, JsonObject json) {
      if (object.tool != ToolContextPredicate.ANY) {
        json.add("tool", ToolContextPredicate.LOADER.serialize(object.tool));
      }
      JsonArray array = new JsonArray();
      for (BlockReplacement replacement : object.replacements) {
        array.add(replacement.serialize());
      }
      json.add("replace", array);
      json.add("radius", object.radius.serialize(new JsonObject()));
    }

    @Override
    public ReplaceBlockWalkerModule fromNetwork(FriendlyByteBuf buffer) {
      int max = buffer.readVarInt();
      ImmutableList.Builder<BlockReplacement> builder = ImmutableList.builder();
      for (int i = 0; i < max; i++) {
        builder.add(BlockReplacement.fromNetwork(buffer));
      }
      return new ReplaceBlockWalkerModule(builder.build(), LevelingValue.fromNetwork(buffer), ToolContextPredicate.LOADER.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(ReplaceBlockWalkerModule object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.replacements.size());
      for (BlockReplacement replacement : object.replacements) {
        replacement.toNetwork(buffer);
      }
      object.radius.toNetwork(buffer);
      ToolContextPredicate.LOADER.toNetwork(object.tool, buffer);
    }
  };


  /* Builder */

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder implements LevelingValue.Builder<ReplaceBlockWalkerModule> {
    private final ImmutableList.Builder<BlockReplacement> replacements = ImmutableList.builder();
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<IToolContext> tool = ToolContextPredicate.ANY;

    private Builder() {}

    /** Replaces at the given level range */
    private Builder replaceLevelRange(IJsonPredicate<BlockState> target, BlockState replacement, IntRange modifierLevel) {
      this.replacements.add(new BlockReplacement(target, replacement, modifierLevel));
      return this;
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceLevelRange(IJsonPredicate<BlockState> target, BlockState replacement, int min, int max) {
      return replaceLevelRange(target, replacement, MODIFIER_LEVEL.range(min, max));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceMinLevel(IJsonPredicate<BlockState> target, BlockState replacement, int max) {
      return replaceLevelRange(target, replacement, MODIFIER_LEVEL.max(max));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceMaxLevel(IJsonPredicate<BlockState> target, BlockState replacement, int min) {
      return replaceLevelRange(target, replacement, MODIFIER_LEVEL.min(min));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceAlways(IJsonPredicate<BlockState> target, BlockState replacement) {
      return replaceLevelRange(target, replacement, MODIFIER_LEVEL);
    }

    @Override
    public ReplaceBlockWalkerModule amount(float flat, float eachLevel) {
      List<BlockReplacement> replacements = this.replacements.build();
      if (replacements.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 replacement");
      }
      return new ReplaceBlockWalkerModule(replacements, new LevelingValue(flat, eachLevel), tool);
    }
  }
}
