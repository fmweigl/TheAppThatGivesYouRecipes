#!/usr/bin/env python3
"""Renders the Google Play store graphics into fastlane/metadata/android/en-US/images/:
the 512x512 icon and the 1024x500 feature graphic.

The icon motif comes from art/app-icon/render_icons.py, so the store icon matches the launcher
icon. Text uses Noto Serif from art/fonts (SIL Open Font License, see art/fonts/OFL.txt), the
serif the app's headlines use on Android.

Requires Inkscape and Pillow. Run from anywhere:
    python3 art/play-store/render_store_graphics.py
"""

import importlib.util
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[2]
FONTS = ROOT / "art/fonts"
OUT = ROOT / "fastlane/metadata/android/en-US/images"

spec = importlib.util.spec_from_file_location("render_icons", ROOT / "art/app-icon/render_icons.py")
icons = importlib.util.module_from_spec(spec)
spec.loader.exec_module(icons)

TITLE = "YetAnotherMealsApp"
# Describes the app's features (random recipes, favorites). Revisit it (and the listing texts) when features are added.
TAGLINE = "Find a recipe. Keep your favorites."
FOOTER = "Open source. No ads. No tracking."

WIDTH, HEIGHT = 1024, 500


def font(name, size):
    return ImageFont.truetype(str(FONTS / name), size)


def fit(draw, text, name, size, max_width):
    """The font at [size], shrunk until [text] fits into [max_width]."""
    while draw.textlength(text, font=font(name, size)) > max_width:
        size -= 1
    return font(name, size)


def icon():
    # Full square and opaque: Play applies the rounded mask. Same visible area as the launcher icon.
    out = OUT / "icon.png"
    icons.render(icons.icon_svg(icons.LIGHT, (18, 72)), out, 512)
    Image.open(out).convert("RGB").save(out)


def feature_graphic():
    out = OUT / "featureGraphic.png"
    # In the (ignored) root build directory: render() reports paths relative to the repository.
    motif_png = ROOT / "build/store-graphics/motif.png"
    motif_size = 420
    icons.render(icons.icon_svg(icons.LIGHT, (14, 80)), motif_png, motif_size)
    motif = Image.open(motif_png).convert("RGB")

    image = Image.new("RGB", (WIDTH, HEIGHT), icons.BASIL)
    image.paste(motif, (40, (HEIGHT - motif_size) // 2))

    draw = ImageDraw.Draw(image)
    left, right = 480, WIDTH - 48
    width = right - left
    title = fit(draw, TITLE, "NotoSerif-Bold.ttf", 64, width)
    tagline = fit(draw, TAGLINE, "NotoSerif-Italic.ttf", 34, width)
    footer = fit(draw, FOOTER, "NotoSerif-Italic.ttf", 24, width)

    draw.text((left, 150), TITLE, font=title, fill=icons.CREAM)
    draw.text((left, 248), TAGLINE, font=tagline, fill=icons.SAFFRON)
    draw.text((left, 320), FOOTER, font=footer, fill=icons.CREAM)
    image.save(out)
    print(f"wrote {out.relative_to(ROOT)}")


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    icon()
    feature_graphic()


if __name__ == "__main__":
    main()
