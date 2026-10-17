#!/usr/bin/env python3
"""Draws the mod icon deterministically (no generative AI involved).

The icon mirrors the in-game look of the mod: dark rounded square with two
translucent pills — a translate chip with 文 and a target-language chip with EN.

Usage:  python3 tools/make_icon.py
Output: src/main/resources/assets/translatebar/icon.png (256x256)
        modrinth/icon.png (copy for the project page upload)
"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

SIZE = 256
RADIUS = 52

BG_TOP = (30, 35, 43)       # #1E232B
BG_BOTTOM = (13, 15, 19)    # #0D0F13
BORDER = (51, 58, 70)       # #333A46

PILL_TOP = (35, 41, 53)     # #232935
PILL_BOTTOM = (20, 23, 29)  # #14171D
PILL_BORDER = (58, 66, 80)  # #3A4250

TEXT_MAIN = (242, 245, 250)
TEXT_DIM = (225, 231, 240)
SHADOW = (0, 0, 0, 140)
TEXT_SHADOW = (0, 0, 0, 130)

CJK_FONT = "/usr/share/fonts/noto-cjk/NotoSansCJK-Bold.ttc"
LATIN_FONT = "/usr/share/fonts/TTF/DejaVuSans-Bold.ttf"

ROOT = Path(__file__).resolve().parents[1]


def vertical_gradient(width: int, height: int, top: tuple, bottom: tuple) -> Image.Image:
    grad = Image.new("RGBA", (1, height))
    for y in range(height):
        t = y / max(1, height - 1)
        color = tuple(round(a + (b - a) * t) for a, b in zip(top, bottom)) + (255,)
        grad.putpixel((0, y), color)
    return grad.resize((width, height), Image.BILINEAR)


def rounded_mask(size: tuple, radius: int) -> Image.Image:
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, size[0] - 1, size[1] - 1], radius=radius, fill=255)
    return mask


def draw_pill(base: Image.Image, box: tuple, radius: int, label: str, font: ImageFont.FreeTypeFont,
              text_color: tuple) -> None:
    x0, y0, x1, y1 = box
    w, h = x1 - x0, y1 - y0

    # Soft drop shadow under the pill.
    shadow = Image.new("RGBA", base.size, (0, 0, 0, 0))
    ImageDraw.Draw(shadow).rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=SHADOW)
    base.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(5)), (0, 3))

    # Translucent body with a subtle vertical gradient.
    body = vertical_gradient(w, h, PILL_TOP, PILL_BOTTOM)
    base.paste(body, (x0, y0), rounded_mask((w, h), radius))

    draw = ImageDraw.Draw(base)
    # Border and a light top edge (the same "glass" cue as in the mod).
    draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, outline=PILL_BORDER, width=2)
    draw.rounded_rectangle([x0 + 8, y0 + 4, x1 - 8, y0 + 5], radius=1, fill=(255, 255, 255, 26))

    # Centered label with a slight shadow.
    cx, cy = (x0 + x1) / 2, (y0 + y1) / 2
    draw.text((cx, cy + 2), label, font=font, fill=TEXT_SHADOW, anchor="mm")
    draw.text((cx, cy), label, font=font, fill=text_color, anchor="mm")


def main() -> None:
    base = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))

    # Background: dark rounded square with a gentle gradient and border.
    bg = vertical_gradient(SIZE, SIZE, BG_TOP, BG_BOTTOM)
    base.paste(bg, (0, 0), rounded_mask((SIZE, SIZE), RADIUS))
    ImageDraw.Draw(base).rounded_rectangle([1, 1, SIZE - 2, SIZE - 2], radius=RADIUS - 1,
                                           outline=BORDER, width=2)

    # Two pills, side by side — the mod's signature pair of buttons.
    cjk = ImageFont.truetype(CJK_FONT, 60, index=0)
    latin = ImageFont.truetype(LATIN_FONT, 30)

    pill_h = 84
    y0 = (SIZE - pill_h) // 2
    y1 = y0 + pill_h
    gap = 12
    w1, w2 = 124, 72
    total = w1 + gap + w2
    x = (SIZE - total) // 2

    draw_pill(base, (x, y0, x + w1, y1), 18, "文", cjk, TEXT_MAIN)
    draw_pill(base, (x + w1 + gap, y0, x + w1 + gap + w2, y1), 18, "EN", latin, TEXT_DIM)

    out = ROOT / "src/main/resources/assets/translatebar/icon.png"
    out.parent.mkdir(parents=True, exist_ok=True)
    base.save(out)
    print("saved", out)

    copy = ROOT / "modrinth/icon.png"
    copy.parent.mkdir(parents=True, exist_ok=True)
    base.save(copy)
    print("saved", copy)


if __name__ == "__main__":
    main()
