#!/usr/bin/env python3
"""Renders the app icon's bitmaps for Android (legacy), iOS and desktop.

The icon is drawn on Android's 108x108 adaptive-icon grid. The Android adaptive icon itself is
vector (androidApp/src/main/res/drawable*/ic_launcher_*.xml); keep it in sync with MOTIF below.

Requires Inkscape (SVG rendering) and Pillow (.ico/.icns). Run from anywhere:
    python3 art/app-icon/render_icons.py
"""

import subprocess
import tempfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]

# Colors from MealsTheme (core/designsystem/.../Color.kt).
BASIL = "#4C6B30"
BASIL_DARK = "#354E1B"
CREAM = "#FFF8F0"
SAFFRON = "#F2BF48"
ESPRESSO = "#18120E"

LIGHT = dict(background=BASIL, disc=BASIL_DARK, fork=CREAM, leaf=SAFFRON)
DARK = dict(background=ESPRESSO, disc=BASIL_DARK, fork=CREAM, leaf=SAFFRON)
TINTED = dict(background="#000000", disc="#262626", fork="#FFFFFF", leaf="#B3B3B3")

# A two-pronged fork shaped like a "Y", with a leaf. The fork and leaf are scaled around the
# center so they stay inside the adaptive icon's 66-unit safe zone.
MOTIF = """
<rect width="108" height="108" fill="{background}"/>
<circle cx="54" cy="54" r="30" fill="{disc}"/>
<g transform="translate(54 54) scale(0.85) translate(-54 -54)">
  <path d="M54 80V61C54 50 39 49 39 31M54 61C54 50 69 49 69 31" fill="none" stroke="{fork}"
        stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>
  <circle cx="54" cy="61" r="5.5" fill="{fork}"/>
  <path d="M69 31c4.5-4.5 10-4 12 0c-4 3-8.5 3-12 0z" fill="{leaf}"/>
</g>
"""


def icon_svg(colors, crop, shape=None, inset=0.0):
    """An SVG of the icon: the grid area `crop` = (origin, size), clipped to `shape`
    ("circle", "rounded" or None for a full square), with transparent margins of `inset`."""
    size = 1000
    inner = size * (1 - 2 * inset)
    offset = size * inset
    origin, extent = crop
    clips = {
        "circle": f'<circle cx="{size / 2}" cy="{size / 2}" r="{inner / 2}"/>',
        "rounded": f'<rect x="{offset}" y="{offset}" width="{inner}" height="{inner}" rx="{inner * 0.225}"/>',
    }
    body = (
        f'<svg x="{offset}" y="{offset}" width="{inner}" height="{inner}" '
        f'viewBox="{origin} {origin} {extent} {extent}">{MOTIF.format(**colors)}</svg>'
    )
    if shape:
        body = f'<defs><clipPath id="c">{clips[shape]}</clipPath></defs><g clip-path="url(#c)">{body}</g>'
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {size} {size}">{body}</svg>'


def render(svg, out, px):
    out.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.NamedTemporaryFile("w", suffix=".svg", delete=False) as f:
        f.write(svg)
    subprocess.run(
        ["inkscape", f.name, "--export-type=png", f"--export-filename={out}", "-w", str(px), "-h", str(px)],
        check=True,
        capture_output=True,
    )
    Path(f.name).unlink()
    print(f"wrote {out.relative_to(ROOT)}")


def main():
    # Android launcher icons for API 24-25 (26+ uses the adaptive vector icon): the visible
    # 72-unit area of the grid.
    res = ROOT / "androidApp/src/main/res"
    visible = (18, 72)
    for density, px in dict(mdpi=48, hdpi=72, xhdpi=96, xxhdpi=144, xxxhdpi=192).items():
        render(icon_svg(LIGHT, visible, "rounded"), res / f"mipmap-{density}/ic_launcher.png", px)
        render(icon_svg(LIGHT, visible, "circle"), res / f"mipmap-{density}/ic_launcher_round.png", px)

    # iOS: full-bleed and opaque; the system applies the mask.
    ios = ROOT / "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset"
    full = (14, 80)
    render(icon_svg(LIGHT, full), ios / "app-icon-1024.png", 1024)
    render(icon_svg(DARK, full), ios / "app-icon-1024-dark.png", 1024)
    render(icon_svg(TINTED, full), ios / "app-icon-1024-tinted.png", 1024)
    for name in ("app-icon-1024.png", "app-icon-1024-dark.png", "app-icon-1024-tinted.png"):
        path = ios / name
        Image.open(path).convert("RGB").save(path)  # iOS rejects app icons with an alpha channel.

    # Desktop: a rounded tile with transparent margins, for the window and the installers.
    desktop = ROOT / "desktopApp"
    png = desktop / "src/main/resources/icon.png"
    render(icon_svg(LIGHT, full, "rounded", inset=0.06), png, 1024)
    image = Image.open(png)
    image.save(desktop / "icons/icon.ico", sizes=[(s, s) for s in (16, 24, 32, 48, 64, 128, 256)])
    image.save(desktop / "icons/icon.icns")
    image.resize((512, 512), Image.LANCZOS).save(png)
    print("wrote desktopApp/icons/icon.ico, desktopApp/icons/icon.icns")


if __name__ == "__main__":
    main()
