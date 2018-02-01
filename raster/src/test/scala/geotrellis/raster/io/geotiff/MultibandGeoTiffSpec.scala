/*
 * Copyright 2018 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.io.geotiff

import geotrellis.raster._
import geotrellis.raster.resample.NearestNeighbor
import geotrellis.raster.io.geotiff.reader._
import geotrellis.raster.testkit.RasterMatchers

import spire.syntax.cfor._
import org.scalatest._

class MultibandGeoTiffSpec extends FunSpec with Matchers with RasterMatchers with GeoTiffTestUtils {
  describe("Building Overviews") {
    val tiff = MultibandGeoTiff(geoTiffPath("overviews/multiband.tif"))
    val ovr = tiff.buildOverview(3, NearestNeighbor, 128)

    it("should reduce pixels by decimation factor") {
      ovr.tile.cols should be (tiff.tile.cols / 3)
      ovr.tile.rows should be (tiff.tile.rows / 3)
    }

    it("should match tile-wise resample"){
      val expectedTile = tiff.raster.resample(ovr.rasterExtent).tile
      assertEqual(expectedTile, ovr.tile)
    }
  }

}
