package za.co.no9.draw

trait Shape {
	def nestedShapes: List[Shape]

	def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape
}

trait LayedOutShape {
	def nestedShapes: List[LayedOutShape]

	def boundingRectangle: Rectangle

	def grips: Grips

	def render(canvas: Canvas): Unit

	val previous: Option[LayedOutShape]

	val name: String

	def last(back: Int): Option[LayedOutShape] = if (back <= 0) Option(this) else previous.flatMap(_.last(back - 1))

	def last(name: String): Option[LayedOutShape] = if (name.equals(this.name)) Option(this) else previous.flatMap(_.last(name))
}

