package slimeknights.tconstruct.world.village.workshop;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageToolWorkshopHandler implements IVillageCreationHandler {

  @Override
  public PieceWeight getVillagePieceWeight(Random random, int i) {
    return new PieceWeight(ComponentToolWorkshop.class, 30, i + random.nextInt(4));
  }

  @Override
  public Class<?> getComponentClass() {
    return ComponentToolWorkshop.class;
  }

  @Override
  public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
    return ComponentToolWorkshop.createPiece(startPiece, pieces, random, p1, p2, p3, facing, p5);
  }

}
