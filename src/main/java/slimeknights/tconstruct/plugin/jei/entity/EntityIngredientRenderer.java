package slimeknights.tconstruct.plugin.jei.entity;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renderer for entity type ingredients
 */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class EntityIngredientRenderer implements IIngredientRenderer<EntityType> {
  /** Entity types that will not render, as they either errored or are the wrong type */
  private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

  private final int size;

  /** Cache of entities for each entity type */
  private final Map<EntityType<?>,Entity> ENTITY_MAP = new HashMap<>();

  @Override
  public void render(MatrixStack matrixStack, int x, int y, @Nullable EntityType type) {
    if (type != null) {
      World world = MinecraftClient.getInstance().world;
      if (world != null && !IGNORED_ENTITIES.contains(type)) {
        Entity entity;
        // players cannot be created using the type, but we can use the client player
        // side effect is it renders armor/items
        if (type == EntityType.PLAYER) {
          entity = MinecraftClient.getInstance().player;
        } else {
          // entity is created with the client world, but the entity map is thrown away when JEI restarts so they should be okay I think
          entity = ENTITY_MAP.computeIfAbsent(type, t -> t.create(world));
        }
        // only can draw living entities, plus non-living ones don't get recipes anyways
        if (entity instanceof LivingEntity) {
          // scale down large mobs, but don't scale up small ones
          LivingEntity livingEntity = (LivingEntity) entity;
          int scale = size / 2;
          float height = entity.getHeight();
          float width = entity.getWidth();
          if (height > 2 || width > 2) {
            scale = (int)(size / Math.max(height, width));
          }
          // catch exceptions drawing the entity to be safe, any caught exceptions blacklist the entity
          try {
            InventoryScreen.drawEntity(x + size / 2, y + size, scale, 0, 10, livingEntity);
            return;
          } catch (Exception e) {
            TConstruct.log.error("Error drawing entity " + type.getRegistryName(), e);
            IGNORED_ENTITIES.add(type);
            ENTITY_MAP.remove(type);
          }
        } else {
          // not living, so might as well skip next time
          IGNORED_ENTITIES.add(type);
          ENTITY_MAP.remove(type);
        }
      }

      // fallback, draw a pink and black "spawn egg"
      MinecraftClient minecraft = MinecraftClient.getInstance();
      minecraft.getTextureManager().bindTexture(EntityMeltingRecipeCategory.BACKGROUND_LOC);
      int offset = (size - 16) / 2;
      Screen.drawTexture(matrixStack, x + offset, y + offset, 149f, 58f, 16, 16, 256, 256);
    }
  }

  @Override
  public List<Text> getTooltip(EntityType type, TooltipContext flag) {
    List<Text> tooltip = new ArrayList<>();
    tooltip.add(type.getName());
    return tooltip;
  }
}
