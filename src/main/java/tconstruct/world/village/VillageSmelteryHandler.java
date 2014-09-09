package tconstruct.world.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import java.util.*;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageSmelteryHandler implements IVillageCreationHandler
{
    @Override
    public PieceWeight getVillagePieceWeight (Random random, int i)
    {
        return new PieceWeight(ComponentSmeltery.class, 9, 1);
    }

    @Override
    public Class<?> getComponentClass ()
    {
        return ComponentSmeltery.class;
    }

    @Override
    public Object buildComponent (PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
    {
        return ComponentSmeltery.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
    }

}
