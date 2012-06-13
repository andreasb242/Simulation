#!/usr/bin/env python

import pygtk
pygtk.require('2.0')

import gtk
import sys

# Check for new pygtk: this is new class in PyGtk 2.4
if gtk.pygtk_version < (2,3,90):
	print "PyGtk 2.3.90 or later required for this example"
	sys.exit(3)

if len(sys.argv) < 6:
	sys.exit(1)

if sys.argv[4]== "open":
	dialog = gtk.FileChooserDialog(sys.argv[1],
		                           None,
		                           gtk.FILE_CHOOSER_ACTION_OPEN,
		                           (gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL,
		                            gtk.STOCK_OPEN, gtk.RESPONSE_OK))
else:
	dialog = gtk.FileChooserDialog(sys.argv[1],
		                           None,
		                           gtk.FILE_CHOOSER_ACTION_SAVE,
		                           (gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL,
		                            gtk.STOCK_SAVE, gtk.RESPONSE_OK))

dialog.set_default_response(gtk.RESPONSE_OK)
dialog.set_current_folder(sys.argv[4])

filter = gtk.FileFilter()
filter.set_name(sys.argv[2])
filter.add_pattern(sys.argv[3])
dialog.add_filter(filter)

response = dialog.run()
if response == gtk.RESPONSE_OK:
	print 'FileSelected:' + dialog.get_filename()
	dialog.destroy()
	sys.exit(0)
elif response == gtk.RESPONSE_CANCEL:
	dialog.destroy()
	sys.exit(2)

sys.exit(2)

