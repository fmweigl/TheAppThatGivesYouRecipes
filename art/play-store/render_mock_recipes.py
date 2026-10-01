#!/usr/bin/env python3
"""Draws the illustrated dishes of the store screenshots' mock recipes (shakshuka, spaghetti al
pomodoro) into art/play-store/mock-recipes/. They replace TheMealDB's photos, whose rights are
unclear for store screenshots; the debug build's screenshot mode shows them (see CLAUDE.md,
"Store listing").

Flat, top-down illustrations in the MealsTheme palette. Seeded, so every run draws the same.
Requires Inkscape. Run from anywhere:
    python3 art/play-store/render_mock_recipes.py
"""

import math
import random
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "art/play-store/mock-recipes"
SIZE = 1080

PAPRIKA = "#A8401E"
TOMATO = "#C8502A"
TOMATO_LIGHT = "#DD6B3D"
BASIL = "#4C6B30"
BASIL_DARK = "#354E1B"
HERB = "#7FA04A"
SAFFRON = "#F2BF48"
YOLK = "#F5A623"
CREAM = "#FFF8F0"
ESPRESSO = "#18120E"
IRON = "#2B2522"


def leaf(x, y, length, angle, color):
    """A small leaf (herb or basil), pointing along [angle] degrees."""
    w = length * 0.42
    return (
        f'<g transform="translate({x:.1f} {y:.1f}) rotate({angle:.1f})">'
        f'<path d="M0 0 C{length * 0.3:.1f} {-w:.1f} {length * 0.75:.1f} {-w:.1f} {length:.1f} 0 '
        f'C{length * 0.75:.1f} {w:.1f} {length * 0.3:.1f} {w:.1f} 0 0z" fill="{color}"/>'
        f'<path d="M{length * 0.1:.1f} 0 H{length * 0.85:.1f}" stroke="{BASIL_DARK}" stroke-width="{length * 0.04:.1f}" '
        f'stroke-linecap="round" opacity=".5"/></g>'
    )


def blob(cx, cy, r, rng, wobble=0.18, points=9):
    """A closed, irregular round shape (egg white, sauce, cheese)."""
    pts = []
    for i in range(points):
        a = 2 * math.pi * i / points
        rr = r * (1 + rng.uniform(-wobble, wobble))
        pts.append((cx + rr * math.cos(a), cy + rr * math.sin(a)))
    d = f"M{(pts[0][0] + pts[-1][0]) / 2:.1f} {(pts[0][1] + pts[-1][1]) / 2:.1f}"
    for i, (x, y) in enumerate(pts):
        nx, ny = pts[(i + 1) % points]
        d += f" Q{x:.1f} {y:.1f} {(x + nx) / 2:.1f} {(y + ny) / 2:.1f}"
    return d + "z"


def shakshuka():
    rng = random.Random(7)
    c = SIZE / 2
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{BASIL_DARK}"/>']
    # Linen napkin and a wooden spoon at the side.
    parts.append(f'<rect x="-60" y="760" width="420" height="420" rx="24" fill="{CREAM}" opacity=".9" transform="rotate(-12 150 970)"/>')
    for i in range(6):
        parts.append(f'<path d="M{-40 + i * 70} 780 l60 420" stroke="{SAFFRON}" stroke-width="10" opacity=".5" transform="rotate(-12 150 970)"/>')
    # Cast-iron pan with handle.
    parts.append(f'<rect x="{c + 330}" y="{c - 45}" width="320" height="90" rx="40" fill="{IRON}" transform="rotate(-35 {c} {c})"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="400" fill="{IRON}"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="372" fill="#3A322E"/>')
    # Tomato sauce with lighter streaks.
    parts.append(f'<circle cx="{c}" cy="{c}" r="352" fill="{TOMATO}"/>')
    for _ in range(26):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 300)
        parts.append(f'<path d="{blob(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(18, 46), rng)}" fill="{rng.choice([TOMATO_LIGHT, PAPRIKA])}" opacity=".7"/>')
    # Five eggs.
    eggs = [(c - 150, c - 120), (c + 140, c - 150), (c + 170, c + 120), (c - 120, c + 160), (c + 10, c + 10)]
    for x, y in eggs:
        parts.append(f'<path d="{blob(x, y, 105, rng, wobble=0.16, points=11)}" fill="{CREAM}"/>')
        parts.append(f'<circle cx="{x + rng.uniform(-10, 10):.1f}" cy="{y + rng.uniform(-10, 10):.1f}" r="44" fill="{YOLK}"/>')
        parts.append(f'<circle cx="{x - 12:.1f}" cy="{y - 14:.1f}" r="12" fill="{CREAM}" opacity=".55"/>')
    # Crumbled feta, herbs and chili flakes.
    for _ in range(14):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(60, 320)
        parts.append(f'<path d="{blob(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(10, 18), rng, 0.3, 6)}" fill="#FFFFFF" opacity=".95"/>')
    for _ in range(34):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(20, 330)
        parts.append(leaf(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(22, 36), rng.uniform(0, 360), rng.choice([HERB, BASIL])))
    for _ in range(40):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 340)
        parts.append(f'<circle cx="{c + r * math.cos(a):.1f}" cy="{c + r * math.sin(a):.1f}" r="{rng.uniform(2.5, 5):.1f}" fill="#7A1E0B"/>')
    return parts


