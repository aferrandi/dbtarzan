package dbtarzan.db.util

object ResourceManagement {
	def using[T <: { def close() }]
	    (resource: T)
	    (block: T => Unit) : Unit =
	{
	  try {
	    block(resource)
	  } finally {
	    if (resource != null) resource.close()
	  }
	}
}