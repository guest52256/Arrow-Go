package com.example.data

import com.example.model.*

object ArrowLevels {
    val levels: List<GameLevel> by lazy {
        rawLevels.map { lvl ->
            lvl.copy(arrows = lvl.arrows.map { ProceduralLevelGenerator.sanitizeArrowPath(it) })
        }
    }

    private val rawLevels: List<GameLevel> = listOf(
        // C1: Rooster
        GameLevel(
            id = "c1_rooster",
            levelNumber = 1,
            name = "C1: Rooster",
            difficulty = Difficulty.EASY,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("r_comb_top", "Red", ArrowDirection.UP, listOf(Point(7, 2), Point(7, 1), Point(8, 1))),
                Arrow("r_comb_back", "Orange", ArrowDirection.LEFT, listOf(Point(6, 2), Point(5, 2), Point(5, 1))),
                Arrow("r_beak", "Yellow", ArrowDirection.RIGHT, listOf(Point(9, 3), Point(12, 3), Point(12, 4))),
                Arrow("r_wattle", "Red", ArrowDirection.DOWN, listOf(Point(10, 5), Point(10, 6), Point(11, 6))),
                Arrow("r_tail_high", "Cyan", ArrowDirection.UP, listOf(Point(2, 4), Point(1, 4), Point(1, 2), Point(2, 2))),
                Arrow("r_tail_mid", "Blue", ArrowDirection.LEFT, listOf(Point(4, 6), Point(2, 6), Point(2, 5))),
                Arrow("r_tail_low", "Purple", ArrowDirection.LEFT, listOf(Point(4, 8), Point(2, 8), Point(2, 7))),
                Arrow("r_tail_base", "Pink", ArrowDirection.DOWN, listOf(Point(3, 9), Point(1, 9), Point(1, 12))),
                Arrow("r_neck", "Orange", ArrowDirection.UP, listOf(Point(7, 4), Point(7, 3), Point(6, 3))),
                Arrow("r_breast", "Green", ArrowDirection.RIGHT, listOf(Point(8, 5), Point(9, 5), Point(9, 6))),
                Arrow("r_chest", "Yellow", ArrowDirection.RIGHT, listOf(Point(8, 7), Point(12, 7), Point(12, 8))),
                Arrow("r_wing_top", "Purple", ArrowDirection.UP, listOf(Point(5, 6), Point(6, 6), Point(6, 4), Point(5, 4))),
                Arrow("r_wing_mid", "Blue", ArrowDirection.RIGHT, listOf(Point(5, 8), Point(7, 8), Point(7, 9))),
                Arrow("r_wing_tip", "Cyan", ArrowDirection.DOWN, listOf(Point(3, 10), Point(3, 12), Point(4, 12))),
                Arrow("r_belly", "Green", ArrowDirection.RIGHT, listOf(Point(5, 11), Point(8, 11), Point(8, 10))),
                Arrow("r_leg_f", "Orange", ArrowDirection.DOWN, listOf(Point(8, 13), Point(8, 14))),
                Arrow("r_leg_b", "Yellow", ArrowDirection.DOWN, listOf(Point(6, 13), Point(6, 14))),
                Arrow("r_feet", "Red", ArrowDirection.RIGHT, listOf(Point(7, 15), Point(11, 15)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 150,
            rewardMax = 300
        ),

        // C2: Elephant
        GameLevel(
            id = "c2_elephant",
            levelNumber = 2,
            name = "C2: Elephant",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("e_crown_l", "Cyan", ArrowDirection.UP, listOf(Point(6, 3), Point(6, 1), Point(7, 1))),
                Arrow("e_crown_r", "Blue", ArrowDirection.UP, listOf(Point(9, 3), Point(9, 1), Point(8, 1))),
                Arrow("e_ear_l_top", "Purple", ArrowDirection.LEFT, listOf(Point(5, 2), Point(2, 2), Point(2, 3))),
                Arrow("e_ear_l_rim", "Pink", ArrowDirection.UP, listOf(Point(1, 6), Point(1, 4), Point(3, 4))),
                Arrow("e_ear_l_bot", "Blue", ArrowDirection.LEFT, listOf(Point(4, 5), Point(2, 5), Point(2, 7))),
                Arrow("e_ear_r_top", "Purple", ArrowDirection.RIGHT, listOf(Point(10, 2), Point(13, 2), Point(13, 3))),
                Arrow("e_ear_r_rim", "Pink", ArrowDirection.UP, listOf(Point(14, 6), Point(14, 4), Point(12, 4))),
                Arrow("e_ear_r_bot", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(13, 5), Point(13, 7))),
                Arrow("e_eye_l", "Orange", ArrowDirection.LEFT, listOf(Point(6, 4), Point(5, 4), Point(5, 5))),
                Arrow("e_eye_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(9, 4), Point(10, 4), Point(10, 5))),
                Arrow("e_trunk_base", "Green", ArrowDirection.UP, listOf(Point(7, 5), Point(8, 5), Point(8, 3), Point(8, 2))),
                Arrow("e_trunk_mid", "Cyan", ArrowDirection.DOWN, listOf(Point(7, 6), Point(8, 6), Point(8, 8))),
                Arrow("e_trunk_tip", "Yellow", ArrowDirection.LEFT, listOf(Point(7, 9), Point(5, 9), Point(5, 11))),
                Arrow("e_flank_l", "Orange", ArrowDirection.LEFT, listOf(Point(4, 7), Point(3, 7), Point(3, 9))),
                Arrow("e_flank_r", "Purple", ArrowDirection.RIGHT, listOf(Point(11, 7), Point(12, 7), Point(12, 9))),
                Arrow("e_leg_fl", "Blue", ArrowDirection.DOWN, listOf(Point(4, 10), Point(4, 13), Point(3, 13))),
                Arrow("e_leg_fr", "Cyan", ArrowDirection.DOWN, listOf(Point(11, 10), Point(11, 13), Point(12, 13))),
                Arrow("e_leg_il", "Green", ArrowDirection.DOWN, listOf(Point(5, 12), Point(5, 14))),
                Arrow("e_leg_ir", "Orange", ArrowDirection.DOWN, listOf(Point(10, 12), Point(10, 14))),
                Arrow("e_belly", "Red", ArrowDirection.DOWN, listOf(Point(6, 10), Point(9, 10), Point(9, 14)))
            ),
            initialHearts = 3,
            moveLimit = 30,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C3: Gourd
        GameLevel(
            id = "c3_gourd",
            levelNumber = 3,
            name = "C3: Gourd",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("g_stem_top", "Green", ArrowDirection.UP, listOf(Point(8, 2), Point(8, 0))),
                Arrow("g_stem_leaf", "Cyan", ArrowDirection.RIGHT, listOf(Point(9, 1), Point(11, 1), Point(11, 0))),
                Arrow("g_top_l", "Orange", ArrowDirection.LEFT, listOf(Point(7, 4), Point(5, 4), Point(5, 5))),
                Arrow("g_top_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(9, 4), Point(12, 4), Point(12, 5))),
                Arrow("g_top_crest_l", "Red", ArrowDirection.UP, listOf(Point(6, 3), Point(6, 1), Point(5, 1))),
                Arrow("g_top_crest_r", "Pink", ArrowDirection.UP, listOf(Point(10, 3), Point(10, 2), Point(9, 2))),
                Arrow("g_waist_l", "Purple", ArrowDirection.LEFT, listOf(Point(6, 6), Point(4, 6), Point(4, 5))),
                Arrow("g_waist_r", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 6), Point(11, 6), Point(11, 5))),
                Arrow("g_waist_c", "Green", ArrowDirection.UP, listOf(Point(7, 6), Point(8, 6), Point(8, 5))),
                Arrow("g_bot_fl_l", "Cyan", ArrowDirection.LEFT, listOf(Point(5, 8), Point(3, 8), Point(3, 7))),
                Arrow("g_bot_fl_r", "Blue", ArrowDirection.RIGHT, listOf(Point(10, 8), Point(12, 8), Point(12, 7))),
                Arrow("g_bot_edge_l", "Purple", ArrowDirection.DOWN, listOf(Point(3, 9), Point(2, 9), Point(2, 13))),
                Arrow("g_bot_edge_r", "Pink", ArrowDirection.DOWN, listOf(Point(12, 9), Point(13, 9), Point(13, 13))),
                Arrow("g_bot_mid_l", "Orange", ArrowDirection.LEFT, listOf(Point(6, 10), Point(4, 10), Point(4, 11))),
                Arrow("g_bot_mid_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(9, 10), Point(11, 10), Point(11, 11))),
                Arrow("g_base_l", "Red", ArrowDirection.DOWN, listOf(Point(5, 12), Point(5, 14), Point(6, 14))),
                Arrow("g_base_r", "Green", ArrowDirection.DOWN, listOf(Point(10, 12), Point(10, 14), Point(9, 14))),
                Arrow("g_base_c", "Cyan", ArrowDirection.DOWN, listOf(Point(7, 13), Point(8, 13), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 28,
            rewardMin = 180,
            rewardMax = 350
        ),

        // C4: Square
        GameLevel(
            id = "c4_square",
            levelNumber = 4,
            name = "C4: Square",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("sq_top_tip", "Cyan", ArrowDirection.UP, listOf(Point(7, 2), Point(8, 2), Point(8, 1))),
                Arrow("sq_top_l", "Blue", ArrowDirection.LEFT, listOf(Point(5, 2), Point(3, 2), Point(3, 1))),
                Arrow("sq_top_r", "Purple", ArrowDirection.RIGHT, listOf(Point(10, 2), Point(12, 2), Point(12, 1))),
                Arrow("sq_corner_l", "Pink", ArrowDirection.LEFT, listOf(Point(3, 6), Point(1, 6), Point(1, 7))),
                Arrow("sq_corner_r", "Orange", ArrowDirection.RIGHT, listOf(Point(12, 6), Point(14, 6), Point(14, 7))),
                Arrow("sq_bot_l", "Green", ArrowDirection.DOWN, listOf(Point(4, 11), Point(3, 11), Point(3, 14))),
                Arrow("sq_bot_r", "Yellow", ArrowDirection.DOWN, listOf(Point(11, 11), Point(12, 11), Point(12, 14))),
                Arrow("sq_bot_tip", "Red", ArrowDirection.DOWN, listOf(Point(7, 13), Point(8, 13), Point(8, 14))),
                Arrow("sq_in_nw", "Cyan", ArrowDirection.UP, listOf(Point(5, 5), Point(6, 5), Point(6, 3))),
                Arrow("sq_in_ne", "Blue", ArrowDirection.UP, listOf(Point(10, 5), Point(9, 5), Point(9, 3))),
                Arrow("sq_in_w", "Green", ArrowDirection.LEFT, listOf(Point(5, 8), Point(3, 8), Point(3, 9))),
                Arrow("sq_in_e", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 8), Point(12, 8), Point(12, 9))),
                Arrow("sq_in_sw", "Purple", ArrowDirection.DOWN, listOf(Point(6, 10), Point(5, 10), Point(5, 13))),
                Arrow("sq_in_se", "Pink", ArrowDirection.DOWN, listOf(Point(9, 10), Point(10, 10), Point(10, 13))),
                Arrow("sq_core_h", "Orange", ArrowDirection.RIGHT, listOf(Point(7, 7), Point(9, 7), Point(9, 6))),
                Arrow("sq_core_v", "Red", ArrowDirection.UP, listOf(Point(7, 9), Point(7, 8), Point(8, 8)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 180,
            rewardMax = 360
        ),

        // C5: Dove
        GameLevel(
            id = "c5_dove",
            levelNumber = 5,
            name = "C5: Dove",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("d_beak", "Yellow", ArrowDirection.RIGHT, listOf(Point(11, 2), Point(13, 2), Point(13, 3))),
                Arrow("d_crown", "Cyan", ArrowDirection.UP, listOf(Point(10, 2), Point(10, 1), Point(11, 1))),
                Arrow("d_throat", "Orange", ArrowDirection.RIGHT, listOf(Point(10, 4), Point(12, 4), Point(12, 5))),
                Arrow("d_neck", "Blue", ArrowDirection.UP, listOf(Point(8, 3), Point(9, 3), Point(9, 1))),
                Arrow("d_breast_top", "Pink", ArrowDirection.RIGHT, listOf(Point(9, 5), Point(11, 5), Point(11, 6))),
                Arrow("d_breast_mid", "Purple", ArrowDirection.RIGHT, listOf(Point(8, 7), Point(11, 7), Point(11, 8))),
                Arrow("d_wing_crest", "Green", ArrowDirection.UP, listOf(Point(6, 3), Point(7, 3), Point(7, 1))),
                Arrow("d_wing_f1", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 4), Point(4, 4), Point(4, 3))),
                Arrow("d_wing_f2", "Blue", ArrowDirection.LEFT, listOf(Point(5, 6), Point(3, 6), Point(3, 5))),
                Arrow("d_wing_f3", "Purple", ArrowDirection.LEFT, listOf(Point(5, 8), Point(2, 8), Point(2, 7))),
                Arrow("d_wing_cov", "Pink", ArrowDirection.DOWN, listOf(Point(6, 7), Point(6, 9), Point(5, 9))),
                Arrow("d_belly", "Yellow", ArrowDirection.DOWN, listOf(Point(7, 10), Point(9, 10), Point(9, 12))),
                Arrow("d_tail_up", "Orange", ArrowDirection.LEFT, listOf(Point(4, 10), Point(1, 10), Point(1, 9))),
                Arrow("d_tail_low", "Red", ArrowDirection.LEFT, listOf(Point(4, 12), Point(1, 12), Point(1, 11))),
                Arrow("d_feet", "Green", ArrowDirection.DOWN, listOf(Point(7, 13), Point(8, 13), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 26,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C6: Crab
        GameLevel(
            id = "c6_crab",
            levelNumber = 6,
            name = "C6: Crab",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("cr_claw_l_t", "Red", ArrowDirection.UP, listOf(Point(3, 2), Point(2, 2), Point(2, 1))),
                Arrow("cr_claw_l_b", "Orange", ArrowDirection.LEFT, listOf(Point(3, 4), Point(1, 4), Point(1, 3))),
                Arrow("cr_claw_r_t", "Red", ArrowDirection.UP, listOf(Point(12, 2), Point(13, 2), Point(13, 1))),
                Arrow("cr_claw_r_b", "Orange", ArrowDirection.RIGHT, listOf(Point(12, 4), Point(14, 4), Point(14, 3))),
                Arrow("cr_eye_l", "Cyan", ArrowDirection.UP, listOf(Point(6, 3), Point(6, 1), Point(7, 1))),
                Arrow("cr_eye_r", "Blue", ArrowDirection.UP, listOf(Point(9, 3), Point(9, 1), Point(8, 1))),
                Arrow("cr_arm_l", "Pink", ArrowDirection.LEFT, listOf(Point(5, 5), Point(3, 5), Point(3, 6))),
                Arrow("cr_arm_r", "Purple", ArrowDirection.RIGHT, listOf(Point(10, 5), Point(12, 5), Point(12, 6))),
                Arrow("cr_shell_t", "Yellow", ArrowDirection.UP, listOf(Point(7, 4), Point(8, 4), Point(8, 2))),
                Arrow("cr_shell_m", "Green", ArrowDirection.DOWN, listOf(Point(7, 6), Point(8, 6), Point(8, 8))),
                Arrow("cr_leg_l1", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 7), Point(2, 7), Point(2, 8))),
                Arrow("cr_leg_l2", "Blue", ArrowDirection.LEFT, listOf(Point(4, 9), Point(1, 9), Point(1, 10))),
                Arrow("cr_leg_l3", "Purple", ArrowDirection.LEFT, listOf(Point(4, 11), Point(2, 11), Point(2, 12))),
                Arrow("cr_leg_r1", "Cyan", ArrowDirection.RIGHT, listOf(Point(11, 7), Point(13, 7), Point(13, 8))),
                Arrow("cr_leg_r2", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 9), Point(14, 9), Point(14, 10))),
                Arrow("cr_leg_r3", "Purple", ArrowDirection.RIGHT, listOf(Point(11, 11), Point(13, 11), Point(13, 12))),
                Arrow("cr_belly", "Orange", ArrowDirection.DOWN, listOf(Point(6, 12), Point(9, 12), Point(9, 14)))
            ),
            initialHearts = 3,
            moveLimit = 28,
            rewardMin = 220,
            rewardMax = 440
        ),

        // C7: Diamond
        GameLevel(
            id = "c7_diamond",
            levelNumber = 7,
            name = "C7: Diamond",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("dm_table_l", "Cyan", ArrowDirection.UP, listOf(Point(6, 2), Point(7, 2), Point(7, 1))),
                Arrow("dm_table_r", "Blue", ArrowDirection.UP, listOf(Point(9, 2), Point(8, 2), Point(8, 1))),
                Arrow("dm_crown_l", "Purple", ArrowDirection.LEFT, listOf(Point(5, 3), Point(3, 3), Point(3, 2))),
                Arrow("dm_crown_r", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 3), Point(12, 3), Point(12, 2))),
                Arrow("dm_girdle_l", "Red", ArrowDirection.LEFT, listOf(Point(4, 5), Point(1, 5), Point(1, 6))),
                Arrow("dm_girdle_r", "Orange", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(14, 5), Point(14, 6))),
                Arrow("dm_pav_l1", "Yellow", ArrowDirection.LEFT, listOf(Point(5, 7), Point(2, 7), Point(2, 8))),
                Arrow("dm_pav_r1", "Green", ArrowDirection.RIGHT, listOf(Point(10, 7), Point(13, 7), Point(13, 8))),
                Arrow("dm_pav_l2", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 9), Point(3, 9), Point(3, 10))),
                Arrow("dm_pav_r2", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 9), Point(12, 9), Point(12, 10))),
                Arrow("dm_culet_l", "Purple", ArrowDirection.DOWN, listOf(Point(6, 11), Point(7, 11), Point(7, 14))),
                Arrow("dm_culet_r", "Pink", ArrowDirection.DOWN, listOf(Point(9, 11), Point(8, 11), Point(8, 14))),
                Arrow("dm_core", "Yellow", ArrowDirection.DOWN, listOf(Point(7, 6), Point(8, 6), Point(8, 9)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 190,
            rewardMax = 380
        ),

        // C8: Heart
        GameLevel(
            id = "c8_heart",
            levelNumber = 8,
            name = "C8: Heart",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("h_lobe_l_t", "Red", ArrowDirection.UP, listOf(Point(4, 2), Point(5, 2), Point(5, 1))),
                Arrow("h_lobe_l_o", "Pink", ArrowDirection.LEFT, listOf(Point(3, 3), Point(1, 3), Point(1, 4))),
                Arrow("h_lobe_r_t", "Red", ArrowDirection.UP, listOf(Point(11, 2), Point(10, 2), Point(10, 1))),
                Arrow("h_lobe_r_o", "Pink", ArrowDirection.RIGHT, listOf(Point(12, 3), Point(14, 3), Point(14, 4))),
                Arrow("h_cleft", "Purple", ArrowDirection.UP, listOf(Point(7, 3), Point(8, 3), Point(8, 1))),
                Arrow("h_side_l1", "Orange", ArrowDirection.LEFT, listOf(Point(3, 6), Point(1, 6), Point(1, 7))),
                Arrow("h_side_r1", "Yellow", ArrowDirection.RIGHT, listOf(Point(12, 6), Point(14, 6), Point(14, 7))),
                Arrow("h_side_l2", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 9), Point(2, 9), Point(2, 10))),
                Arrow("h_side_r2", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 9), Point(13, 9), Point(13, 10))),
                Arrow("h_taper_l", "Green", ArrowDirection.DOWN, listOf(Point(5, 11), Point(6, 11), Point(6, 14))),
                Arrow("h_taper_r", "Yellow", ArrowDirection.DOWN, listOf(Point(10, 11), Point(9, 11), Point(9, 14))),
                Arrow("h_point", "Red", ArrowDirection.DOWN, listOf(Point(7, 13), Point(8, 13), Point(8, 15))),
                Arrow("h_center", "Pink", ArrowDirection.DOWN, listOf(Point(6, 6), Point(9, 6), Point(9, 8)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 190,
            rewardMax = 380
        ),

        // C9: Pine Leaf
        GameLevel(
            id = "c9_pine_leaf",
            levelNumber = 9,
            name = "C9: Pine Leaf",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("pl_tip", "Green", ArrowDirection.UP, listOf(Point(7, 1), Point(8, 1), Point(8, 0))),
                Arrow("pl_tier1_l", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 3), Point(3, 3), Point(3, 2))),
                Arrow("pl_tier1_r", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 3), Point(12, 3), Point(12, 2))),
                Arrow("pl_tier2_l", "Green", ArrowDirection.LEFT, listOf(Point(5, 5), Point(2, 5), Point(2, 4))),
                Arrow("pl_tier2_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 5), Point(13, 5), Point(13, 4))),
                Arrow("pl_tier3_l", "Orange", ArrowDirection.LEFT, listOf(Point(4, 8), Point(1, 8), Point(1, 7))),
                Arrow("pl_tier3_r", "Pink", ArrowDirection.RIGHT, listOf(Point(11, 8), Point(14, 8), Point(14, 7))),
                Arrow("pl_tier4_l", "Red", ArrowDirection.LEFT, listOf(Point(4, 11), Point(1, 11), Point(1, 10))),
                Arrow("pl_tier4_r", "Purple", ArrowDirection.RIGHT, listOf(Point(11, 11), Point(14, 11), Point(14, 10))),
                Arrow("pl_trunk_t", "Yellow", ArrowDirection.UP, listOf(Point(7, 4), Point(8, 4), Point(8, 2))),
                Arrow("pl_trunk_m", "Green", ArrowDirection.DOWN, listOf(Point(7, 7), Point(8, 7), Point(8, 9))),
                Arrow("pl_trunk_b", "Cyan", ArrowDirection.DOWN, listOf(Point(7, 12), Point(8, 12), Point(8, 15)))
            ),
            initialHearts = 3,
            moveLimit = 22,
            rewardMin = 180,
            rewardMax = 360
        ),

        // C10: Pyramid
        GameLevel(
            id = "c10_pyramid",
            levelNumber = 10,
            name = "C10: Pyramid",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("pyr_apex", "Yellow", ArrowDirection.UP, listOf(Point(7, 2), Point(8, 2), Point(8, 1))),
                Arrow("pyr_t1_l", "Orange", ArrowDirection.LEFT, listOf(Point(6, 4), Point(4, 4), Point(4, 3))),
                Arrow("pyr_t1_r", "Red", ArrowDirection.RIGHT, listOf(Point(9, 4), Point(11, 4), Point(11, 3))),
                Arrow("pyr_t2_l", "Pink", ArrowDirection.LEFT, listOf(Point(5, 6), Point(3, 6), Point(3, 5))),
                Arrow("pyr_t2_r", "Purple", ArrowDirection.RIGHT, listOf(Point(10, 6), Point(12, 6), Point(12, 5))),
                Arrow("pyr_t3_l", "Blue", ArrowDirection.LEFT, listOf(Point(4, 8), Point(2, 8), Point(2, 7))),
                Arrow("pyr_t3_r", "Cyan", ArrowDirection.RIGHT, listOf(Point(11, 8), Point(13, 8), Point(13, 7))),
                Arrow("pyr_t4_l", "Green", ArrowDirection.LEFT, listOf(Point(3, 10), Point(1, 10), Point(1, 9))),
                Arrow("pyr_t4_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(12, 10), Point(14, 10), Point(14, 9))),
                Arrow("pyr_base_l", "Orange", ArrowDirection.DOWN, listOf(Point(3, 12), Point(6, 12), Point(6, 14))),
                Arrow("pyr_base_r", "Red", ArrowDirection.DOWN, listOf(Point(12, 12), Point(9, 12), Point(9, 14))),
                Arrow("pyr_center", "Cyan", ArrowDirection.DOWN, listOf(Point(7, 8), Point(8, 8), Point(8, 13)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C11: Trophy
        GameLevel(
            id = "c11_trophy",
            levelNumber = 11,
            name = "C11: Trophy",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("tr_rim_l", "Yellow", ArrowDirection.UP, listOf(Point(5, 2), Point(6, 2), Point(6, 1))),
                Arrow("tr_rim_r", "Orange", ArrowDirection.UP, listOf(Point(10, 2), Point(9, 2), Point(9, 1))),
                Arrow("tr_rim_c", "Red", ArrowDirection.UP, listOf(Point(7, 2), Point(8, 2), Point(8, 1))),
                Arrow("tr_handle_l", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 4), Point(2, 4), Point(2, 6))),
                Arrow("tr_handle_r", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 4), Point(13, 4), Point(13, 6))),
                Arrow("tr_cup_l", "Yellow", ArrowDirection.LEFT, listOf(Point(5, 5), Point(4, 5), Point(4, 7))),
                Arrow("tr_cup_r", "Orange", ArrowDirection.RIGHT, listOf(Point(10, 5), Point(11, 5), Point(11, 7))),
                Arrow("tr_cup_c", "Pink", ArrowDirection.DOWN, listOf(Point(7, 4), Point(8, 4), Point(8, 6))),
                Arrow("tr_stem", "Purple", ArrowDirection.DOWN, listOf(Point(7, 8), Point(8, 8), Point(8, 10))),
                Arrow("tr_pedestal", "Green", ArrowDirection.DOWN, listOf(Point(6, 11), Point(9, 11), Point(9, 12))),
                Arrow("tr_base_l", "Cyan", ArrowDirection.LEFT, listOf(Point(5, 14), Point(2, 14), Point(2, 13))),
                Arrow("tr_base_r", "Blue", ArrowDirection.RIGHT, listOf(Point(10, 14), Point(13, 14), Point(13, 13)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C12: Hexagon
        GameLevel(
            id = "c12_hexagon",
            levelNumber = 12,
            name = "C12: Hexagon",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("hex_top_l", "Cyan", ArrowDirection.UP, listOf(Point(5, 2), Point(6, 2), Point(6, 1))),
                Arrow("hex_top_r", "Blue", ArrowDirection.UP, listOf(Point(10, 2), Point(9, 2), Point(9, 1))),
                Arrow("hex_nw", "Purple", ArrowDirection.LEFT, listOf(Point(4, 4), Point(2, 4), Point(2, 3))),
                Arrow("hex_ne", "Pink", ArrowDirection.RIGHT, listOf(Point(11, 4), Point(13, 4), Point(13, 3))),
                Arrow("hex_mid_l", "Orange", ArrowDirection.LEFT, listOf(Point(3, 7), Point(1, 7), Point(1, 8))),
                Arrow("hex_mid_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(12, 7), Point(14, 7), Point(14, 8))),
                Arrow("hex_sw", "Red", ArrowDirection.LEFT, listOf(Point(4, 10), Point(2, 10), Point(2, 11))),
                Arrow("hex_se", "Green", ArrowDirection.RIGHT, listOf(Point(11, 10), Point(13, 10), Point(13, 11))),
                Arrow("hex_bot_l", "Cyan", ArrowDirection.DOWN, listOf(Point(5, 12), Point(6, 12), Point(6, 14))),
                Arrow("hex_bot_r", "Blue", ArrowDirection.DOWN, listOf(Point(10, 12), Point(9, 12), Point(9, 14))),
                Arrow("hex_core_t", "Yellow", ArrowDirection.UP, listOf(Point(7, 5), Point(8, 5), Point(8, 3))),
                Arrow("hex_core_b", "Purple", ArrowDirection.DOWN, listOf(Point(7, 9), Point(8, 9), Point(8, 11)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 190,
            rewardMax = 380
        ),

        // C13: Octagon
        GameLevel(
            id = "c13_octagon",
            levelNumber = 13,
            name = "C13: Octagon",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("oct_top_l", "Red", ArrowDirection.UP, listOf(Point(5, 2), Point(6, 2), Point(6, 1))),
                Arrow("oct_top_c", "Orange", ArrowDirection.UP, listOf(Point(7, 2), Point(8, 2), Point(8, 1))),
                Arrow("oct_top_r", "Yellow", ArrowDirection.UP, listOf(Point(10, 2), Point(9, 2), Point(9, 1))),
                Arrow("oct_corner_nw", "Pink", ArrowDirection.LEFT, listOf(Point(4, 4), Point(2, 4), Point(2, 3))),
                Arrow("oct_corner_ne", "Purple", ArrowDirection.RIGHT, listOf(Point(11, 4), Point(13, 4), Point(13, 3))),
                Arrow("oct_side_w", "Cyan", ArrowDirection.LEFT, listOf(Point(3, 7), Point(1, 7), Point(1, 8))),
                Arrow("oct_side_e", "Blue", ArrowDirection.RIGHT, listOf(Point(12, 7), Point(14, 7), Point(14, 8))),
                Arrow("oct_corner_sw", "Green", ArrowDirection.LEFT, listOf(Point(4, 10), Point(2, 10), Point(2, 11))),
                Arrow("oct_corner_se", "Yellow", ArrowDirection.RIGHT, listOf(Point(11, 10), Point(13, 10), Point(13, 11))),
                Arrow("oct_bot_l", "Orange", ArrowDirection.DOWN, listOf(Point(5, 12), Point(6, 12), Point(6, 14))),
                Arrow("oct_bot_c", "Red", ArrowDirection.DOWN, listOf(Point(7, 12), Point(8, 12), Point(8, 14))),
                Arrow("oct_bot_r", "Pink", ArrowDirection.DOWN, listOf(Point(10, 12), Point(9, 12), Point(9, 14))),
                Arrow("oct_core_w", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 7), Point(5, 7), Point(5, 6))),
                Arrow("oct_core_e", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 7), Point(10, 7), Point(10, 8)))
            ),
            initialHearts = 3,
            moveLimit = 26,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C14: Cute Cat
        GameLevel(
            id = "c14_cute_cat",
            levelNumber = 14,
            name = "C14: Cute Cat",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("cat_ear_l", "Orange", ArrowDirection.UP, listOf(Point(4, 2), Point(3, 2), Point(3, 1))),
                Arrow("cat_ear_r", "Yellow", ArrowDirection.UP, listOf(Point(11, 2), Point(12, 2), Point(12, 1))),
                Arrow("cat_crown", "Pink", ArrowDirection.UP, listOf(Point(7, 2), Point(8, 2), Point(8, 1))),
                Arrow("cat_cheek_l", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 5), Point(2, 5), Point(2, 4))),
                Arrow("cat_cheek_r", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(13, 5), Point(13, 4))),
                Arrow("cat_eye_l", "Green", ArrowDirection.UP, listOf(Point(5, 4), Point(6, 4), Point(6, 3))),
                Arrow("cat_eye_r", "Purple", ArrowDirection.UP, listOf(Point(10, 4), Point(9, 4), Point(9, 3))),
                Arrow("cat_muzzle", "Red", ArrowDirection.DOWN, listOf(Point(7, 5), Point(8, 5), Point(8, 7))),
                Arrow("cat_whisk_l", "Orange", ArrowDirection.LEFT, listOf(Point(4, 6), Point(1, 6), Point(1, 7))),
                Arrow("cat_whisk_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(11, 6), Point(14, 6), Point(14, 7))),
                Arrow("cat_chest", "Pink", ArrowDirection.DOWN, listOf(Point(6, 8), Point(9, 8), Point(9, 10))),
                Arrow("cat_paws", "Cyan", ArrowDirection.DOWN, listOf(Point(5, 12), Point(7, 12), Point(7, 14))),
                Arrow("cat_tail", "Purple", ArrowDirection.LEFT, listOf(Point(10, 9), Point(13, 9), Point(13, 10)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C15: Cute Dog
        GameLevel(
            id = "c15_cute_dog",
            levelNumber = 15,
            name = "C15: Cute Dog",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("dog_ear_l", "Orange", ArrowDirection.LEFT, listOf(Point(4, 4), Point(2, 4), Point(2, 6))),
                Arrow("dog_ear_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(11, 4), Point(13, 4), Point(13, 6))),
                Arrow("dog_crown", "Cyan", ArrowDirection.UP, listOf(Point(7, 1), Point(8, 1), Point(7, 1))),
                Arrow("dog_brow_l", "Blue", ArrowDirection.UP, listOf(Point(5, 3), Point(6, 3), Point(6, 2))),
                Arrow("dog_brow_r", "Purple", ArrowDirection.UP, listOf(Point(10, 3), Point(9, 3), Point(9, 2))),
                Arrow("dog_snout", "Red", ArrowDirection.UP, listOf(Point(7, 5), Point(8, 5), Point(8, 4))),
                Arrow("dog_nose", "Pink", ArrowDirection.RIGHT, listOf(Point(7, 6), Point(8, 6), Point(8, 7))),
                Arrow("dog_tongue", "Red", ArrowDirection.DOWN, listOf(Point(7, 8), Point(8, 8), Point(8, 9))),
                Arrow("dog_collar", "Green", ArrowDirection.LEFT, listOf(Point(4, 10), Point(7, 10), Point(7, 9))),
                Arrow("dog_chest", "Yellow", ArrowDirection.LEFT, listOf(Point(9, 11), Point(11, 11), Point(11, 12))),
                Arrow("dog_paw_l", "Orange", ArrowDirection.DOWN, listOf(Point(5, 13), Point(6, 13), Point(6, 15))),
                Arrow("dog_paw_r", "Orange", ArrowDirection.DOWN, listOf(Point(10, 13), Point(10, 15))),
                Arrow("dog_tail", "Cyan", ArrowDirection.RIGHT, listOf(Point(12, 8), Point(14, 8), Point(14, 5)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C16: Gift Box
        GameLevel(
            id = "c16_gift_box",
            levelNumber = 16,
            name = "C16: Gift Box",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("gb_bow_l", "Pink", ArrowDirection.UP, listOf(Point(6, 2), Point(5, 2), Point(5, 1))),
                Arrow("gb_bow_r", "Red", ArrowDirection.UP, listOf(Point(9, 2), Point(10, 2), Point(10, 1))),
                Arrow("gb_knot", "Orange", ArrowDirection.UP, listOf(Point(7, 1), Point(8, 1), Point(8, 0))),
                Arrow("gb_lid_l", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 4), Point(3, 4), Point(3, 3))),
                Arrow("gb_lid_r", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 4), Point(12, 4), Point(12, 3))),
                Arrow("gb_lid_ribbon", "Yellow", ArrowDirection.UP, listOf(Point(7, 3), Point(8, 3), Point(7, 3))),
                Arrow("gb_body_tl", "Purple", ArrowDirection.LEFT, listOf(Point(6, 6), Point(4, 6), Point(4, 5))),
                Arrow("gb_body_bl", "Pink", ArrowDirection.LEFT, listOf(Point(6, 9), Point(4, 9), Point(4, 8))),
                Arrow("gb_wall_l", "Blue", ArrowDirection.DOWN, listOf(Point(3, 7), Point(2, 7), Point(2, 11))),
                Arrow("gb_body_tr", "Purple", ArrowDirection.RIGHT, listOf(Point(9, 6), Point(11, 6), Point(11, 5))),
                Arrow("gb_body_br", "Pink", ArrowDirection.RIGHT, listOf(Point(9, 9), Point(11, 9), Point(11, 8))),
                Arrow("gb_wall_r", "Blue", ArrowDirection.DOWN, listOf(Point(12, 7), Point(13, 7), Point(13, 11))),
                Arrow("gb_rib_top", "Red", ArrowDirection.RIGHT, listOf(Point(7, 6), Point(8, 6), Point(8, 7))),
                Arrow("gb_rib_bot", "Red", ArrowDirection.LEFT, listOf(Point(7, 10), Point(8, 10), Point(8, 11))),
                Arrow("gb_base", "Green", ArrowDirection.DOWN, listOf(Point(5, 12), Point(10, 12), Point(10, 14)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 200,
            rewardMax = 420
        ),

        // C17: Ginkgo Leaf
        GameLevel(
            id = "c17_ginkgo_leaf",
            levelNumber = 17,
            name = "C17: Ginkgo Leaf",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("gk_notch_l", "Yellow", ArrowDirection.UP, listOf(Point(6, 1), Point(7, 1), Point(7, 0))),
                Arrow("gk_notch_r", "Orange", ArrowDirection.UP, listOf(Point(9, 1), Point(8, 1), Point(8, 0))),
                Arrow("gk_fan_fl_l", "Green", ArrowDirection.LEFT, listOf(Point(5, 3), Point(2, 3), Point(2, 2))),
                Arrow("gk_fan_fl_r", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 3), Point(13, 3), Point(13, 2))),
                Arrow("gk_rib_l1", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 5), Point(2, 5), Point(2, 6))),
                Arrow("gk_rib_r1", "Blue", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(13, 5), Point(13, 6))),
                Arrow("gk_rib_l2", "Purple", ArrowDirection.LEFT, listOf(Point(5, 7), Point(3, 7), Point(3, 8))),
                Arrow("gk_rib_r2", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 7), Point(12, 7), Point(12, 8))),
                Arrow("gk_core_l", "Yellow", ArrowDirection.UP, listOf(Point(5, 4), Point(6, 4), Point(6, 2))),
                Arrow("gk_core_r", "Orange", ArrowDirection.UP, listOf(Point(10, 4), Point(9, 4), Point(9, 2))),
                Arrow("gk_taper_l", "Green", ArrowDirection.DOWN, listOf(Point(5, 8), Point(6, 8), Point(6, 10))),
                Arrow("gk_taper_r", "Pink", ArrowDirection.DOWN, listOf(Point(10, 8), Point(9, 8), Point(9, 10))),
                Arrow("gk_stem", "Green", ArrowDirection.DOWN, listOf(Point(7, 11), Point(8, 11), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 220,
            rewardMax = 440
        ),

        // C18: Moon and Stars
        GameLevel(
            id = "c18_moon_stars",
            levelNumber = 18,
            name = "C18: Moon and Stars",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("mn_horn_t", "Yellow", ArrowDirection.UP, listOf(Point(5, 2), Point(6, 2), Point(6, 1))),
                Arrow("mn_out_nw", "Orange", ArrowDirection.LEFT, listOf(Point(4, 2), Point(2, 2), Point(2, 1))),
                Arrow("mn_out_w", "Pink", ArrowDirection.LEFT, listOf(Point(3, 6), Point(1, 6), Point(1, 7))),
                Arrow("mn_out_sw", "Yellow", ArrowDirection.LEFT, listOf(Point(4, 10), Point(2, 10), Point(2, 11))),
                Arrow("mn_horn_b", "Orange", ArrowDirection.DOWN, listOf(Point(5, 12), Point(6, 12), Point(6, 14))),
                Arrow("mn_in_t", "Cyan", ArrowDirection.UP, listOf(Point(5, 4), Point(5, 3), Point(4, 3))),
                Arrow("mn_in_m", "Blue", ArrowDirection.LEFT, listOf(Point(5, 7), Point(3, 7), Point(3, 8))),
                Arrow("mn_in_b", "Purple", ArrowDirection.DOWN, listOf(Point(5, 9), Point(5, 11), Point(4, 11))),
                Arrow("st1_top", "Yellow", ArrowDirection.UP, listOf(Point(11, 2), Point(12, 2), Point(12, 1))),
                Arrow("st1_arm", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 3), Point(13, 3), Point(13, 4))),
                Arrow("st1_bot", "Orange", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(12, 5), Point(12, 6))),
                Arrow("st2_top", "Cyan", ArrowDirection.RIGHT, listOf(Point(11, 8), Point(12, 8), Point(12, 7))),
                Arrow("st2_arm", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 10), Point(14, 10), Point(14, 9))),
                Arrow("st2_bot", "Purple", ArrowDirection.DOWN, listOf(Point(11, 12), Point(13, 12), Point(13, 14)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 220,
            rewardMax = 450
        ),

        // C19: Number 9
        GameLevel(
            id = "c19_number_9",
            levelNumber = 19,
            name = "C19: Number 9",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("n9_top_arch", "Cyan", ArrowDirection.UP, listOf(Point(7, 1), Point(9, 1), Point(9, 0))),
                Arrow("n9_top_l", "Blue", ArrowDirection.LEFT, listOf(Point(6, 3), Point(4, 3), Point(4, 2))),
                Arrow("n9_spine_l", "Purple", ArrowDirection.LEFT, listOf(Point(4, 5), Point(3, 5), Point(3, 6))),
                Arrow("n9_mid_cross", "Pink", ArrowDirection.LEFT, listOf(Point(5, 7), Point(8, 7), Point(8, 6))),
                Arrow("n9_loop_c", "Yellow", ArrowDirection.LEFT, listOf(Point(6, 5), Point(5, 5), Point(5, 4))),
                Arrow("n9_spine_t", "Orange", ArrowDirection.UP, listOf(Point(10, 4), Point(11, 4), Point(11, 1))),
                Arrow("n9_spine_m", "Red", ArrowDirection.RIGHT, listOf(Point(10, 7), Point(12, 7), Point(12, 8))),
                Arrow("n9_stem", "Green", ArrowDirection.DOWN, listOf(Point(10, 9), Point(10, 12), Point(9, 12))),
                Arrow("n9_tail", "Cyan", ArrowDirection.LEFT, listOf(Point(9, 13), Point(6, 13), Point(6, 14)))
            ),
            initialHearts = 3,
            moveLimit = 20,
            rewardMin = 180,
            rewardMax = 360
        ),

        // C20: Question Mark
        GameLevel(
            id = "c20_question_mark",
            levelNumber = 20,
            name = "C20: Question Mark",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("qm_top_arch", "Purple", ArrowDirection.UP, listOf(Point(7, 1), Point(9, 1), Point(9, 0))),
                Arrow("qm_top_l", "Pink", ArrowDirection.LEFT, listOf(Point(6, 3), Point(4, 3), Point(4, 2))),
                Arrow("qm_top_r", "Orange", ArrowDirection.RIGHT, listOf(Point(10, 3), Point(12, 3), Point(12, 2))),
                Arrow("qm_r_bend", "Red", ArrowDirection.RIGHT, listOf(Point(11, 5), Point(12, 5), Point(12, 6))),
                Arrow("qm_curl_in", "Yellow", ArrowDirection.UP, listOf(Point(10, 5), Point(8, 5), Point(8, 4))),
                Arrow("qm_mid_post", "Green", ArrowDirection.LEFT, listOf(Point(7, 7), Point(7, 9), Point(6, 9))),
                Arrow("qm_dot_top", "Cyan", ArrowDirection.RIGHT, listOf(Point(7, 12), Point(8, 12), Point(8, 11))),
                Arrow("qm_dot_bot", "Blue", ArrowDirection.DOWN, listOf(Point(7, 13), Point(8, 13), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 18,
            rewardMin = 160,
            rewardMax = 320
        ),

        // C21: Spy
        GameLevel(
            id = "c21_spy",
            levelNumber = 21,
            name = "C21: Spy",
            difficulty = Difficulty.HARD,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("spy_hat_top", "Cyan", ArrowDirection.UP, listOf(Point(8, 1), Point(9, 1), Point(9, 0))),
                Arrow("spy_hat_crease", "Blue", ArrowDirection.UP, listOf(Point(6, 2), Point(7, 2), Point(7, 1))),
                Arrow("spy_brim_l", "Purple", ArrowDirection.LEFT, listOf(Point(6, 4), Point(2, 4), Point(2, 3))),
                Arrow("spy_brim_r", "Pink", ArrowDirection.RIGHT, listOf(Point(9, 4), Point(13, 4), Point(13, 3))),
                Arrow("spy_ribbon", "Red", ArrowDirection.RIGHT, listOf(Point(6, 3), Point(9, 3), Point(10, 3))),
                Arrow("spy_glass_l", "Green", ArrowDirection.LEFT, listOf(Point(5, 6), Point(3, 6), Point(3, 7))),
                Arrow("spy_glass_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 6), Point(12, 6), Point(12, 7))),
                Arrow("spy_bridge", "Cyan", ArrowDirection.RIGHT, listOf(Point(7, 5), Point(8, 5), Point(8, 4))),
                Arrow("spy_col_l", "Orange", ArrowDirection.LEFT, listOf(Point(5, 8), Point(3, 8), Point(3, 9))),
                Arrow("spy_col_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 8), Point(12, 8), Point(12, 9))),
                Arrow("spy_lapel_l", "Pink", ArrowDirection.DOWN, listOf(Point(6, 9), Point(4, 9), Point(4, 13))),
                Arrow("spy_lapel_r", "Purple", ArrowDirection.DOWN, listOf(Point(9, 9), Point(11, 9), Point(11, 13))),
                Arrow("spy_tie", "Red", ArrowDirection.DOWN, listOf(Point(7, 7), Point(8, 7), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 25,
            rewardMin = 220,
            rewardMax = 440
        ),

        // C22: Squirrel
        GameLevel(
            id = "c22_squirrel",
            levelNumber = 22,
            name = "C22: Squirrel",
            difficulty = Difficulty.EXPERT,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("sq_tail_curl", "Orange", ArrowDirection.UP, listOf(Point(4, 2), Point(2, 2), Point(2, 1))),
                Arrow("sq_tail_outer", "Yellow", ArrowDirection.LEFT, listOf(Point(2, 3), Point(1, 3), Point(1, 6))),
                Arrow("sq_tail_ridge", "Red", ArrowDirection.UP, listOf(Point(3, 5), Point(4, 5), Point(4, 3))),
                Arrow("sq_tail_bot", "Pink", ArrowDirection.DOWN, listOf(Point(2, 7), Point(1, 7), Point(1, 12))),
                Arrow("sq_ear", "Cyan", ArrowDirection.UP, listOf(Point(9, 2), Point(10, 2), Point(10, 1))),
                Arrow("sq_crown", "Blue", ArrowDirection.UP, listOf(Point(7, 3), Point(8, 3), Point(8, 1))),
                Arrow("sq_snout", "Green", ArrowDirection.RIGHT, listOf(Point(10, 4), Point(13, 4), Point(13, 3))),
                Arrow("sq_acorn_cap", "Purple", ArrowDirection.UP, listOf(Point(10, 7), Point(11, 7), Point(11, 6))),
                Arrow("sq_acorn_nut", "Pink", ArrowDirection.RIGHT, listOf(Point(11, 8), Point(13, 8), Point(13, 9))),
                Arrow("sq_paw_f", "Orange", ArrowDirection.RIGHT, listOf(Point(8, 8), Point(10, 8), Point(10, 10))),
                Arrow("sq_back", "Yellow", ArrowDirection.UP, listOf(Point(5, 7), Point(7, 7), Point(7, 4))),
                Arrow("sq_belly", "Red", ArrowDirection.DOWN, listOf(Point(7, 9), Point(8, 9), Point(8, 12))),
                Arrow("sq_thigh", "Blue", ArrowDirection.LEFT, listOf(Point(5, 10), Point(3, 10), Point(3, 11))),
                Arrow("sq_foot", "Cyan", ArrowDirection.DOWN, listOf(Point(6, 13), Point(8, 13), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 28,
            rewardMin = 240,
            rewardMax = 480
        ),

        // C23: Stopwatch
        GameLevel(
            id = "c23_stopwatch",
            levelNumber = 23,
            name = "C23: Stopwatch",
            difficulty = Difficulty.MEDIUM,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("sw_btn_top", "Red", ArrowDirection.UP, listOf(Point(7, 1), Point(8, 1), Point(8, 0))),
                Arrow("sw_ring_l", "Cyan", ArrowDirection.LEFT, listOf(Point(6, 2), Point(5, 2), Point(5, 1))),
                Arrow("sw_ring_r", "Blue", ArrowDirection.RIGHT, listOf(Point(9, 2), Point(10, 2), Point(10, 1))),
                Arrow("sw_side_btn", "Orange", ArrowDirection.RIGHT, listOf(Point(11, 2), Point(13, 2), Point(13, 1))),
                Arrow("sw_dial_nw", "Purple", ArrowDirection.LEFT, listOf(Point(5, 4), Point(3, 4), Point(3, 3))),
                Arrow("sw_dial_ne", "Pink", ArrowDirection.RIGHT, listOf(Point(10, 4), Point(12, 4), Point(12, 3))),
                Arrow("sw_dial_w", "Green", ArrowDirection.LEFT, listOf(Point(4, 7), Point(2, 7), Point(2, 8))),
                Arrow("sw_dial_e", "Yellow", ArrowDirection.RIGHT, listOf(Point(11, 7), Point(13, 7), Point(13, 8))),
                Arrow("sw_dial_sw", "Purple", ArrowDirection.DOWN, listOf(Point(4, 10), Point(3, 10), Point(3, 13))),
                Arrow("sw_dial_se", "Pink", ArrowDirection.DOWN, listOf(Point(11, 10), Point(12, 10), Point(12, 13))),
                Arrow("sw_dial_s", "Red", ArrowDirection.DOWN, listOf(Point(7, 13), Point(9, 13), Point(9, 14))),
                Arrow("sw_hand_min", "Cyan", ArrowDirection.UP, listOf(Point(6, 4), Point(7, 4), Point(7, 2))),
                Arrow("sw_hand_sec", "Orange", ArrowDirection.RIGHT, listOf(Point(7, 7), Point(10, 7), Point(10, 6))),
                Arrow("sw_center", "Yellow", ArrowDirection.DOWN, listOf(Point(6, 8), Point(8, 8), Point(8, 10)))
            ),
            initialHearts = 3,
            moveLimit = 24,
            rewardMin = 200,
            rewardMax = 400
        ),

        // C24: Transformers
        GameLevel(
            id = "c24_transformers",
            levelNumber = 24,
            name = "C24: Transformers",
            difficulty = Difficulty.EXPERT,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("tf_crest", "Red", ArrowDirection.UP, listOf(Point(7, 1), Point(8, 1), Point(8, 0))),
                Arrow("tf_horn_l", "Orange", ArrowDirection.LEFT, listOf(Point(5, 2), Point(3, 2), Point(3, 1))),
                Arrow("tf_horn_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 2), Point(12, 2), Point(12, 1))),
                Arrow("tf_brow_l", "Cyan", ArrowDirection.UP, listOf(Point(5, 4), Point(6, 4), Point(6, 2))),
                Arrow("tf_brow_r", "Blue", ArrowDirection.UP, listOf(Point(10, 4), Point(9, 4), Point(9, 2))),
                Arrow("tf_eye_l", "Cyan", ArrowDirection.LEFT, listOf(Point(5, 6), Point(3, 6), Point(3, 5))),
                Arrow("tf_eye_r", "Blue", ArrowDirection.RIGHT, listOf(Point(10, 6), Point(12, 6), Point(12, 5))),
                Arrow("tf_nose", "Pink", ArrowDirection.RIGHT, listOf(Point(7, 6), Point(8, 6), Point(8, 7))),
                Arrow("tf_cheek_l", "Purple", ArrowDirection.LEFT, listOf(Point(4, 7), Point(2, 7), Point(2, 8))),
                Arrow("tf_cheek_r", "Pink", ArrowDirection.RIGHT, listOf(Point(11, 7), Point(13, 7), Point(13, 8))),
                Arrow("tf_vent_l", "Orange", ArrowDirection.LEFT, listOf(Point(5, 9), Point(3, 9), Point(3, 10))),
                Arrow("tf_vent_r", "Yellow", ArrowDirection.RIGHT, listOf(Point(10, 9), Point(12, 9), Point(12, 10))),
                Arrow("tf_mouth_m", "Green", ArrowDirection.DOWN, listOf(Point(6, 9), Point(9, 9), Point(9, 11))),
                Arrow("tf_chin_l", "Red", ArrowDirection.DOWN, listOf(Point(5, 11), Point(5, 13), Point(4, 13))),
                Arrow("tf_chin_r", "Orange", ArrowDirection.DOWN, listOf(Point(10, 11), Point(10, 13), Point(11, 13))),
                Arrow("tf_chin_c", "Yellow", ArrowDirection.DOWN, listOf(Point(7, 12), Point(8, 12), Point(8, 14)))
            ),
            initialHearts = 3,
            moveLimit = 30,
            rewardMin = 260,
            rewardMax = 520
        ),

        // C25: Unicorn
        GameLevel(
            id = "c25_unicorn",
            levelNumber = 25,
            name = "C25: Unicorn",
            difficulty = Difficulty.EXTREME,
            gridWidth = 16,
            gridHeight = 16,
            arrows = listOf(
                Arrow("u_horn_tip", "Pink", ArrowDirection.UP, listOf(Point(12, 1), Point(13, 1), Point(13, 0))),
                Arrow("u_horn_mid", "Purple", ArrowDirection.RIGHT, listOf(Point(11, 2), Point(14, 2), Point(14, 1))),
                Arrow("u_horn_base", "Cyan", ArrowDirection.UP, listOf(Point(10, 4), Point(12, 4), Point(12, 3))),
                Arrow("u_ear_l", "Yellow", ArrowDirection.UP, listOf(Point(8, 2), Point(9, 2), Point(9, 1))),
                Arrow("u_crown", "Orange", ArrowDirection.UP, listOf(Point(7, 4), Point(7, 2), Point(7, 1))),
                Arrow("u_mane_lock1", "Pink", ArrowDirection.LEFT, listOf(Point(6, 3), Point(3, 3), Point(3, 2))),
                Arrow("u_mane_lock2", "Purple", ArrowDirection.LEFT, listOf(Point(5, 5), Point(2, 5), Point(2, 4))),
                Arrow("u_mane_lock3", "Blue", ArrowDirection.LEFT, listOf(Point(4, 7), Point(1, 7), Point(1, 6))),
                Arrow("u_mane_lock4", "Cyan", ArrowDirection.LEFT, listOf(Point(4, 9), Point(1, 9), Point(1, 8))),
                Arrow("u_snout", "Green", ArrowDirection.RIGHT, listOf(Point(10, 5), Point(13, 5), Point(13, 4))),
                Arrow("u_chin", "Yellow", ArrowDirection.RIGHT, listOf(Point(9, 6), Point(12, 6), Point(12, 7))),
                Arrow("u_jaw", "Orange", ArrowDirection.DOWN, listOf(Point(8, 7), Point(8, 8), Point(9, 8))),
                Arrow("u_neck_spine", "Cyan", ArrowDirection.UP, listOf(Point(5, 6), Point(6, 6), Point(6, 4))),
                Arrow("u_chest", "Pink", ArrowDirection.RIGHT, listOf(Point(7, 10), Point(11, 10), Point(11, 11))),
                Arrow("u_shoulder", "Purple", ArrowDirection.DOWN, listOf(Point(5, 11), Point(5, 13), Point(6, 13))),
                Arrow("u_leg_f", "Blue", ArrowDirection.DOWN, listOf(Point(8, 11), Point(8, 14), Point(8, 15)))
            ),
            initialHearts = 3,
            moveLimit = 32,
            rewardMin = 300,
            rewardMax = 600
        )
    )
}
