package za.co.no9.draw

import java.awt.geom.{AffineTransform, Point2D}

case class LayoutState(tx: AffineTransform, rotation: Double, scale: Double, translation: Point) {
	def this(rotation: Double, scale: Double, translation: Point) = this(new AffineTransform(), rotation, scale, translation)

	tx.translate(translation.x, translation.y)
	tx.scale(scale, scale)
	tx.rotate(Math.toRadians(rotation))

	def transform(point: Point): Point = {
		val outputPoint = tx.transform(new Point2D.Double(point.x, point.y), null)
		Point(outputPoint.getX, outputPoint.getY)
	}

	def absoluteTranslate(point: Point) = {
		translate(inverseTransform(point))
	}

	def inverseTransform(point: Point): Point = {
		val outputPoint = new Point2D.Double(point.x, point.y)
		tx.inverseTransform(new Point2D.Double(point.x, point.y), outputPoint)
		Point(outputPoint.getX, outputPoint.getY)
	}

	def translate(point: Point): LayoutState = LayoutState(tx.clone.asInstanceOf[AffineTransform], 0.0, 1.0, point)

	def rotate(angle: Double): LayoutState = LayoutState(tx.clone.asInstanceOf[AffineTransform], angle, 1.0, Point(0.0, 0.0))

	def absoluteRotation(rotation: Double) = {
		val newTX = tx.clone.asInstanceOf[AffineTransform]
		newTX.setToRotation(0.0)
		LayoutState(newTX, 0.0, 1.0, Point(0.0, 0.0))
	}
}
