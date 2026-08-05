const $=(selector,root=document)=>root.querySelector(selector);
const $$=(selector,root=document)=>[...root.querySelectorAll(selector)];

const branches=[
  {id:"leaf",number:"01",name:"葉脈工藝",theme:"從葉片抽出纖維，再織成生活與巡禮所需。",icon:"beetpunk:beet_leaf",groups:[
    {id:"textile",name:"纖維與織物",hint:"葉片 · 纖維 · 布料 · 濾網"},
    {id:"wood",name:"枝條與木工",hint:"枝條 · 木材 · 家具 · 建築"},
    {id:"equipment",name:"工具與巡禮裝備",hint:"手杖 · 工具 · 武器 · 裝備"}
  ]},
  {id:"farm",number:"02",name:"農務循環",theme:"讓種子、水土與收成逐步變成範圍法則。",icon:"beetpunk:seed_glyph",groups:[
    {id:"farming",name:"水土與農業",hint:"土壤 · 水 · 肥料 · 採收"},
    {id:"food",name:"食物與生活",hint:"甜菜 · 乾糧 · 食物用途"},
    {id:"temples",name:"十三神殿",hint:"聖文字 · 經書 · 核心 · 天啟"}
  ]},
  {id:"root",number:"03",name:"根系工業",theme:"把根莖壓榨、研磨與篩洗成石材和礦物。",icon:"beetpunk:beet_block",groups:[
    {id:"processing",name:"榨取與副產物",hint:"甜菜塊 · 水滴 · 渣 · 油"},
    {id:"masonry",name:"石材與建築",hint:"磚 · 鵝卵石 · 礫石 · 沙"},
    {id:"minerals",name:"礦物突破",hint:"鐵 · 紅石 · 晶粒 · 紫水晶"},
    {id:"automation",name:"轉經機械",hint:"底座 · 三種桶身 · 自動化"}
  ]}
];

const vanillaNames={"minecraft:beetroot":"甜菜根","minecraft:beetroot_seeds":"甜菜種子","minecraft:arrow":"箭矢","minecraft:redstone":"紅石粉"};
let data,recipes=[],items=new Map(),mode="trunk",activeResult=-1;

function iconPath(id){
  if(id==="minecraft:beetroot")return "assets/icons/beetroot.png";
  if(id==="minecraft:beetroot_seeds")return "assets/icons/beetroot_seeds.png";
  if(!id?.startsWith("beetpunk:"))return null;
  const name=id.split(":")[1],overrides={beet_crank_base:"beet_crank_base_preview",beet_extractor_barrel:"beet_extractor_barrel_side",beet_grinder_barrel:"beet_grinder_barrel_side",beet_washing_barrel:"beet_washing_barrel_side",beet_sprinkler:"beet_water_drop"};
  return `assets/icons/${overrides[name]||name}.png`;
}
function iconMarkup(id,alt=""){
  const src=iconPath(id);return src?`<img src="${src}" alt="${alt}" onerror="this.replaceWith(Object.assign(document.createElement('span'),{className:'item-fallback',textContent:'B'}))">`:`<span class="item-fallback">${id?.includes("beetroot")?"甜":"◇"}</span>`;
}
function register(item){if(!item?.id)return;const name=vanillaNames[item.id]||item.name||item.id.split(":").pop().replaceAll("_"," ");items.set(item.id,{...items.get(item.id),...item,name});}
function category(id){
  const n=id.split(":").pop();
  if(/glyph|scripture|temple|beet_ink|revelation/.test(n))return "temples";
  if(/ration|soup|bowl|food/.test(n))return "food";
  if(/soil|farmland|water|sprinkler|fertilizer|harvest|campfire|seed/.test(n))return "farming";
  if(/crank|barrel|processing_table|geothermal|mechanical/.test(n))return "automation";
  if(/iron|redstone|crystal|amethyst|clay|dripstone|mineral/.test(n))return "minerals";
  if(/brick|cobble|gravel|sand|stone|stairs|slab|wall/.test(n))return "masonry";
  if(/dried|residue|oil|beet_block|water_drop/.test(n))return "processing";
  if(/plank|stick|wooden|chest|door|fence|gate|sign|ladder|boat|table|pressure_plate|button/.test(n))return "wood";
  if(/leaf|fiber|cloth|wool|carpet|bed|banner|filter/.test(n))return "textile";
  if(/pilgrim|pickaxe|axe|shovel|hoe|sword|armor|helmet|chestplate|leggings|boots|arrow|bow|shield|tool/.test(n))return "equipment";
  return n.includes("beet")?"processing":"equipment";
}

