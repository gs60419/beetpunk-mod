const temples = [
  {id:"seed",name:"萌種神殿",theme:"播種，是一切文明的起點",need:"無前置神殿。帶著甜菜種子與巡禮之書，從第一塊耕地開始。",glyph:"在耕地種下甜菜種子時，有 3% 機率取得萌種聖文字。",core:"萌種 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["手杖一次播種 3×3。","播種範圍擴張至 5×5。","播種範圍擴張至 7×7。","最大 9×9；每第 4 株免耗 1 顆種子。"]},
  {id:"soil",name:"沃土神殿",theme:"收穫之後，學會整理土地",need:"先在萌種神殿留下神殿印記。",glyph:"徒手收成成熟甜菜時，有 2% 機率取得沃土聖文字。",core:"沃土 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["手杖將單格土壤轉成甜菜耕土。","整地範圍擴張至 3×3。","3×3 直接形成沃化甜菜耕土。","最大 5×5 沃化整地。"]},
  {id:"sprout",name:"初芽神殿",theme:"讓幼芽自己尋找可以生長的方向",need:"先在沃土神殿留下神殿印記。",glyph:"徒手收成成熟甜菜時，有 2% 機率取得初芽聖文字。",core:"初芽 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["幼苗有 35% 機率成長一階，每輪嘗試 24 次。","每輪 48 次，並向四個正方向擴散。","每輪 80 次，擴散包含斜角方向。","每輪 120 次、額外擴散，並預先整理甜菜土。"]},
  {id:"water",name:"清水神殿",theme:"水開始沿著甜菜根系流動",need:"先在初芽神殿留下神殿印記。",glyph:"合成甜菜水時，有 20% 機率取得清水聖文字。",core:"清水 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["噴灑器半徑提升至 2 格。","噴灑器半徑提升至 3 格。","噴灑器半徑提升至 4 格。","最大半徑 5 格；50% 機率不消耗水。"]},
  {id:"growth",name:"壯苗神殿",theme:"水與肥料化為可控制的時間",need:"先在清水神殿留下神殿印記。",glyph:"噴灑器成功催生甜菜時，附近玩家有 20% 機率取得壯苗聖文字。",core:"壯苗 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["手杖或肥料使單株成長 1 階。","單株一次成長 2 階。","一次催生 3×3 範圍。","最大 5×5；肥料有 50% 機率不消耗。"]},
  {id:"light",name:"耀光神殿",theme:"光不只照明，也為聚落劃出邊界",need:"先在壯苗神殿留下神殿印記。",glyph:"合成甜菜營火時，有 20% 機率取得耀光聖文字。",core:"耀光 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["範圍內怪物獲得發光效果。","怪物同時獲得虛弱。","怪物再受到緩速。","移除範圍內所有未命名怪物。"]},
  {id:"leaf",name:"茂葉神殿",theme:"枝葉不再是副產物，而是織物與木材",need:"先在耀光神殿留下神殿印記。",glyph:"合成甜菜纖維時，有 15% 機率取得茂葉聖文字。",core:"茂葉 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["收成時額外獲得 1 片甜菜葉。","收成時有機會額外取得纖維。","提高額外葉片與纖維產量。","產物優先送入附近採收箱，再送玩家背包。"]},
  {id:"root",name:"豐根神殿",theme:"根部被壓榨，流出水、渣與油",need:"先在茂葉神殿留下神殿印記。",glyph:"合成甜菜塊時，有 15% 機率取得豐根聖文字。",core:"豐根 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["榨取時間縮短為原本的 80%。","縮短為 2/3；甜菜塊額外產出水滴。","縮短為 1/2；甜菜石塊額外產出甜菜渣。","縮短為 40%；甜菜石塊額外產出甜菜油。"]},
  {id:"beet",name:"甜菜神殿",theme:"終於，甜菜本身成為一套收穫法則",need:"先在豐根神殿留下神殿印記。",glyph:"用榨取桶把甜菜塊加工成甜菜石塊時，附近玩家有 15% 機率取得甜菜聖文字。",core:"甜菜 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["單株收成有 30% 機率多得 1 顆甜菜。","手杖一次收成 3×3。","3×3 收成後自動補種。","最大 5×5；產物自動收納，補種每第 4 株免耗種子。"]},
  {id:"soup",name:"甘湯神殿",theme:"收成進入身體，成為旅人的續航",need:"先在甜菜神殿留下神殿印記。",glyph:"合成甜菜乾糧時，有 15% 機率取得甘湯聖文字。",core:"甘湯 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["食用甜菜食物後獲得生命回復。","追加瞬間飽和。","追加傷害吸收，並強化生命回復。","清除飢餓並追加抗性提升。"]},
  {id:"dye",name:"染彩神殿",theme:"紅色從食物變成顏料與召喚媒介",need:"先在甘湯神殿留下神殿印記。",glyph:"把甜菜轉成紅色染料時，有 20% 機率取得染彩聖文字。",core:"染彩 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["每次轉換有 35% 機率多得 1 份紅色染料。","每次固定多得 1 份染料。","再有 50% 機率多得 1 份。","額外染料自動收納；使用天啟可召喚豬。"]},
  {id:"pig",name:"聖豬神殿",theme:"甜菜餵養生命，生命回贈最後一枚印",need:"先在染彩神殿留下神殿印記。",glyph:"用甜菜餵食可繁殖的成年豬；基礎機率 20%，本殿 LV2／3／4 提升為 35%／50%／75%。",core:"聖豬 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["使用甜菜天啟可召喚一隻豬。","聖文字取得率提升至 35%。","聖文字取得率提升至 50%。","取得率 75% 並額外生成小豬；十二印俱足時降下至高聖文字。"]},
  {id:"supreme",name:"至高神殿",theme:"十二種法則在此匯聚，文明開始交易",need:"取得前十二座神殿印記，並在聖豬神殿啟動 LV4。",glyph:"條件滿足時，至高聖文字會由聖豬神殿降下；沒有隨機機率。",core:"至高 LV1 經書＋甜菜磚 ×2＋甜菜晶粒 ×2",levels:["使用甜菜天啟可召喚村民。","有職業的村民直接晉升大師。","補滿村民交易庫存。","交易獲得固定優惠。"]}
];

const names={seed:"萌種",soil:"沃土",sprout:"初芽",water:"清水",growth:"壯苗",light:"耀光",leaf:"茂葉",root:"豐根",beet:"甜菜",soup:"甘湯",dye:"染彩",pig:"聖豬",supreme:"至高"};
const icon=(name)=>`assets/icons/${name}.png`;
const scriptureSuffix=["","_ii","_iii","_iv"];
function scriptureRecipe(t,index){
  const previous=index?`${names[temples[index-1].id]} LV1 經書`:'甜菜濾網';
  return [
    `甜菜布 ×2＋${names[t.id]}聖文字＋甜菜墨水＋${previous}`,
    `LV1＋${names[t.id]}聖文字 ×2＋二階墨水 ×6`,
    `LV2＋${names[t.id]}聖文字 ×2＋三階墨水 ×6`,
    `LV3＋${names[t.id]}聖文字＋四階墨水 ×6＋甜菜天啟`
  ];
}
function render(){
  document.querySelector("#temple-nav").innerHTML=temples.map((t,i)=>`<a href="#temple-${t.id}" data-id="${t.id}"><span>${String(i+1).padStart(2,"0")} ${names[t.id]}</span></a>`).join("");
  document.querySelector("#temple-scroll").innerHTML=temples.map((t,i)=>{
    const recipes=scriptureRecipe(t,i);
    return `<section class="shrine" id="temple-${t.id}" data-id="${t.id}" data-number="${String(i+1).padStart(2,"0")}"><div class="shrine-inner">
      <header class="shrine-intro"><div class="shrine-emblem"><img src="${icon(`${t.id}_temple_core`)}" alt="${t.name}核心"></div><div><div class="shrine-order">TEMPLE ${String(i+1).padStart(2,"0")} · ${names[t.id].toUpperCase()}</div><h2>${t.name}</h2><p class="shrine-theme">${t.theme}</p></div></header>
      <div class="shrine-content"><article class="ritual-card"><p class="card-label">進殿條件</p><p class="condition">${t.need}</p><div class="glyph-row"><img class="pixel-icon" src="${icon(`${t.id}_glyph`)}" alt=""><div><strong>${names[t.id]}聖文字</strong><small>${t.glyph}</small></div></div><p class="core-recipe"><strong>神殿核心：</strong>${t.core}</p><div class="scriptures">${recipes.map((recipe,l)=>`<div class="scripture"><img src="${icon(`${t.id}_scripture${scriptureSuffix[l]}`)}" alt=""><div><strong>LV${l+1} 經書</strong><small>${recipe}</small></div></div>`).join("")}</div></article>
      <article class="effects-card"><p class="card-label">經書效果</p><div class="levels">${t.levels.map((level,l)=>`<div class="level"><b>LV ${l+1}</b><span>${level}</span></div>`).join("")}</div><p class="range-note">神殿作用半徑：LV1 16 格 · LV2 32 格 · LV3 48 格 · LV4 64 格</p></article></div>
      ${i<temples.length-1?`<a class="next-shrine" href="#temple-${temples[i+1].id}">繼續下行 · ${String(i+2).padStart(2,"0")} ${temples[i+1].name} ↓</a>`:''}
    </div></section>`;
  }).join("");
}
function bind(){
  const links=[...document.querySelectorAll("#temple-nav a")],sections=[...document.querySelectorAll(".shrine")],current=document.querySelector("#rail-current");
  const observer=new IntersectionObserver(entries=>{const visible=entries.filter(e=>e.isIntersecting).sort((a,b)=>b.intersectionRatio-a.intersectionRatio)[0];if(!visible)return;const id=visible.target.dataset.id,index=temples.findIndex(t=>t.id===id);current.textContent=String(index+1).padStart(2,"0");links.forEach(a=>a.classList.toggle("active",a.dataset.id===id));},{rootMargin:"-25% 0px -55%",threshold:[0,.2,.5]});
  sections.forEach(section=>observer.observe(section));
  const toggle=document.querySelector(".nav-toggle"),nav=document.querySelector("#site-nav");toggle.addEventListener("click",()=>{nav.classList.toggle("open");toggle.setAttribute("aria-expanded",nav.classList.contains("open"))});
}
render();bind();
