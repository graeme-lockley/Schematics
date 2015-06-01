package za.co.no9.draw

trait Shape {
	def nestedShapes: List[Shape]

	def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape
}

trait LayedOutShape {
	def __ls: LayoutState

	def nestedShapes: List[LayedOutShape]

	def realBoundedRectangle: Rectangle

	def relativeBoundedRectangle: Rectangle

	def grips: Grips

	def render(canvas: Canvas): Unit

	val name: String

	val previous: Option[LayedOutShape]

	def last(back: Int): Option[LayedOutShape]

	def last(name: String): Option[LayedOutShape]
}

trait UsableLayedOutShape {
	this: LayedOutShape =>
	val name: String

	val previous: Option[LayedOutShape]

	def last(back: Int): Option[LayedOutShape] = if (back <= 0) Option(this) else previous.flatMap(_.last(back - 1))

	def last(name: String): Option[LayedOutShape] = if (name.equals(this.name)) Option(this) else previous.flatMap(_.last(name))
}

trait IgnoredLayedOutShape {
	val previous: Option[LayedOutShape]

	def last(back: Int): Option[LayedOutShape] = if (previous.isDefined) previous.get.last(back) else None

	def last(name: String): Option[LayedOutShape] = if (previous.isDefined) previous.get.last(name) else None
}

