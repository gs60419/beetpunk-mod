const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

const tree = [
  ["minecraft:beetroot", 40, 350, "material"],
  ["beetpunk:beet_leaf", 235, 90, "material"], ["minecraft:beetroot_seeds", 235, 350, "material"], ["beetpunk:beet_block", 235, 610, "material"],
  ["beetpunk:beet_fiber", 430, 30, "material"], ["beetpunk:beet_stick", 430, 155, "material"], ["beetpunk:beet_pilgrim_book", 430, 285, "equipment"], ["beetpunk:beet_crank_base", 430, 415, "machine"], ["beetpunk:dried_beet_block", 430, 610, "material"], ["beetpunk:beet_water_drop", 430, 735, "material"],
  ["beetpunk:beet_cloth", 625, 20, "material"], ["beetpunk:beet_plank_block", 625, 135, "material"], ["beetpunk:beet_pilgrim_staff", 625, 260, "equipment"], ["beetpunk:beet_extractor_barrel", 625, 390, "machine"], ["beetpunk:beet_brick_block", 625, 520, "material"], ["beetpunk:beet_residue", 625, 650, "material"], ["beetpunk:beet_oil", 625, 770, "material"],
  ["beetpunk:beet_filter", 820, 20, "material"], ["beetpunk:beet_wooden_pickaxe", 820, 135, "equipment"], ["beetpunk:seed_glyph", 820, 260, "temple"], ["beetpunk:beet_grinder_barrel", 820, 390, "machine"], ["beetpunk:beet_cobblestone", 820, 520, "material"], ["beetpunk:beet_fertilizer", 820, 650, "material"], ["beetpunk:beet_water", 820, 770, "material"],
  ["beetpunk:seed_scripture", 1015, 250, "temple"], ["beetpunk:beet_washing_barrel", 1015, 390, "machine"], ["beetpunk:beet_gravel", 1015, 520, "material"], ["beetpunk:fertilized_beet_soil", 1015, 650, "material"], ["beetpunk:beet_sprinkler", 1015, 770, "machine"],
  ["beetpunk:seed_temple_core", 1210, 250, "temple"], ["beetpunk:beet_sand", 1210, 470, "material"], ["beetpunk:beet_crystal_grain", 1210, 585, "material"],
  ["beetpunk:beet_iron_dust", 1390, 425, "material"], ["beetpunk:beet_redstone_dust", 1390, 550, "material"], ["minecraft:amethyst_shard", 1390, 675, "material"],
];

const edges = [
  ["minecraft:beetroot","beetpunk:beet_leaf"],["minecraft:beetroot","minecraft:beetroot_seeds"],["minecraft:beetroot","beetpunk:beet_block"],
  ["beetpunk:beet_leaf","beetpunk:beet_fiber"],["beetpunk:beet_leaf","beetpunk:beet_stick"],["beetpunk:beet_leaf","beetpunk:beet_pilgrim_book"],
  ["beetpunk:beet_fiber","beetpunk:beet_cloth"],["beetpunk:beet_fiber","beetpunk:beet_filter"],["beetpunk:beet_stick","beetpunk:beet_plank_block"],["beetpunk:beet_stick","beetpunk:beet_crank_base"],
  ["beetpunk:beet_plank_block","beetpunk:beet_wooden_pickaxe"],["beetpunk:beet_pilgrim_book","beetpunk:beet_pilgrim_staff"],["beetpunk:beet_pilgrim_book","beetpunk:seed_glyph"],
  ["minecraft:beetroot_seeds","beetpunk:seed_glyph"],["beetpunk:seed_glyph","beetpunk:seed_scripture"],["beetpunk:seed_scripture","beetpunk:seed_temple_core"],
  ["beetpunk:beet_crank_base","beetpunk:beet_extractor_barrel"],["beetpunk:beet_crank_base","beetpunk:beet_grinder_barrel"],["beetpunk:beet_crank_base","beetpunk:beet_washing_barrel"],
  ["beetpunk:beet_block","beetpunk:dried_beet_block","processing"],["beetpunk:beet_block","beetpunk:beet_water_drop","processing"],["beetpunk:dried_beet_block","beetpunk:beet_residue","processing"],["beetpunk:dried_beet_block","beetpunk:beet_oil","processing"],
  ["beetpunk:dried_beet_block","beetpunk:beet_brick_block"],["beetpunk:beet_brick_block","beetpunk:beet_cobblestone","processing"],["beetpunk:beet_cobblestone","beetpunk:beet_gravel","processing"],["beetpunk:beet_gravel","beetpunk:beet_sand","processing"],["beetpunk:beet_gravel","beetpunk:beet_crystal_grain","processing"],
  ["beetpunk:beet_sand","beetpunk:beet_iron_dust","processing"],["beetpunk:beet_crystal_grain","beetpunk:beet_redstone_dust","processing"],["beetpunk:beet_crystal_grain","minecraft:amethyst_shard"],
  ["beetpunk:beet_residue","beetpunk:beet_fertilizer"],["beetpunk:beet_oil","beetpunk:beet_fertilizer"],["beetpunk:beet_fertilizer","beetpunk:fertilized_beet_soil"],
  ["beetpunk:beet_water_drop","beetpunk:beet_water"],["beetpunk:beet_water","beetpunk:beet_sprinkler"],
];

