package slimeknights.tconstruct.library.client.modifiers.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;

import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.block.model.ElementBlockModifierModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

/**
 * Manager for getting block modifier models
 */
@Log4j2
public class BlockModifierModelManager extends SimpleJsonResourceReloadListener {
    /** Folder for the block modifier models */
    public static final String FOLDER = "tinkering/modifiers/models";
    /** Instance of this manager */
    public static final BlockModifierModelManager INSTANCE = new BlockModifierModelManager();

    private volatile Map<ResourceLocation, BlockModifierModel> models = Map.of();

    public BlockModifierModelManager() {
        super(JsonHelper.DEFAULT_GSON, FOLDER);
    }

    /**
     * Load models from the resource manager
     * Only used in BlockModifierModelMapLoader
     * 
     * @param resourceManager Resource manager
     */
    void load(ResourceManager resourceManager) {
        apply(prepare(resourceManager, InactiveProfiler.INSTANCE), resourceManager, InactiveProfiler.INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, BlockModifierModel> newModels = new HashMap<>();
        for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            if (entry.getValue() instanceof JsonObject obj) {
                try {
                    if (!obj.has("type")) {
                        newModels.put(entry.getKey(), ElementBlockModifierModel.LOADER.deserialize(obj));
                    } else {
                        newModels.put(entry.getKey(), BlockModifierModel.LOADER.deserialize(obj));
                    }
                } catch (Exception e) {
                    log.error("Error deserializing block modifier model: {}", entry.getKey(), e);
                }
            } else {
                log.error("Invalid block modifier model (not a JSON object): {}", entry.getKey());
            }
        }
        this.models = Collections.unmodifiableMap(newModels);
        log.info("Loaded {} block modifier models: {}", newModels.size(), newModels.keySet());
    }

    /**
     * Get a model by ID
     * 
     * @param id Model ID
     * @return Model
     */
    @Nullable
    public BlockModifierModel getModel(ResourceLocation id) {
        BlockModifierModel model = models.get(id);
        if (model == null) {
            log.warn("Block modifier model not found: {}, available: {}", id, models.keySet());
        }
        return model;
    }

    /**
     * Resolves all models in the list, converting resource location references into actual models
     * 
     * @param models List of either model instances or resource location references
     * @return Resolved list of models
     */
    public List<BlockModifierModel> getModels(List<Either<BlockModifierModel, ResourceLocation>> models) {
        List<BlockModifierModel> resolved = new ArrayList<>();
        for (Either<BlockModifierModel, ResourceLocation> either : models) {
            either.left().ifPresent(resolved::add);
            either.right().ifPresent(id -> {
                BlockModifierModel model = getModel(id);
                if (model != null) {
                    resolved.add(model);
                } else {
                    log.warn("Block modifier model not found: {}", id);
                }
            });
        }
        return resolved;
    }

    /**
     * Get the ID of a model
     * 
     * @param model Model
     * @return Model ID
     */
    @Nullable
    public ResourceLocation getKey(BlockModifierModel model) {
        if (model == null) {
            return null;
        }
        for (Map.Entry<ResourceLocation, BlockModifierModel> entry : models.entrySet()) {
            if (entry.getValue() == model) {
                return entry.getKey();
            }
        }
        return null;
    }
}
