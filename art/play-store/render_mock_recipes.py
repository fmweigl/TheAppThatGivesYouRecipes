#!/usr/bin/env python3
"""Draws the illustrated dishes of the store screenshots' mock recipes (shakshuka, spaghetti al
pomodoro, tomato soup, stuffed tomatoes, tomato bruschetta, tomato tarte tatin) into
art/play-store/mock-recipes/. They replace TheMealDB's photos, whose rights are
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
WOOD = "#B5835A"
WOOD_DARK = "#8C5E3C"
CRUST = "#D9A55B"
CRUST_DARK = "#B97F35"


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


def calyx(x, y, r, angle, color=BASIL):
    """The green star on top of a tomato."""
    points = []
    for i in range(10):
        a = math.radians(angle + i * 36)
        rr = r if i % 2 == 0 else r * 0.35
        points.append(f"{x + rr * math.cos(a):.1f},{y + rr * math.sin(a):.1f}")
    return f'<polygon points="{" ".join(points)}" fill="{color}" stroke="{color}" stroke-width="{r * 0.12:.1f}" stroke-linejoin="round"/>'


def whole_tomato(x, y, r, rng):
    """A whole tomato seen from above, with its calyx and a highlight."""
    return (
        f'<circle cx="{x:.1f}" cy="{y + r * 0.08:.1f}" r="{r:.1f}" fill="{ESPRESSO}" opacity=".2"/>'
        f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r:.1f}" fill="{TOMATO}"/>'
        f'<ellipse cx="{x - r * 0.38:.1f}" cy="{y - r * 0.38:.1f}" rx="{r * 0.22:.1f}" ry="{r * 0.12:.1f}" fill="#FFFFFF" opacity=".35" transform="rotate(-40 {x - r * 0.38:.1f} {y - r * 0.38:.1f})"/>'
        + calyx(x, y, r * 0.38, rng.uniform(0, 72))
    )


def halved_tomato(x, y, r, rng, angle=0.0):
    """A tomato cut in half: skin, flesh, three seed chambers."""
    parts = [
        f'<g transform="rotate({angle:.1f} {x:.1f} {y:.1f})">',
        f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r:.1f}" fill="{PAPRIKA}"/>',
        f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r * 0.86:.1f}" fill="{TOMATO_LIGHT}"/>',
    ]
    for k in range(3):
        a = math.radians(90 + k * 120)
        cx, cy = x + r * 0.42 * math.cos(a), y + r * 0.42 * math.sin(a)
        parts.append(f'<ellipse cx="{cx:.1f}" cy="{cy:.1f}" rx="{r * 0.26:.1f}" ry="{r * 0.2:.1f}" fill="{TOMATO}" transform="rotate({90 + k * 120} {cx:.1f} {cy:.1f})"/>')
        for _ in range(3):
            sx, sy = cx + rng.uniform(-r * 0.12, r * 0.12), cy + rng.uniform(-r * 0.08, r * 0.08)
            parts.append(f'<ellipse cx="{sx:.1f}" cy="{sy:.1f}" rx="{r * 0.05:.1f}" ry="{r * 0.03:.1f}" fill="{SAFFRON}"/>')
    parts.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r * 0.14:.1f}" fill="{TOMATO_LIGHT}"/>')
    parts.append("</g>")
    return "".join(parts)


def tomato_soup():
    rng = random.Random(23)
    c = SIZE / 2
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{WOOD_DARK}"/>']
    # Wooden boards.
    for i in range(0, SIZE, 180):
        parts.append(f'<rect x="0" y="{i}" width="{SIZE}" height="176" fill="{WOOD}"/>')
        parts.append(f'<path d="M0 {i + 60} C300 {i + 40} 700 {i + 90} {SIZE} {i + 70}" stroke="{WOOD_DARK}" stroke-width="5" fill="none" opacity=".45"/>')
    # Two whole tomatoes and a spoon beside the bowl.
    parts.append(whole_tomato(150, 150, 95, rng))
    parts.append(whole_tomato(930, 940, 85, rng))
    parts.append(f'<g transform="rotate(40 {c} {c})" fill="#D9D2CA">'
                 f'<rect x="{c - 13}" y="{c + 380}" width="26" height="300" rx="13"/>'
                 f'<ellipse cx="{c}" cy="{c + 345}" rx="58" ry="80"/></g>')
    # Bowl.
    parts.append(f'<circle cx="{c}" cy="{c + 16}" r="385" fill="{ESPRESSO}" opacity=".25"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="380" fill="{CREAM}"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="352" fill="#EFE3D3"/>')
    # Soup with a cream swirl.
    parts.append(f'<circle cx="{c}" cy="{c}" r="318" fill="{TOMATO}"/>')
    for _ in range(18):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 270)
        parts.append(f'<path d="{blob(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(20, 50), rng)}" fill="{rng.choice([TOMATO_LIGHT, PAPRIKA])}" opacity=".5"/>')
    swirl = f"M{c - 20} {c}"
    for k in range(1, 40):
        a = k * 0.42
        r = 8 + k * 4.6
        swirl += f" L{c + r * math.cos(a):.1f} {c + r * math.sin(a):.1f}"
    parts.append(f'<path d="{swirl}" fill="none" stroke="{CREAM}" stroke-width="22" stroke-linecap="round" stroke-linejoin="round" opacity=".95"/>')
    # Croutons, basil and pepper.
    for _ in range(9):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(120, 280)
        x, y = c + r * math.cos(a), c + r * math.sin(a)
        parts.append(f'<rect x="{x - 22:.1f}" y="{y - 22:.1f}" width="44" height="44" rx="8" fill="{CRUST}" stroke="{CRUST_DARK}" stroke-width="5" transform="rotate({rng.uniform(0, 90):.0f} {x:.1f} {y:.1f})"/>')
    for angle in (190, 250, 310):
        parts.append(leaf(c + 40, c - 40, 110, angle, BASIL))
    for _ in range(30):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 300)
        parts.append(f'<circle cx="{c + r * math.cos(a):.1f}" cy="{c + r * math.sin(a):.1f}" r="{rng.uniform(2.5, 4.5):.1f}" fill="{ESPRESSO}"/>')
    return parts


def stuffed_tomatoes():
    rng = random.Random(31)
    c = SIZE / 2
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{CREAM}"/>']
    # Linen with saffron stripes under the dish.
    for i in range(0, SIZE, 120):
        parts.append(f'<rect x="{i}" y="0" width="16" height="{SIZE}" fill="{SAFFRON}" opacity=".45"/>')
    # Baking dish.
    parts.append(f'<rect x="80" y="150" width="920" height="800" rx="90" fill="{ESPRESSO}" opacity=".2"/>')
    parts.append(f'<rect x="70" y="130" width="940" height="800" rx="90" fill="{PAPRIKA}"/>')
    parts.append(f'<rect x="110" y="170" width="860" height="720" rx="60" fill="#7A2E14"/>')
    # Oil and juices in the dish.
    for _ in range(14):
        parts.append(f'<path d="{blob(rng.uniform(170, 910), rng.uniform(230, 830), rng.uniform(25, 60), rng)}" fill="{SAFFRON}" opacity=".25"/>')
    # Six tomatoes with their caps off, filled with herbed rice.
    for row in range(2):
        for col in range(3):
            x = 260 + col * 280 + rng.uniform(-12, 12)
            y = 375 + row * 310 + rng.uniform(-12, 12)
            parts.append(f'<circle cx="{x:.1f}" cy="{y + 8:.1f}" r="122" fill="{ESPRESSO}" opacity=".3"/>')
            parts.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="120" fill="{TOMATO}"/>')
            parts.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="92" fill="{TOMATO_LIGHT}"/>')
            parts.append(f'<path d="{blob(x, y, 80, rng, 0.08, 10)}" fill="#F4E6C8"/>')
            for _ in range(26):
                a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 68)
                gx, gy = x + r * math.cos(a), y + r * math.sin(a)
                parts.append(f'<ellipse cx="{gx:.1f}" cy="{gy:.1f}" rx="9" ry="4" fill="#FFFFFF" transform="rotate({rng.uniform(0, 180):.0f} {gx:.1f} {gy:.1f})"/>')
            for _ in range(7):
                a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 60)
                parts.append(leaf(x + r * math.cos(a), y + r * math.sin(a), rng.uniform(16, 24), rng.uniform(0, 360), HERB))
            parts.append(f'<path d="{blob(x - 30, y - 40, 22, rng, 0.25, 7)}" fill="{CRUST}"/>')
    # Two caps resting on the rim.
    for x, y in ((150, 960), (960, 120)):
        parts.append(f'<circle cx="{x}" cy="{y}" r="70" fill="{TOMATO}"/>')
        parts.append(calyx(x, y, 40, rng.uniform(0, 72)))
        parts.append(f'<rect x="{x - 5}" y="{y - 40}" width="10" height="40" rx="5" fill="{BASIL_DARK}"/>')
    return parts


def bruschetta():
    rng = random.Random(43)
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{ESPRESSO}"/>']
    # Wooden board.
    parts.append(f'<rect x="60" y="110" width="960" height="860" rx="70" fill="{WOOD}"/>')
    for i in range(7):
        y = 180 + i * 115
        parts.append(f'<path d="M100 {y} C400 {y - 30} 700 {y + 30} 980 {y}" stroke="{WOOD_DARK}" stroke-width="5" fill="none" opacity=".4"/>')
    # Four slices of toasted bread with diced tomato and basil.
    slices = [(310, 340, -14), (770, 330, 10), (300, 750, 8), (770, 750, -10)]
    for x, y, angle in slices:
        parts.append(f'<g transform="rotate({angle} {x} {y})">')
        parts.append(f'<ellipse cx="{x}" cy="{y + 12}" rx="205" ry="150" fill="{ESPRESSO}" opacity=".3"/>')
        parts.append(f'<path d="{blob(x, y, 175, rng, 0.06, 12)}" fill="{CRUST_DARK}" transform="scale(1.15 0.82) translate({-x * 0.15 / 1.15:.1f} {y * 0.18 / 0.82:.1f})"/>')
        parts.append(f'<ellipse cx="{x}" cy="{y}" rx="180" ry="125" fill="{CRUST}"/>')
        for _ in range(16):
            parts.append(f'<circle cx="{x + rng.uniform(-150, 150):.1f}" cy="{y + rng.uniform(-100, 100):.1f}" r="{rng.uniform(4, 8):.1f}" fill="{CRUST_DARK}" opacity=".6"/>')
        for _ in range(22):
            dx, dy = rng.uniform(-125, 125), rng.uniform(-80, 80)
            if (dx / 140) ** 2 + (dy / 95) ** 2 > 1:
                continue
            cx, cy = x + dx, y + dy
            parts.append(f'<rect x="{cx - 17:.1f}" y="{cy - 17:.1f}" width="34" height="34" rx="7" fill="{rng.choice([TOMATO, TOMATO_LIGHT, PAPRIKA])}" transform="rotate({rng.uniform(0, 90):.0f} {cx:.1f} {cy:.1f})"/>')
        parts.append(leaf(x - 20, y - 10, 90, rng.uniform(0, 360), BASIL))
        parts.append(leaf(x + 10, y + 10, 70, rng.uniform(0, 360), HERB))
        parts.append("</g>")
    # Olive oil drizzle and salt flakes.
    for x, y, _ in slices:
        parts.append(f'<path d="M{x - 120} {y - 30} C{x - 40} {y - 90} {x + 40} {y + 60} {x + 130} {y - 10}" stroke="{SAFFRON}" stroke-width="7" fill="none" stroke-linecap="round" opacity=".7"/>')
    for _ in range(40):
        parts.append(f'<rect x="{rng.uniform(100, 980):.1f}" y="{rng.uniform(150, 930):.1f}" width="7" height="7" fill="#FFFFFF" opacity=".85"/>')
    # Cherry tomatoes on the vine in a corner.
    parts.append(f'<path d="M920 70 C860 120 830 160 800 70" stroke="{BASIL_DARK}" stroke-width="10" fill="none"/>')
    for x, y in ((930, 110), (850, 150), (790, 95)):
        parts.append(whole_tomato(x, y, 48, rng))
    return parts


def tomato_tarte_tatin():
    rng = random.Random(59)
    c = SIZE / 2
    parts = [f'<rect width="{SIZE}" height="{SIZE}" fill="{BASIL_DARK}"/>']
    # Napkin.
    parts.append(f'<rect x="640" y="-80" width="520" height="420" rx="24" fill="{CREAM}" opacity=".9" transform="rotate(14 900 130)"/>')
    for i in range(5):
        parts.append(f'<path d="M640 {-40 + i * 80} h520" stroke="{PAPRIKA}" stroke-width="10" opacity=".5" transform="rotate(14 900 130)"/>')
    # Plate.
    parts.append(f'<circle cx="{c}" cy="{c + 16}" r="420" fill="{ESPRESSO}" opacity=".35"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="415" fill="{CREAM}"/>')
    parts.append(f'<circle cx="{c}" cy="{c}" r="380" fill="#F3E8DA"/>')
    # Pastry rim.
    parts.append(f'<path d="{blob(c, c, 350, rng, 0.025, 24)}" fill="{CRUST_DARK}"/>')
    parts.append(f'<path d="{blob(c, c, 335, rng, 0.02, 24)}" fill="{CRUST}"/>')
    # Caramel.
    parts.append(f'<circle cx="{c}" cy="{c}" r="300" fill="#8A3A12"/>')
    # Halved tomatoes in two rings and one in the middle.
    for ring, count, size in ((215, 9, 72), (95, 4, 64)):
        offset = rng.uniform(0, 360)
        for k in range(count):
            a = math.radians(offset + k * 360 / count)
            parts.append(halved_tomato(c + ring * math.cos(a), c + ring * math.sin(a), size, rng, rng.uniform(0, 120)))
    parts.append(halved_tomato(c, c, 52, rng, 30))
    # Caramel glaze, thyme and flaky salt.
    for _ in range(18):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 290)
        parts.append(f'<path d="{blob(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(8, 16), rng)}" fill="{SAFFRON}" opacity=".45"/>')
    for _ in range(40):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 300)
        parts.append(leaf(c + r * math.cos(a), c + r * math.sin(a), rng.uniform(12, 18), rng.uniform(0, 360), HERB))
    for _ in range(30):
        a, r = rng.uniform(0, 2 * math.pi), rng.uniform(0, 300)
        parts.append(f'<rect x="{c + r * math.cos(a):.1f}" y="{c + r * math.sin(a):.1f}" width="6" height="6" fill="#FFFFFF" opacity=".85"/>')
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
    render("tomato-soup", tomato_soup())
    render("stuffed-tomatoes", stuffed_tomatoes())
    render("bruschetta", bruschetta())
    render("tomato-tarte-tatin", tomato_tarte_tatin())
