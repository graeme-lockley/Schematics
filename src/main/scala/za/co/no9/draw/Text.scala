package za.co.no9.draw

class FontStyle(val code: Int)

case class Plain() extends FontStyle(0)

case class Bold() extends FontStyle(1)

case class Italic() extends FontStyle(2)

case class BoldItalic() extends FontStyle(3)

case class Text(content: String, fontName: String = "Helvetica", fontSize: Int = 10, fontStyle: FontStyle = Plain(), orientation: CompassDirection = Centre())
