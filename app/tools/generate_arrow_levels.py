#!/usr/bin/env python3
import sys

def pt(x, y):
    return (x, y)

class ArrowDef:
    def __init__(self, id, color, direction, points):
        self.id = id
        self.color = color
        self.direction = direction
        self.points = points

def get_occupied(pts):
    occupied = set()
    for i in range(len(pts) - 1):
        p1 = pts[i]
        p2 = pts[i + 1]
        min_x, max_x = min(p1[0], p2[0]), max(p1[0], p2[0])
        min_y, max_y = min(p1[1], p2[1]), max(p1[1], p2[1])
        if p1[0] == p2[0]:
            for y in range(min_y, max_y + 1):
                occupied.add((p1[0], y))
        elif p1[1] == p2[1]:
            for x in range(min_x, max_x + 1):
                occupied.add((x, p1[1]))
        else:
            occupied.add(p1)
            occupied.add(p2)
    return occupied

def can_exit(arrow, all_arrows, grid_w=16, grid_h=16):
    head = arrow.points[-1]
    ray = []
    if arrow.direction == 'UP':
        for y in range(head.y - 1 if hasattr(head, 'y') else head[1] - 1, -2, -1):
            ray.append((head[0], y))
    elif arrow.direction == 'DOWN':
        for y in range((head[1] + 1), grid_h + 1):
            ray.append((head[0], y))
    elif arrow.direction == 'LEFT':
        for x in range((head[0] - 1), -2, -1):
            ray.append((x, head[1]))
    elif arrow.direction == 'RIGHT':
        for x in range((head[0] + 1), grid_w + 1):
            ray.append((x, head[1]))

    other_occupied = set()
    for a in all_arrows:
        if a.id != arrow.id:
            other_occupied.update(get_occupied(a.points))

    for r in ray:
        if r in other_occupied:
            return False
    return True

