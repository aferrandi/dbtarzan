package dbtarzan.db.util

object ResourceManagement {


	def using[T <: { def close() }, R]
	    (resource: T)
	    (block: T => R) : R =
	{
	  try {
	    block(resource)
	  } finally {
	    if (resource != null) resource.close()
	  }
	}
}