package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.tconstruct.tables.client.model.ModelProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Model that handles generating variants for connected textures
 * TODO: move to Mantle
 */
public class ConnectedModel implements IModelGeometry<ConnectedModel> {
  /** Parent model */
  private final BlockModel model;
  /** List of textures that connect */
  private final Set<String> connectedTextures;
  /** Map of full texture name to the resulting material, filled during getTextures */
  private final Map<String,Either<Material,String>> suffixedTextures;

  protected ConnectedModel(BlockModel model, Set<String> connectedTextures) {
    this.model = model;
    this.connectedTextures = connectedTextures;
    this.suffixedTextures = new HashMap<>();
  }

  /** Texture suffixes for each of the 4 textures, index as 0bENWS */
   private static final String[] SUFFIXES = {
    "",     // 0000
    "d",    // 0001
    "l",    // 0010
    "dl",   // 0011
    "u",    // 0100
    "ud",   // 0101
    "ul",   // 0110
    "udl",  // 0111
    "r",    // 1000
    "dr",   // 1001
    "lr",   // 1010
    "dlr",  // 1011
    "ur",   // 1100
    "udr",  // 1101
    "ulr",  // 1110
    "udlr"  // 1111
  };

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Collection<Material> textures = model.getTextures(modelGetter, missingTextureErrors);
    // for all connected textures, add suffix textures
    for (String name : connectedTextures) {
      // fetch data from the base texture
      Material base = owner.resolveTexture(name);
      ResourceLocation atlas = base.getAtlasLocation();
      ResourceLocation texture = base.getTextureLocation();
      String namespace = texture.getNamespace();
      String path = texture.getPath();
      // use base atlas and texture, but suffix the name
      // skip suffix 0, because we already added that texture
      for (int i = 1; i < SUFFIXES.length; i++) {
        String suffixedName = name + "_" + SUFFIXES[i];
        Material mat;
        // allow overriding a specific texture
        if (owner.isTexturePresent(suffixedName)) {
          mat = owner.resolveTexture(suffixedName);
        } else {
          mat = new Material(atlas, new ResourceLocation(namespace, path + "/" + SUFFIXES[i]));
        }
        // cache the texture name, we use it a lot in rebaking
        suffixedTextures.put(name + "_" + SUFFIXES[i], Either.left(mat));
        textures.add(mat);
      }
    }
    return textures;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(bakery, model, spriteGetter, transform, location, true);
    return new BakedModel(bakery, transform, baked, this);
  }

  protected static class BakedModel extends BakedModelWrapper<IBakedModel> {
    private static final ResourceLocation BAKE_LOCATION = new ResourceLocation("tconstruct:connected_model");

    private final ModelBakery bakery;
    private final ConnectedModel parent;
    private final IModelTransform transforms;
    private final IBakedModel[] cache = new IBakedModel[64];
    private final Map<String,String> nameMappingCache = new HashMap<>();
    protected BakedModel(ModelBakery bakery, IModelTransform transforms, IBakedModel baked, ConnectedModel parent) {
      super(baked);
      this.bakery = bakery;
      this.transforms = transforms;
      this.parent = parent;
      // all directions false gives cache key of 0, that is ourself
      this.cache[0] = baked;
    }

    /**
     * Gets an array of localized directions to whether a block exists on the side
     * @param state     Block state
     * @param rotation  State rotation
     * @return  Boolean array of data
     */
    private static boolean[] getFaces(BlockState state, Matrix4f rotation) {
      boolean[] faces = new boolean[6];
      for (Direction dir : Direction.values()) {
        // if the prop is missing, treat as false. Prevents crashes if a resource pack maker attempts to use on a non-connected block
        BooleanProperty prop = ModelProperties.CONNECTED_DIRECTIONS.get(Direction.rotateFace(rotation, dir));
        faces[dir.getIndex()] = state.has(prop) && state.get(prop);
      }
      return faces;
    }

    /**
     * Gets the texture suffix
     * @param faces  Face data
     * @return  Key used to cache it
     */
    private static String getTextureSuffix(boolean[] faces, Function<Direction,Direction> transform) {
      int key = 0;
      for (Direction dir : Plane.HORIZONTAL) {
        if (faces[transform.apply(dir).getIndex()]) {
          key |= 1 << dir.getHorizontalIndex();
        }
      }
      if (key == 0) {
        return "";
      }
      return "_" + SUFFIXES[key];
    }

    /**
     * Gets the direction rotated
     * @param direction  Original direction to rotate
     * @param rotation   Rotation origin, UP is identity
     * @return  Rotated direction
     */
    private static Direction rotateDirection(Direction direction, Direction rotation) {
      if (rotation == Direction.UP) {
        return direction;
      }
      if (rotation == Direction.DOWN) {
        // Z is backwards on the bottom
        if (direction.getAxis() == Axis.Z) {
          return direction.getOpposite();
        }
        // X is normal
        return direction;
      }
      // sides all just have the next side for left and right, and consistent up and down
      switch(direction) {
        case NORTH: return Direction.UP;
        case SOUTH: return Direction.DOWN;
        case EAST: return rotation.rotateYCCW();
        case WEST: return rotation.rotateY();
      }
      throw new IllegalArgumentException("Direction must be horizontal axis");
    }

    /**
     * Gets a transform function based on the block part UV and block face
     * @param face   Block face in question
     * @param uv     Block UV data
     * @return  Direction transform function
     */
    private Function<Direction,Direction> getTransform(Direction face, BlockFaceUV uv) {
      // TODO: how do I apply UV lock?
      // final transform switches from face (NSWE) to world direction, the rest are composed in to apply first
      Function<Direction,Direction> transform = (d) -> rotateDirection(d, face);

      // flipping
      boolean flipV = uv.uvs[1] > uv.uvs[3];
      if (uv.uvs[0] > uv.uvs[2]) {
        // flip both
        if (flipV) {
          transform = transform.compose(Direction::getOpposite);
        } else {
          // flip U
          transform = transform.compose((d) -> {
            if (d.getAxis() == Axis.X) {
              return d.getOpposite();
            }
            return d;
          });
        }
      } else if (flipV) {
        transform = transform.compose((d) -> {
          if (d.getAxis() == Axis.Z) {
            return d.getOpposite();
          }
          return d;
        });
      }

      // rotation
      switch (uv.rotation) {
        // 90 degrees
        case 90:
          transform = transform.compose(Direction::rotateY);
          break;
        case 180:
          transform = transform.compose(Direction::getOpposite);
          break;
        case 270:
          transform = transform.compose(Direction::rotateYCCW);
          break;
      }

      return transform;
    }

    /**
     * Gets the name of this texture that supports connected textures, or null if never is connected
     * @param key  Name of the part texture
     * @return  Name of the connected texture
     */
    @Nullable
    private String getConnectedName(String key) {
      if (key.charAt(0) == '#') {
        key = key.substring(1);
      }
      // if the name is connected, we are done
      if (parent.connectedTextures.contains(key)) {
        return key;
      }

      // if we already found it, return what we found before
      if (nameMappingCache.containsKey(key)) {
        return nameMappingCache.get(key);
      }

      // otherwise, iterate into the parent models, trying to find a match
      String name = key;
      for(BlockModel model = parent.model; model != null; model = model.parent) {
        Either<Material, String> either = model.textures.get(name);
        if (either != null) {
          // if no name, its not connected
          Optional<String> newName = either.right();
          if (!newName.isPresent()) {
            nameMappingCache.put(key, null);
            return null;
          }
          // if the name is connected, we are done, return it
          name = newName.get();
          if (parent.connectedTextures.contains(name)) {
            nameMappingCache.put(key, name);
            return name;
          }
        }
      }
      // never found it
      nameMappingCache.put(key, null);
      return null;
    }

    /**
     * Gets the model based on the connections in the given model data
     * @param state  Block state
     * @return  Model with connections applied
     */
    @SuppressWarnings("deprecation")
    private BlockModel applyConnections(BlockState state) {
      // get the suffix based on the placement in world
      boolean[] faces = getFaces(state, transforms.getRotation().getMatrix());

      // will add new textures to this map based on connections
      Map<String,Either<Material, String>> textures = Maps.newHashMap(parent.model.textures);

      // copy each element with updated faces
      List<BlockPart> elements = Lists.newArrayList();
      for (BlockPart part : parent.model.getElements()) {
        Map<Direction,BlockPartFace> partFaces = new EnumMap<>(Direction.class);
        for (Map.Entry<Direction,BlockPartFace> entry : part.mapFaces.entrySet()) {
          // first, determine which texture to use on this side
          Direction dir = entry.getKey();
          BlockPartFace original = entry.getValue();
          BlockPartFace face = original;
          // if empty string, we can keep the old face
          String suffix = getTextureSuffix(faces, getTransform(dir, original.blockFaceUV));
          if (!suffix.isEmpty()) {
            // follow the texture name back to the original name
            // if it never reaches a connected texture, skip
            String connectedTexture = getConnectedName(original.texture);
            if (connectedTexture != null) {
              // suffix the texture
              String fullTexture = connectedTexture + suffix;
              face = new BlockPartFace(original.cullFace, original.tintIndex, "#" + fullTexture, original.blockFaceUV);
              // add the suffixed texture to the map if needed
              if (!textures.containsKey(fullTexture)) {
                textures.put(fullTexture, parent.suffixedTextures.get(fullTexture));
              }
            }
          }
          // add the updated face
          partFaces.put(dir, face);
        }
        // add the updated parts into a new model part
        elements.add(new BlockPart(part.positionFrom, part.positionTo, partFaces, part.partRotation, part.shade));
      }

      // create the final model, copying data we do not change
      BlockModel newModel = new BlockModel(parent.model.getParentLocation(), elements, textures,
                                           parent.model.isAmbientOcclusion(), parent.model.func_230176_c_(),
                                           parent.model.getAllTransforms(), Lists.newArrayList(parent.model.getOverrides()));
      newModel.name = parent.model.name;
      newModel.parent = parent.model.parent;
      return newModel;
    }

    /**
     * Gets the key to cache the given model data
     * @param state  Block state
     * @return  Key used to cache it
     */
    private static int getKey(BlockState state) {
      // iterate each of the six directions
      int key = 0;
      for (Direction dir : Direction.values()) {
        // if the prop is missing, treat as false. Prevents crashes if a resource pack maker attempts to use on a non-connected block
        BooleanProperty prop = ModelProperties.CONNECTED_DIRECTIONS.get(dir);
        if (state.has(prop) && state.get(prop)) {
          key |= 1 << dir.getIndex();
        }
      }
      return key;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
      if (state == null) {
        return super.getQuads(null, side, rand, data);
      }
      // get data based on state
      int cacheKey = getKey(state);
      // bake a new model if the orientation is not yet baked
      if (cache[cacheKey] == null) {
        BlockModel connected = applyConnections(state);
        cache[cacheKey] = connected.bakeModel(bakery, connected, ModelLoader.defaultTextureGetter(), transforms, BAKE_LOCATION, true);
      }
      // get the model for the given orientation
      return cache[cacheKey].getQuads(state, side, rand, data);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
      return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }
  }

  public static class Loader implements IModelLoader<ConnectedModel> {
    /** Shared loader instance */
    public static final ConnectedModel.Loader INSTANCE = new ConnectedModel.Loader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public ConnectedModel read(JsonDeserializationContext context, JsonObject json) {
      BlockModel model = ModelUtils.deserialize(context, json);

      // need at least one connected texture
      JsonArray connected = JSONUtils.getJsonArray(json, "connected");
      if (connected.size() == 0) {
        throw new JsonSyntaxException("Must have at least one texture in connected");
      }

      // build texture list
      ImmutableSet.Builder<String> connectedTextures = new ImmutableSet.Builder<>();
      for (int i = 0; i < connected.size(); i++) {
        String name = JSONUtils.getString(connected.get(i), "connected[" + i + "]");
        // texture must be in the model
        if(!model.textures.containsKey(name)) {
          throw new JsonSyntaxException("Invalid connected texture " + name + ", missing in model");
        }
        connectedTextures.add(name);
      }
      // final model instance
      return new ConnectedModel(model, connectedTextures.build());
    }
  }
}