const temples = [
  ["seed","萌種神殿","甜菜種子","範圍播種","最大範圍播種；每種下第 4 株可免耗 1 顆種子。"],
  ["soil","沃土神殿","土壤","整地與沃化","手杖整地範圍擴張至 5×5，並支援沃化耕土。"],
  ["sprout","初芽神殿","萌芽","自然成長與擴散","幼苗可向八方向擴散，並預先把甜菜土轉成耕土。"],
  ["water","清水神殿","水","灑水與滴灌","噴灑器達最大範圍，成功噴灑有 50% 機率不耗水。"],
  ["growth","壯苗神殿","成長","催熟與肥料","催熟擴張至 5×5，肥料有 50% 機率不消耗。"],
  ["light","耀光神殿","光","照明與壓制怪物","依序施加發光、虛弱與緩速；LV4 移除未命名怪物。"],
  ["leaf","茂葉神殿","甜菜葉","纖維產能","提高葉與纖維收成，LV4 自動送入附近採收箱。"],
  ["root","豐根神殿","甜菜根","榨取產能","加快榨取並提高水滴、甜菜渣與甜菜油產量。"],
  ["beet","甜菜神殿","甜菜本體","範圍收成","範圍收成、自動補種，並將產物送入採收箱。"],
  ["soup","甘湯神殿","甜菜湯","食物增益","食用甜菜食物獲得回復、飽和、吸收與抗性。"],
  ["dye","染彩神殿","紅色染料","染料增產","提高紅色染料產量並自動收納高階產物。"],
  ["pig","聖豬神殿","豬","生物來源","以甜菜天啟生成豬；LV4 額外生成小豬並通往至高聖文字。"],
  ["supreme","至高神殿","統合","村民與交易","生成村民、直升大師、補貨，最終提供交易折扣。"],
];

const fallbackNames = {"minecraft:beetroot":"甜菜根","minecraft:beetroot_seeds":"甜菜種子","minecraft:clay_ball":"黏土球","minecraft:amethyst_shard":"紫水晶碎片"};
let data, allRecipes, nameMap = new Map(), graphState = { x: 10, y: 10, scale: .68 }, selectedId = null;

function iconPath(id) {
  if (!id?.startsWith("beetpunk:")) return null;
  const name = id.split(":")[1];
  const overrides = {
    beet_crank_base: "beet_crank_base_preview",
    beet_extractor_barrel: "beet_extractor_barrel_side",
    beet_grinder_barrel: "beet_grinder_barrel_side",
    beet_washing_barrel: "beet_washing_barrel_side",
    beet_water: "beet_water_drop",
    beet_sprinkler: "beet_water_drop",
  };
  return `assets/icons/${overrides[name] || name}.png`;
}
function nameOf(id) { return nameMap.get(id) || fallbackNames[id] || id.split(":").pop().replaceAll("_", " "); }
function iconMarkup(id, alt = "") {
  const src = iconPath(id);
  return src ? `<img class="node-icon" src="${src}" alt="${alt}" onerror="this.replaceWith(Object.assign(document.createElement('span'),{className:'node-fallback',textContent:'B'}))">` : `<span class="node-fallback">${id.includes("beetroot") ? "甜" : "◇"}</span>`;
}

