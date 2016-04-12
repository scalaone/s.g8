package $package$.support

import com.typesafe.config.ConfigFactory

trait ConfigSupport {
  val conf = ConfigFactory.load
}
