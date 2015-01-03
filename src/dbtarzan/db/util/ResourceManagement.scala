package dbtarzan.db.util

object ResourceManagement {
	def using[T <: { def close() }]
	    (resource: T)
	    (block: T => Unit) : Unit =
	{
	  try {
	    block(resource)
	  } catch {
	  	case e: Exception => println("exception caught: " + e);
	  } finally {
	    if (resource != null) resource.close()
	  }
	}
}