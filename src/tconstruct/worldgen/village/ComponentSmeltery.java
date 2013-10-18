package tconstruct.worldgen.village;

import java.util.List;
import java.util.Random;

import tconstruct.TConstruct;
import tconstruct.common.TContent;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentSmeltery extends ComponentVillage
{
    private int averageGroundLevel = -1;
    
    public ComponentSmeltery() {}

    public ComponentSmeltery(ComponentVillageStartPiece par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static ComponentSmeltery buildComponent (ComponentVillageStartPiece villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
    {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 3, 7, p4);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentSmeltery(villagePiece, p5, random,
                structureboundingbox, p4) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts (World world, Random random, StructureBoundingBox sbb)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 2, 0);
        }

        /**
         * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
         * maxZ, int placeBlockId, int replaceBlockId, boolean alwaysreplace)
         */

        this.fillWithBlocks(world, sbb, 1, 0, 0, 7, 0, 6, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false); //Base
        this.fillWithBlocks(world, sbb, 0, 0, 1, 0, 0, 5, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false);
        this.fillWithBlocks(world, sbb, 8, 0, 1, 8, 0, 5, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false);
        this.fillWithBlocks(world, sbb, 0, 1, 0, 9, 3, 7, 0, 0, false);

        this.fillWithMetaBlocks(world, sbb, 2, 0, 1, 6, 2, 5, TContent.smeltery.blockID, 2, TContent.smeltery.blockID, 2, false); //Basin
        this.fillWithBlocks(world, sbb, 3, 1, 2, 5, 2, 4, 0, 0, false);

        this.placeBlockAtCurrentPosition(world, TContent.searedBlock.blockID, 0, 1, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TContent.searedBlock.blockID, 2, 1, 1, 4, sbb);
        this.placeBlockAtCurrentPosition(world, TContent.searedBlock.blockID, 0, 7, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TContent.searedBlock.blockID, 2, 7, 1, 4, sbb);

        for (int l = 1; l < 6; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.fillCurrentPositionBlocksDownwards(world, Block.stoneBrick.blockID, 0, i1, -1, l, sbb);
            }
        }

        for (int l = 0; l < 7; ++l)
        {
            for (int i1 = 1; i1 < 8; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.fillCurrentPositionBlocksDownwards(world, Block.stoneBrick.blockID, 0, i1, -1, l, sbb);
            }
        }
        return true;
    }

    int remapDirection (int direction)
    {
        TConstruct.logger.info("Direction: " + direction);
        switch (direction)
        {
        case 0:
            return 2;
        case 1:
            return 3;
        case 2:
            return 1;
        case 3:
            return 0;
        }
        TConstruct.logger.severe("This shouldn't happen (remapDirection in tconstruct.worldgen.village.ComponentSmeltery)");
        return -1;
    }

    protected void fillWithMetaBlocks (World par1World, StructureBoundingBox par2StructureBoundingBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int placeBlockID,
            int placeBlockMeta, int replaceBlockID, int replaceBlockMeta, boolean alwaysReplace)
    {
        int i2 = this.getBiomeSpecificBlock(placeBlockID, placeBlockMeta);
        int j2 = this.getBiomeSpecificBlockMetadata(placeBlockID, placeBlockMeta);
        int k2 = this.getBiomeSpecificBlock(replaceBlockID, replaceBlockMeta);
        int l2 = this.getBiomeSpecificBlockMetadata(replaceBlockID, replaceBlockMeta);
        super.fillWithMetadataBlocks(par1World, par2StructureBoundingBox, minX, minY, minZ, maxX, maxY, maxZ, i2, j2, k2, l2, alwaysReplace);
    }
}
