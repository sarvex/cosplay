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

package org.cosplay.games.mir.scenes.sprites

import org.cosplay.*
import CPPixel.*
import games.mir.*
import os.*

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
  *
  */
class CPMirConsoleSprite extends CPCanvasSprite(id = "console") with CPMirConsole:
    case class ZChar(ch: Char, fg: CPColor, bg: CPColor, z: Int):
        lazy val px: CPPixel = ch&&(fg, bg)

    private final val SPACE = ZChar(' ', FG, BG, Int.MinValue)
    private final val W = 300
    private final val H = 100
    private final val LAST_X = W - 1
    private final val LAST_Y = H - 1
    private val pane = Array.ofDim[ZChar](H, W)
    private var curX = 0
    private var curY = 0
    private var curVis = true
    private var dim = CPDim(W, H)
    private final val mux = Object()

    clear()

    override def clear(): Unit = for x ← 0 until W; y ← 0 until H do pane(y)(x) = SPACE
    override def clearLeft(): Unit = for x ← 0 until curX do pane(curY)(x) = SPACE
    override def clearRight(): Unit = for x ← curX + 1 until W do pane(curY)(x) = SPACE
    override def clearRow(): Unit = for x ← 0 until W do pane(x)(curY) = SPACE
    override def clearColumn(): Unit = for y ← 0 until H do pane(y)(curX) = SPACE
    override def clearAbove(): Unit = for y ← 0 until curY do pane(y)(curX) = SPACE
    override def clearBelow(): Unit = for y ← curY + 1 until H do pane(y)(curX) = SPACE

    inline private def isPositionValid(x: Int, y: Int): Boolean = x >= 0 && x < W && y >= 0 && y < H

    inline override def isCursorVisible: Boolean = curVis
    inline override def setCursorVisible(f: Boolean): Unit = curVis = f
    override def moveCursor(x: Int, y: Int): Unit =
        if isPositionValid(x, y) then mux.synchronized {
            curX = x
            curY = y
        }
    override def getSize: CPDim = dim
    inline override def getCursorX: Int = curX
    inline override def getCursorY: Int = curY
    override def putChar(x: Int, y: Int, z: Int, ch: Char, fg: CPColor, bg: CPColor): Unit =
        if isPositionValid(x, y) then mux.synchronized {
            pane(y)(x) = ZChar(ch, fg, bg, z)
        }

    override def render(ctx: CPSceneObjectContext): Unit =
        val canv = ctx.getCanvas

        mux.synchronized {
            dim = canv.dim

            val startY = if curY < canv.h then 0 else curY - canv.h + 1
            val w = canv.w.min(W)
            val h = canv.h

            var y = 0
            while y < h do
                var x = 0
                while x < w do
                    val zch = pane(y + startY)(x)
                    canv.drawPixel(zch.px, x, y, zch.z)
                    x += 1
                y += 1

            // TODO: cursor blinking
        }

    private def advanceCursor(): Unit =
        if curX < LAST_X then curX += 1
        else if curY < LAST_Y then
            curX = 0
            curY += 1
        else
            for y ← 1 until H do Array.copy(pane(y), 0, pane(y - 1), 0, W)
            for x ← 0 until W do pane(LAST_Y)(x) = SPACE
            curX = 0

    override def print(x: Any): Unit =
        mux.synchronized {
            def put(ch: Char): Unit =
                putChar(getCursorX, getCursorY, ch)
                advanceCursor()
            x.toString.foreach(ch ⇒ ch match
                case '\r' ⇒ curX = 0
                case '\n' ⇒
                    curX = LAST_X
                    advanceCursor()
                case '\t' ⇒ (0 until 8).foreach(_ ⇒ put(' '))
                case _ ⇒ put(ch)
            )
        }