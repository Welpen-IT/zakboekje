for fn in *.jpg; do
	convert $fn $fn.bmp
	potrace -s $fn.bmp
done
