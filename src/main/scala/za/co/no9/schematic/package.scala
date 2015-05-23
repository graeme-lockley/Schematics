package za.co.no9

package object schematic {
  def defaultBlockShapeTranslation(sr: SchematicRuntime, location: Location): Location = sr.style.direction match {
    case Up() => location.add(location.width / 2, location.height / 2)
    case Down() => location.add(location.width / 2, -location.height / 2)
	case Left() => location.add(location.width, 0.0)
	case Right() => location
  }
}
