package za.co.no9.draw

class FontStyle(val code: Int)

case class Plain() extends FontStyle(0)

case class Bold() extends FontStyle(1)

case class Italic() extends FontStyle(2)

case class BoldItalic() extends FontStyle(3)

case class TextStyle(name: String = "Helvetica", size: Int = 10, style: FontStyle = Plain(), orientation: CompassDirection = Centre(), colour: Colour = Colour.BLACK)

case class Text(content: String, style: TextStyle = TextStyle())
