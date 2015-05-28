package za.co.no9.draw

import java.awt.geom.AffineTransform
import java.awt.image.{BufferedImage => BI}
import java.awt.{BasicStroke, Color, Font}
import java.io.File
import javax.imageio.ImageIO

trait Canvas {
	def drawText(text: Text, boundingRectangle: Rectangle)

	def drawRectangle(rectangle: Rectangle, lineStyle: Option[LineStyle])

	def setPaint(colour: Colour)

	def setTransform(tx: AffineTransform)
}

class BufferedImage(dimension: Rectangle, boundary: Double = 2.0, scale: Int = 5) extends Canvas {
	val renderBI = new BI((dimension.width + boundary * 2).toInt * scale, (dimension.height + boundary * 2).toInt * scale, BI.TYPE_INT_ARGB)
	val graphics = renderBI.createGraphics()

	def toColor(colour: Colour): Color = new Color(colour.r, colour.g, colour.b)

	override def drawRectangle(rectangle: Rectangle, lineStyle: Option[LineStyle]) = {
		if (lineStyle.isDefined) {
			val stroke = new BasicStroke(lineStyle.get.width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f)
			graphics.setStroke(stroke)
			setPaint(lineStyle.get.colour)

			graphics.drawRect(rectangle.topLeft.x.toInt, rectangle.topLeft.y.toInt, rectangle.width.toInt, rectangle.height.toInt)
		}
	}

	override def setTransform(tx: AffineTransform) = {
		val originTX = new AffineTransform()
		originTX.translate((-dimension.topLeft.x + boundary) * scale, (-dimension.topLeft.y + boundary) * scale)
		originTX.scale(scale, scale)
		graphics.setTransform(originTX)
		graphics.transform(tx)
	}

	override def setPaint(colour: Colour) = graphics.setPaint(toColor(colour))

	override def drawText(text: Text, boundingRectangle: Rectangle) = {
		val font = new Font(text.style.name, text.style.style.code, text.style.size)
		graphics.setFont(font)
		val fontMetrics = graphics.getFontMetrics
		val stringWidth = fontMetrics.stringWidth(text.content)
		val stringHeight = fontMetrics.getAscent

		val renderContext = graphics.getFontRenderContext
		val glyphVector = font.createGlyphVector(renderContext, text.content)
		val visualBounds = glyphVector.getVisualBounds.getBounds

		setPaint(text.style.colour)

		val origin = text.style.orientation match {
			case Centre() =>
				boundingRectangle.topLeft.midPoint(boundingRectangle.bottomRight).add(-stringWidth / 2, -visualBounds.height / 2 - visualBounds.y)
		}
		graphics.drawString(text.content, origin.x.toInt, origin.y.toInt)
	}

	def write(format: String, fileName: String): Unit = ImageIO.write(renderBI, format, new File(fileName))
}