levels_data = [
    # 1. C1: Rooster
    {
        "id": "c1_rooster",
        "num": 1,
        "name": "C1: Rooster",
        "diff": "EASY",
        "arrows": [
            ArrowDef("r_comb_top", "Red", "UP", [(7, 2), (7, 1), (8, 1)]),
            ArrowDef("r_comb_back", "Orange", "LEFT", [(6, 2), (5, 2), (5, 1)]),
            ArrowDef("r_beak", "Yellow", "RIGHT", [(9, 3), (12, 3), (12, 4)]),
            ArrowDef("r_wattle", "Red", "DOWN", [(9, 5), (9, 7), (10, 7)]),
            ArrowDef("r_tail_high", "Cyan", "UP", [(2, 4), (1, 4), (1, 2), (2, 2)]),
            ArrowDef("r_tail_mid", "Blue", "LEFT", [(4, 6), (2, 6), (2, 5)]),
            ArrowDef("r_tail_low", "Purple", "LEFT", [(4, 8), (2, 8), (2, 7)]),
            ArrowDef("r_tail_base", "Pink", "DOWN", [(3, 9), (1, 9), (1, 12)]),
            ArrowDef("r_neck", "Orange", "UP", [(7, 4), (7, 3), (6, 3)]),
            ArrowDef("r_breast", "Green", "RIGHT", [(8, 5), (11, 5), (11, 6)]),
            ArrowDef("r_chest", "Yellow", "RIGHT", [(8, 7), (12, 7), (12, 8)]),
            ArrowDef("r_wing_top", "Purple", "UP", [(5, 6), (6, 6), (6, 4), (5, 4)]),
            ArrowDef("r_wing_mid", "Blue", "RIGHT", [(5, 8), (7, 8), (7, 9)]),
            ArrowDef("r_wing_tip", "Cyan", "DOWN", [(5, 10), (5, 12), (4, 12)]),
            ArrowDef("r_belly", "Green", "RIGHT", [(4, 11), (8, 11), (8, 10)]),
            ArrowDef("r_leg_f", "Orange", "DOWN", [(8, 13), (8, 14)]),
            ArrowDef("r_leg_b", "Yellow", "DOWN", [(6, 13), (6, 14)]),
            ArrowDef("r_feet", "Red", "RIGHT", [(7, 15), (11, 15)])
        ],
        "hearts": 3, "limit": 25, "min_r": 150, "max_r": 300
    },

    # 2. C2: Elephant
    {
        "id": "c2_elephant",
        "num": 2,
        "name": "C2: Elephant",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("e_crown_l", "Cyan", "UP", [(6, 3), (6, 1), (7, 1)]),
            ArrowDef("e_crown_r", "Blue", "UP", [(9, 3), (9, 1), (8, 1)]),
            ArrowDef("e_ear_l_top", "Purple", "LEFT", [(5, 2), (2, 2), (2, 3)]),
            ArrowDef("e_ear_l_rim", "Pink", "UP", [(1, 6), (1, 4), (3, 4)]),
            ArrowDef("e_ear_l_bot", "Blue", "LEFT", [(4, 5), (2, 5), (2, 7)]),
            ArrowDef("e_ear_r_top", "Purple", "RIGHT", [(10, 2), (13, 2), (13, 3)]),
            ArrowDef("e_ear_r_rim", "Pink", "UP", [(14, 6), (14, 4), (12, 4)]),
            ArrowDef("e_ear_r_bot", "Blue", "RIGHT", [(11, 5), (13, 5), (13, 7)]),
            ArrowDef("e_eye_l", "Orange", "LEFT", [(6, 4), (5, 4), (5, 5)]),
            ArrowDef("e_eye_r", "Yellow", "RIGHT", [(9, 4), (10, 4), (10, 5)]),
            ArrowDef("e_trunk_base", "Green", "UP", [(7, 5), (8, 5), (8, 3), (8, 2)]),
            ArrowDef("e_trunk_mid", "Cyan", "DOWN", [(7, 6), (8, 6), (8, 8)]),
            ArrowDef("e_trunk_tip", "Yellow", "LEFT", [(7, 9), (6, 9), (6, 11)]),
            ArrowDef("e_flank_l", "Orange", "LEFT", [(4, 7), (3, 7), (3, 9)]),
            ArrowDef("e_flank_r", "Purple", "RIGHT", [(11, 7), (12, 7), (12, 9)]),
            ArrowDef("e_leg_fl", "Blue", "DOWN", [(4, 10), (4, 13), (3, 13)]),
            ArrowDef("e_leg_fr", "Cyan", "DOWN", [(11, 10), (11, 13), (12, 13)]),
            ArrowDef("e_leg_il", "Green", "DOWN", [(5, 12), (5, 14)]),
            ArrowDef("e_leg_ir", "Orange", "DOWN", [(10, 12), (10, 14)]),
            ArrowDef("e_belly", "Red", "DOWN", [(6, 10), (9, 10), (9, 14)])
        ],
        "hearts": 3, "limit": 30, "min_r": 200, "max_r": 400
    },

    # 3. C3: Gourd
    {
        "id": "c3_gourd",
        "num": 3,
        "name": "C3: Gourd",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("g_stem_top", "Green", "UP", [(8, 2), (8, 0), (9, 0)]),
            ArrowDef("g_stem_leaf", "Cyan", "RIGHT", [(7, 1), (9, 1), (9, 2)]),
            ArrowDef("g_top_l", "Orange", "LEFT", [(7, 3), (5, 3), (5, 4)]),
            ArrowDef("g_top_r", "Yellow", "RIGHT", [(8, 3), (10, 3), (10, 4)]),
            ArrowDef("g_top_crest_l", "Red", "UP", [(6, 4), (6, 2), (5, 2)]),
            ArrowDef("g_top_crest_r", "Pink", "UP", [(9, 4), (9, 2), (10, 2)]),
            ArrowDef("g_waist_l", "Purple", "LEFT", [(6, 6), (4, 6), (4, 5)]),
            ArrowDef("g_waist_r", "Blue", "RIGHT", [(9, 6), (11, 6), (11, 5)]),
            ArrowDef("g_waist_c", "Green", "UP", [(7, 6), (8, 6), (8, 4)]),
            ArrowDef("g_bot_fl_l", "Cyan", "LEFT", [(5, 8), (3, 8), (3, 7)]),
            ArrowDef("g_bot_fl_r", "Blue", "RIGHT", [(10, 8), (12, 8), (12, 7)]),
            ArrowDef("g_bot_edge_l", "Purple", "DOWN", [(3, 9), (2, 9), (2, 13)]),
            ArrowDef("g_bot_edge_r", "Pink", "DOWN", [(12, 9), (13, 9), (13, 13)]),
            ArrowDef("g_bot_mid_l", "Orange", "LEFT", [(6, 10), (4, 10), (4, 11)]),
            ArrowDef("g_bot_mid_r", "Yellow", "RIGHT", [(9, 10), (11, 10), (11, 11)]),
            ArrowDef("g_base_l", "Red", "DOWN", [(5, 12), (5, 14), (6, 14)]),
            ArrowDef("g_base_r", "Green", "DOWN", [(10, 12), (10, 14), (9, 14)]),
            ArrowDef("g_base_c", "Cyan", "DOWN", [(7, 13), (8, 13), (8, 14)])
        ],
        "hearts": 3, "limit": 28, "min_r": 180, "max_r": 350
    },

    # 4. C4: Square / Diamond
    {
        "id": "c4_square",
        "num": 4,
        "name": "C4: Square",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("sq_top_tip", "Cyan", "UP", [(7, 2), (8, 2), (8, 1)]),
            ArrowDef("sq_top_l", "Blue", "LEFT", [(6, 3), (3, 3), (3, 2)]),
            ArrowDef("sq_top_r", "Purple", "RIGHT", [(9, 3), (12, 3), (12, 2)]),
            ArrowDef("sq_corner_l", "Pink", "LEFT", [(3, 6), (1, 6), (1, 7)]),
            ArrowDef("sq_corner_r", "Orange", "RIGHT", [(12, 6), (14, 6), (14, 7)]),
            ArrowDef("sq_bot_l", "Green", "DOWN", [(4, 11), (3, 11), (3, 14)]),
            ArrowDef("sq_bot_r", "Yellow", "DOWN", [(11, 11), (12, 11), (12, 14)]),
            ArrowDef("sq_bot_tip", "Red", "DOWN", [(7, 13), (8, 13), (8, 14)]),
            ArrowDef("sq_in_nw", "Cyan", "UP", [(5, 5), (6, 5), (6, 3)]),
            ArrowDef("sq_in_ne", "Blue", "UP", [(10, 5), (9, 5), (9, 3)]),
            ArrowDef("sq_in_w", "Green", "LEFT", [(5, 8), (3, 8), (3, 9)]),
            ArrowDef("sq_in_e", "Yellow", "RIGHT", [(10, 8), (12, 8), (12, 9)]),
            ArrowDef("sq_in_sw", "Purple", "DOWN", [(6, 10), (5, 10), (5, 13)]),
            ArrowDef("sq_in_se", "Pink", "DOWN", [(9, 10), (10, 10), (10, 13)]),
            ArrowDef("sq_core_h", "Orange", "RIGHT", [(7, 7), (9, 7), (9, 6)]),
            ArrowDef("sq_core_v", "Red", "UP", [(7, 9), (7, 8), (8, 8)])
        ],
        "hearts": 3, "limit": 25, "min_r": 180, "max_r": 360
    },

    # 5. C5: Dove
    {
        "id": "c5_dove",
        "num": 5,
        "name": "C5: Dove",
        "diff": "HARD",
        "arrows": [
            ArrowDef("d_beak", "Yellow", "RIGHT", [(11, 2), (13, 2), (13, 3)]),
            ArrowDef("d_crown", "Cyan", "UP", [(10, 2), (10, 1), (11, 1)]),
            ArrowDef("d_throat", "Orange", "RIGHT", [(10, 4), (12, 4), (12, 5)]),
            ArrowDef("d_neck", "Blue", "UP", [(8, 3), (9, 3), (9, 1)]),
            ArrowDef("d_breast_top", "Pink", "RIGHT", [(9, 5), (11, 5), (11, 6)]),
            ArrowDef("d_breast_mid", "Purple", "RIGHT", [(8, 7), (11, 7), (11, 8)]),
            ArrowDef("d_wing_crest", "Green", "UP", [(6, 3), (7, 3), (7, 1)]),
            ArrowDef("d_wing_f1", "Cyan", "LEFT", [(6, 4), (4, 4), (4, 3)]),
            ArrowDef("d_wing_f2", "Blue", "LEFT", [(5, 6), (3, 6), (3, 5)]),
            ArrowDef("d_wing_f3", "Purple", "LEFT", [(5, 8), (2, 8), (2, 7)]),
            ArrowDef("d_wing_cov", "Pink", "DOWN", [(6, 7), (6, 9), (5, 9)]),
            ArrowDef("d_belly", "Yellow", "DOWN", [(7, 10), (9, 10), (9, 12)]),
            ArrowDef("d_tail_up", "Orange", "LEFT", [(4, 10), (1, 10), (1, 9)]),
            ArrowDef("d_tail_low", "Red", "LEFT", [(4, 12), (1, 12), (1, 11)]),
            ArrowDef("d_feet", "Green", "DOWN", [(7, 13), (8, 13), (8, 14)])
        ],
        "hearts": 3, "limit": 26, "min_r": 200, "max_r": 400
    },

    # 6. C6: Crab
    {
        "id": "c6_crab",
        "num": 6,
        "name": "C6: Crab",
        "diff": "HARD",
        "arrows": [
            ArrowDef("cr_claw_l_t", "Red", "UP", [(3, 2), (2, 2), (2, 1)]),
            ArrowDef("cr_claw_l_b", "Orange", "LEFT", [(3, 4), (1, 4), (1, 3)]),
            ArrowDef("cr_claw_r_t", "Red", "UP", [(12, 2), (13, 2), (13, 1)]),
            ArrowDef("cr_claw_r_b", "Orange", "RIGHT", [(12, 4), (14, 4), (14, 3)]),
            ArrowDef("cr_eye_l", "Cyan", "UP", [(6, 3), (6, 1), (7, 1)]),
            ArrowDef("cr_eye_r", "Blue", "UP", [(9, 3), (9, 1), (8, 1)]),
            ArrowDef("cr_arm_l", "Pink", "LEFT", [(5, 5), (3, 5), (3, 6)]),
            ArrowDef("cr_arm_r", "Purple", "RIGHT", [(10, 5), (12, 5), (12, 6)]),
            ArrowDef("cr_shell_t", "Yellow", "UP", [(7, 4), (8, 4), (8, 2)]),
            ArrowDef("cr_shell_m", "Green", "DOWN", [(7, 6), (8, 6), (8, 8)]),
            ArrowDef("cr_leg_l1", "Cyan", "LEFT", [(4, 7), (2, 7), (2, 8)]),
            ArrowDef("cr_leg_l2", "Blue", "LEFT", [(4, 9), (1, 9), (1, 10)]),
            ArrowDef("cr_leg_l3", "Purple", "LEFT", [(4, 11), (2, 11), (2, 12)]),
            ArrowDef("cr_leg_r1", "Cyan", "RIGHT", [(11, 7), (13, 7), (13, 8)]),
            ArrowDef("cr_leg_r2", "Blue", "RIGHT", [(11, 9), (14, 9), (14, 10)]),
            ArrowDef("cr_leg_r3", "Purple", "RIGHT", [(11, 11), (13, 11), (13, 12)]),
            ArrowDef("cr_belly", "Orange", "DOWN", [(6, 12), (9, 12), (9, 14)])
        ],
        "hearts": 3, "limit": 28, "min_r": 220, "max_r": 440
    },

    # 7. C7: Diamond
    {
        "id": "c7_diamond",
        "num": 7,
        "name": "C7: Diamond",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("dm_table_l", "Cyan", "UP", [(6, 2), (7, 2), (7, 1)]),
            ArrowDef("dm_table_r", "Blue", "UP", [(9, 2), (8, 2), (8, 1)]),
            ArrowDef("dm_crown_l", "Purple", "LEFT", [(5, 3), (3, 3), (3, 2)]),
            ArrowDef("dm_crown_r", "Pink", "RIGHT", [(10, 3), (12, 3), (12, 2)]),
            ArrowDef("dm_girdle_l", "Red", "LEFT", [(4, 5), (1, 5), (1, 6)]),
            ArrowDef("dm_girdle_r", "Orange", "RIGHT", [(11, 5), (14, 5), (14, 6)]),
            ArrowDef("dm_pav_l1", "Yellow", "LEFT", [(5, 7), (2, 7), (2, 8)]),
            ArrowDef("dm_pav_r1", "Green", "RIGHT", [(10, 7), (13, 7), (13, 8)]),
            ArrowDef("dm_pav_l2", "Cyan", "LEFT", [(6, 9), (3, 9), (3, 10)]),
            ArrowDef("dm_pav_r2", "Blue", "RIGHT", [(9, 9), (12, 9), (12, 10)]),
            ArrowDef("dm_culet_l", "Purple", "DOWN", [(6, 11), (7, 11), (7, 14)]),
            ArrowDef("dm_culet_r", "Pink", "DOWN", [(9, 11), (8, 11), (8, 14)]),
            ArrowDef("dm_core", "Yellow", "DOWN", [(7, 6), (8, 6), (8, 9)])
        ],
        "hearts": 3, "limit": 24, "min_r": 190, "max_r": 380
    },

    # 8. C8: Heart
    {
        "id": "c8_heart",
        "num": 8,
        "name": "C8: Heart",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("h_lobe_l_t", "Red", "UP", [(4, 2), (5, 2), (5, 1)]),
            ArrowDef("h_lobe_l_o", "Pink", "LEFT", [(3, 3), (1, 3), (1, 4)]),
            ArrowDef("h_lobe_r_t", "Red", "UP", [(11, 2), (10, 2), (10, 1)]),
            ArrowDef("h_lobe_r_o", "Pink", "RIGHT", [(12, 3), (14, 3), (14, 4)]),
            ArrowDef("h_cleft", "Purple", "UP", [(7, 3), (8, 3), (8, 1)]),
            ArrowDef("h_side_l1", "Orange", "LEFT", [(3, 6), (1, 6), (1, 7)]),
            ArrowDef("h_side_r1", "Yellow", "RIGHT", [(12, 6), (14, 6), (14, 7)]),
            ArrowDef("h_side_l2", "Cyan", "LEFT", [(4, 9), (2, 9), (2, 10)]),
            ArrowDef("h_side_r2", "Blue", "RIGHT", [(11, 9), (13, 9), (13, 10)]),
            ArrowDef("h_taper_l", "Green", "DOWN", [(5, 11), (6, 11), (6, 14)]),
            ArrowDef("h_taper_r", "Yellow", "DOWN", [(10, 11), (9, 11), (9, 14)]),
            ArrowDef("h_point", "Red", "DOWN", [(7, 13), (8, 13), (8, 15)]),
            ArrowDef("h_center", "Pink", "DOWN", [(6, 6), (9, 6), (9, 8)])
        ],
        "hearts": 3, "limit": 24, "min_r": 190, "max_r": 380
    },

    # 9. C9: Pine Leaf
    {
        "id": "c9_pine_leaf",
        "num": 9,
        "name": "C9: Pine Leaf",
        "diff": "HARD",
        "arrows": [
            ArrowDef("pl_tip", "Green", "UP", [(7, 1), (8, 1), (8, 0)]),
            ArrowDef("pl_tier1_l", "Cyan", "LEFT", [(6, 3), (3, 3), (3, 2)]),
            ArrowDef("pl_tier1_r", "Blue", "RIGHT", [(9, 3), (12, 3), (12, 2)]),
            ArrowDef("pl_tier2_l", "Green", "LEFT", [(5, 5), (2, 5), (2, 4)]),
            ArrowDef("pl_tier2_r", "Yellow", "RIGHT", [(10, 5), (13, 5), (13, 4)]),
            ArrowDef("pl_tier3_l", "Orange", "LEFT", [(4, 8), (1, 8), (1, 7)]),
            ArrowDef("pl_tier3_r", "Pink", "RIGHT", [(11, 8), (14, 8), (14, 7)]),
            ArrowDef("pl_tier4_l", "Red", "LEFT", [(4, 11), (1, 11), (1, 10)]),
            ArrowDef("pl_tier4_r", "Purple", "RIGHT", [(11, 11), (14, 11), (14, 10)]),
            ArrowDef("pl_trunk_t", "Yellow", "UP", [(7, 4), (8, 4), (8, 2)]),
            ArrowDef("pl_trunk_m", "Green", "DOWN", [(7, 7), (8, 7), (8, 9)]),
            ArrowDef("pl_trunk_b", "Cyan", "DOWN", [(7, 12), (8, 12), (8, 15)])
        ],
        "hearts": 3, "limit": 22, "min_r": 180, "max_r": 360
    },

    # 10. C10: Pyramid
    {
        "id": "c10_pyramid",
        "num": 10,
        "name": "C10: Pyramid",
        "diff": "HARD",
        "arrows": [
            ArrowDef("pyr_apex", "Yellow", "UP", [(7, 2), (8, 2), (8, 1)]),
            ArrowDef("pyr_t1_l", "Orange", "LEFT", [(6, 4), (4, 4), (4, 3)]),
            ArrowDef("pyr_t1_r", "Red", "RIGHT", [(9, 4), (11, 4), (11, 3)]),
            ArrowDef("pyr_t2_l", "Pink", "LEFT", [(5, 6), (3, 6), (3, 5)]),
            ArrowDef("pyr_t2_r", "Purple", "RIGHT", [(10, 6), (12, 6), (12, 5)]),
            ArrowDef("pyr_t3_l", "Blue", "LEFT", [(4, 8), (2, 8), (2, 7)]),
            ArrowDef("pyr_t3_r", "Cyan", "RIGHT", [(11, 8), (13, 8), (13, 7)]),
            ArrowDef("pyr_t4_l", "Green", "LEFT", [(3, 10), (1, 10), (1, 9)]),
            ArrowDef("pyr_t4_r", "Yellow", "RIGHT", [(12, 10), (14, 10), (14, 9)]),
            ArrowDef("pyr_base_l", "Orange", "DOWN", [(3, 12), (6, 12), (6, 14)]),
            ArrowDef("pyr_base_r", "Red", "DOWN", [(12, 12), (9, 12), (9, 14)]),
            ArrowDef("pyr_center", "Cyan", "DOWN", [(7, 8), (8, 8), (8, 13)])
        ],
        "hearts": 3, "limit": 24, "min_r": 200, "max_r": 400
    },

    # 11. C11: Trophy
    {
        "id": "c11_trophy",
        "num": 11,
        "name": "C11: Trophy",
        "diff": "HARD",
        "arrows": [
            ArrowDef("tr_rim_l", "Yellow", "UP", [(5, 2), (6, 2), (6, 1)]),
            ArrowDef("tr_rim_r", "Orange", "UP", [(10, 2), (9, 2), (9, 1)]),
            ArrowDef("tr_rim_c", "Red", "UP", [(7, 2), (8, 2), (8, 1)]),
            ArrowDef("tr_handle_l", "Cyan", "LEFT", [(4, 4), (2, 4), (2, 6)]),
            ArrowDef("tr_handle_r", "Blue", "RIGHT", [(11, 4), (13, 4), (13, 6)]),
            ArrowDef("tr_cup_l", "Yellow", "LEFT", [(5, 5), (4, 5), (4, 7)]),
            ArrowDef("tr_cup_r", "Orange", "RIGHT", [(10, 5), (11, 5), (11, 7)]),
            ArrowDef("tr_cup_c", "Pink", "DOWN", [(7, 4), (8, 4), (8, 6)]),
            ArrowDef("tr_stem", "Purple", "DOWN", [(7, 8), (8, 8), (8, 10)]),
            ArrowDef("tr_pedestal", "Green", "DOWN", [(6, 11), (9, 11), (9, 12)]),
            ArrowDef("tr_base_l", "Cyan", "LEFT", [(5, 14), (2, 14), (2, 13)]),
            ArrowDef("tr_base_r", "Blue", "RIGHT", [(10, 14), (13, 14), (13, 13)])
        ],
        "hearts": 3, "limit": 24, "min_r": 200, "max_r": 400
    },

    # 12. C12: Hexagon
    {
        "id": "c12_hexagon",
        "num": 12,
        "name": "C12: Hexagon",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("hex_top_l", "Cyan", "UP", [(5, 2), (6, 2), (6, 1)]),
            ArrowDef("hex_top_r", "Blue", "UP", [(10, 2), (9, 2), (9, 1)]),
            ArrowDef("hex_nw", "Purple", "LEFT", [(4, 4), (2, 4), (2, 3)]),
            ArrowDef("hex_ne", "Pink", "RIGHT", [(11, 4), (13, 4), (13, 3)]),
            ArrowDef("hex_mid_l", "Orange", "LEFT", [(3, 7), (1, 7), (1, 8)]),
            ArrowDef("hex_mid_r", "Yellow", "RIGHT", [(12, 7), (14, 7), (14, 8)]),
            ArrowDef("hex_sw", "Red", "LEFT", [(4, 10), (2, 10), (2, 11)]),
            ArrowDef("hex_se", "Green", "RIGHT", [(11, 10), (13, 10), (13, 11)]),
            ArrowDef("hex_bot_l", "Cyan", "DOWN", [(5, 12), (6, 12), (6, 14)]),
            ArrowDef("hex_bot_r", "Blue", "DOWN", [(10, 12), (9, 12), (9, 14)]),
            ArrowDef("hex_core_t", "Yellow", "UP", [(7, 5), (8, 5), (8, 3)]),
            ArrowDef("hex_core_b", "Purple", "DOWN", [(7, 9), (8, 9), (8, 11)])
        ],
        "hearts": 3, "limit": 24, "min_r": 190, "max_r": 380
    },

    # 13. C13: Octagon
    {
        "id": "c13_octagon",
        "num": 13,
        "name": "C13: Octagon",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("oct_top_l", "Red", "UP", [(5, 2), (6, 2), (6, 1)]),
            ArrowDef("oct_top_c", "Orange", "UP", [(7, 2), (8, 2), (8, 1)]),
            ArrowDef("oct_top_r", "Yellow", "UP", [(10, 2), (9, 2), (9, 1)]),
            ArrowDef("oct_corner_nw", "Pink", "LEFT", [(4, 4), (2, 4), (2, 3)]),
            ArrowDef("oct_corner_ne", "Purple", "RIGHT", [(11, 4), (13, 4), (13, 3)]),
            ArrowDef("oct_side_w", "Cyan", "LEFT", [(3, 7), (1, 7), (1, 8)]),
            ArrowDef("oct_side_e", "Blue", "RIGHT", [(12, 7), (14, 7), (14, 8)]),
            ArrowDef("oct_corner_sw", "Green", "LEFT", [(4, 10), (2, 10), (2, 11)]),
            ArrowDef("oct_corner_se", "Yellow", "RIGHT", [(11, 10), (13, 10), (13, 11)]),
            ArrowDef("oct_bot_l", "Orange", "DOWN", [(5, 12), (6, 12), (6, 14)]),
            ArrowDef("oct_bot_c", "Red", "DOWN", [(7, 12), (8, 12), (8, 14)]),
            ArrowDef("oct_bot_r", "Pink", "DOWN", [(10, 12), (9, 12), (9, 14)]),
            ArrowDef("oct_core_w", "Cyan", "LEFT", [(6, 7), (5, 7), (5, 6)]),
            ArrowDef("oct_core_e", "Blue", "RIGHT", [(9, 7), (10, 7), (10, 8)])
        ],
        "hearts": 3, "limit": 26, "min_r": 200, "max_r": 400
    },

    # 14. C14: Cute Cat
    {
        "id": "c14_cute_cat",
        "num": 14,
        "name": "C14: Cute Cat",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("cat_ear_l", "Orange", "UP", [(4, 2), (3, 2), (3, 1)]),
            ArrowDef("cat_ear_r", "Yellow", "UP", [(11, 2), (12, 2), (12, 1)]),
            ArrowDef("cat_crown", "Pink", "UP", [(7, 2), (8, 2), (8, 1)]),
            ArrowDef("cat_cheek_l", "Cyan", "LEFT", [(4, 5), (2, 5), (2, 4)]),
            ArrowDef("cat_cheek_r", "Blue", "RIGHT", [(11, 5), (13, 5), (13, 4)]),
            ArrowDef("cat_eye_l", "Green", "UP", [(5, 4), (6, 4), (6, 3)]),
            ArrowDef("cat_eye_r", "Purple", "UP", [(10, 4), (9, 4), (9, 3)]),
            ArrowDef("cat_muzzle", "Red", "DOWN", [(7, 5), (8, 5), (8, 7)]),
            ArrowDef("cat_whisk_l", "Orange", "LEFT", [(4, 6), (1, 6), (1, 7)]),
            ArrowDef("cat_whisk_r", "Yellow", "RIGHT", [(11, 6), (14, 6), (14, 7)]),
            ArrowDef("cat_chest", "Pink", "DOWN", [(6, 8), (9, 8), (9, 10)]),
            ArrowDef("cat_paws", "Cyan", "DOWN", [(5, 12), (7, 12), (7, 14)]),
            ArrowDef("cat_tail", "Purple", "LEFT", [(10, 9), (13, 9), (13, 10)])
        ],
        "hearts": 3, "limit": 25, "min_r": 200, "max_r": 400
    },

    # 15. C15: Cute Dog
    {
        "id": "c15_cute_dog",
        "num": 15,
        "name": "C15: Cute Dog",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("dog_ear_l", "Orange", "LEFT", [(4, 4), (2, 4), (2, 6)]),
            ArrowDef("dog_ear_r", "Yellow", "RIGHT", [(11, 4), (13, 4), (13, 6)]),
            ArrowDef("dog_crown", "Cyan", "UP", [(7, 1), (8, 1), (7, 1)]),
            ArrowDef("dog_brow_l", "Blue", "UP", [(5, 3), (6, 3), (6, 2)]),
            ArrowDef("dog_brow_r", "Purple", "UP", [(10, 3), (9, 3), (9, 2)]),
            ArrowDef("dog_snout", "Red", "UP", [(7, 5), (8, 5), (8, 4)]),
            ArrowDef("dog_nose", "Pink", "RIGHT", [(7, 6), (8, 6), (8, 7)]),
            ArrowDef("dog_tongue", "Red", "DOWN", [(7, 8), (8, 8), (8, 14)]),
            ArrowDef("dog_collar", "Green", "LEFT", [(4, 9), (11, 9), (11, 10)]),
            ArrowDef("dog_chest", "Yellow", "LEFT", [(6, 11), (9, 11), (9, 12)]),
            ArrowDef("dog_paw_l", "Orange", "DOWN", [(5, 13), (6, 13), (6, 15)]),
            ArrowDef("dog_paw_r", "Orange", "DOWN", [(10, 13), (10, 15)]),
            ArrowDef("dog_tail", "Cyan", "RIGHT", [(12, 8), (14, 8), (14, 5)])
        ],
        "hearts": 3, "limit": 25, "min_r": 200, "max_r": 400
    },

    # 16. C16: Gift Box
    {
        "id": "c16_gift_box",
        "num": 16,
        "name": "C16: Gift Box",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("gb_bow_l", "Pink", "UP", [(6, 2), (5, 2), (5, 1)]),
            ArrowDef("gb_bow_r", "Red", "UP", [(9, 2), (10, 2), (10, 1)]),
            ArrowDef("gb_knot", "Orange", "UP", [(7, 1), (8, 1), (8, 0)]),
            ArrowDef("gb_lid_l", "Cyan", "LEFT", [(6, 4), (3, 4), (3, 3)]),
            ArrowDef("gb_lid_r", "Blue", "RIGHT", [(9, 4), (12, 4), (12, 3)]),
            ArrowDef("gb_lid_ribbon", "Yellow", "UP", [(7, 3), (8, 3), (7, 3)]),
            ArrowDef("gb_body_tl", "Purple", "LEFT", [(6, 6), (4, 6), (4, 5)]),
            ArrowDef("gb_body_bl", "Pink", "LEFT", [(6, 9), (4, 9), (4, 8)]),
            ArrowDef("gb_wall_l", "Blue", "DOWN", [(3, 7), (2, 7), (2, 11)]),
            ArrowDef("gb_body_tr", "Purple", "RIGHT", [(9, 6), (11, 6), (11, 5)]),
            ArrowDef("gb_body_br", "Pink", "RIGHT", [(9, 9), (11, 9), (11, 8)]),
            ArrowDef("gb_wall_r", "Blue", "DOWN", [(12, 7), (13, 7), (13, 11)]),
            ArrowDef("gb_rib_top", "Red", "RIGHT", [(7, 6), (8, 6), (8, 7)]),
            ArrowDef("gb_rib_bot", "Red", "LEFT", [(7, 10), (8, 10), (8, 11)]),
            ArrowDef("gb_base", "Green", "DOWN", [(5, 12), (10, 12), (10, 14)])
        ],
        "hearts": 3, "limit": 25, "min_r": 200, "max_r": 420
    },

    # 17. C17: Ginkgo Leaf
    {
        "id": "c17_ginkgo_leaf",
        "num": 17,
        "name": "C17: Ginkgo Leaf",
        "diff": "HARD",
        "arrows": [
            ArrowDef("gk_notch_l", "Yellow", "UP", [(6, 1), (7, 1), (7, 0)]),
            ArrowDef("gk_notch_r", "Orange", "UP", [(9, 1), (8, 1), (8, 0)]),
            ArrowDef("gk_fan_fl_l", "Green", "LEFT", [(5, 3), (2, 3), (2, 2)]),
            ArrowDef("gk_fan_fl_r", "Pink", "RIGHT", [(10, 3), (13, 3), (13, 2)]),
            ArrowDef("gk_rib_l1", "Cyan", "LEFT", [(4, 5), (2, 5), (2, 6)]),
            ArrowDef("gk_rib_r1", "Blue", "RIGHT", [(11, 5), (13, 5), (13, 6)]),
            ArrowDef("gk_rib_l2", "Purple", "LEFT", [(5, 7), (3, 7), (3, 8)]),
            ArrowDef("gk_rib_r2", "Pink", "RIGHT", [(10, 7), (12, 7), (12, 8)]),
            ArrowDef("gk_core_l", "Yellow", "UP", [(5, 4), (6, 4), (6, 2)]),
            ArrowDef("gk_core_r", "Orange", "UP", [(10, 4), (9, 4), (9, 2)]),
            ArrowDef("gk_taper_l", "Green", "DOWN", [(5, 8), (6, 8), (6, 10)]),
            ArrowDef("gk_taper_r", "Pink", "DOWN", [(10, 8), (9, 8), (9, 10)]),
            ArrowDef("gk_stem", "Green", "DOWN", [(7, 11), (8, 11), (8, 14)])
        ],
        "hearts": 3, "limit": 25, "min_r": 220, "max_r": 440
    },

    # 18. C18: Moon and Stars
    {
        "id": "c18_moon_stars",
        "num": 18,
        "name": "C18: Moon and Stars",
        "diff": "HARD",
        "arrows": [
            ArrowDef("mn_horn_t", "Yellow", "UP", [(5, 2), (6, 2), (6, 1)]),
            ArrowDef("mn_out_nw", "Orange", "LEFT", [(4, 2), (2, 2), (2, 1)]),
            ArrowDef("mn_out_w", "Pink", "LEFT", [(3, 6), (1, 6), (1, 7)]),
            ArrowDef("mn_out_sw", "Yellow", "LEFT", [(4, 10), (2, 10), (2, 11)]),
            ArrowDef("mn_horn_b", "Orange", "DOWN", [(5, 12), (6, 12), (6, 14)]),
            ArrowDef("mn_in_t", "Cyan", "UP", [(5, 4), (5, 3), (4, 3)]),
            ArrowDef("mn_in_m", "Blue", "LEFT", [(5, 7), (3, 7), (3, 8)]),
            ArrowDef("mn_in_b", "Purple", "DOWN", [(5, 9), (5, 11), (4, 11)]),
            ArrowDef("st1_top", "Yellow", "UP", [(11, 2), (12, 2), (12, 1)]),
            ArrowDef("st1_arm", "Pink", "RIGHT", [(10, 3), (13, 3), (13, 4)]),
            ArrowDef("st1_bot", "Orange", "RIGHT", [(11, 5), (12, 5), (12, 6)]),
            ArrowDef("st2_top", "Cyan", "RIGHT", [(11, 8), (12, 8), (12, 7)]),
            ArrowDef("st2_arm", "Pink", "RIGHT", [(10, 10), (14, 10), (14, 9)]),
            ArrowDef("st2_bot", "Purple", "DOWN", [(11, 12), (13, 12), (13, 14)])
        ],
        "hearts": 3, "limit": 25, "min_r": 220, "max_r": 450
    },

    # 19. C19: Number 9
    {
        "id": "c19_number_9",
        "num": 19,
        "name": "C19: Number 9",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("n9_top_arch", "Cyan", "UP", [(7, 1), (9, 1), (9, 0)]),
            ArrowDef("n9_top_l", "Blue", "LEFT", [(6, 3), (4, 3), (4, 2)]),
            ArrowDef("n9_spine_l", "Purple", "LEFT", [(4, 5), (3, 5), (3, 6)]),
            ArrowDef("n9_mid_cross", "Pink", "LEFT", [(5, 7), (8, 7), (8, 6)]),
            ArrowDef("n9_loop_c", "Yellow", "LEFT", [(6, 5), (5, 5), (5, 4)]),
            ArrowDef("n9_spine_t", "Orange", "UP", [(10, 4), (11, 4), (11, 1)]),
            ArrowDef("n9_spine_m", "Red", "RIGHT", [(10, 7), (12, 7), (12, 8)]),
            ArrowDef("n9_stem", "Green", "DOWN", [(10, 9), (10, 12), (9, 12)]),
            ArrowDef("n9_tail", "Cyan", "LEFT", [(9, 13), (6, 13), (6, 14)])
        ],
        "hearts": 3, "limit": 20, "min_r": 180, "max_r": 360
    },

    # 20. C20: Question Mark
    {
        "id": "c20_question_mark",
        "num": 20,
        "name": "C20: Question Mark",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("qm_top_arch", "Purple", "UP", [(7, 1), (9, 1), (9, 0)]),
            ArrowDef("qm_top_l", "Pink", "LEFT", [(6, 3), (4, 3), (4, 2)]),
            ArrowDef("qm_top_r", "Orange", "RIGHT", [(10, 3), (12, 3), (12, 2)]),
            ArrowDef("qm_r_bend", "Red", "RIGHT", [(11, 5), (12, 5), (12, 6)]),
            ArrowDef("qm_curl_in", "Yellow", "UP", [(10, 5), (8, 5), (8, 4)]),
            ArrowDef("qm_mid_post", "Green", "LEFT", [(7, 7), (7, 9), (6, 9)]),
            ArrowDef("qm_dot_top", "Cyan", "RIGHT", [(7, 12), (8, 12), (8, 11)]),
            ArrowDef("qm_dot_bot", "Blue", "DOWN", [(7, 13), (8, 13), (8, 14)])
        ],
        "hearts": 3, "limit": 18, "min_r": 160, "max_r": 320
    },

    # 21. C21: Spy
    {
        "id": "c21_spy",
        "num": 21,
        "name": "C21: Spy",
        "diff": "HARD",
        "arrows": [
            ArrowDef("spy_hat_top", "Cyan", "UP", [(7, 1), (9, 1), (9, 0)]),
            ArrowDef("spy_hat_crease", "Blue", "UP", [(6, 2), (8, 2), (8, 1)]),
            ArrowDef("spy_brim_l", "Purple", "LEFT", [(6, 4), (2, 4), (2, 3)]),
            ArrowDef("spy_brim_r", "Pink", "RIGHT", [(9, 4), (13, 4), (13, 3)]),
            ArrowDef("spy_ribbon", "Red", "RIGHT", [(6, 3), (9, 3), (10, 3)]),
            ArrowDef("spy_glass_l", "Green", "LEFT", [(5, 6), (3, 6), (3, 7)]),
            ArrowDef("spy_glass_r", "Yellow", "RIGHT", [(10, 6), (12, 6), (12, 7)]),
            ArrowDef("spy_bridge", "Cyan", "RIGHT", [(7, 5), (8, 5), (8, 4)]),
            ArrowDef("spy_col_l", "Orange", "LEFT", [(5, 8), (3, 8), (3, 9)]),
            ArrowDef("spy_col_r", "Yellow", "RIGHT", [(10, 8), (12, 8), (12, 9)]),
            ArrowDef("spy_lapel_l", "Pink", "DOWN", [(6, 9), (4, 9), (4, 13)]),
            ArrowDef("spy_lapel_r", "Purple", "DOWN", [(9, 9), (11, 9), (11, 13)]),
            ArrowDef("spy_tie", "Red", "DOWN", [(7, 7), (8, 7), (8, 14)])
        ],
        "hearts": 3, "limit": 25, "min_r": 220, "max_r": 440
    },

    # 22. C22: Squirrel
    {
        "id": "c22_squirrel",
        "num": 22,
        "name": "C22: Squirrel",
        "diff": "EXPERT",
        "arrows": [
            ArrowDef("sq_tail_curl", "Orange", "UP", [(4, 2), (2, 2), (2, 1)]),
            ArrowDef("sq_tail_outer", "Yellow", "LEFT", [(2, 3), (1, 3), (1, 6)]),
            ArrowDef("sq_tail_ridge", "Red", "UP", [(3, 5), (4, 5), (4, 3)]),
            ArrowDef("sq_tail_bot", "Pink", "DOWN", [(2, 7), (1, 7), (1, 12)]),
            ArrowDef("sq_ear", "Cyan", "UP", [(9, 2), (10, 2), (10, 1)]),
            ArrowDef("sq_crown", "Blue", "UP", [(8, 3), (9, 3), (9, 1)]),
            ArrowDef("sq_snout", "Green", "RIGHT", [(10, 4), (13, 4), (13, 3)]),
            ArrowDef("sq_acorn_cap", "Purple", "UP", [(10, 7), (11, 7), (11, 6)]),
            ArrowDef("sq_acorn_nut", "Pink", "RIGHT", [(11, 8), (13, 8), (13, 9)]),
            ArrowDef("sq_paw_f", "Orange", "RIGHT", [(8, 8), (10, 8), (10, 10)]),
            ArrowDef("sq_back", "Yellow", "UP", [(5, 7), (7, 7), (7, 4)]),
            ArrowDef("sq_belly", "Red", "DOWN", [(7, 9), (8, 9), (8, 12)]),
            ArrowDef("sq_thigh", "Blue", "LEFT", [(5, 10), (3, 10), (3, 11)]),
            ArrowDef("sq_foot", "Cyan", "DOWN", [(6, 13), (8, 13), (8, 14)])
        ],
        "hearts": 3, "limit": 28, "min_r": 240, "max_r": 480
    },

    # 23. C23: Stopwatch
    {
        "id": "c23_stopwatch",
        "num": 23,
        "name": "C23: Stopwatch",
        "diff": "MEDIUM",
        "arrows": [
            ArrowDef("sw_btn_top", "Red", "UP", [(7, 1), (8, 1), (8, 0)]),
            ArrowDef("sw_ring_l", "Cyan", "LEFT", [(6, 2), (5, 2), (5, 1)]),
            ArrowDef("sw_ring_r", "Blue", "RIGHT", [(9, 2), (10, 2), (10, 1)]),
            ArrowDef("sw_side_btn", "Orange", "RIGHT", [(11, 2), (13, 2), (13, 1)]),
            ArrowDef("sw_dial_nw", "Purple", "LEFT", [(5, 4), (3, 4), (3, 3)]),
            ArrowDef("sw_dial_ne", "Pink", "RIGHT", [(10, 4), (12, 4), (12, 3)]),
            ArrowDef("sw_dial_w", "Green", "LEFT", [(4, 7), (2, 7), (2, 8)]),
            ArrowDef("sw_dial_e", "Yellow", "RIGHT", [(11, 7), (13, 7), (13, 8)]),
            ArrowDef("sw_dial_sw", "Purple", "DOWN", [(4, 10), (3, 10), (3, 13)]),
            ArrowDef("sw_dial_se", "Pink", "DOWN", [(11, 10), (12, 10), (12, 13)]),
            ArrowDef("sw_dial_s", "Red", "DOWN", [(7, 13), (9, 13), (9, 14)]),
            ArrowDef("sw_hand_min", "Cyan", "UP", [(6, 4), (7, 4), (7, 2)]),
            ArrowDef("sw_hand_sec", "Orange", "RIGHT", [(7, 7), (10, 7), (10, 6)]),
            ArrowDef("sw_center", "Yellow", "DOWN", [(6, 8), (8, 8), (8, 10)])
        ],
        "hearts": 3, "limit": 24, "min_r": 200, "max_r": 400
    },

    # 24. C24: Transformers
    {
        "id": "c24_transformers",
        "num": 24,
        "name": "C24: Transformers",
        "diff": "EXPERT",
        "arrows": [
            ArrowDef("tf_crest", "Red", "UP", [(7, 1), (8, 1), (8, 0)]),
            ArrowDef("tf_horn_l", "Orange", "LEFT", [(5, 2), (3, 2), (3, 1)]),
            ArrowDef("tf_horn_r", "Yellow", "RIGHT", [(10, 2), (12, 2), (12, 1)]),
            ArrowDef("tf_brow_l", "Cyan", "UP", [(5, 4), (6, 4), (6, 2)]),
            ArrowDef("tf_brow_r", "Blue", "UP", [(10, 4), (9, 4), (9, 2)]),
            ArrowDef("tf_eye_l", "Cyan", "LEFT", [(5, 6), (3, 6), (3, 5)]),
            ArrowDef("tf_eye_r", "Blue", "RIGHT", [(10, 6), (12, 6), (12, 5)]),
            ArrowDef("tf_nose", "Pink", "RIGHT", [(7, 6), (8, 6), (8, 7)]),
            ArrowDef("tf_cheek_l", "Purple", "LEFT", [(4, 7), (2, 7), (2, 8)]),
            ArrowDef("tf_cheek_r", "Pink", "RIGHT", [(11, 7), (13, 7), (13, 8)]),
            ArrowDef("tf_vent_l", "Orange", "LEFT", [(5, 9), (3, 9), (3, 10)]),
            ArrowDef("tf_vent_r", "Yellow", "RIGHT", [(10, 9), (12, 9), (12, 10)]),
            ArrowDef("tf_mouth_m", "Green", "DOWN", [(6, 9), (9, 9), (9, 11)]),
            ArrowDef("tf_chin_l", "Red", "DOWN", [(5, 11), (5, 13), (4, 13)]),
            ArrowDef("tf_chin_r", "Orange", "DOWN", [(10, 11), (10, 13), (11, 13)]),
            ArrowDef("tf_chin_c", "Yellow", "DOWN", [(7, 12), (8, 12), (8, 14)])
        ],
        "hearts": 3, "limit": 30, "min_r": 260, "max_r": 520
    },

    # 25. C25: Unicorn
    {
        "id": "c25_unicorn",
        "num": 25,
        "name": "C25: Unicorn",
        "diff": "EXTREME",
        "arrows": [
            ArrowDef("u_horn_tip", "Pink", "UP", [(12, 1), (13, 1), (13, 0)]),
            ArrowDef("u_horn_mid", "Purple", "RIGHT", [(11, 2), (14, 2), (14, 1)]),
            ArrowDef("u_horn_base", "Cyan", "UP", [(10, 3), (12, 3), (12, 2)]),
            ArrowDef("u_ear_l", "Yellow", "UP", [(8, 2), (9, 2), (9, 1)]),
            ArrowDef("u_crown", "Orange", "UP", [(7, 4), (8, 4), (8, 2)]),
            ArrowDef("u_mane_lock1", "Pink", "LEFT", [(6, 3), (3, 3), (3, 2)]),
            ArrowDef("u_mane_lock2", "Purple", "LEFT", [(5, 5), (2, 5), (2, 4)]),
            ArrowDef("u_mane_lock3", "Blue", "LEFT", [(4, 7), (1, 7), (1, 6)]),
            ArrowDef("u_mane_lock4", "Cyan", "LEFT", [(4, 9), (1, 9), (1, 8)]),
            ArrowDef("u_snout", "Green", "RIGHT", [(10, 5), (13, 5), (13, 4)]),
            ArrowDef("u_chin", "Yellow", "RIGHT", [(9, 6), (12, 6), (12, 7)]),
            ArrowDef("u_jaw", "Orange", "DOWN", [(8, 7), (8, 8), (9, 8)]),
            ArrowDef("u_neck_spine", "Cyan", "UP", [(5, 6), (6, 6), (6, 4)]),
            ArrowDef("u_chest", "Pink", "RIGHT", [(7, 10), (11, 10), (11, 11)]),
            ArrowDef("u_shoulder", "Purple", "DOWN", [(5, 11), (5, 13), (6, 13)]),
            ArrowDef("u_leg_f", "Blue", "DOWN", [(8, 11), (8, 14), (8, 15)])
        ],
        "hearts": 3, "limit": 32, "min_r": 300, "max_r": 600
    }
]

