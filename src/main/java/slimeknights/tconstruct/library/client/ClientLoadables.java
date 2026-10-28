package slimeknights.tconstruct.library.client;

import com.google.gson.GsonBuilder;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.common.CodecLoadable;
import slimeknights.tconstruct.library.utils.GsonLoadable;

public class ClientLoadables {

    /** Models */
    public static final Loadable<BlockElement> BLOCK_ELEMENT = new GsonLoadable<>(new GsonBuilder().registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).create(), BlockElement.class);
    public static final Loadable<Transformation> TRANSFORMATION = new CodecLoadable(Transformation.CODEC);
    
}
