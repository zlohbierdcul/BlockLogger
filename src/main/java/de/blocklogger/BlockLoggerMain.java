package de.blocklogger;

import com.google.gson.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class BlockLoggerMain implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {

		LOGGER.info("Im the BlockLogger Mod!");

		File directory = new File("./blocklogger");
		if (!directory.exists()) directory.mkdir();

		createBlockFile();
		createItemFile();

		LOGGER.info("BlockLogger: I'm done with my shit!");
	}

	private void createBlockFile() {
		File file = new File("./blocklogger/blocks.json");
		JsonObject jsonObject = new JsonObject();

		if (file.exists()) {
			LOGGER.info("BlockLogger: Block logging skipped, the file already exists");
			return;
		}

		LOGGER.info("BlockLogger: Starting Block logging ...");
		List<String> blockNames = Registry.BLOCK.stream().map(Block::toString).toList();

		try {
			file.createNewFile();
		} catch (IOException e) {
			LOGGER.error("BlockLogger: File Error! File could not be created!");
			return;
		}

		for (String m : blockNames) {
			String block = m.replace("Block{", "").replace("}","");
			String mod = block.split(":")[0];

			if (!jsonObject.has(mod)) {
				jsonObject.add(mod, new JsonArray());
			}

			jsonObject.getAsJsonArray(mod).add(block);
		}

		if (writeFile(file, jsonObject)) {
			LOGGER.info("BlockLogger: Finished Block logging!");
		} else {
			LOGGER.error("BlockLogger: Failed Block logging!");
		}
	}

	private void createItemFile() {
		File file = new File("./blocklogger/items.json");
		JsonObject jsonObject = new JsonObject();

		if (file.exists()) {
			LOGGER.info("BlockLogger: Item logging skipped, the file already exists");
			return;
		}

		LOGGER.info("BlockLogger: Starting Item logging ...");
		List<String> itemNames = Registry.ITEM.stream().map(Item::getTranslationKey).toList();

		try {
			file.createNewFile();
		} catch (IOException e) {
			LOGGER.error("BlockLogger: File Error! File could not be created!");
			return;
		}

		for (String i : itemNames) {
			String item = formatItemName(i);
			String mod = item.split(":")[0];

			if (!jsonObject.has(mod)) {
				jsonObject.add(mod, new JsonArray());
			}

			jsonObject.getAsJsonArray(mod).add(item);
		}

		if (writeFile(file, jsonObject)) {
			LOGGER.info("BlockLogger: Finished Item logging!");
		} else {
			LOGGER.error("BlockLogger: Failed Item logging!");
		}
	}

	private boolean writeFile(File file, JsonElement jsonElement) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			Writer writer = new FileWriter(file);
			writer.write(gson.toJson(jsonElement));
			writer.close();
		} catch (IOException e) {
			LOGGER.error("BlockLogger: Writing JSON file failed!");
			return false;
		}
		return true;
	}

	private String formatItemName(String item) {
		if (item.contains("item")) {
			 return item.replace("item.", "").replace(".",":");
		} else {
			 return item.replace("block.", "").replace(".",":");
		}
	}
}
