import { mkdir, readFile, readdir, copyFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const recipeDir = path.join(root, "src/main/resources/data/beetpunk/recipe");
const assetsDir = path.join(root, "src/main/resources/assets/beetpunk");
const outputDir = path.join(root, "docs/data");
const iconDir = path.join(root, "docs/assets/icons");

const readJson = async (file) => JSON.parse((await readFile(file, "utf8")).replace(/^\uFEFF/, ""));
const lang = await readJson(path.join(assetsDir, "lang/zh_tw.json"));
const files = (await readdir(recipeDir)).filter((file) => file.endsWith(".json")).sort();

function itemName(id) {
  if (!id) return "未知物品";
  const [namespace, name] = id.split(":");
  if (namespace === "beetpunk") {
    return lang[`item.beetpunk.${name}`] || lang[`block.beetpunk.${name}`] || name;
  }
  const vanilla = {
    "minecraft:beetroot": "甜菜根",
    "minecraft:beetroot_seeds": "甜菜種子",
    "minecraft:clay_ball": "黏土球",
    "minecraft:red_dye": "紅色染料",
    "minecraft:pointed_dripstone": "滴水石錐",
    "minecraft:amethyst_shard": "紫水晶碎片",
    "minecraft:stick": "木棒",
    "minecraft:string": "線",
    "minecraft:paper": "紙",
    "minecraft:book": "書",
    "minecraft:water_bucket": "水桶",
  };
  return vanilla[id] || name.replaceAll("_", " ");
}

function ingredientId(value) {
  if (typeof value === "string") return value;
  if (Array.isArray(value)) return ingredientId(value[0]);
  return value?.item || value?.id || value?.tag || null;
}

function normalizeIngredients(recipe) {
  if (recipe.type === "minecraft:crafting_shaped") {
    const counts = {};
    for (const row of recipe.pattern || []) {
      for (const symbol of row) {
        if (symbol === " ") continue;
        const id = ingredientId(recipe.key?.[symbol]);
        if (id) counts[id] = (counts[id] || 0) + 1;
      }
    }
    return Object.entries(counts).map(([id, count]) => ({ id, name: itemName(id), count }));
  }
  const counts = {};
  for (const ingredient of recipe.ingredients || []) {
    const id = ingredientId(ingredient);
    if (id) counts[id] = (counts[id] || 0) + 1;
  }
  return Object.entries(counts).map(([id, count]) => ({ id, name: itemName(id), count }));
}

function resultOf(recipe) {
  const raw = recipe.result;
  const id = typeof raw === "string" ? raw : raw?.id || raw?.item;
  return { id, name: itemName(id), count: raw?.count || 1 };
}

const recipes = [];
for (const file of files) {
  const recipe = await readJson(path.join(recipeDir, file));
  const result = resultOf(recipe);
  if (!result.id) continue;
  recipes.push({
    id: `beetpunk:${file.replace(/\.json$/, "")}`,
    type: recipe.type.endsWith("shaped") ? "有序合成" : "無序合成",
    pattern: recipe.pattern || null,
    ingredients: normalizeIngredients(recipe),
    result,
  });
}

const processing = [
  ["榨取桶", "beetpunk:beet_block", [["beetpunk:dried_beet_block", 1], ["beetpunk:beet_water_drop", 1]]],
  ["榨取桶", "beetpunk:dried_beet_block", [["beetpunk:beet_residue", 1], ["beetpunk:beet_oil", 1]]],
  ["榨取桶", "minecraft:beetroot_seeds", [["beetpunk:beet_residue", 1], ["beetpunk:beet_oil", 1]]],
  ["研磨桶", "beetpunk:beet_brick_block", [["beetpunk:beet_cobblestone", 1]]],
  ["研磨桶", "beetpunk:beet_cobblestone", [["beetpunk:beet_gravel", 1]]],
  ["研磨桶", "beetpunk:beet_gravel", [["beetpunk:beet_sand", 1], ["beetpunk:beet_crystal_grain", 1]]],
  ["篩洗桶", "beetpunk:beet_sand", [["beetpunk:beet_iron_dust", 1], ["minecraft:clay_ball", 1]]],
  ["篩洗桶", "beetpunk:beet_crystal_grain", [["beetpunk:beet_redstone_dust", 1]]],
].map(([station, input, outputs], index) => ({
  id: `processing:${index}`,
  type: station,
  ingredients: [{ id: input, name: itemName(input), count: 1 }],
  results: outputs.map(([id, count]) => ({ id, name: itemName(id), count })),
  result: { id: outputs[0][0], name: itemName(outputs[0][0]), count: outputs[0][1] },
}));

await mkdir(outputDir, { recursive: true });
await mkdir(iconDir, { recursive: true });

const copyPngs = async (source) => {
  for (const file of await readdir(source)) {
    if (!file.endsWith(".png")) continue;
    await copyFile(path.join(source, file), path.join(iconDir, file));
  }
};
await copyPngs(path.join(assetsDir, "textures/block"));
await copyPngs(path.join(assetsDir, "textures/item"));

await writeFile(
  path.join(outputDir, "recipes.json"),
  JSON.stringify({ generatedAt: new Date().toISOString(), recipes, processing, lang }, null, 2) + "\n",
);

console.log(`Generated ${recipes.length} crafting recipes and ${processing.length} processing recipes.`);
