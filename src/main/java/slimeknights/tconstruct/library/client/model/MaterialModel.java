package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.tconstruct.library.client.materials.IMaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.shared.TinkerClient;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
public class MaterialModel implements IModelGeometry<MaterialModel> {

  @Nullable
  private final MaterialId material;
  private final int index;
  public MaterialModel(@Nullable MaterialId material, int index) {
    this.material = material;
    this.index = index;
  }

  protected MaterialModel withMaterial(MaterialId material) {
    return new MaterialModel(material, index);
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<Material> allTextures = Sets.newHashSet();
    Material texture = owner.resolveTexture("texture");
    allTextures.add(texture);
    // texture should exist in item/tool, or the validator cannot handle them
    Consumer<Material> textureAdder;
    if (texture.getTextureLocation().getPath().startsWith("item/tool")) {
      // keep track of skipped textures, so we do not debug print the same resource twice
      Set<ResourceLocation> skipped = new HashSet<>();
      textureAdder = (mat) -> {
        // either must be non-blocks, or must exist. We have fallbacks if it does not exist
        ResourceLocation loc = mat.getTextureLocation();
        if (!PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(mat.getAtlasLocation()) || TinkerClient.textureValidator.test(loc)) {
          allTextures.add(mat);
        } else if (!skipped.contains(loc)) {
          skipped.add(loc);
          log.debug("Skipping loading texture '{}' as it does not exist in the resource pack", loc);
        }
      };
    } else {
      // just directly add with no filter, nothing we can do
      textureAdder = allTextures::add;
      log.error("Texture '{}' is not in item/tool, unable to safely validate optional material textures", texture.getTextureLocation());
    }
    // if no specific material is set, load all materials as dependencies
    // if no material, get textures for all materials
    if (material == null) {
      MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().forEach((info) -> info.getTextureDependencies(textureAdder, texture));
    } else {
      MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material).ifPresent((info) -> info.getTextureDependencies(textureAdder, texture));
    }
    return allTextures;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    Material texture = owner.resolveTexture("texture");
    int tintIndex = -1;
    TextureAtlasSprite finalSprite = null;
    // if the base material is non-null, try to find the sprite for that material
    if (material != null) {
      // first, find a render info
      Collection<IMaterialRenderInfo> infos = MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos();
      ResourceLocation textureLocation = texture.getTextureLocation();
      Optional<IMaterialRenderInfo> renderInfo = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      if(renderInfo.isPresent()) {
        // get a list of texture options
        List<Material> textureOptions = renderInfo.get().getTextureChoices(texture);
        Optional<TextureAtlasSprite> sprite = textureOptions.stream()
                                                            .map(spriteGetter)
                                                            .filter((s) -> !MissingTextureSprite.getLocation().equals(s.getName()))
                                                            .findFirst();
        // if found, use that sprite
        if(sprite.isPresent()) {
          finalSprite = sprite.get();
          // if the sprite was not the first, set the tint index so it gets colored
          if(!finalSprite.getName().equals(textureOptions.get(0).getTextureLocation())) {
            tintIndex = this.index;
          }
        }
      }
    }
    // if we have no material, or the material failed to fetch, use the default sprite
    if (finalSprite == null) {
      finalSprite = spriteGetter.apply(texture);
    }

    // fetch some model properties needed
    TransformationMatrix transform = modelTransform.getRotation();
    IModelTransform transformsFromModel = owner.getCombinedTransform();
    ImmutableMap<TransformType, TransformationMatrix> transformMap = transformsFromModel != null ?
                                                                     PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(transformsFromModel, modelTransform)) :
                                                                     PerspectiveMapWrapper.getTransforms(modelTransform);

    ImmutableList<BakedQuad> quads = ItemLayerModel.getQuadsForSprite(tintIndex, finalSprite, transform);
    return new BakedModel(bakery, owner, this, quads, finalSprite, Maps.immutableEnumMap(transformMap), transform.isIdentity(), modelTransform, owner.isSideLit());
  }

  private static final class MaterialOverrideHandler extends ItemOverrideList {
    private final ModelBakery bakery;
    private static final ResourceLocation BAKE_LOCATION = new ResourceLocation("tconstruct:material_model");

    private MaterialOverrideHandler(ModelBakery bakery) {
      this.bakery = bakery;
    }

    @Override
    public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
      // fetch the material from the stack
      MaterialModel.BakedModel model = (MaterialModel.BakedModel)originalModel;
      IMaterial material = IMaterialItem.getMaterialFromStack(stack);
      // if no material, try to fetch from the tool model
      if (material == IMaterial.UNKNOWN) {
        // needs to have a valid index
        int index = model.parent.index;
        if (index < 0) {
          return originalModel;
        }
        // fetch the tool material at the given index
        Optional<IMaterial> toolMaterial = Optional.ofNullable(stack.getTag())
                                                   .map(ToolData::readFromNBT)
                                                   .map(ToolData::getMaterials)
                                                   .filter((mats) -> mats.size() > index)
                                                   .map((mats) -> mats.get(index))
                                                   .filter((mat) -> mat != IMaterial.UNKNOWN);
        // material must exist
        if (toolMaterial.isPresent()) {
          material = toolMaterial.get();
        } else {
          return originalModel;
        }
      }
      // cache all baked material models, they will not need to be recreated
      MaterialId materialID = material.getIdentifier();
      return model.cache.computeIfAbsent(materialID.toString(), (id) -> {
        MaterialModel newModel = model.parent.withMaterial(materialID);
        return newModel.bake(model.owner, bakery, ModelLoader.defaultTextureGetter(), model.originalTransform, model.getOverrides(), BAKE_LOCATION);
      });
    }
  }

  /** Baked model for a material model */
  private static final class BakedModel extends BakedItemModel {
    private final IModelConfiguration owner;
    private final MaterialModel parent;
    private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
    private final IModelTransform originalTransform;

    BakedModel(ModelBakery bakery, IModelConfiguration owner, MaterialModel parent, ImmutableList<BakedQuad> quads,
               TextureAtlasSprite particle, ImmutableMap<TransformType,TransformationMatrix> transforms, boolean untransformed,
               IModelTransform originalTransform, boolean isSideLit) {
      super(quads, particle, transforms, new MaterialOverrideHandler(bakery), untransformed, isSideLit);
      this.owner = owner;
      this.parent = parent;
      this.originalTransform = originalTransform;
      this.cache = new HashMap<>();
    }
  }

  public static class Loader implements IModelLoader<MaterialModel> {
    public static final MaterialModel.Loader INSTANCE = new MaterialModel.Loader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
      // nothing to do
    }

    @Override
    public MaterialModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      int index = 0;
      if (modelContents.has("index")) {
        index = JSONUtils.getInt(modelContents, "index");
      }

      // static material
      MaterialId material = null;
      if (modelContents.has("material")) {
        material = new MaterialId(JSONUtils.getString(modelContents, "material"));
      }

      return new MaterialModel(material, index);
    }
  }
}
