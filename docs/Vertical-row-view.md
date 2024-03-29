---

layout: default
title: Vertical row view
resource: true
categories: [GUI]
 
---

## Vertical row view

On an open table tab, it is possible to open the so called **Vertical row view**.
Just double-click on a row. 
Also you can just select the row you want to see in detail, press Enter, open the table menu and select **Row details**. 

![Table menu](images/accelerators.png)

The vertical row view appears on the right side of the original table.

![Vertical Row View](images/verticalrowview.png)

The vertical view shows vertically all the fields of the selected row. This is beneficial in the following cases:

- The table contains so **many columns** that it is not possible to see them all at the same time in the table, forcing you to scroll horizontally. The vertical row view can only show a single row, but there is a good chance that it can show all its fields without need to scroll.
- The table contains very **large text fields**, for example LOBs. For a field of this kind only a part of it is visible in the table (normally the first line),  but the entire field is visible in the vertical row view, with the help of  scrollbars. 

The vertical row view can be shown or hidden very easily (it is just needed to press Enter) and is supposed to help only when there is a need for it, without being annoying.

Additionally it is possible to copy the content of individual fields from the vertical row, differently from the [copy menu](Copy-Data) of the table.

With the menu close to each text or binary field, it is possible to:
* **expand** the field to see better its content when it is multiline.
* **open** a dialog that shows the wrapped content of the field, useful when the content is very large.
* **download** the content of the field, as a **textual** file for text fields and as a **binary** file for binary fields (for example images)

![Vertical Row View Menu](images/verticalrowviewmenu.png)


### Filter fields

On the top of the view there is a text box, called Filter fields.
Writing on this text box, the view shows only the fields with a name **containing the text**.