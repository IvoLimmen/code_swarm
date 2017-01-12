#!/bin/bash
ffmpeg -framerate 24 -i 'data/%05d.png' -start_number 1 -c:v libx264 -r 30 -pix_fmt yuv420p out.mp4
