package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Objects;
import java.util.Set;

/**
 * Modifier matching a block
 */
@RequiredArgsConstructor
public
class SetBlockPredicate implements BlockPredicate {

  private final Set<Block> blocks;

  @Override
  public boolean matches(BlockState state) {
    return blocks.contains(state.getBlock());
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<SetBlockPredicate> LOADER = new IGenericLoader<SetBlockPredicate>() {
    @Override
    public SetBlockPredicate deserialize(JsonObject json) {
      Set<Block> blocks = ImmutableSet.copyOf(JsonHelper.parseList(json, "blocks", (element, key) -> {
        ResourceLocation name = JsonHelper.convertToResourceLocation(element, key);
        return Registry.BLOCK.getOptional(name).orElseThrow(() -> new JsonSyntaxException("Unknown block '" + name + "'"));
      }));
      return new SetBlockPredicate(blocks);
    }

    @Override
    public SetBlockPredicate fromNetwork(FriendlyByteBuf buffer) {
      ImmutableSet.Builder<Block> blocks = ImmutableSet.builder();
      int max = buffer.readVarInt();
      for (int i = 0; i < max; i++) {
        blocks.add(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
      }
      return new SetBlockPredicate(blocks.build());
    }

    @Override
    public void serialize(SetBlockPredicate object, JsonObject json) {
      JsonArray blocksJson = new JsonArray();
      for (Block block : object.blocks) {
        blocksJson.add(Objects.requireNonNull(block.getRegistryName()).toString());
      }
      json.add("blocks", blocksJson);
    }

    @Override
    public void toNetwork(SetBlockPredicate object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.blocks.size());
      for (Block block : object.blocks) {
        buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
      }
    }
  };
}