def spaghetti():
    rng = random.Random(11)
    c = SIZE / 2
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{CREAM}"/>']
    # Gingham tablecloth.
    for i in range(0, SIZE, 90):
        parts.append(f'<rect x="{i}" y="0" width="45" height="{SIZE}" fill="{PAPRIKA}" opacity=".45"/>')
        parts.append(f'<rect x="0" y="{i}" width="{SIZE}" height="45" fill="{PAPRIKA}" opacity=".45"/>')
    # Fork beside the plate.
    parts.append(f'<g transform="rotate(28 {c} {c})" fill="#D9D2CA">'
                 f'<rect x="{c + 425}" y="{c - 330}" width="26" height="520" rx="13"/>'
                 + "".join(f'<rect x="{c + 405 + k * 22}" y="{c - 440}" width="10" height="130" rx="5"/>' for k in range(4))
                 + f'<rect x="{c + 403}" y="{c - 330}" width="78" height="40" rx="16"/></g>')
    # Plate.
    parts.append(f'<circle cx="{c}" cy="{c + 14}" r="410" fill="{ESPRESSO}" opacity=".18"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="405" fill="#FFFFFF"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="300" fill="{CREAM}"/>')
    # Spaghetti: loose arcs swirling around the center.
    for _ in range(150):
        r = rng.uniform(40, 255)
        start = rng.uniform(0, 2 * math.pi)
        sweep = rng.uniform(0.8, 2.4)
        x1, y1 = c + r * math.cos(start), c + r * math.sin(start)
        x2, y2 = c + r * math.cos(start + sweep), c + r * math.sin(start + sweep)
        color = rng.choice([SAFFRON, "#F5CF6E", "#E8B23A"])
        parts.append(f'<path d="M{x1:.1f} {y1:.1f} A{r:.1f} {r:.1f} 0 0 1 {x2:.1f} {y2:.1f}" fill="none" stroke="{color}" stroke-width="9" stroke-linecap="round"/>')
    # Tomato sauce on top, with chunks.
    parts.append(f'<path d="{blob(c, c, 130, rng, 0.2, 10)}" fill="{TOMATO}"/>')
    for _ in range(9):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 100)
        parts.append(f'<path d="{blob(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(16, 30), rng)}" fill="{rng.choice([PAPRIKA, TOMATO_LIGHT])}"/>')
    # Parmesan shavings and basil leaves.
    for _ in range(12):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(40, 200)
        x, y = c + r * math.cos(a), c + r * math.sin(a)
        parts.append(f'<rect x="{x:.1f}" y="{y:.1f}" width="34" height="12" rx="4" fill="#FFF3D6" transform="rotate({rng.uniform(0, 180):.0f} {x:.1f} {y:.1f})"/>')
    for angle in (200, 270, 335):
        parts.append(leaf(c + 10, c - 10, 120, angle, BASIL))
    parts.append(leaf(c + 10, c - 10, 85, 120, HERB))
    return parts


def render(name, parts):
    OUT.mkdir(parents=True, exist_ok=True)
    svg = OUT / f"{name}.svg"
    png = OUT / f"{name}.png"
    svg.write_text(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {SIZE} {SIZE}">{"".join(parts)}</svg>\n')
    subprocess.run(
        ["inkscape", str(svg), "--export-type=png", f"--export-filename={png}", "-w", str(SIZE), "-h", str(SIZE)],
        check=True,
        capture_output=True,
    )
    print(f"wrote {png.relative_to(ROOT)}")


if __name__ == "__main__":
    render("shakshuka", shakshuka())
    render("spaghetti", spaghetti())
