for fn in *.gif; do
	bn=`basename "$fn" ".gif"`
	convert $bn.gif $bn.bmp
	potrace -s $bn.bmp
done
