package slimeknights.tconstruct.test;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Objects;

/** Block based harvest logic implementation. Included here rather than in library as block harvest logic use is discouraged, its just easy for testing */
@RequiredArgsConstructor
public class BlockHarvestLogic implements IHarvestLogic {
  public static final Loader LOADER = new Loader();

  private final Block block;

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.getBlock() == block;
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<BlockHarvestLogic> {
    @Override
    public BlockHarvestLogic deserialize(JsonObject json) {
      return new BlockHarvestLogic(ForgeRegistries.BLOCKS.getValue(JsonHelper.getResourceLocation(json, "block")));
    }

    @Override
    public BlockHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      return new BlockHarvestLogic(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
    }

    @Override
    public void serialize(BlockHarvestLogic object, JsonObject json) {
      json.addProperty("block", Objects.requireNonNull(object.block.getRegistryName()).toString());
    }

    @Override
    public void toNetwork(BlockHarvestLogic object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, object.block);
    }
  }
}
