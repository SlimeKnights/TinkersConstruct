package slimeknights.tconstruct.library.client.modifiers.block.model;

import java.util.Map;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.json.EitherLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;

public interface TexturedBlockModifierModel extends BlockModifierModel, ModifierBakingContext.MaterialSupplier {
    public static final Loadable<Map<String, Either<Material, String>>> SPRITE_LOADABLE = StringLoadable.DEFAULT.mapWithValues(EitherLoadable.create(ModifierModel.MATERIAL_LOADABLE, StringLoadable.DEFAULT));

    /**
     * Removes a leading '#' prefix from a reference key if present.
     * If the string is null, empty, or doesn't start with '#', returns it unchanged.
     */
    static String resolveReferenceKey(String reference) {
        if (reference == null || reference.isEmpty()) {
            return reference;
        }
        if (reference.startsWith("#")) {
            return reference.substring(1);
        }
        return reference;
    }

    LoadableField<Map<String, Either<Material, String>>, TexturedBlockModifierModel> TEXTURE_FIELD = SPRITE_LOADABLE.defaultField("textures", Map.of(), TexturedBlockModifierModel::texture);

    /**
     * Fallback texture used when a requested material name is not defined in the texture map.
     */
    Material MISSING = new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());

    Map<String, Either<Material, String>> texture();

    @Override
    default boolean hasMaterial(IToolStackView tool, ModifierEntry modifier, String name, ModifierBakingContext.TextureResolver resolver) {
        Either<Material, String> texture = this.texture().get(name);
        if (texture == null)
            return false;
        if (texture.left().isPresent())
            return true;
        return resolver.hasMaterial(resolveReferenceKey(texture.right().get()));
    }

    @Override
    default Material getMaterial(IToolStackView tool, ModifierEntry modifier, String name, ModifierBakingContext.TextureResolver resolver) {
        return this.texture().getOrDefault(name, Either.left(MISSING)).map(l -> l, r -> resolver.getMaterial(resolveReferenceKey(r)));
    }
}
