package za.co.no9.draw

trait CompassDirection {
	def translate(ls: LayoutState, grips: Grips): LayoutState
}

case class NorthWest() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.nw.invert())
}

case class North() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.north.invert())
}

case class NorthEast() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.ne.invert())
}

case class West() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.west.invert())
}

case class Centre() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.centre.invert())
}

case class East() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.east.invert())
}

case class SouthWest() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.sw.invert())
}

case class South() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.south.invert())
}

case class SouthEast() extends CompassDirection {
	override def translate(ls: LayoutState, grips: Grips): LayoutState = ls.translate(grips.se.invert())
}

trait Grips {
	def centre: Point

	def north: Point

	def ne: Point

	def east: Point

	def se: Point

	def south: Point

	def sw: Point

	def west: Point

	def nw: Point

	def topLeft: Point

	def bottomRight: Point

	def boundedRectangle: Rectangle
}

class RectangleGrips(positionA: Point, positionB: Point) extends Grips {
	val topLeft = positionA.topLeft(positionB)
	val bottomRight = positionA.bottomRight(positionB)

	def this(rectangle: Rectangle) = this(rectangle.topLeft, rectangle.bottomRight)

	override def se: Point = bottomRight

	override def sw: Point = bottomRight.newX(topLeft.x)

	override def west: Point = topLeft.newY(centre.y)

	override def north: Point = topLeft.newX(centre.x)

	override def nw: Point = topLeft

	override def ne: Point = topLeft.newX(bottomRight.x)

	override def east: Point = bottomRight.newY(centre.y)

	override def centre: Point = topLeft.midPoint(bottomRight)

	override def south: Point = bottomRight.newX(centre.x)

	def add(deltaX: Double, deltaY: Double): RectangleGrips = new RectangleGrips(topLeft.add(deltaX, deltaY), bottomRight.add(deltaX, deltaY))

	override def boundedRectangle: Rectangle = {
		val topLeftPoint = topLeft
		val bottomRightPoint = bottomRight

		new Rectangle {
			override val topLeft: Point = topLeftPoint
			override val bottomRight: Point = bottomRightPoint
		}
	}
}

case class DiscreteGrips(nw: Point, north: Point, ne: Point, west: Point, centre: Point, east: Point, sw: Point, south: Point, se: Point) extends Grips {
	override def boundedRectangle: Rectangle = {
		val topLeftPoint = topLeft
		val bottomRightPoint = bottomRight

		new Rectangle {
			override val topLeft: Point = topLeftPoint
			override val bottomRight: Point = bottomRightPoint
		}
	}

	def topLeft = nw.topLeft(north.topLeft(ne.topLeft(west.topLeft(centre.topLeft(east.topLeft(sw.topLeft(south.topLeft(se))))))))

	def bottomRight = nw.bottomRight(north.bottomRight(ne.bottomRight(west.bottomRight(centre.bottomRight(east.bottomRight(sw.bottomRight(south.bottomRight(se))))))))
}
