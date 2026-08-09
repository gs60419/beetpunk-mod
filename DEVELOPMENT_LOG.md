# 甜菜龐克 26.2 開發紀錄

最後更新：2026-08-06

這份文件是給後續 Codex / 優優接手用的快速交接筆記。改模組前請先讀這份。

## 目前工作區

- 主要 repo：`<project-path>`
- 目前測試用 Prism 實例：`<prism-instance>`
- 每次成功 build 後，要覆蓋的 jar：
  `<prism-instance>\.minecraft\mods\beetpunk-0.1.0.jar`
- Minecraft 目標版本：`26.2`
- Fabric Loader：`0.19.3`
- Fabric API：`0.154.0+26.2`
- build 使用 Java：`Java 25`

Build 與覆蓋 jar 指令：

```powershell
$env:JAVA_HOME='<java-25-path>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build --no-daemon
Copy-Item -LiteralPath '<project-path>\build\libs\beetpunk-0.1.0.jar' -Destination '<prism-instance>\.minecraft\mods\beetpunk-0.1.0.jar' -Force
```

如果 `python` 會打開 Microsoft Store alias，可以改用專案環境中可用的 Python：

```text
<python-path>
```

## 目前設計方向

這是一個 Fabric 甜菜龐克空島 / 材料循環 prototype，核心概念是盡量用甜菜路線重建大部分原版建材與生存體驗。

目前主要系統包括：

- 甜菜衍生物與材料路線：甜菜葉、纖維、布、枝條、木材、石頭路線、礫石/沙、鐵砂、紅石粉、甜菜油、甜菜水滴、甜菜水、肥料。
- 甜菜木材路線：木材方塊、階梯、半磚、柵欄、門、活板門、告示牌、懸掛式告示牌、船、木筏、床。
- 甜菜石頭路線：甜菜石塊作為類石頭素材，衍生甜菜鵝卵塊、礫石、沙、甜菜磚、雕刻甜菜磚。
- 甜菜鐵路線：甜菜鐵砂、甜菜鐵錠、工具與盔甲。
- 巡禮帳：前段是路線手冊，後段是 13 神殿朱印頁。
- 甜菜手杖：整合型萬能工具路線；舊的萌種播種器、沃土鋤等特殊工具已移除或棄用。
- 甜菜釣竿：原版胡蘿蔔釣竿的甜菜版，可用來操控已裝鞍的豬。
- 13 神殿核心與 13 x 4 階經書。
- 甜菜賢者村民職業與甜菜交易台。
- 灑水器、甜菜收成箱、神殿效果與部分自動化能力。

## 甜菜手杖

甜菜手杖是早期可取得的整合型農務工具：

- 右鍵可種植耕地：範圍播種，受萌種神殿等級影響。
- 右鍵泥土 / 耕地 / 甜菜土：轉成甜菜耕土或沃化甜菜耕土，受沃土神殿等級影響。
- 右鍵未成熟甜菜：催熟甜菜，受壯苗神殿等級影響。
- 右鍵成熟甜菜：採收甜菜，受甜菜神殿等級影響。
- 甜菜神殿 LV2 擴大採收範圍，LV3 採後補種，LV4 可將收成導入附近甜菜收成箱。
- 外觀會依持有玩家的神殿朱印數自動切換：0、1、4、8、13 印五階；目前用 `CustomModelData` 在背包 tick 時刷新，功能仍以玩家進度判定。

## 聖豬與騎乘

- `beet_on_a_stick`（甜菜釣竿）是原版胡蘿蔔釣竿的甜菜替代品。
- 合成：`minecraft:fishing_rod` + `minecraft:beetroot`。
- 已裝鞍的豬會接受甜菜釣竿作為騎乘控制道具。
- 甜菜釣竿本體使用原版 `FoodOnAStickItem`，因此右鍵加速、耐久消耗、損壞後轉回釣竿的手感跟原版一致。
- 豬也會被手持甜菜釣竿的玩家吸引。

## 轉經桶處理系統

目前正式 UX 是轉經底座加三種桶身；舊的三種單方塊處理台已移除：

- `beet_crank_base` 是唯一真正開 UI 的處理底座。
- 三種桶身是功能頭：
  - `beet_extractor_barrel`：榨取桶。
  - `beet_grinder_barrel`：研磨桶。
  - `beet_washing_barrel`：篩洗桶。