# Validation
print("=== VALIDATING ALL 25 LEVELS ===")
for lvl in levels_data:
    name = lvl["name"]
    arrows = lvl["arrows"]

    # 1. Body intersections check
    occupied_map = {}
    for a in arrows:
        occupied_map[a.id] = get_occupied(a.points)

    for i in range(len(arrows)):
        for j in range(i + 1, len(arrows)):
            a1 = arrows[i]
            a2 = arrows[j]
            inter = occupied_map[a1.id].intersection(occupied_map[a2.id])
            if inter:
                print(f"FAILED: Level '{name}': arrows '{a1.id}' and '{a2.id}' share {inter}")
                sys.exit(1)

    # 2. Solvability check
    remaining = list(arrows)
    cleared = []
    while remaining:
        can_clear = None
        for a in remaining:
            if can_exit(a, remaining):
                can_clear = a
                break
        if not can_clear:
            print(f"FAILED: Level '{name}' cannot be cleared. Remaining: {[a.id for a in remaining]}")
            sys.exit(1)
        remaining.remove(can_clear)
        cleared.append(can_clear.id)

    print(f"✓ {name}: 0 intersections, 100% solvable (Cleared: {len(cleared)} arrows)")

print("ALL 25 LEVELS VERIFIED SUCCESSFULLY!")

