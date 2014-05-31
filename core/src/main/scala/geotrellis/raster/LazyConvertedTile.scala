/*
 * Copyright (c) 2014 Azavea.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster

import geotrellis._
import geotrellis.feature.Extent

import scalaxy.loops._

/**
 * LazyConvertedTile represents a lazily-applied conversion to any type.
 *
 * @note     If you care converting to a RasterType with less bits
 *           than the type of the underlying data, you are responsible
 *           for managing overflow. This convert does not do any casting;
 *           therefore converting from a TypeInt to TypeByte could still
 *           return values greater than 127 from apply().
 */
final case class LazyConvertedTile(inner: ArrayTile, cellType: CellType)
  extends ArrayTile {

  val cols = inner.cols
  val rows = inner.rows

  def apply(i: Int) = inner.apply(i)
  def applyDouble(i: Int) = inner.applyDouble(i)

  override def toArray = inner.toArray
  override def toArrayDouble = inner.toArrayDouble

  def force(): ArrayTile = {
    val forcedData = ArrayTile.alloc(cellType, cols, rows)
    if(cellType.isFloatingPoint) {
      for(col <- 0 until cols optimized) {
        for(row <- 0 until rows optimized) {
          forcedData.setDouble(col, row, inner.getDouble(col, row))
        }
      }
    } else {
      for(col <- 0 until cols optimized) {
        for(row <- 0 until rows optimized) {
          forcedData.set(col, row, inner.get(col, row))
        }
      }
    }
    forcedData
  }
  
  def toBytes: Array[Byte] = force.toBytes

  def warp(current: Extent, target: RasterExtent): ArrayTile =
    LazyConvertedTile(inner.warp(current, target), cellType)
}
