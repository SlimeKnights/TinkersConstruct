package slimeknights.tconstruct.debug;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

// The old forge thingie code from Lex
public class TextureDump {

  @SubscribeEvent
  public void postTextureStitch(TextureStitchEvent.Post e) throws Exception {
    saveGlTexture(getName(e.getMap()), e.getMap().getGlTextureId(), getMip(e.getMap()));

  }

  private static Field fName;

  private static String getName(TextureMap map) throws Exception {
    if(fName == null) {
      fName = TextureMap.class.getDeclaredFields()[6];
      fName.setAccessible(true);
    }
    return ((String) fName.get(map)).replace('/', '_');
  }

  private static Field fMip;

  private static int getMip(TextureMap map) throws Exception {
    if(fMip == null) {
      fMip = TextureMap.class.getDeclaredFields()[8];
      fMip.setAccessible(true);
    }
    return fMip.getInt(map);
  }


  public static void saveGlTexture(String name, int textureId, int mipmapLevels) {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

    GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

    for(int level = 0; level <= mipmapLevels; level++) {
      int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH);
      int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT);
      int size = width * height;

      BufferedImage bufferedimage = new BufferedImage(width, height, 2);
      File output = new File(name + "_" + level + ".png");

      IntBuffer buffer = BufferUtils.createIntBuffer(size);
      int[] data = new int[size];

      GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
      buffer.get(data);
      bufferedimage.setRGB(0, 0, width, height, data, 0, width);

      try {
        ImageIO.write(bufferedimage, "png", output);
        FMLLog.info("[TextureDump] Exported png to: " + output.getAbsolutePath());
      } catch(IOException ioexception) {
        FMLLog.info("[TextureDump] Unable to write: ", ioexception);
      }
    }
  }
}
