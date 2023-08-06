package dbtarzan.db.util

import scala.reflect.Selectable.reflectiveSelectable

/* like the try(){} function in java or the C# using statement, automatically closes the resources that it opens when they are not needed anymore 
Here used to close an open SQL ressult set */
object ResourceManagement {
	def using[T <: { def close(): Unit }, R]
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