- 對桶身右鍵：手轉 / 讓桶身旋轉。
- 對放在底座上的桶身 Shift + 右鍵：開底座 UI。
- 對底座右鍵：開底座 UI。
- 桶身單獨放置時不再打開舊處理 UI，只作為可手轉的視覺/手動物件。
- 轉經底座 UI 右側有配方提示側欄，會依上方桶身顯示榨取、研磨或篩洗配方。
- 配方側欄支援展開/收合、點選配方列、輸入與輸出圖示預覽、滑鼠提示與背包缺料提示。
- 同種桶身疊在底座上是專門機：連續同種桶身越多，處理步數越多，最多計算 4 個桶身。
- 不同桶身混搭在底座上是萬能機：可依實際安裝的桶身處理多類配方，但不吃同種專門機的加速倍率。
- 沒有底座時，右鍵任一桶身會讓整串相連轉經桶一起手轉，不限制 4 格高度；這主要是視覺與手轉互動，不是底座加工加速。
- 已移除舊方塊：`beet_processing_table`、`beet_grinder_table`、`beet_washing_table`。

相關檔案：

- `src/main/java/net/gs60419/beetpunk/BeetCrankBaseBlockEntity.java`
- `src/main/java/net/gs60419/beetpunk/BeetCrankBaseBlock.java`
- `src/main/java/net/gs60419/beetpunk/BeetCrankBaseMenu.java`
- `src/client/java/net/gs60419/beetpunk/BeetCrankBaseScreen.java`
- `src/main/java/net/gs60419/beetpunk/BeetProcessingTableBlock.java`
- `src/main/java/net/gs60419/beetpunk/BeetProcessingTableBlockEntity.java`
- `src/client/java/net/gs60419/beetpunk/BeetPrayerBarrelRenderer.java`

## 多桶堆疊

底座會讀取上方垂直堆疊的同類型桶身。

- 最大計算高度：`4`
- 同類型桶身會增加處理速度，也就是額外處理步數。
- 目前行為：
  - 1 個桶：每次手轉 / tick 間隔處理 1 步。
  - 2 個桶：處理 2 步。
  - 3 個桶：處理 3 步。
  - 4 個桶：處理 4 步。
- 目前是加速處理，不是一次消耗多份輸入。
- 如果混用不同桶身，會在第一個不相同的桶身停止計算。

實作筆記：

- `BeetCrankBaseBlockEntity.barrelStackHeight(...)` 負責計算同類型桶身堆疊。
- `processSteps(...)` 會迴圈呼叫多次 `processStep(...)`。
- `setBarrelStackSpinning(...)` 會切換整疊桶身的 `spinning` 狀態。
- `BeetPrayerBarrelRenderer.findBase(...)` 會往下掃描堆疊桶身來尋找底座。

## 目前處理配方

榨取桶：

- `BEET_BLOCK` -> `DRIED_BEET_BLOCK` + `BEET_WATER_DROP`
- `DRIED_BEET_BLOCK` -> `BEET_RESIDUE` + `BEET_OIL`
- `BEETROOT_SEEDS` -> `BEET_RESIDUE` + `BEET_OIL`

研磨桶：

- `BEET_BRICK_BLOCK` -> `BEET_COBBLESTONE`
- `BEET_COBBLESTONE` -> `BEET_GRAVEL`
- `BEET_GRAVEL` -> `BEET_SAND` + `BEET_CRYSTAL_GRAIN`

篩洗桶：

- `BEET_SAND` -> `BEET_IRON_DUST` + `CLAY_BALL`
- `BEET_CRYSTAL_GRAIN` -> `BEET_REDSTONE_DUST`

## 目前燃料清單

目前這些物品可以放進轉經底座燃料槽：

- `BEET_STICK`：`100` ticks
- `BEET_PLANK_BLOCK`：`300` ticks
- `DRIED_BEET_BLOCK`：`800` ticks
- `BEET_OIL`：`1600` ticks
- 原版木材 / 木板：`300` ticks
- 原版木炭：`800` ticks
- 原版煤炭：`1600` ticks

相關檔案：

- `src/main/java/net/gs60419/beetpunk/ModFuels.java`

