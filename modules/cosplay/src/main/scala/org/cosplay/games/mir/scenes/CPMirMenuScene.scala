/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cosplay.games.mir.scenes

import org.cosplay.*
import games.mir.*
import prefabs.sprites.*
import prefabs.shaders.*

/*
   _________            ______________
   __  ____/_______________  __ \__  /_____ _____  __
   _  /    _  __ \_  ___/_  /_/ /_  /_  __ `/_  / / /
   / /___  / /_/ /(__  )_  ____/_  / / /_/ /_  /_/ /
   \____/  \____//____/ /_/     /_/  \__,_/ _\__, /
                                            /____/

          2D ASCII GAME ENGINE FOR SCALA3
            (C) 2021 Rowan Games, Inc.
               ALl rights reserved.
*/

/**
  * [C] - Continue
  * [N] - New Game
  * [S] - Save Game
  * [L] - Load Game
  * [O] - Options
  * [T] - Tutorial
  * [Q] - Quit
  */
object CPMirMenuScene extends CPMirStarStreakSceneBase("menu", "bg1.wav"):
    private val img = CPArrayImage(
        dlgMarkup.process(
            """
              |Game Menu
              |_________
              |
              |[C] - Continue
              |
              |[N] - New Game
              |[S] - Save Game
              |[L] - Load Game
              |
              |[O] - Options
              |[T] - Tutorial
              |[Q] - Quit
              |""".stripMargin
        ),
        REV_BG_PX,
        align = -1
    ).cropByInsets(CPInsets(4, 10, 4, 10), REV_BG_PX)

    addObjects(
        new CPCenteredImageSprite(img = img, 1, Seq(CPBorderShader(false, 4, true, -.05f, true))),
        // Add full-screen shaders - order is important.
        new CPOffScreenSprite(shaders = Seq(/*starStreakShdr, */crtShdr, fadeInShdr, fadeOutShdr))
    )
