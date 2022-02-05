package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.LazyTag;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Harvest logic that is effective if the tool has the correct tag */
@RequiredArgsConstructor
public class TagHarvestLogic implements IHarvestLogic {
  public static final Loader LOADER = new Loader();

  protected final LazyTag<Block> tag;
  public TagHarvestLogic(Tag.Named<Block> tag) {
    this.tag = LazyTag.of(tag);
  }

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.is(tag) && TierSortingRegistry.isCorrectTierForDrops(tool.getStats().get(ToolStats.HARVEST_TIER), state);
  }

  @Override
  public float getDestroySpeed(IToolStackView tool, BlockState state) {
    // destroy speed does not require right tier to boost
    return state.is(tag) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1.0f;
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<TagHarvestLogic> {
    @Override
    public TagHarvestLogic deserialize(JsonObject json) {
      LazyTag<Block> tag = LazyTag.fromJson(Registry.BLOCK_REGISTRY, json, "effective");
      return new TagHarvestLogic(tag);
    }

    @Override
    public TagHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      LazyTag<Block> tag = LazyTag.fromNetwork(Registry.BLOCK_REGISTRY, buffer);
      return new TagHarvestLogic(tag);
    }

    @Override
    public void serialize(TagHarvestLogic object, JsonObject json) {
      json.addProperty("effective", object.tag.getName().toString());
    }

    @Override
    public void toNetwork(TagHarvestLogic object, FriendlyByteBuf buffer) {
      object.tag.toNetwork(buffer);
    }
  }
}
