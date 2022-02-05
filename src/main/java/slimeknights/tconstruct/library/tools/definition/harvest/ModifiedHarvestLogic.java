package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.LazyTag;
import slimeknights.tconstruct.library.tools.definition.harvest.predicate.BlockPredicate;
import slimeknights.tconstruct.library.tools.definition.harvest.predicate.SetBlockPredicate;
import slimeknights.tconstruct.library.tools.definition.harvest.predicate.TagBlockPredicate;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;
import java.util.List;

/** Same as tag harvest, but applies additional modifiers to the break speed */
public class ModifiedHarvestLogic extends TagHarvestLogic {

  public static final Loader LOADER = new Loader();
  private final SpeedModifier[] speedModifiers;
  protected ModifiedHarvestLogic(LazyTag<Block> tag, SpeedModifier[] speedModifiers) {
    super(tag);
    this.speedModifiers = speedModifiers;
  }

  /** Creates a builder for this logic */
  public static Builder builder(Tag.Named<Block> tag) {
    return new Builder(LazyTag.of(tag));
  }

  @Override
  public IGenericLoader<?> getLoader() {
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
    private final LazyTag<Block> tag;
    private final List<SpeedModifier> speedModifiers = new ArrayList<>();

    /** Base method to add a modifier */
    public Builder addModifier(float modifier, BlockPredicate predicate) {
      speedModifiers.add(new SpeedModifier(modifier, predicate));
      return this;
    }

    /** Adds a modifier when the block matches a tag */
    public Builder tagModifier(Tag.Named<Block> tag, float modifier) {
      return addModifier(modifier, new TagBlockPredicate(LazyTag.of(tag)));
    }

    /** Adds a modifier when the block does not match a tag */
    public Builder notTagModifier(Tag.Named<Block> tag, float modifier) {
      return addModifier(modifier, new TagBlockPredicate(LazyTag.of(tag)).inverted());
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
      LazyTag<Block> tag = LazyTag.fromJson(Registry.BLOCK_REGISTRY, json, "effective");
      SpeedModifier[] modifiers = JsonHelper.parseList(json, "modifiers", SpeedModifier::fromJson).toArray(new SpeedModifier[0]);
      return new ModifiedHarvestLogic(tag, modifiers);
    }

    @Override
    public ModifiedHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      LazyTag<Block> tag = LazyTag.fromNetwork(Registry.BLOCK_REGISTRY, buffer);
      SpeedModifier[] modifiers = new SpeedModifier[buffer.readVarInt()];
      for (int i = 0; i < modifiers.length; i++) {
        modifiers[i] = SpeedModifier.fromNetwork(buffer);
      }
      return new ModifiedHarvestLogic(tag, modifiers);
    }

    @Override
    public void serialize(ModifiedHarvestLogic object, JsonObject json) {
      json.addProperty("effective", object.tag.getName().toString());
      JsonArray modifiers = new JsonArray();
      for (SpeedModifier modifier : object.speedModifiers) {
        modifiers.add(modifier.toJson());
      }
      json.add("modifiers", modifiers);
    }

    @Override
    public void toNetwork(ModifiedHarvestLogic object, FriendlyByteBuf buffer) {
      object.tag.toNetwork(buffer);
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
    protected final BlockPredicate predicate;

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
      BlockPredicate predicate = BlockPredicate.LOADER.deserialize(GsonHelper.getAsJsonObject(json, "predicate"));
      return new SpeedModifier(modifier, predicate);
    }

    /** Parses a speed modifier from the packet buffer */
    private static SpeedModifier fromNetwork(FriendlyByteBuf buffer) {
      float modifier = buffer.readFloat();
      BlockPredicate predicate = BlockPredicate.LOADER.fromNetwork(buffer);
      return new SpeedModifier(modifier, predicate);
    }
  }
}
