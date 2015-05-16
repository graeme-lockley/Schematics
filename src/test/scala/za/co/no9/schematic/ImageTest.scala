package za.co.no9.schematic

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class ImageTest extends FlatSpec with PropertyChecks with Matchers {
  "Given 2 boxes and an arrow right" should "render it" in {
    val i = new Image()
    i.box(title = "Hello")
    i.arrow()
    i.box(title = "World", width = 150.0)

    def layoutImage = i.layoutImage()

    assertLocation(layoutImage.location, Position(0, 0), Position(280, 30))

    assert(layoutImage.embeddedLayouts.length === 3)
    assertLocation(layoutImage.embeddedLayouts(0).location, Position(0, -15), Position(100, 15))
    assertLocation(layoutImage.embeddedLayouts(1).location, Position(100, 0), Position(130, 0))
    assertLocation(layoutImage.embeddedLayouts(2).location, Position(130, -15), Position(280, 15))
  }

  "Given 4 boxes with 2 of the boxes in an up group" should "render it" in {
    val i = new Image()

    i.down() // 0
    i.box(title = "A", width = 100.0) // 1
    i.right() // 2
    i.arrow() // 3

    val b = i.block() // 4
    // b.point // 4.0
    b.up() // 4.1
    b.box(title = "B", width = 100.0) // 4.2
    b.arrow() // 4.3
    b.box(title = "C", width = 100.0) // 4.4

    i.right() // 5
    i.arrow() // 6
    i.box(title = "D") // 7

    def layoutImage = i.layoutImage()

    assertLocation(layoutImage.location, Position(0, 0), Position(360, 80))

    assert(layoutImage.embeddedLayouts.length === 8)
    assertLocation(layoutImage.embeddedLayouts(0).location, Position(0, 0), Position(0, 0))
    assertLocation(layoutImage.embeddedLayouts(1).location, Position(-50, 0), Position(50, 30))
    assertLocation(layoutImage.embeddedLayouts(2).location, Position(-50, 0), Position(50, 30))
    assertLocation(layoutImage.embeddedLayouts(3).location, Position(50, 15), Position(80, 15))

    assertLocation(layoutImage.embeddedLayouts(4).location, Position(80, -25), Position(180, 55))
    assert(layoutImage.embeddedLayouts(4).embeddedLayouts.length === 5)

    assertLocation(layoutImage.embeddedLayouts(5).location, Position(80, -25), Position(180, 55))
    assertLocation(layoutImage.embeddedLayouts(6).location, Position(180, 15), Position(210, 15))
    assertLocation(layoutImage.embeddedLayouts(7).location, Position(210, 0), Position(310, 30))
  }

  "Given 4 boxes with 2 of the boxes in a down group" should "render it" in {
    val i = new Image()

    i.down() // 0
    i.box(title = "A", width = 100.0) // 1
    i.right() // 2
    i.arrow() // 3

    val b = i.block() // 4
    // b.point // 4.0
    b.down() // 4.1
    b.box(title = "B", width = 100.0) // 4.2
    b.arrow() // 4.3
    b.box(title = "C", width = 100.0) // 4.4

    i.right() // 5
    i.arrow() // 6
    i.box(title = "D") // 7

    def layoutImage = i.layoutImage()

    assertLocation(layoutImage.location, Position(0, 0), Position(360, 80))

    assert(layoutImage.embeddedLayouts.length === 8)
    assertLocation(layoutImage.embeddedLayouts(0).location, Position(0, 0), Position(0, 0))
    assertLocation(layoutImage.embeddedLayouts(1).location, Position(-50, 0), Position(50, 30))
    assertLocation(layoutImage.embeddedLayouts(2).location, Position(-50, 0), Position(50, 30))
    assertLocation(layoutImage.embeddedLayouts(3).location, Position(50, 15), Position(80, 15))

    assertLocation(layoutImage.embeddedLayouts(4).location, Position(80, -25), Position(180, 55))
    assert(layoutImage.embeddedLayouts(4).embeddedLayouts.length === 5)

    assertLocation(layoutImage.embeddedLayouts(5).location, Position(80, -25), Position(180, 55))
    assertLocation(layoutImage.embeddedLayouts(6).location, Position(180, 15), Position(210, 15))
    assertLocation(layoutImage.embeddedLayouts(7).location, Position(210, 0), Position(310, 30))
  }

  def assertLocation(location: Location, upperLeft: Position, lowerRight: Position): Unit = {
    assert(location.nw === upperLeft)
    assert(location.se === lowerRight)
  }
}
