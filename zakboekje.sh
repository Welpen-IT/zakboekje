export JAVA_HOME=/usr/lib/jvm/java-6-sun
export PATH=$JAVA_HOME/bin:$PATH

rm pdf/*.pdf

for fn in svg/*.svg; do
	pdf=`basename "$fn" ".svg"`
	echo $pdf
	inkscape --export-pdf=pdf/$pdf.pdf $fn 
done

cd java
ant
