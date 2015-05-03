package za.co.no9.schematic

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D, RenderingHints}
import java.io.File
import javax.imageio.ImageIO

import scala.collection.mutable

abstract class Direction {
  def origin(sr: SchematicRuntime): Position
}

case class Up() extends Direction {
  override def origin(sr: SchematicRuntime): Position = sr.last.north
}

case class Down() extends Direction {
  override def origin(sr: SchematicRuntime): Position = sr.last.south
}

case class Left() extends Direction {
  override def origin(sr: SchematicRuntime): Position = sr.last.west
}

case class Right() extends Direction {
  override def origin(sr: SchematicRuntime): Position = sr.last.east
}

class Styles {
  var boxWidth: Double = 100.0
  var boxHeight: Double = 25.0
  var moveWidth: Double = 30.0
  var moveHeight: Double = 20.0
  var textHeight: Double = 15.0
  var direction: Direction = Right()
}

class SchematicRuntime(val ig: Graphics2D, val style: Styles) {
  private val nestedLayouts = new mutable.Stack[mutable.MutableList[ShapeLayout]]()

  private def lastLocation(nl: List[mutable.MutableList[ShapeLayout]]): Location = nl match {
    case Nil => new RectangleLocation(Position(0, 0), Position(0, 0))
    case x::xs => if (x.isEmpty) lastLocation(xs) else x.last.location
  }

  def last: Location = lastLocation(nestedLayouts.toList)

  def pushLayouts(layouts: mutable.MutableList[ShapeLayout]): Unit = nestedLayouts.push(layouts)

  def popLayouts(): Unit = nestedLayouts.pop()
}

trait Operations {
  protected def addShape(shape: Shape): Shape
  protected def styles(): Styles

  def arrow(): ArrowShape = {
    val arrow = new ArrowShape()
    addShape(arrow)
    arrow
  }

  def box(title: String = null, width: Double = styles().boxWidth, height: Double = styles().boxHeight, textHeight: Double = styles().textHeight): BoxShape = {
    val box = new BoxShape(title, width, height, textHeight)
    addShape(box)
    box
  }

  def block(): BlockShape = {
    val blockShape = new BlockShape(styles())
    addShape(blockShape)
    blockShape
  }

  def up(): DirectionShape = {
    val directionShape = new DirectionShape(Up())
    addShape(directionShape)
    directionShape
  }

  def down(): DirectionShape = {
    val directionShape = new DirectionShape(Down())
    addShape(directionShape)
    directionShape
  }

  def left(): DirectionShape = {
    val directionShape = new DirectionShape(Left())
    addShape(directionShape)
    directionShape
  }

  def right(): DirectionShape = {
    val directionShape = new DirectionShape(Right())
    addShape(directionShape)
    directionShape
  }
}

class Image extends Operations {
  val blockShape = new BlockShape(new Styles())

  override protected def addShape(shape: Shape): Shape = blockShape.addShape(shape)

  override protected def styles(): Styles = blockShape.styles()

  def draw(): Unit = {
    val width = 600
    val height = 400

    val layoutBI = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED)
    val layoutGraphics = layoutBI.createGraphics()

    val layouts = mutable.MutableList[ShapeLayout]()
    val schematicRuntime = new SchematicRuntime(layoutGraphics, styles())
    schematicRuntime.pushLayouts(layouts)

    val shapeLayout = blockShape.layout(schematicRuntime)

    schematicRuntime.popLayouts()

    val renderWidth = 2 + shapeLayout.location.ne.x - shapeLayout.location.sw.x
    val renderHeight = 2 + shapeLayout.location.ne.y - shapeLayout.location.sw.y
    println(renderWidth)
    println(renderHeight)
    val renderBI = new BufferedImage(renderWidth.toInt, renderHeight.toInt, BufferedImage.TYPE_INT_ARGB)
    val renderGraphics = renderBI.createGraphics()
    renderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    println(shapeLayout.location.sw)

    renderGraphics.translate(-shapeLayout.location.sw.x, -shapeLayout.location.sw.y)

    shapeLayout.render(renderGraphics)

    ImageIO.write(renderBI, "PNG", new File("bob.png"))
  }
}

case class Position(x: Double, y: Double) {
  def newX(newX: Double): Position = Position(newX, y)

  def newY(newY: Double): Position = Position(x, newY)

  def add(deltaX: Double, deltaY: Double): Position = Position(x + deltaX, y + deltaY)

  def midPoint(other: Position): Position = Position((x + other.x) / 2, (y + other.y) / 2)

  def lowerLeft(other: Position): Position = Position(Math.min(x, other.x), Math.min(y, other.y))

  def upperRight(other: Position): Position = Position(Math.max(x, other.x), Math.max(y, other.y))
}

trait Location {
  def centre: Position

