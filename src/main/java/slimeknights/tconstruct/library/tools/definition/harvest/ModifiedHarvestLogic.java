package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.LazyTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
  public float getDestroySpeed(IModifierToolStack tool, BlockState state) {
    float speed = super.getDestroySpeed(tool, state);
    for (SpeedModifier modifier : speedModifiers) {
      if (modifier.matches(state)) {
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
    private Builder addModifier(SpeedModifier modifier) {
      speedModifiers.add(modifier);
      return this;
    }

    /** Adds a modifier when the block matches a tag */
    public Builder tagModifier(Tag.Named<Block> tag, float modifier) {
      return addModifier(new TagSpeedModifier(LazyTag.of(tag), modifier, false));
    }

    /** Adds a modifier when the block does not match a tag */
    public Builder notTagModifier(Tag.Named<Block> tag, float modifier) {
      return addModifier(new TagSpeedModifier(LazyTag.of(tag), modifier, true));
    }

    /** Adds a modifier when the block matches a tag */
    public Builder blockModifier(float modifier, Block... blocks) {
      return addModifier(new BlockSpeedModifier(ImmutableSet.copyOf(blocks), modifier, false));
    }

    /** Adds a modifier when the block matches a tag */
    public Builder notBlockModifier(float modifier, Block... blocks) {
      return addModifier(new BlockSpeedModifier(ImmutableSet.copyOf(blocks), modifier, true));
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
  private static abstract class SpeedModifier {
    protected final float modifier;
    protected final boolean inverted;

    /** Checks if this modifier matches the block */
    abstract boolean matches(BlockState state);

    /** Writes this object to JSON */
    abstract JsonObject toJson();

    /** Writes this object to the network */
    abstract void toNetwork(FriendlyByteBuf buffer);

    /** Parses a speed modifier from JSON */
    private static SpeedModifier fromJson(JsonObject json) {
      float modifier = GsonHelper.getAsFloat(json, "modifier");
      boolean inverted = GsonHelper.getAsBoolean(json, "inverted", false);
      if (json.has("tag")) {
        return new TagSpeedModifier(LazyTag.fromJson(Registry.BLOCK_REGISTRY, json, "tag"), modifier, inverted);
      }
      if (json.has("blocks")) {
        Set<Block> blocks = ImmutableSet.copyOf(JsonHelper.parseList(json, "blocks", (element, key) -> {
          ResourceLocation name = JsonHelper.convertToResourceLocation(element, key);
          return Registry.BLOCK.getOptional(name).orElseThrow(() -> new JsonSyntaxException("Unknown block '" + name + "'"));
        }));
        return new BlockSpeedModifier(blocks, modifier, inverted);
      }
      throw new JsonParseException("Must specify either 'tag' or 'blocks'");
    }

    /** Parses a speed modifier from the packet buffer */
    private static SpeedModifier fromNetwork(FriendlyByteBuf buffer) {
      ModifierType type = buffer.readEnum(ModifierType.class);
      float modifier = buffer.readFloat();
      boolean inverted = buffer.readBoolean();
      switch (type) {
        case TAG -> {
          return new TagSpeedModifier(LazyTag.fromNetwork(Registry.BLOCK_REGISTRY, buffer), modifier, inverted);
        }
        case BLOCK -> {
          int size = buffer.readVarInt();
          ImmutableSet.Builder<Block> blocks = ImmutableSet.builder();
          for (int i = 0; i < size; i++) {
            blocks.add(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
          }
          return new BlockSpeedModifier(blocks.build(), modifier, inverted);
        }
      }
      throw new IllegalStateException("Invalid enum value " + type);
    }
  }

  /** Modifier matching a tag */
  private static class TagSpeedModifier extends SpeedModifier {
    private final LazyTag<Block> tag;
    public TagSpeedModifier(LazyTag<Block> tag, float modifier, boolean inverted) {
      super(modifier, inverted);
      this.tag = tag;
    }

    @Override
    boolean matches(BlockState state) {
      return state.is(tag) != inverted;
    }

    @Override
    JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("tag", tag.getName().toString());
      json.addProperty("modifier", modifier);
      if (inverted) {
        json.addProperty("inverted", true);
      }
      return json;
    }

    @Override
    void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeEnum(ModifierType.TAG);
      buffer.writeFloat(modifier);
      buffer.writeBoolean(inverted);
      tag.toNetwork(buffer);
    }
  }

  /** Modifier matching a block */
  private static class BlockSpeedModifier extends SpeedModifier {
    private final Set<Block> blocks;
    public BlockSpeedModifier(Set<Block> blocks, float modifier, boolean inverted) {
      super(modifier, inverted);
      this.blocks = blocks;
    }

    @Override
    boolean matches(BlockState state) {
      return blocks.contains(state.getBlock()) != inverted;
    }

    @Override
    JsonObject toJson() {
      JsonObject json = new JsonObject();
      JsonArray blocksJson = new JsonArray();
      for (Block block : blocks) {
        blocksJson.add(Objects.requireNonNull(block.getRegistryName()).toString());
      }
      json.add("blocks", blocksJson);
      json.addProperty("modifier", modifier);
      if (inverted) {
        json.addProperty("inverted", true);
      }
      return json;
    }

    @Override
    void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeEnum(ModifierType.BLOCK);
      buffer.writeFloat(modifier);
      buffer.writeBoolean(inverted);
      buffer.writeVarInt(blocks.size());
      for (Block block : blocks) {
        buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
      }
    }
  }

  private enum ModifierType { TAG, BLOCK }
}
