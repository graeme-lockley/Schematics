package za.co.no9.draw

import java.awt.geom.AffineTransform
import java.awt.image.{BufferedImage => BI}
import java.awt.{Color, Font}
import java.io.File
import javax.imageio.ImageIO

trait Canvas {
	def drawText(text: Text, boundingRectangle: Rectangle)

	def drawRectangle(rectangle: Rectangle)

	def setPaint(black: Color)

	def setTransform(tx: AffineTransform)
}

class BufferedImage(dimension: Rectangle, boundary: Double = 2.0, scale: Int = 5) extends Canvas {
	val renderBI = new BI((dimension.width + boundary * 2).toInt * scale, (dimension.height + boundary * 2).toInt * scale, BI.TYPE_INT_ARGB)
	val graphics = renderBI.createGraphics()

	override def drawRectangle(rectangle: Rectangle): Unit = graphics.drawRect(rectangle.topLeft.x.toInt, rectangle.topLeft.y.toInt, rectangle.width.toInt, rectangle.height.toInt)

	override def setTransform(tx: AffineTransform): Unit = {
		val originTX = new AffineTransform()
		originTX.translate((-dimension.topLeft.x + boundary) * scale, (-dimension.topLeft.y + boundary) * scale)
		originTX.scale(scale, scale)
		graphics.setTransform(originTX)
		graphics.transform(tx)
	}

	override def setPaint(colour: Color): Unit = graphics.setPaint(colour)

	override def drawText(text: Text, boundingRectangle: Rectangle) = {
		val font = new Font(text.fontName, text.fontStyle.code, text.fontSize)
		graphics.setFont(font)
		val fontMetrics = graphics.getFontMetrics
		val stringWidth = fontMetrics.stringWidth(text.content)
		val stringHeight = fontMetrics.getAscent

		val renderContext = graphics.getFontRenderContext
		val glyphVector = font.createGlyphVector(renderContext, text.content)
		val visualBounds = glyphVector.getVisualBounds.getBounds

		graphics.setPaint(Color.black)
		val origin = text.orientation match {
			case Centre() =>
				boundingRectangle.topLeft.midPoint(boundingRectangle.bottomRight).add(-stringWidth / 2, -visualBounds.height / 2 - visualBounds.y)
		}
		graphics.drawString(text.content, origin.x.toInt, origin.y.toInt)
	}

	def write(format: String, fileName: String): Unit = ImageIO.write(renderBI, format, new File(fileName))
}