  def north: Position

  def ne: Position

  def east: Position

  def se: Position

  def south: Position

  def sw: Position

  def west: Position

  def nw: Position
}

class RectangleLocation(positionA: Position, positionB: Position) extends Location {
  val lowerLeft = positionA.lowerLeft(positionB)
  val upperRight = positionA.upperRight(positionB)

  override def centre: Position = lowerLeft.midPoint(upperRight)

  override def se: Position = upperRight.newY(lowerLeft.y)

  override def sw: Position = lowerLeft

  override def west: Position = lowerLeft.newY(centre.y)

  override def north: Position = upperRight.newX(centre.x)

  override def nw: Position = upperRight.newX(lowerLeft.x)

  override def ne: Position = upperRight

  override def east: Position = upperRight.newY(centre.y)

  override def south: Position = lowerLeft.newX(centre.x)
}

trait Shape {
  def layout(sr: SchematicRuntime): ShapeLayout
}

trait ShapeLayout {
  def render(graphics: Graphics2D): Unit

  def location: Location
}

class BlockShape(val xxstyles: Styles) extends Shape with Operations {
  val shapes = mutable.MutableList[Shape]()

  override def layout(sr: SchematicRuntime): ShapeLayout = {
    val layouts = mutable.MutableList[ShapeLayout]()
    sr.pushLayouts(layouts)
    for (shape <- shapes) {
      layouts += shape.layout(sr)
    }
    sr.popLayouts()

    new ShapeLayout {
      val location: Location = layouts.tail.foldLeft(layouts.head.location)((a, b) => new RectangleLocation(a.sw.lowerLeft(b.location.sw), a.ne.upperRight(b.location.ne)))

      override def render(graphics: Graphics2D): Unit = layouts.foreach { _.render(graphics)}
    }
  }

  override def styles(): Styles = this.xxstyles

  override def addShape(shape: Shape): Shape = {
    shapes += shape
    shape
  }
}

class BoxShape(var title: String, var width: Double, var height: Double, var textHeight: Double, var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr)) extends Shape {
  def width(width: Int): BoxShape = {
    this.width = width
    this
  }

  override def layout(sr: SchematicRuntime): ShapeLayout = {
    val lowerLeft: Position = sr.style.direction match {
      case Up() => origin(sr).add(-width / 2, 0)
      case Down() => origin(sr).add(-width / 2, -height)
      case Left() => origin(sr).add(-width, -height / 2)
      case Right() => origin(sr).add(0, -height / 2)
    }

    new ShapeLayout {
      val location = new RectangleLocation(lowerLeft, lowerLeft.add(width, height))

      override def render(graphics: Graphics2D): Unit = {
        graphics.setPaint(Color.black)
        graphics.drawRect(location.sw.x.toInt, location.sw.y.toInt, width.toInt, height.toInt)
        val font = new Font("Arial", Font.PLAIN, textHeight.toInt)
        graphics.setFont(font)
        val fontMetrics = graphics.getFontMetrics
        val stringWidth = fontMetrics.stringWidth(title)
        val stringHeight = fontMetrics.getAscent
        graphics.setPaint(Color.black)
        graphics.drawString(title, (location.centre.x - stringWidth / 2).toInt, (location.centre.y + stringHeight / 2).toInt)
      }
    }
  }
}

class ArrowShape(var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr)) extends Shape {
  override def layout(sr: SchematicRuntime): ShapeLayout = {
    val sourcePoint = origin(sr)
    val endPoint = sr.style.direction match {
      case Up() => sourcePoint.add(0, sr.style.moveHeight)
      case Down() => sourcePoint.add(0, -sr.style.moveHeight)
      case Left() => sourcePoint.add(-sr.style.moveWidth, 0)
      case Right() => sourcePoint.add(sr.style.moveWidth, 0)
    }

    new ShapeLayout {
      val location: Location = new RectangleLocation(sourcePoint, endPoint)

      override def render(graphics: Graphics2D): Unit = {
        graphics.setPaint(Color.black)
        graphics.drawLine(sourcePoint.x.toInt, sourcePoint.y.toInt, endPoint.x.toInt, endPoint.y.toInt)
      }
    }
  }
}

class DirectionShape(val direction: Direction) extends Shape {
  override def layout(sr: SchematicRuntime): ShapeLayout = {
    sr.style.direction = direction
    new ShapeLayout {
      val location: Location = sr.last

      override def render(graphics: Graphics2D): Unit = ()
    }
  }
}

object Tester {
  def main(args: Array[String]) {
    val i = new Image()
    i.right()
    i.box(title = "Hello", width = 100.0)
    i.arrow()
    i.box("World")
    i.down()
    i.arrow()
    i.box("Bye bye love")

    i.draw()
  }
}