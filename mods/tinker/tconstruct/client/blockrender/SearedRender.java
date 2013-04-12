package mods.tinker.tconstruct.client.blockrender;

import mods.tinker.common.BlockSkinRenderHelper;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.logic.CastingTableLogic;
import mods.tinker.tconstruct.logic.FaucetLogic;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SearedRender implements ISimpleBlockRenderingHandler
{
	public static int searedModel = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == searedModel)
		{
			if (metadata == 0)
			{
				//Top
				renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
				renderDo(renderer, block, metadata);

				//Lip
				renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
				renderDo(renderer, block, metadata);

				//Legs
				renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
				renderDo(renderer, block, metadata);
			}
			else
			{
				renderer.setRenderBounds(0.25, 0.25, 0.625, 0.75, 0.375, 1);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.25, 0.25, 0.625, 0.375, 0.625, 1);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.625, 0.25, 0.625, 0.75, 0.625, 1);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1);
			}
		}
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		if (modelID == searedModel)
		{
			int metadata = world.getBlockMetadata(x, y, z);
			if (metadata == 0)
			{
				//Top
				renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);

				//Lip
				renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
				renderer.renderStandardBlock(block, x, y, z);

				//Legs
				renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);

				CastingTableLogic logic = (CastingTableLogic) world.getBlockTileEntity(x, y, z);
				if (logic.liquid != null)
				{
					float height = logic.getLiquidAmount() / (logic.getCapacity() * 1.03F) / 16F;
					renderer.setRenderBounds(0.0625F, 0.9375F, 0.0625F, 0.9375F, 0.9375F + height, 0.9375F);
					if (logic.liquid.itemID < 4096) //Block
					{
						Block liquidBlock = Block.blocksList[logic.liquid.itemID];
						if (liquidBlock != null)
						{
							//ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
							BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, logic.liquid.itemMeta, x, y, z, renderer, world);
						}
					}
					else
					//Item
					{
						Item liquidItem = Item.itemsList[logic.liquid.itemID];
						if (liquidItem != null)
						{
							//ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
							BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(logic.liquid.itemMeta), logic.liquid.itemMeta, x, y, z, renderer, world);
						}
					}
				}
			}
			else if (metadata == 1)
			{
				FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
				float xMin = 0.375F, zMin = 0.375F, xMax = 0.625F, zMax = 0.625F;
				switch (logic.getRenderDirection())
				{
				case 2:
					renderer.setRenderBounds(0.25, 0.25, 0.625, 0.75, 0.375, 1);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.25, 0.375, 0.625, 0.375, 0.625, 1);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.625, 0.375, 0.625, 0.75, 0.625, 1);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1);
					zMin = 0.5F;
					//zMin = 0.625F;
					break;
				case 3:
					renderer.setRenderBounds(0.25, 0.25, 0, 0.75, 0.375, 0.375);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.25, 0.375, 0, 0.375, 0.625, 0.375);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.625, 0.375, 0, 0.75, 0.625, 0.375);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.375, 0.375, 0, 0.625, 0.625, 0.375);
					zMax = 0.5F;
					break;
				case 4:
					renderer.setRenderBounds(0.625, 0.25, 0.25, 1, 0.375, 0.75);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.625, 0.375, 0.25, 1, 0.625, 0.375);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.625, 0.375, 0.625, 1, 0.625, 0.75);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0.625, 0.375, 0.375, 1, 0.625, 0.625);
					xMin = 0.5F;
					break;
				case 5:
					renderer.setRenderBounds(0, 0.25, 0.25, 0.375, 0.375, 0.75);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0, 0.375, 0.25, 0.375, 0.625, 0.375);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0, 0.375, 0.625, 0.375, 0.625, 0.75);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.setRenderBounds(0, 0.375, 0.375, 0.375, 0.625, 0.625);
					xMax = 0.5F;
					break;
				}

				float yMin = 0F;
				int uID = world.getBlockId(x, y-1, z);
				int uMeta = world.getBlockMetadata(x, y-1, z);
				if (uID == TContent.searedBlock.blockID && uMeta == 0)
				{
					yMin = -0.125F;
				}
				else if (uID == TContent.lavaTank.blockID)
				{
					yMin = -1F;
				}
				if (logic.liquid != null)
				{
					ItemStack blockToRender = new ItemStack(logic.liquid.itemID, 1, logic.liquid.itemMeta);
					if (blockToRender.itemID < 4096) //Block
					{
						Block liquidBlock = Block.blocksList[blockToRender.itemID];
						//ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
						BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), x, y, z, renderer, world);
						renderer.setRenderBounds(xMin, yMin, zMin, xMax, 0.625, zMax);
						BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), x, y, z, renderer, world);
					}
					else
					//Item
					{
						Item liquidItem = Item.itemsList[blockToRender.itemID];
						//ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
						int meta = blockToRender.getItemDamage();
						BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(meta), meta, x, y, z, renderer, world);
						renderer.setRenderBounds(xMin, yMin, zMin, xMax, 0.625, zMax);
						BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(meta), meta, x, y, z, renderer, world);
					}
					//renderer.renderStandardBlock(block, x, y, z);
				}

			}
			else
			{
				renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory ()
	{
		return true;
	}

	@Override
	public int getRenderId ()
	{
		return searedModel;
	}

	private void renderDo (RenderBlocks renderblocks, Block block, int meta)
	{
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
