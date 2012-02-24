<?php

define(NEWLINE, "\n");

$n = file_get_contents('res/Info.xml');

$cp = '';

$libdir = '../../lib/';

if (is_dir($libdir)) {
	if ($dh = opendir($libdir)) {
		while (($file = readdir($dh)) !== false) {
			if(substr($file, -4) == '.jar') {
				$cp .= ':$JAVAROOT/lib/' . $file;
			}
		}
		closedir($dh);
	}
}

$cp .= ':$JAVAROOT/Simulation.jar';

$n = str_replace('{CLASSPATH}', substr($cp, 1), $n);

file_put_contents($_SERVER['argv'][1], $n);

?>