package tconstruct.landmine;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.ForgeDirection;

/**
 * 
 * @author fuj1n
 * 
 */
public class Helper {

	public static ForgeDirection convertMetaToForgeOrientation(int metadata) {
		switch (metadata) {
		case 6:
			return ForgeDirection.DOWN;
		case 7:
			return ForgeDirection.UP;
		case 1:
			return ForgeDirection.WEST;
		case 3:
			return ForgeDirection.NORTH;
		case 2:
			return ForgeDirection.EAST;
		case 4:
			return ForgeDirection.SOUTH;
		case 5:
			return ForgeDirection.DOWN;
		case 0:
			return ForgeDirection.UP;
		}

		return ForgeDirection.UNKNOWN;
	}

	public static void renderInventoryCube(Block block, int metadata,
			int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0, 0, 0,
				renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
	}

	// Useful to make small tweaks to private fields
	public static class PrivateFieldHelper {
		public static void setPrivateField(Class clazz, String name,
				Object obj, Object val) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				f.set(obj, val);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		public static Object getPrivateField(Class clazz, String name,
				Object obj) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return f.get(obj);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return null;
		}

		public static Field getField(Class clazz, String name) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
