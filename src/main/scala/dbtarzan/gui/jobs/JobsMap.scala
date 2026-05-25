package dbtarzan.gui.jobs

import dbtarzan.gui.database.TableTabs
import dbtarzan.gui.tabletabs.TTableWithTab
import dbtarzan.messages.{Jobid, QueryId}

import scala.collection.mutable

class JobsMap {
  private val mapTable = mutable.HashMap.empty[Jobid, TableTabs]
}
