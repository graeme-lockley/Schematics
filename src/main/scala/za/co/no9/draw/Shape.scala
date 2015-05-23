package za.co.no9.draw

import java.awt.Graphics2D

trait Shape {
	def nestedShapes: List[Shape]

	def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape
}

trait LayedOutShape {
	def nestedShapes: List[LayedOutShape]

	def boundingRectangle: Rectangle

	def grips: Grips

	def render(g: Graphics2D): Unit

	val previous: Option[LayedOutShape]

	val name: Option[String]

	def last(back: Int): Option[LayedOutShape] = if (back <= 0) Option(this) else previous.flatMap(_.last(back - 1))

	def last(name: String): Option[LayedOutShape] = if (name.equals(this.name.getOrElse(""))) Option(this) else previous.flatMap(_.last(name))
}