async function init() {
  try {
    data = await fetch("data/recipes.json").then((response) => { if (!response.ok) throw new Error(response.status); return response.json(); });
    allRecipes = [...data.recipes, ...data.processing];
    for (const recipe of allRecipes) {
      nameMap.set(recipe.result.id, recipe.result.name);
      recipe.ingredients.forEach((item) => nameMap.set(item.id, item.name));
      recipe.results?.forEach((item) => nameMap.set(item.id, item.name));
    }
    renderGraph();
    bindTreeControls();
  } catch (error) {
    $("#recipe-graph").innerHTML = `<p class="data-error">配方資料讀取失敗。請透過網站伺服器開啟本頁。</p>`;
  }
  renderTemples();
  renderMachine("extract");
  bindNavigation();
}

function renderGraph() {
  const graph = $("#recipe-graph");
  graph.innerHTML = `<div class="graph-stage"><svg class="graph-lines" width="1600" height="900" aria-hidden="true"></svg></div>`;
  const stage = $(".graph-stage", graph), svg = $(".graph-lines", stage);
  const nodeMap = new Map(tree.map(([id,x,y,category]) => [id,{id,x,y,category}]));
  for (const [from,to,type] of edges) {
    const a=nodeMap.get(from), b=nodeMap.get(to); if(!a||!b) continue;
    const x1=a.x+152,y1=a.y+32,x2=b.x,y2=b.y+32,mid=(x1+x2)/2;
    svg.insertAdjacentHTML("beforeend",`<path class="${type||""}" d="M${x1} ${y1} C${mid} ${y1},${mid} ${y2},${x2} ${y2}"/>`);
  }
  for (const [id,x,y,category] of tree) {
    const button=document.createElement("button"); button.className="graph-node"; button.dataset.id=id; button.dataset.category=category; button.style.left=`${x}px`; button.style.top=`${y}px`;
    button.innerHTML=`${iconMarkup(id,nameOf(id))}<strong>${nameOf(id)}</strong><small>${categoryLabel(category)}</small>`;
    button.addEventListener("click",()=>selectNode(id)); stage.append(button);
  }
  applyTransform();
}
function categoryLabel(category){return {material:"材料路線",machine:"轉經機械",temple:"神殿路線",equipment:"裝備與工具"}[category]||category}

function selectNode(id) {
  selectedId=id; $$(".graph-node").forEach((node)=>node.classList.toggle("selected",node.dataset.id===id));
  const produced=allRecipes.filter((recipe)=>recipe.result.id===id || recipe.results?.some((r)=>r.id===id));
  const uses=allRecipes.filter((recipe)=>recipe.ingredients.some((item)=>item.id===id));
  const panel=$("#recipe-panel");
  panel.innerHTML=`<div class="panel-header">${iconMarkup(id,nameOf(id))}<div><h3>${nameOf(id)}</h3><div class="panel-id">${id}</div></div></div>
    <h4>取得方式</h4>${produced.length?produced.map(recipeMarkup).join(""):'<p>由收成、事件或神殿行為取得。</p>'}
    <h4>可通往</h4>${uses.length?`<ul class="use-list">${uses.slice(0,18).map((recipe)=>`<li><button data-jump="${recipe.result.id}">${recipe.result.name}<small>　${recipe.type}</small></button></li>`).join("")}</ul>`:'<p>目前沒有被其他配方使用。</p>'}`;
  $$('[data-jump]',panel).forEach((button)=>button.addEventListener('click',()=>selectNode(button.dataset.jump)));
}
function recipeMarkup(recipe) {
  const outputs=recipe.results || [recipe.result];
  return `<div class="recipe-box"><div class="recipe-type">${recipe.type}</div><div class="ingredient-list">${recipe.ingredients.map((item)=>`<span class="ingredient">${item.name}${item.count>1?` ×${item.count}`:""}</span>`).join("")}<span>→</span>${outputs.map((item)=>`<span class="ingredient"><strong>${item.name}</strong>${item.count>1?` ×${item.count}`:""}</span>`).join("")}</div></div>`;
}

