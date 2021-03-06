package za.co.no9.draw

import java.awt.geom.AffineTransform

class LineShape(points: (LaidOutShape) => List[At], val text: Option[Text] = None, val lineStyle: Option[LineStyle] = Some(LineStyle(width = 1.0f, colour = Colour(0, 0, 0))), val rotation: Double = 0.0, val name: String = "_box") extends Shape {
	override def nestedShapes: List[Shape] = List()

	override def layout(previousLaidOutShape: LaidOutShape, layoutState: TX): LaidOutShape = {
		val absolutePoints = points(previousLaidOutShape).map(atPoint => {
			val pointTX: TX = TX(new AffineTransform(), layoutState.rotation, layoutState.scale, atPoint.position)
			pointTX.transform(atPoint.relative)
		})

		new LineLaidOutShape(previousLaidOutShape, absolutePoints, name)
	}
}

class LineLaidOutShape(val previousLaidOutShape: LaidOutShape, val absolutePoints: List[Point], val shapeName: String) extends LaidOutShape with UsableLaidOutShape {
	override def relativeBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

	override def grips: Grips = PointRectangle(Point(0, 0)).grips

	override def realBoundedRectangle: Rectangle = {
		val initial: Rectangle = PointRectangle(absolutePoints.head)
		absolutePoints.tail.foldLeft(initial)((x, y) => x.union(PointRectangle(y)))
	}

	override def render(canvas: Canvas): Unit = canvas.drawLine(absolutePoints)

	override def nestedShapes: List[LaidOutShape] = List()

	override val name: String = "_" + shapeName

	override val previous: Option[LaidOutShape] = Some(previousLaidOutShape)
}
