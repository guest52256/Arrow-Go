#!/usr/bin/env python3
import sys

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y
    def __repr__(self):
        return f"Point({self.x}, {self.y})"
    def __eq__(self, other):
        return isinstance(other, Point) and self.x == other.x and self.y == other.y
    def __hash__(self):
        return hash((self.x, self.y))

class Arrow:
    def __init__(self, id, color, direction, path_points):
        self.id = id
        self.color = color
        self.direction = direction # 'UP', 'DOWN', 'LEFT', 'RIGHT'
        self.path_points = [Point(p[0], p[1]) for p in path_points]

def get_occupied_points(arrow):
    pts = set()
    for i in range(len(arrow.path_points) - 1):
        p1 = arrow.path_points[i]
        p2 = arrow.path_points[i + 1]
        min_x, max_x = min(p1.x, p2.x), max(p1.x, p2.x)
        min_y, max_y = min(p1.y, p2.y), max(p1.y, p2.y)
        if p1.x == p2.x:
            for y in range(min_y, max_y + 1):
                pts.add((p1.x, y))
        elif p1.y == p2.y:
            for x in range(min_x, max_x + 1):
                pts.add((x, p1.y))
        else:
            pts.add((p1.x, p1.y))
            pts.add((p2.x, p2.y))
    return pts

def can_exit(arrow, all_arrows, grid_w=16, grid_h=16):
    if not arrow.path_points:
        return False
    head = arrow.path_points[-1]
    ray_points = []
    if arrow.direction == 'UP':
        for y in range(head.y - 1, -2, -1):
            ray_points.append((head.x, y))
    elif arrow.direction == 'DOWN':
        for y in range(head.y + 1, grid_h + 1):
            ray_points.append((head.x, y))
    elif arrow.direction == 'LEFT':
        for x in range(head.x - 1, -2, -1):
            ray_points.append((x, head.y))
    elif arrow.direction == 'RIGHT':
        for x in range(head.x + 1, grid_w + 1):
            ray_points.append((x, head.y))

    other_arrows = [a for a in all_arrows if a.id != arrow.id]
    occupied_by_others = set()
    for a in other_arrows:
        occupied_by_others.update(get_occupied_points(a))

    for rpt in ray_points:
        if rpt in occupied_by_others:
            return False
    return True

def validate_level(name, arrows):
    # 1. Check body intersections
    occupied_map = {}
    for a in arrows:
        pts = get_occupied_points(a)
        occupied_map[a.id] = pts

    for i in range(len(arrows)):
        for j in range(i + 1, len(arrows)):
            a1 = arrows[i]
            a2 = arrows[j]
            inter = occupied_map[a1.id].intersection(occupied_map[a2.id])
            if inter:
                raise ValueError(f"Level '{name}': arrows '{a1.id}' and '{a2.id}' intersect at {inter}")

    # 2. Check solvability
    remaining = list(arrows)
    cleared = []
    while remaining:
        can_clear = None
        for a in remaining:
            if can_exit(a, remaining):
                can_clear = a
                break
        if not can_clear:
            rem_ids = [a.id for a in remaining]
            raise ValueError(f"Level '{name}' is STUCK! Cannot clear any of {rem_ids}. Cleared so far: {cleared}")
        remaining.remove(can_clear)
        cleared.append(can_clear.id)
    print(f"Level '{name}' passed! Solved in order: {cleared}")

print("Validator ready.")
