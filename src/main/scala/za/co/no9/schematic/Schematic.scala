package za.co.no9.schematic

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

class SchematicRuntime(val style: Styles) {
	private val nestedLayouts = new mutable.Stack[mutable.MutableList[ShapeLayout]]()

	private def lastLocation(nl: List[mutable.MutableList[ShapeLayout]]): Location = nl match {
		case Nil => new RectangleLocation(Position(0, 0), Position(0, 0))
		case x :: xs => if (x.isEmpty) lastLocation(xs) else x.last.location
	}

	def last: Location = lastLocation(nestedLayouts.toList)

	def pushLayouts(layouts: mutable.MutableList[ShapeLayout]): Unit = nestedLayouts.push(layouts)

	def popLayouts(): Unit = nestedLayouts.pop()
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

	def width: Double = east.x - west.x

	def height: Double = south.y - north.y

	def add(deltaX: Double, deltaY: Double): Location
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

	override def add(deltaX: Double, deltaY: Double): RectangleLocation = new RectangleLocation(lowerLeft.add(deltaX, deltaY), upperRight.add(deltaX, deltaY))
}

class PointLocation(position: Position) extends Location {

	override def centre: Position = position

	override def se: Position = position

	override def sw: Position = position

	override def west: Position = position

	override def north: Position = position

	override def nw: Position = position

	override def ne: Position = position

	override def east: Position = position

	override def south: Position = position

	override def add(deltaX: Double, deltaY: Double): PointLocation = new PointLocation(position.add(deltaX, deltaY))
}

