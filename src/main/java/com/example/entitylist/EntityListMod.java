package com.example.entitylist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class EntityListMod implements ModInitializer {
    public static final String MOD_ID = "entitylist";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyBinding listKey;

    @Override
    public void onInitialize() {
        listKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.entitylist.list", GLFW.GLFW_KEY_L, "category.entitylist"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (listKey.wasPressed()) {
                listEntities(client);
            }
        });

        LOGGER.info("EntityList mod initialized. Press L to list entities in your chunk.");
    }

    private void listEntities(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        ChunkPos chunkPos = client.player.getChunkPos();
        List<Entity> entities = client.world.getEntities()
                .stream()
                .filter(e -> e.getChunkPos().equals(chunkPos))
                .collect(Collectors.toList());

        if (entities.isEmpty()) {
            client.player.sendMessage(Text.literal("§6[EntityList] §cNo entities found in your chunk."), false);
            return;
        }

        client.player.sendMessage(Text.literal("§6[EntityList] §aEntities in chunk " +
                chunkPos.x + ", " + chunkPos.z + " (" + entities.size() + "):"), false);

        for (Entity e : entities) {
            String name = e.getName().getString();
            String type = e.getType().getTranslationKey();
            String coords = String.format("(%.1f, %.1f, %.1f)", e.getX(), e.getY(), e.getZ());
            client.player.sendMessage(
                    Text.literal("§7- " + name + " §8(" + type + ") §f" + coords),
                    false
            );
        }
    }
          }
