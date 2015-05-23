package za.co.no9.schematic

import java.awt.{BasicStroke, Color, Font, Graphics2D}

import scala.collection.mutable

trait Shape {
	def layout(sr: SchematicRuntime): ShapeLayout
}

trait ShapeLayout {
	def render(graphics: Graphics2D): Unit

	def location: Location

	def embeddedLayouts: List[ShapeLayout]
}

class BlockShape(val blockStyles: Styles, var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr), val translate: ((SchematicRuntime, Location) => Location) = defaultBlockShapeTranslation) extends Shape with Operations {
	val shapes = mutable.MutableList[Shape]()

	override def layout(sr: SchematicRuntime): ShapeLayout = {
		val layouts = mutable.MutableList[ShapeLayout]()
		sr.pushLayouts(layouts)
		for (shape <- shapes) {
			layouts += shape.layout(sr)
		}
		sr.popLayouts()
		val blockLocation: Location = layouts.tail.foldLeft(layouts.head.location)((a, b) => new RectangleLocation(a.nw.upperLeft(b.location.nw), a.se.lowerRight(b.location.se)))

		new ShapeLayout {
			val location: Location = translate(sr, blockLocation)
			val newOrigin = sr.style.direction match {
				case Right() => Position(layouts.head.location.west.x, layouts.head.location.west.y)
				case Left() => Position(layouts.head.location.east.x, layouts.head.location.east.y)
				case Down() => Position(layouts.head.location.north.x, layouts.head.location.north.y)
				case Up() => Position(layouts.head.location.south.x, layouts.head.location.south.y)
			}

			override def render(graphics: Graphics2D): Unit = {
				graphics.translate(-(blockLocation.nw.x - location.nw.x).toInt, -(blockLocation.nw.y - location.nw.y).toInt)

				graphics.setPaint(Color.black)
				val currentStroke = graphics.getStroke
				graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, Array[Float](10.0f), 0.0f))
				graphics.drawRect(blockLocation.nw.x.toInt, blockLocation.nw.y.toInt, (blockLocation.ne.x - blockLocation.sw.x).toInt, (blockLocation.sw.y - blockLocation.ne.y).toInt)
				graphics.setStroke(currentStroke)

				println(s"block: original: ${blockLocation.nw} - ${blockLocation.se}  normalized: ${location.nw} - ${location.se}")
				layouts.foreach {
					_.render(graphics)
				}
				graphics.translate((blockLocation.nw.x - location.nw.x).toInt, (blockLocation.nw.y - location.nw.y).toInt)
			}

			override def embeddedLayouts: List[ShapeLayout] = layouts.toList
		}
	}

	override def styles(): Styles = this.blockStyles

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
			case Up() => origin(sr).add(-width / 2, -height)
			case Down() => origin(sr).add(-width / 2, 0)
			case Left() => origin(sr).add(-width, -height / 2)
			case Right() => origin(sr).add(0, -height / 2)
		}

		new ShapeLayout {
			val location = new RectangleLocation(lowerLeft, lowerLeft.add(width, height))

			override def render(graphics: Graphics2D): Unit = {
				val angle = 270.0

				graphics.setPaint(Color.black)
				graphics.translate(location.centre.x, location.centre.y)
				graphics.rotate(Math.toRadians(angle))

				graphics.drawRect(-(width / 2).toInt, -(height / 2).toInt, width.toInt, height.toInt)
				val font = new Font("Arial", Font.PLAIN, textHeight.toInt)
				graphics.setFont(font)
				val fontMetrics = graphics.getFontMetrics
				val stringWidth = fontMetrics.stringWidth(title)
				val stringHeight = fontMetrics.getAscent

				graphics.setPaint(Color.black)
				graphics.drawString(title, -stringWidth / 2, stringHeight / 2)
				graphics.drawRect(-stringWidth / 2, -stringHeight / 2, stringWidth, stringHeight)
				graphics.rotate(-Math.toRadians(angle))
				graphics.translate(-location.centre.x, -location.centre.y)

				println(s"box: ${location.nw} - ${location.se}: $title (${(location.centre.x - stringWidth / 2).toInt}, ${(location.centre.y - stringHeight / 2).toInt})")
			}

			override def embeddedLayouts: List[ShapeLayout] = List()
		}
	}
}

class ArrowShape(var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr)) extends Shape {
	override def layout(sr: SchematicRuntime): ShapeLayout = {
		val sourcePoint = origin(sr)
		val endPoint = sr.style.direction match {
			case Up() => sourcePoint.add(0, -sr.style.moveHeight)
			case Down() => sourcePoint.add(0, sr.style.moveHeight)
			case Left() => sourcePoint.add(-sr.style.moveWidth, 0)
			case Right() => sourcePoint.add(sr.style.moveWidth, 0)
		}

		new ShapeLayout {
			val location: Location = new RectangleLocation(sourcePoint, endPoint)

			override def render(graphics: Graphics2D): Unit = {
				graphics.setPaint(Color.black)
				graphics.drawLine(sourcePoint.x.toInt, sourcePoint.y.toInt, endPoint.x.toInt, endPoint.y.toInt)

				println(s"line: $sourcePoint - $endPoint")
			}

			override def embeddedLayouts: List[ShapeLayout] = List()
		}
	}
}

class DirectionShape(val direction: Direction) extends Shape {
	override def layout(sr: SchematicRuntime): ShapeLayout = {
		sr.style.direction = direction
		new ShapeLayout {
			val location: Location = sr.last

			override def render(graphics: Graphics2D): Unit = ()

			override def embeddedLayouts: List[ShapeLayout] = List()
		}
	}
}

class PointShape(var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr)) extends Shape {
	override def layout(sr: SchematicRuntime): ShapeLayout = {
		new ShapeLayout {
			val sourcePoint = origin(sr)
			val location: Location = new PointLocation(sourcePoint)

			override def render(graphics: Graphics2D): Unit = {
				println(s"point: $sourcePoint")
			}

			override def embeddedLayouts: List[ShapeLayout] = List()
		}
	}
}
