package slimeknights.tconstruct.world.client;

import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.world.TinkerHeadType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Helps with creation and registration of skull block models */
public class SkullModelHelper {
  /** Map of head type to model layer location for each head type */
  public static final Map<TinkerHeadType,ModelLayerLocation> HEAD_LAYERS = Arrays.stream(TinkerHeadType.values()).collect(
    Collectors.toMap(Function.identity(), type -> new ModelLayerLocation(TConstruct.getResource(type.getSerializedName() + "_head"), "main"), (a, b) -> a, () -> new EnumMap<>(TinkerHeadType.class)));

  private SkullModelHelper() {}

  /** Creates a head with the given start and texture size */
  public static LayerDefinition createHeadLayer(int headX, int headY, int width, int height) {
    MeshDefinition mesh = new MeshDefinition();
    mesh.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(headX, headY).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
    return LayerDefinition.create(mesh, width, height);
  }

  /** Creates a head with a hat, starting the head at 0,0, hat at the values, and using the given size */
  @SuppressWarnings("SameParameterValue")
  public static LayerDefinition createHeadHatLayer(int hatX, int hatY, int width, int height) {
    MeshDefinition mesh = SkullModel.createHeadModel();
    mesh.getRoot().getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(hatX, hatY).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
    return LayerDefinition.create(mesh, width, height);
  }
}
