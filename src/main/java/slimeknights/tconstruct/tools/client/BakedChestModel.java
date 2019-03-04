package slimeknights.tconstruct.tools.client;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import slimeknights.mantle.client.model.TRSRBakedModel;
import slimeknights.tconstruct.shared.block.BlockTable;

public class BakedChestModel extends BakedModelWrapper<IBakedModel> {

  private final Map<EnumFacing, IBakedModel> cache = Maps.newEnumMap(EnumFacing.class);
  public BakedChestModel(IBakedModel originalModel) {
    super(originalModel);
  }

  @Nonnull
  @Override
  public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    // get face from state
    EnumFacing face = null;
    if(state instanceof IExtendedBlockState) {
      IExtendedBlockState extendedState = (IExtendedBlockState) state;

      if(extendedState.getUnlistedNames().contains(BlockTable.FACING)) {
        face = extendedState.getValue((IUnlistedProperty<EnumFacing>) BlockTable.FACING);
      }
    }

    IBakedModel out = originalModel;
    if(face != null) {
      out = cache.computeIfAbsent(face, (facing) -> new TRSRBakedModel(originalModel, facing));
    }

    // the model returned by getActualModel should be a simple model with no special handling
    return out.getQuads(state, side, rand);
  }
}