## 渲染注意事項

轉經桶渲染修過很多輪，不要隨便回退這部分。

已修好的問題：

- 桶身旋轉時，原本靜止的方塊模型不能留在原地。
- `spinning=true` 時會隱藏一般靜態桶身模型。
- 旋轉桶身由 client block entity renderer 額外繪製。

重要細節：

- 三種桶身 blockstate 目前同時保留兩種 property 順序，因為測試時匹配曾經不穩：
  - `facing=north,spinning=true`
  - `spinning=true,facing=north`
- `spinning=true` 使用 `beet_prayer_barrel_spin_anchor`，不是完全空模型。
- spin anchor 是一片很薄的深色底板，用來避免玩家從上往下看時穿透桶身看到天空。
- `BeetProcessingTableBlock.shouldChangedStateKeepBlockEntity(...)` 回傳 true，避免切換 `spinning` 時重建或掉落 block entity。
- `BeetProcessingTableBlockEntity.tick(...)` 在桶身被底座或下方堆疊桶控制時，不能覆蓋 `spinning`。
- `BeetCrankBaseBlockEntity.setBarrelStackSpinning(...)` 負責控制整疊桶身。

模型與材質：

- 桶身模型模板：
  `src/main/resources/assets/beetpunk/models/block/template_prayer_barrel.json`
- 旋轉用 anchor：
  `src/main/resources/assets/beetpunk/models/block/beet_prayer_barrel_spin_anchor.json`
- 底座模型：
  `src/main/resources/assets/beetpunk/models/block/beet_crank_base.json`
- 桶身材質：
  `src/main/resources/assets/beetpunk/textures/block/beet_extractor_barrel_side.png`
  `src/main/resources/assets/beetpunk/textures/block/beet_grinder_barrel_side.png`
  `src/main/resources/assets/beetpunk/textures/block/beet_washing_barrel_side.png`
- 轉經底座材質是 32x32：
  `beet_crank_base_top.png`
  `beet_crank_base_side.png`
  `beet_crank_base_bottom.png`

## 美術方向筆記

轉經桶：

- 參考西藏轉經筒的輪廓。
- 不要有側邊凸出的把手或軸。
- 主體為甜菜紅，搭配金色飾帶與功能色中央面板。
- 榨取桶：橘色 / 油感。
- 研磨桶：灰色 / 石頭感。
- 篩洗桶：藍色 / 水感。

轉經底座：

- 32x32 材質。
- 需要看起來像穩固的紅木 / 金屬機械底座。
- 上方有深色承座或軸承區，用來接桶身。
- 側面有紅木板、金色軌線與中央軸承面板。

經書圖示：

- 書皮固定甜菜紅。
- 書頁保持白色或暖白色，讓它仍然像書。
- 神殿身份用彩色標題線、細節或 LV4 光效呈現。
- 小小 16x16 經書圖示上暫時不要塞聖文字圖案，之前測試過會太亂。

雕刻甜菜磚：

- 以原版浮雕凝灰岩磚結構為基底。
- 保留三區結構：上飾帶、中間面板、下飾帶。
- 兩顆甜菜圖案水平並排在中間面板。
- 不要用方框框住甜菜，陰影與高光要跟著甜菜輪廓。

## 已知清理與後續想法

- 可以把轉經底座與多桶堆疊行為寫進巡禮帳或 tooltip。
- 可以在底座 UI 顯示目前桶身堆疊等級 / 速度。
- 未來再決定多桶堆疊是只加速，還是改成增加單次處理量，或綁定高階神殿解鎖。
- 如果轉經桶渲染問題又回來，優先檢查 `spinning` 狀態是否互相覆蓋。
- 如果遊戲內中文字變成 `????`，檢查 `zh_tw.json` 編碼；之前修法是用 JSON Unicode escape 重寫。

## 最近穩定狀態

截至這份紀錄：

- 三種桶身旋轉時不會留下靜止桶。
- 從上往下看穿透看到天空的問題，已用薄片 spin anchor 修掉。
- 桶身放在底座上或單獨放置都可以旋轉。
- 底座是目前唯一預期會開處理 UI 的方塊。
- 同類型桶身垂直堆疊可加速處理，最高 4 倍。
