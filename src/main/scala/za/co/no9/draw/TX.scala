package za.co.no9.draw

import java.awt.geom.{AffineTransform, Point2D}

case class TX(tx: AffineTransform, rotation: Double, scale: Double, translation: Point) {
	def this(rotation: Double, scale: Double, translation: Point) = this(new AffineTransform(), rotation, scale, translation)

	tx.translate(translation.x, translation.y)
	tx.scale(scale, scale)
	tx.rotate(Math.toRadians(rotation))

	def transform(point: Point): Point = {
		val outputPoint = tx.transform(new Point2D.Double(point.x, point.y), null)
		Point(outputPoint.getX, outputPoint.getY)
	}

	def transform(rectangle: Rectangle): Rectangle = transform(rectangle.grips).realBoundedRectangle

	def transform(grips: Grips): Grips =
		DiscreteGrips(
			transform(grips.nw), transform(grips.north), transform(grips.ne),
			transform(grips.west), transform(grips.centre), transform(grips.east),
			transform(grips.sw), transform(grips.south), transform(grips.se))

	def absoluteTranslate(point: Point): TX = translate(inverseTransform(point))

	def inverseTransform(point: Point): Point = {
		val outputPoint = new Point2D.Double(point.x, point.y)
		tx.inverseTransform(new Point2D.Double(point.x, point.y), outputPoint)
		Point(outputPoint.getX, outputPoint.getY)
	}

	def translate(point: Point): TX = TX(tx.clone.asInstanceOf[AffineTransform], 0.0, 1.0, point)

	def rotate(angle: Double): TX = TX(tx.clone.asInstanceOf[AffineTransform], angle, 1.0, Point(0.0, 0.0))

	def absoluteRotation(rotation: Double): TX = {
		val newTX = tx.clone.asInstanceOf[AffineTransform]
		newTX.setToRotation(0.0)
		TX(newTX, 0.0, 1.0, Point(0.0, 0.0))
	}
}
