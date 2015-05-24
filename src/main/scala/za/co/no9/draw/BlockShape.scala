package za.co.no9.draw

import scala.collection.mutable

class BlockShape(val nestedShapes: List[Shape], val layoutPoint: LayedOutShape => LayoutPoint, val rotation: Double = 0.0, val name: String = "_") extends Shape {
	override def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape = {
		val ls = layoutState.absoluteTranslate(Point(0, 0)).absoluteRotation(0.0)

		val calculatedLayout = layoutShapeContent(previous, ls)
		val calculatedGrips = calculatedLayout.grips

		val atPosition: LayoutPoint = layoutPoint(previous)
		val atLayoutState: LayoutState = layoutState.absoluteTranslate(atPosition.at.position).translate(atPosition.at.relative).rotate(rotation)

		layoutShapeContent(previous, atPosition.grip.translate(atLayoutState, calculatedGrips))
	}

	protected def layoutShapeContent(previous: LayedOutShape, ls: LayoutState): LayedOutShape = {
		val nestedLayedOutShapes = mutable.MutableList[LayedOutShape]()

		var previousLayoutOutShape = initialLayoutOutShape

		nestedShapes foreach { shape =>
			val nextLayoutOutShape = shape.layout(previousLayoutOutShape, ls)
			nestedLayedOutShapes += nextLayoutOutShape
			previousLayoutOutShape = nextLayoutOutShape
		}

		val layedOutShapeGrips = shapeGrips(nestedLayedOutShapes.toList, ls)

		val shapePrevious = previous
		val shapeName = name

		new LayedOutShape {
			override def boundingRectangle: Rectangle = grips.boundedRectangle

			override def grips: Grips = layedOutShapeGrips

			override def render(canvas: Canvas): Unit = {
				nestedLayedOutShapes foreach {
					_.render(canvas)
				}
				draw(canvas, ls)
			}

			override def nestedShapes: List[LayedOutShape] = nestedLayedOutShapes.toList

			override val name = shapeName

			override val previous: Option[LayedOutShape] = Option(shapePrevious)
		}
	}

	def shapeGrips(nestedLayedOutShapes: List[LayedOutShape], ls: LayoutState): Grips = {
		if (nestedShapes.isEmpty)
			initialLayoutOutShape.grips
		else
			nestedLayedOutShapes.tail.foldLeft(nestedLayedOutShapes.head.boundingRectangle)((br, layedOutShape) => br.union(layedOutShape.boundingRectangle)).grips
	}

	def draw(canvas: Canvas, ls: LayoutState): Unit = {}

	protected def initialLayoutOutShape: LayedOutShape = {
		new LayedOutShape {
			override def boundingRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

			override def render(canvas: Canvas): Unit = ()

			override def nestedShapes: List[LayedOutShape] = List()

			override val name: String = "_"
			override val previous: Option[LayedOutShape] = Option.empty
		}
	}
}
