package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.predicate.block.SetBlockPredicate;
import slimeknights.tconstruct.library.json.predicate.block.TagBlockPredicate;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;
import java.util.List;

/** Same as tag harvest, but applies additional modifiers to the break speed */
public class ModifiedHarvestLogic extends TagHarvestLogic {

  public static final Loader LOADER = new Loader();
  private final SpeedModifier[] speedModifiers;
  protected ModifiedHarvestLogic(TagKey<Block> tag, SpeedModifier[] speedModifiers) {
    super(tag);
    this.speedModifiers = speedModifiers;
  }

  /** Creates a builder for this logic */
  public static Builder builder(TagKey<Block> tag) {
    return new Builder(tag);
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }

  @Override
  public float getDestroySpeed(IToolStackView tool, BlockState state) {
    float speed = super.getDestroySpeed(tool, state);
    for (SpeedModifier modifier : speedModifiers) {
      if (modifier.predicate.matches(state)) {
        return Math.max(1, speed * modifier.modifier);
      }
    }
    return speed;
  }

  /** Builder for the logic */
  @SuppressWarnings("unused")
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Builder {
    private final TagKey<Block> tag;
    private final List<SpeedModifier> speedModifiers = new ArrayList<>();

    /** Base method to add a modifier */
    public Builder addModifier(float modifier, IJsonPredicate<BlockState> predicate) {
      speedModifiers.add(new SpeedModifier(modifier, predicate));
      return this;
    }

    /** Adds a modifier when the block matches a tag */
    public Builder tagModifier(TagKey<Block> tag, float modifier) {
      return addModifier(modifier, new TagBlockPredicate(tag));
    }

    /** Adds a modifier when the block does not match a tag */
    public Builder notTagModifier(TagKey<Block> tag, float modifier) {
      return addModifier(modifier, new TagBlockPredicate(tag).inverted());
    }

    /** Adds a modifier when the block matches a tag */
    public Builder blockModifier(float modifier, Block... blocks) {
      return addModifier(modifier, new SetBlockPredicate(ImmutableSet.copyOf(blocks)));
    }

    /** Adds a modifier when the block matches a tag */
    public Builder notBlockModifier(float modifier, Block... blocks) {
      return addModifier(modifier, new SetBlockPredicate(ImmutableSet.copyOf(blocks)).inverted());
    }

    /** Builds the modifier */
    public ModifiedHarvestLogic build() {
      return new ModifiedHarvestLogic(tag, speedModifiers.toArray(new SpeedModifier[0]));
    }
  }

  private static class Loader implements IGenericLoader<ModifiedHarvestLogic> {
    @Override
    public ModifiedHarvestLogic deserialize(JsonObject json) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, JsonHelper.getResourceLocation(json, "effective"));
      SpeedModifier[] modifiers = JsonHelper.parseList(json, "modifiers", SpeedModifier::fromJson).toArray(new SpeedModifier[0]);
      return new ModifiedHarvestLogic(tag, modifiers);
    }

    @Override
    public ModifiedHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
      SpeedModifier[] modifiers = new SpeedModifier[buffer.readVarInt()];
      for (int i = 0; i < modifiers.length; i++) {
        modifiers[i] = SpeedModifier.fromNetwork(buffer);
      }
      return new ModifiedHarvestLogic(tag, modifiers);
    }

    @Override
    public void serialize(ModifiedHarvestLogic object, JsonObject json) {
      json.addProperty("effective", object.tag.location().toString());
      JsonArray modifiers = new JsonArray();
      for (SpeedModifier modifier : object.speedModifiers) {
        modifiers.add(modifier.toJson());
      }
      json.add("modifiers", modifiers);
    }

    @Override
    public void toNetwork(ModifiedHarvestLogic object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.tag.location());
      buffer.writeVarInt(object.speedModifiers.length);
      for (SpeedModifier modifier : object.speedModifiers) {
        modifier.toNetwork(buffer);
      }
    }
  }

  /** Speed modifier to apply to a block */
  @RequiredArgsConstructor
  private static class SpeedModifier {
    protected final float modifier;
    protected final IJsonPredicate<BlockState> predicate;

    /** Writes this object to JSON */
    public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("modifier", modifier);
      json.add("predicate", BlockPredicate.LOADER.serialize(predicate));
      return json;
    }

    /** Writes this object to the network */
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeFloat(modifier);
      BlockPredicate.LOADER.toNetwork(predicate, buffer);
    }

    /** Parses a speed modifier from JSON */
    private static SpeedModifier fromJson(JsonObject json) {
      float modifier = GsonHelper.getAsFloat(json, "modifier");
      IJsonPredicate<BlockState> predicate = BlockPredicate.LOADER.deserialize(GsonHelper.getAsJsonObject(json, "predicate"));
      return new SpeedModifier(modifier, predicate);
    }

    /** Parses a speed modifier from the packet buffer */
    private static SpeedModifier fromNetwork(FriendlyByteBuf buffer) {
      float modifier = buffer.readFloat();
      IJsonPredicate<BlockState> predicate = BlockPredicate.LOADER.fromNetwork(buffer);
      return new SpeedModifier(modifier, predicate);
    }
  }
}
