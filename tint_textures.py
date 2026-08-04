"""
Extract vanilla textures from Minecraft jar, apply beet-red tint, save as mod textures.

Usage:
    python tint_textures.py <minecraft-jar-path> <assets-output-path>

Cloth block: beetpunk:block/beet_cloth_block.png  (from magenta_wool)
Bed entity:  beetpunk:entity/bed/beet.png          (from magenta entity bed)

Tint: #8B1538 — deep beetroot red (multiply blend).
"""

import sys
import zipfile
from pathlib import Path
from PIL import Image

if len(sys.argv) != 3:
    raise SystemExit("Usage: python tint_textures.py <minecraft-jar-path> <assets-output-path>")

JAR = Path(sys.argv[1])
RES = Path(sys.argv[2])

TINT_R, TINT_G, TINT_B = 0x8B, 0x15, 0x38  # #8B1538

def apply_tint(img: Image.Image) -> Image.Image:
    img = img.convert("RGBA")
    pixels = img.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = pixels[x, y]
            pixels[x, y] = (
                r * TINT_R // 255,
                g * TINT_G // 255,
                b * TINT_B // 255,
                a,
            )
    return img

TARGETS = {
    "assets/minecraft/textures/block/magenta_wool.png":  RES / "beetpunk/textures/block/beet_cloth_block.png",
    "assets/minecraft/textures/entity/bed/magenta.png":  RES / "beetpunk/textures/entity/bed/beet.png",
}

with zipfile.ZipFile(JAR) as jar:
    for src, dest in TARGETS.items():
        dest.parent.mkdir(parents=True, exist_ok=True)
        with jar.open(src) as f:
            img = Image.open(f).copy()
        tinted = apply_tint(img)
        tinted.save(dest)
        print(f"Saved {dest}")

print("Done.")
