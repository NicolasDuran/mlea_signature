arg1=$1
arg2=$2

if test $# -lt 2
    then echo "Usage: ./compSign.sh infile outfile"
    exit 1
fi

ant && java -jar compSign.jar $1 $2

exit 0