# Now generate ArrowLevels.kt
kt_code = """package com.example.data

import com.example.model.*

object ArrowLevels {
    val levels: List<GameLevel> = listOf(
"""

for lvl in levels_data:
    kt_code += f"""        // {lvl['name']}
        GameLevel(
            id = "{lvl['id']}",
            levelNumber = {lvl['num']},
            name = "{lvl['name']}",
            difficulty = Difficulty.{lvl['diff']},
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
"""
    for a in lvl['arrows']:
        pts_str = ", ".join([f"Point({p[0]}, {p[1]})" for p in a.points])
        kt_code += f"""                Arrow("{a.id}", "{a.color}", ArrowDirection.{a.direction}, listOf({pts_str})),\n"""
    kt_code = kt_code.rstrip(",\n") + "\n"
    kt_code += f"""            ),
            initialHearts = {lvl['hearts']},
            moveLimit = {lvl['limit']},
            rewardMin = {lvl['min_r']},
            rewardMax = {lvl['max_r']}
        ),
"""

kt_code = kt_code.rstrip(",\n") + "\n    )\n}\n"

with open("/app/src/main/java/com/example/data/ArrowLevels.kt", "w") as f:
    f.write(kt_code)

print("Generated /app/src/main/java/com/example/data/ArrowLevels.kt successfully!")
