package tconstruct.test;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

public class TinkerModelManager {
  public static MultiModel pickModel;

  @SubscribeEvent
  public void createModels(ModelBakeEvent event) {
    ModelResourceLocation res = new ModelResourceLocation("tconstruct:TestTool", "inventory");
    //IModel original = event.modelLoader.getModel(res);
    IBakedModel originalModel = event.modelManager.getModel(res);
    IBakedModel model1 = event.modelManager.getModel(new ModelResourceLocation("tconstruct:pick_head", "inventory"));
    IBakedModel model2 = event.modelManager.getModel(new ModelResourceLocation("tconstruct:pick_handle", "inventory"));
    IBakedModel model3 = event.modelManager.getModel(new ModelResourceLocation("tconstruct:pick_binding", "inventory"));

    pickModel = new MultiModel(originalModel, model1, model2, model3);

    for(Material material : TinkerRegistry.getAllMaterials()) {
      pickModel.addTexture(material.identifier + "_head", CustomTextureCreator.sprites.get(
          "pick_head_" + material.identifier));
      pickModel.addTexture(material.identifier + "_handle", CustomTextureCreator.sprites.get("pick_handle_" + material.identifier));
    }

    event.modelRegistry.putObject(res, pickModel);
  }
}
