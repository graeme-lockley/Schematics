package za.co.no9.draw

trait Shape {
	def nestedShapes: List[Shape]

	def layout(previous: LaidOutShape, layoutState: LayoutState): LaidOutShape
}

trait LaidOutShape {
	def __ls: LayoutState

	def nestedShapes: List[LaidOutShape]

	def realBoundedRectangle: Rectangle

	def relativeBoundedRectangle: Rectangle

	def grips: Grips

	def render(canvas: Canvas): Unit

	val name: String

	val previous: Option[LaidOutShape]

	def last(back: Int): Option[LaidOutShape]

	def last(name: String): Option[LaidOutShape]
}

trait UsableLaidOutShape {
	this: LaidOutShape =>
	val name: String

	val previous: Option[LaidOutShape]

	def last(back: Int): Option[LaidOutShape] = if (back <= 0) Option(this) else previous.flatMap(_.last(back - 1))

	def last(name: String): Option[LaidOutShape] = if (name.equals(this.name)) Option(this) else previous.flatMap(_.last(name))
}

trait IgnoredLaidOutShape {
	val previous: Option[LaidOutShape]

	def last(back: Int): Option[LaidOutShape] = if (previous.isDefined) previous.get.last(back) else None

	def last(name: String): Option[LaidOutShape] = if (previous.isDefined) previous.get.last(name) else None
}