async function init(){
  bindChrome();
  try{
    data=await fetch("data/recipes.json").then(r=>{if(!r.ok)throw new Error(r.status);return r.json()});
    recipes=[...data.recipes,...data.processing];
    recipes.forEach(recipe=>{register(recipe.result);recipe.ingredients.forEach(register);recipe.results?.forEach(register)});
    register({id:"minecraft:beetroot",name:"甜菜根"});register({id:"minecraft:beetroot_seeds",name:"甜菜種子"});
    renderTree();bindSearch();openFromHash();
  }catch(error){$("#civilization-tree").innerHTML=`<p class="tree-error">配方資料讀取失敗，甜菜暫時無法生根。</p>`}
}

function craftableItems(groupId){
  const ids=new Set();recipes.forEach(r=>{if(r.result&&category(r.result.id)===groupId)ids.add(r.result.id);r.results?.forEach(item=>{if(category(item.id)===groupId)ids.add(item.id)})});
  return [...ids].map(id=>items.get(id)).filter(Boolean).sort((a,b)=>a.name.localeCompare(b.name,"zh-Hant"));
}
function renderTree(){
  const root=$("#civilization-tree");
  root.innerHTML=`<div class="root-node">${iconMarkup("minecraft:beetroot","甜菜根")}<strong>原版甜菜</strong><small>所有文明由此開始</small></div><div class="civilization-branches">${branches.map(branch=>`<article class="main-root" data-branch="${branch.id}"><header class="main-root-head" data-number="${branch.number}">${iconMarkup(branch.icon)}<span>ROOT ${branch.number}</span><h2>${branch.name}</h2><p>${branch.theme}</p></header><div class="subbranches">${branch.groups.map(group=>branchMarkup(group)).join("")}</div><p class="branch-count">${branch.groups.reduce((sum,g)=>sum+craftableItems(g.id).length,0)} 個可探索物品</p></article>`).join("")}</div>`;
  $$(".subbranch-toggle").forEach(button=>button.addEventListener("click",()=>{const branch=button.closest(".subbranch");branch.classList.toggle("expanded");button.setAttribute("aria-expanded",branch.classList.contains("expanded"))}));
  $$(".tree-item").forEach(button=>button.addEventListener("click",()=>openLookup(button.dataset.id)));
}
function branchMarkup(group){
  const list=craftableItems(group.id);
  return `<section class="subbranch" data-group="${group.id}"><button class="subbranch-toggle" aria-expanded="false"><strong>${group.name}</strong><small>${group.hint} · ${list.length} 項</small></button><div class="item-canopy">${list.map(item=>`<button class="tree-item" data-id="${item.id}">${iconMarkup(item.id,item.name)}<strong>${item.name}</strong><small>點擊快速查詢</small></button>`).join("")}</div></section>`;
}
function setMode(next){
  mode=next;$$('[data-mode]').forEach(b=>b.classList.toggle("active",b.dataset.mode===mode));
  $$(".subbranch").forEach(branch=>{const expanded=mode==="full";branch.classList.toggle("expanded",expanded);$(".subbranch-toggle",branch).setAttribute("aria-expanded",expanded)});
  $("#tree-status").textContent=mode==="full"?"完整模式 · 所有枝幹已展開":"主幹模式 · 點擊枝幹逐一展開";
}
function bindChrome(){
  $$("[data-mode]").forEach(button=>button.addEventListener("click",()=>setMode(button.dataset.mode)));
  const toggle=$(".nav-toggle"),nav=$("#site-nav");toggle.addEventListener("click",()=>{nav.classList.toggle("open");toggle.setAttribute("aria-expanded",nav.classList.contains("open"))});
  $(".drawer-close").addEventListener("click",closeLookup);$(".drawer-backdrop").addEventListener("click",closeLookup);
  document.addEventListener("keydown",event=>{if(event.key==="/"&&!/input|textarea/i.test(document.activeElement.tagName)){event.preventDefault();$("#quick-search").focus()}if(event.key==="Escape")closeLookup()});
}
function bindSearch(){
  const input=$("#quick-search"),results=$("#search-results");
  input.addEventListener("input",()=>{activeResult=-1;renderResults(input.value)});
  input.addEventListener("focus",()=>{if(input.value.trim())renderResults(input.value)});
  input.addEventListener("keydown",event=>{const buttons=$$(".search-result",results);if(event.key==="ArrowDown"){event.preventDefault();activeResult=Math.min(buttons.length-1,activeResult+1)}else if(event.key==="ArrowUp"){event.preventDefault();activeResult=Math.max(0,activeResult-1)}else if(event.key==="Enter"&&buttons.length){event.preventDefault();buttons[Math.max(0,activeResult)].click()}buttons.forEach((b,i)=>b.classList.toggle("selected",i===activeResult))});
  $("#clear-search").addEventListener("click",()=>{input.value="";results.hidden=true;input.focus()});
  document.addEventListener("click",event=>{if(!event.target.closest(".quick-lookup"))results.hidden=true});
}
function renderResults(query){
  const box=$("#search-results"),q=query.trim().toLowerCase();if(!q){box.hidden=true;return}
  const matches=[...items.values()].filter(item=>item.name.toLowerCase().includes(q)||item.id.toLowerCase().includes(q)).sort((a,b)=>a.name.startsWith(query)?-1:b.name.startsWith(query)?1:a.name.localeCompare(b.name,"zh-Hant")).slice(0,10);
  box.innerHTML=matches.length?matches.map(item=>`<button class="search-result" data-id="${item.id}">${iconMarkup(item.id,item.name)}<span><strong>${item.name}</strong><small>${item.id}</small></span><em>一次開啟 →</em></button>`).join(""):`<p class="lookup-empty" style="padding:16px">找不到這個物品。</p>`;
  box.hidden=false;$$('.search-result',box).forEach(button=>button.addEventListener('click',()=>{openLookup(button.dataset.id);box.hidden=true}));
}
function recipeMarkup(recipe){
  const outputs=recipe.results||[recipe.result];return `<div class="lookup-recipe"><small>${recipe.type}</small><div class="recipe-flow">${recipe.ingredients.map(item=>`<span class="recipe-chip">${items.get(item.id)?.name||item.name}${item.count>1?` ×${item.count}`:""}</span>`).join("")}<span class="recipe-arrow">→</span>${outputs.map(item=>`<span class="recipe-chip"><b>${items.get(item.id)?.name||item.name}</b>${item.count>1?` ×${item.count}`:""}</span>`).join("")}</div></div>`;
}
function openLookup(id){
  const item=items.get(id)||{id,name:vanillaNames[id]||id};const made=recipes.filter(r=>r.result?.id===id||r.results?.some(x=>x.id===id));const uses=recipes.filter(r=>r.ingredients.some(x=>x.id===id));
  $("#lookup-content").innerHTML=`<header class="lookup-title">${iconMarkup(id,item.name)}<div><h2>${item.name}</h2><div class="lookup-id">${id}</div></div></header><h3 class="lookup-section-title">取得方式</h3>${made.length?made.map(recipeMarkup).join(""):`<p class="lookup-empty">由收成、事件、掉落或神殿行為取得。</p>`}<h3 class="lookup-section-title">後續用途 · ${uses.length}</h3>${uses.length?uses.slice(0,30).map(r=>`<button class="lookup-use" data-id="${r.result.id}">${r.result.name} <span>→</span></button>`).join(""):`<p class="lookup-empty">目前沒有其他配方使用它。</p>`}<p class="lookup-tip">快速查詢不會改變枝幹的展開狀態；關閉後可以繼續原本的探索位置。</p>`;
  $$(".lookup-use").forEach(button=>button.addEventListener("click",()=>openLookup(button.dataset.id)));
  $("#lookup-drawer").classList.add("open");$("#lookup-drawer").setAttribute("aria-hidden","false");$(".drawer-backdrop").hidden=false;history.replaceState(null,"",`#lookup=${encodeURIComponent(id)}`);
}
function closeLookup(){$("#lookup-drawer").classList.remove("open");$("#lookup-drawer").setAttribute("aria-hidden","true");$(".drawer-backdrop").hidden=true;if(location.hash.startsWith("#lookup="))history.replaceState(null,"",location.pathname+location.search)}
function openFromHash(){if(location.hash.startsWith("#lookup="))openLookup(decodeURIComponent(location.hash.slice(8)))}

init();
