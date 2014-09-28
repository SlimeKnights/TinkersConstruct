package tconstruct.library.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

// Stolen from CofHLib RegistryUtil
public final class TextureHelper {
    private TextureHelper() {}

    @SideOnly(Side.CLIENT)
    public static boolean textureExists(ResourceLocation texture) {

        try {
            Minecraft.getMinecraft().getResourceManager().getAllResources(texture);
            return true;
        } catch (Throwable t) { // pokemon!
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean textureExists(String texture) {

        return textureExists(new ResourceLocation(texture));
    }

    @SideOnly(Side.CLIENT)
    public static boolean blockTextureExists(String texture) {

        int i = texture.indexOf(':');

        if (i > 0) {
            texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
        } else {
            texture = "textures/blocks/" + texture;
        }
        return textureExists(texture + ".png");
    }

    @SideOnly(Side.CLIENT)
    public static boolean itemTextureExists(String texture) {

        int i = texture.indexOf(':');

        if (i > 0) {
            texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
        } else {
            texture = "textures/items/" + texture;
        }
        return textureExists(texture + ".png");
    }
}
