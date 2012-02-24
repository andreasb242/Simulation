<?php

define(NEWLINE, "\n");

$mf  = 'Manifest-Version: 1.0' . NEWLINE;

$cp = '';

$libdir = '../lib/';

if (is_dir($libdir)) {
	if ($dh = opendir($libdir)) {
		while (($file = readdir($dh)) !== false) {
			if(substr($file, -4) == '.jar') {
				$cp .= ' ' . $libdir . $file;
			}
		}
		closedir($dh);
	}
}

$mf .= 'Class-Path:' . $cp . NEWLINE;

$mf .= 'Main-Class: startup.SimulationMain' . NEWLINE;

print "creating {$_SERVER['argv'][1]}\n";

file_put_contents($_SERVER['argv'][1], $mf);

?>