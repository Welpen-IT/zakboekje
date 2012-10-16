for fn in *.png; do
	convert $fn $fn.bmp
	potrace -s $fn.bmp
done
