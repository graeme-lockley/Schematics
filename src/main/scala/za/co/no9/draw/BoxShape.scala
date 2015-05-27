package za.co.no9.draw

import java.awt.Color

class BoxShape(override val nestedShapes: List[Shape], override val layoutPoint: LayedOutShape => LayoutPoint, val width: Double, val height: Double, override val rotation: Double = 0.0, override val text: Option[Text] = None, override val name: String = "_") extends BlockShape(nestedShapes, layoutPoint, rotation, text, name) {
	override def shapeGrips(nestedLayedOutShapes: List[LayedOutShape], ls: LayoutState): DiscreteGrips = {
		DiscreteGrips(
			ls.transform(Point(0, 0)), ls.transform(Point(width / 2, 0)), ls.transform(Point(width, 0)),
			ls.transform(Point(0, height / 2)), ls.transform(Point(width / 2, height / 2)), ls.transform(Point(width, height / 2)),
			ls.transform(Point(0, height)), ls.transform(Point(width / 2, height)), ls.transform(Point(width, height)))
	}

	override def draw(canvas: Canvas, ls: LayoutState, boundingRectangle: Rectangle): Unit = {
		super.draw(canvas, ls, boundingRectangle)

		canvas.setPaint(Color.black)

		canvas.drawRectangle(BoundedRectangle(Point(0, 0), Point(width, height)))
	}
}
