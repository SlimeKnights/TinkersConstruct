package slimeknights.tconstruct.library.client.modifiers.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.util.GeometryContextWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Context for baking modifier models with dynamic material resolution.
 * Maintains a map from tool to multimap of modifier -> material suppliers.
 * Use {@link #getResolver(IToolStackView, ModifierEntry)} to obtain a resolver
 * that tracks circular dependencies.
 */
@RequiredArgsConstructor
public class ModifierBakingContext {

    /** Storage: tool -> (modifier -> suppliers), insertion order preserved. */
    private final Map<IToolStackView, Multimap<ModifierEntry, MaterialSupplier>> map = new HashMap<>();

    public final IGeometryBakingContext base;
    public final SimpleBakedModel.Builder builder;
    public final ModelState transform;
    public final ResourceLocation location;

    /**
     * Adds a supplier for the given tool and modifier.
     *
     * @return this context (fluent)
     */
    public ModifierBakingContext with(IToolStackView tool, ModifierEntry modifier, MaterialSupplier supplier) {
        map.computeIfAbsent(tool, k -> LinkedHashMultimap.create())
                .put(modifier, supplier);
        return this;
    }

    /**
     * Creates a resolver bound to the given tool and modifier.
     * <strong>Reuse the same resolver instance throughout a single resolution
     * process.</strong>
     */
    public TextureResolver getResolver(IToolStackView tool, ModifierEntry modifier) {
        return new TextureResolver(tool, modifier);
    }

    /**
     * Resolver bound to a specific tool and modifier.
     * Tracks the resolution chain to detect circular references.
     */
    public class TextureResolver extends GeometryContextWrapper {

        private final IToolStackView tool;
        private final ModifierEntry modifier;
        private final List<String> chain = new ArrayList<>();

        private TextureResolver(IToolStackView tool, ModifierEntry modifier) {
            super(ModifierBakingContext.this.base); // explicit outer access
            this.tool = tool;
            this.modifier = modifier;
        }

        /**
         * Checks if the bound tool/modifier can provide a material with the given name.
         * Returns false if a circular reference is detected.
         */
        @Override
        public boolean hasMaterial(String name) {
            if (chain.contains(name)) {
                TConstruct.LOG.warn("Circular reference: {} -> {}", Joiner.on("->").join(chain), name);
                return false;
            }
            chain.add(name);
            try {
                return map.getOrDefault(tool, ImmutableMultimap.of())
                        .get(modifier)
                        .stream()
                        .anyMatch(s -> s.hasMaterial(tool, modifier, name, this));
            } finally {
                chain.remove(name);
            }
        }

        /**
         * Resolves the material for the given name, using the first matching supplier.
         * Returns a missing-texture material if not found or if a cycle is detected.
         */
        @Override
        public Material getMaterial(String name) {
            if (chain.contains(name)) {
                TConstruct.LOG.warn("Circular reference of block modifier model texture: {} -> {}", Joiner.on("->").join(chain), name);
                return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            }
            chain.add(name);
            try {
                return map.getOrDefault(tool, ImmutableMultimap.of())
                        .get(modifier)
                        .stream()
                        .filter(s -> s.hasMaterial(tool, modifier, name, this))
                        .findFirst()
                        .map(s -> s.getMaterial(tool, modifier, name, this))
                        .orElseGet(() -> new Material(TextureAtlas.LOCATION_BLOCKS,
                                MissingTextureAtlasSprite.getLocation()));
            } finally {
                chain.remove(name);
            }
        }
    }

    /**
     * Supplier of materials for a given tool, modifier, and material name.
     * Implementations should use the provided resolver for nested references.
     */
    public interface MaterialSupplier {

        /**
         * Returns true if this supplier can provide the material.
         */
        boolean hasMaterial(IToolStackView tool, ModifierEntry modifier, String name, TextureResolver resolver);

        /**
         * Returns the actual Material. Should only be called if hasMaterial returned
         * true.
         */
        Material getMaterial(IToolStackView tool, ModifierEntry modifier, String name, TextureResolver resolver);
    }
}