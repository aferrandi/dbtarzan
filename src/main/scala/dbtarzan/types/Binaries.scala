package dbtarzan.types

object Binaries {
  opaque type Binary = Array[Byte]

  object Binary:
    // These are the two ways to lift to the Logarithm type
    def apply(bytes: Array[Byte]): Binary = bytes

  // Extension methods define opaque types' public APIs
  extension (bytes: Binary)
    def asBytes: Array[Byte] = bytes
    def asString: String = if(bytes != null) new String(bytes) else ""

    def truncate(max: Int): Binary = Binary(bytes.take(max))
}