package za.co.no9.schematic

import java.awt.{BasicStroke, Color, Font, Graphics2D}

import scala.collection.mutable

trait Shape {
	def layout(sr: SchematicRuntime): ShapeLayout
}

trait ShapeLayout {
	def render(graphics: Graphics2D): Unit

	def location: Location
}

class BlockShape(val blockStyles: Styles, var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr), val translate: (Location => Location) = blockLocation => blockLocation.add(blockLocation.width / 2, -blockLocation.height / 2)) extends Shape with Operations {
	val shapes = mutable.MutableList[Shape]()

	override def layout(sr: SchematicRuntime): ShapeLayout = {
		val layouts = mutable.MutableList[ShapeLayout]()
		val oldOrigin = sr.style.origin
		sr.pushLayouts(layouts)
		for (shape <- shapes) {
			layouts += shape.layout(sr)
		}
		sr.popLayouts()
		val blockLocation: Location = layouts.tail.foldLeft(layouts.head.location)((a, b) => new RectangleLocation(a.sw.lowerLeft(b.location.sw), a.ne.upperRight(b.location.ne)))

		new ShapeLayout {
			val location: Location = translate(blockLocation)
			val newOrigin = Position(location.width / 2, -location.height / 2)

			override def render(graphics: Graphics2D): Unit = {
				println(s"translating (${location.sw.x.toInt})")
				graphics.translate(newOrigin.x.toInt, newOrigin.y.toInt)

				graphics.setPaint(Color.black)
				val currentStroke = graphics.getStroke
				graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, Array[Float](10.0f), 0.0f))
				graphics.drawRect(blockLocation.sw.x.toInt, blockLocation.sw.y.toInt, (blockLocation.ne.x - blockLocation.sw.x).toInt, (blockLocation.ne.y - blockLocation.sw.y).toInt)
				graphics.setStroke(currentStroke)

				println(s"block: original: ${blockLocation.sw} - ${blockLocation.ne}  normalized: ${location.sw} - ${location.ne}")
				layouts.foreach {
					_.render(graphics)
				}
				graphics.translate(-newOrigin.x.toInt, -newOrigin.y.toInt)
			}
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
				graphics.setPaint(Color.black)
				graphics.drawRect(location.sw.x.toInt, location.sw.y.toInt, width.toInt, height.toInt)
				val font = new Font("Arial", Font.PLAIN, textHeight.toInt)
				graphics.setFont(font)
				val fontMetrics = graphics.getFontMetrics
				val stringWidth = fontMetrics.stringWidth(title)
				val stringHeight = fontMetrics.getAscent
				graphics.setPaint(Color.black)
				graphics.drawString(title, (location.centre.x - stringWidth / 2).toInt, (location.centre.y + stringHeight / 2).toInt)

				println(s"box: ${location.sw} - ${location.ne}: $title")
			}
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

class PointShape(var origin: (SchematicRuntime => Position) = sr => sr.style.direction.origin(sr)) extends Shape {
	override def layout(sr: SchematicRuntime): ShapeLayout = {
		new ShapeLayout {
			val sourcePoint = origin(sr)
			val location: Location = new PointLocation(sourcePoint)

			override def render(graphics: Graphics2D): Unit = {
				println(s"point: $sourcePoint")
			}
		}
	}
}
