package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.format.AmmoPosition;
import slimeknights.tconstruct.library.client.model.format.ToolModelOverride;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;

public class ToolModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".tcon";

  // used to create only actually needed textures in the texturegenerator instead of ALL materials for all parts
  private static final Map<ResourceLocation, ToolCore> modelItemMap = Maps.newHashMap();

  public static void addPartMapping(ResourceLocation resourceLocation, ToolCore tool) {
    modelItemMap.put(resourceLocation, tool);
  }

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath().endsWith(EXTENSION); // tinkertoolmodel extension. Foo.tcon.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    try {
      // Modelblock is used since our format is compatible to the vanilla format
      // and we don't have to write our own json deserializer
      // it also provides us with the textures
      Map<String, String> textures = ModelHelper.loadTexturesFromJson(modelLocation);
      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = ModelHelper.loadTransformFromJson(modelLocation);
      ImmutableList<ToolModelOverride> overrides = ModelHelper.loadToolModelOverridesFromJson(modelLocation);
      AmmoPosition ammoPosition = ModelHelper.loadAmmoPositionFromJson(modelLocation);
      Float[] rotations = ModelHelper.loadLayerRotations(modelLocation);

      if(rotations.length > 0 && textures.size() != rotations.length) {
        TinkerRegistry.log.error("Toolmodel {} has invalid layerrotation entry: Size should be {} but is {}; Skipping rotations.", modelLocation, textures.size(), rotations.length);
        rotations = new Float[0];
      }

      ImmutableList.Builder<ResourceLocation> defaultTextureListBuilder = ImmutableList.builder();
      List<MaterialModel> parts = Lists.newArrayList();
      List<MaterialModel> brokenParts = Lists.newArrayList();

      ToolCore toolCore = modelItemMap.get(MaterialModelLoader.getReducedPath(modelLocation));

      for(Map.Entry<String, String> entry : textures.entrySet()) {
        String name = entry.getKey();
        try {
          int i;
          List<MaterialModel> listToAdd;

          if(name.startsWith("layer")) {
            i = Integer.valueOf(name.substring(5));
            listToAdd = parts;
          }
          else if(name.startsWith("broken")) {
            i = Integer.valueOf(name.substring(6));
            listToAdd = brokenParts;
          }
          // invalid entry, ignore
          else {
            TinkerRegistry.log.warn("Toolmodel {} has invalid texture entry {}; Skipping layer.", modelLocation, name);
            continue;
          }

          ResourceLocation location = new ResourceLocation(entry.getValue());
          MaterialModel partModel = new MaterialModel(ImmutableList.of(location));
          while(listToAdd.size() <= i) {
            listToAdd.add(null);
          }
          listToAdd.set(i, partModel);

          defaultTextureListBuilder.add(location);
          registerCustomTextures(i, location, toolCore);
        } catch(NumberFormatException e) {
          TinkerRegistry.log.error("Toolmodel {} has invalid texture entry {}; Skipping layer.", modelLocation, name);
        }
      }

      // create overrides
      for(ToolModelOverride override : overrides) {
        for(Map.Entry<String, String> entry : override.textures.entrySet()) {
          String name = entry.getKey();
          try {
            int i;
            TIntObjectHashMap<MaterialModel> mapToAdd;

            if(name.startsWith("layer")) {
              i = Integer.valueOf(name.substring(5));
              mapToAdd = override.partModelReplacement;
            }
            else if(name.startsWith("broken")) {
              i = Integer.valueOf(name.substring(6));
              mapToAdd = override.brokenPartModelReplacement;
            }
            // invalid entry, ignore
            else {
              TinkerRegistry.log.warn("Toolmodel {} has invalid texture override entry {}; Skipping layer.", modelLocation, name);
              continue;
            }

            ResourceLocation location = new ResourceLocation(entry.getValue());
            MaterialModel partModel = new MaterialModel(ImmutableList.of(location));
            mapToAdd.put(i, partModel);

            registerCustomTextures(i, location, toolCore);
          } catch(NumberFormatException e) {
            TinkerRegistry.log.error("Toolmodel {} has invalid texture entry {}; Skipping layer.", modelLocation, name);
          }
        }
      }

      // remove models/item/ and .tcon
      String toolName = FilenameUtils.removeExtension(modelLocation.getResourcePath().substring(12));
      IModel mods;
      ModifierModel modifiers = null;
      try {
        mods = ModelLoaderRegistry.getModel(ModifierModelLoader.getLocationForToolModifiers(modelLocation.getResourceDomain(), toolName));

        if(mods == null || !(mods instanceof ModifierModel)) {
          TinkerRegistry.log.trace(
              "Toolmodel {} does not have any modifiers associated with it. Be sure that the Tools internal name, the Toolmodels filename and the name used inside the Modifier Model Definition match!",
              modelLocation);
        }
        else {
          modifiers = (ModifierModel) mods;

          for(ToolModelOverride toolModelOverride : overrides) {
            if(toolModelOverride.modifierSuffix != null) {
              String modifierName = toolName + toolModelOverride.modifierSuffix;
              IModel extraModel = ModelLoaderRegistry.getModel(ModifierModelLoader.getLocationForToolModifiers(modelLocation.getResourceDomain(), modifierName));
              if(extraModel instanceof ModifierModel) {
                ModifierModel overriddenModifierModel = new ModifierModel();
                // fill in non-overridden modifiers
                for(Map.Entry<String, String> entry : modifiers.getModels().entrySet()) {
                  overriddenModifierModel.addModelForModifier(entry.getKey(), entry.getValue());
                }
                // overwrite overridden modifiers
                for(Map.Entry<String, String> entry : ((ModifierModel) extraModel).getModels().entrySet()) {
                  overriddenModifierModel.addModelForModifier(entry.getKey(), entry.getValue());
                }
                toolModelOverride.overrideModifierModel = overriddenModifierModel;
              }
            }
          }
        }
      } catch(Exception e) {
        TinkerRegistry.log.error(e);
        modifiers = null;
      }

      return new ToolModel(defaultTextureListBuilder.build(), parts, brokenParts, rotations, modifiers, transforms, overrides, ammoPosition);
    } catch(IOException e) {
      TinkerRegistry.log.error("Could not load multimodel {}", modelLocation.toString());
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  private void registerCustomTextures(int i, ResourceLocation resourceLocation, ToolCore toolCore) {
    if(toolCore == null) {
      CustomTextureCreator.registerTexture(resourceLocation);
    }
    else {
      for(IToolPart part : toolCore.getRequiredComponents().get(i).getPossibleParts()) {
        CustomTextureCreator.registerTextureForPart(resourceLocation, part);
      }
    }
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

  }
}