function bindTreeControls(){
  const graph=$("#recipe-graph"); let dragging=false,lastX=0,lastY=0;
  graph.addEventListener("pointerdown",(event)=>{if(event.target.closest(".graph-node"))return;dragging=true;lastX=event.clientX;lastY=event.clientY;graph.setPointerCapture(event.pointerId);graph.classList.add("dragging")});
  graph.addEventListener("pointermove",(event)=>{if(!dragging)return;graphState.x+=event.clientX-lastX;graphState.y+=event.clientY-lastY;lastX=event.clientX;lastY=event.clientY;applyTransform()});
  graph.addEventListener("pointerup",()=>{dragging=false;graph.classList.remove("dragging")});
  graph.addEventListener("wheel",(event)=>{event.preventDefault();graphState.scale=Math.max(.4,Math.min(1.25,graphState.scale+(event.deltaY<0?.08:-.08)));applyTransform()},{passive:false});
  $("#zoom-in").onclick=()=>{graphState.scale=Math.min(1.25,graphState.scale+.1);applyTransform()};
  $("#zoom-out").onclick=()=>{graphState.scale=Math.max(.4,graphState.scale-.1);applyTransform()};
  $("#reset-view").onclick=()=>{graphState={x:10,y:10,scale:.68};applyTransform()};
  $("#recipe-search").addEventListener("input",applySearch);
  $$(".filter").forEach((button)=>button.addEventListener("click",()=>{$$(".filter").forEach(b=>b.classList.remove("active"));button.classList.add("active");applySearch()}));
}
function applyTransform(){const stage=$(".graph-stage");if(stage)stage.style.transform=`translate(${graphState.x}px,${graphState.y}px) scale(${graphState.scale})`}
function applySearch(){
  const q=$("#recipe-search").value.trim().toLowerCase(),filter=$(".filter.active").dataset.filter;
  let first=null;$$(".graph-node").forEach((node)=>{const matchText=!q||nameOf(node.dataset.id).toLowerCase().includes(q)||node.dataset.id.includes(q);const matchFilter=filter==="all"||node.dataset.category===filter;const match=matchText&&matchFilter;node.classList.toggle("dim",!match);if(match&&!first)first=node});
  if(q&&first){const id=first.dataset.id;selectNode(id)}
}

const machineContent={
  extract:["榨取桶","甜菜塊 → 甜菜石塊＋甜菜水滴","甜菜石塊／甜菜種子 → 甜菜渣＋甜菜油"],
  grind:["研磨桶","甜菜磚 → 甜菜鵝卵石 → 甜菜礫石","甜菜礫石 → 甜菜沙＋甜菜晶粒"],
  wash:["篩洗桶","甜菜沙 → 甜菜鐵砂＋黏土球","甜菜晶粒 → 甜菜紅石粉"],
};
function renderMachine(key){const [title,...rows]=machineContent[key];$("#machine-recipe").innerHTML=`<strong>${title}</strong>${rows.map(row=>`<p>${row}</p>`).join("")}`}

function renderTemples(){
  const grid=$("#temple-grid");
  grid.innerHTML=temples.map(([id,name,theme,summary,detail],index)=>`<article class="temple-card" data-number="${String(index+1).padStart(2,"0")}" tabindex="0"><img src="assets/icons/${id}_temple_core.png" alt=""><h3>${name}</h3><div class="theme">${theme} · ${summary}</div><p class="summary">${detail.split("；")[0]}。</p><p class="detail">${detail}</p><div class="temple-levels" aria-label="四個神殿等級"><i></i><i></i><i></i><i></i></div></article>`).join("");
  $$(".temple-card").forEach(card=>{const toggle=()=>card.classList.toggle("expanded");card.addEventListener("click",toggle);card.addEventListener("keydown",e=>{if(e.key==="Enter"||e.key===" "){e.preventDefault();toggle()}})});
}
function bindNavigation(){
  $(".nav-toggle").addEventListener("click",(event)=>{const nav=$("#site-nav");nav.classList.toggle("open");event.currentTarget.setAttribute("aria-expanded",nav.classList.contains("open"))});
  $$(".machine-tab").forEach(button=>button.addEventListener("click",()=>{$$(".machine-tab").forEach(b=>b.classList.remove("active"));button.classList.add("active");renderMachine(button.dataset.machine)}));
  $$("#site-nav a").forEach(link=>link.addEventListener("click",()=>$("#site-nav").classList.remove("open")));
}

init();
